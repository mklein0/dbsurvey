FROM alpine:3.10 as jdk-builder

ARG PHOENIX_VERSION
ARG HBASE_VER

RUN set -x \
    && apk --no-cache add \
        bash \
        openjdk8 \
        maven \
    ## query server
    && wget -q -O - https://github.com/apache/phoenix-queryserver/archive/refs/tags/6.0.0RC5.tar.gz \
        | tar -xzf - -C /usr/local \
    && mv /usr/local/phoenix-queryserver-6.0.0RC5 /usr/local/phoenix-queryserver \
    && cd /usr/local/phoenix-queryserver \
    && mvn clean package -Dphoenix.version=${PHOENIX_VERSION} -Dphoenix.client.artifactid=phoenix-client-hbase-${HBASE_VER} \
    && cp ./phoenix-queryserver-assembly/target/phoenix-queryserver-*-bin.tar.gz ./phoenix-queryserver-bin.tar.gz

FROM alpine:3.10

ARG BUILD_DATE
ARG VCS_REF
ARG VERSION
ARG HBASE_VERSION
ARG HBASE_VER
ARG PHOENIX_VERSION
ARG PHOENIX_VER
ARG HADOOP_VERSION

ENV HBASE_VERSION    ${HBASE_VERSION}
ENV HADOOP_VERSION   ${HADOOP_VERSION}
ENV HBASE_VER        ${HBASE_VER}
ENV PHOENIX_VER      ${PHOENIX_VER}

ENV HADOOP_HOME      /usr/local/hadoop-${HADOOP_VERSION}
ENV HBASE_HOME       /usr/local/hbase-${HBASE_VERSION}
ENV PHOENIX_HOME      /usr/local/phoenix-${PHOENIX_VER}

ENV HBASE_CONF_DIR   ${HBASE_HOME}/conf
ENV HBASE_LOG_DIR    /var/log/hbase
ENV HBASE_TMP_DIR    /hbase

ENV JAVA_HOME  /usr/lib/jvm/default-jvm
ENV PATH       $PATH:${JAVA_HOME}/bin:${HBASE_HOME}/sbin:${HBASE_HOME}/bin:${HADOOP_HOME}/bin:${PHOENIX_HOME}/bin

ENV HADOOP_NAMENODE1_HOSTNAME     namenode-1.vnet
ENV HBASE_ROOT_DIR                hdfs://${HADOOP_NAMENODE1_HOSTNAME}:8020/hbase
ENV HBASE_HMASTER1_HOSTNAME       hmaster-1.vnet
ENV HBASE_REGIONSERVER1_HOSTNAME  regionserver-1.vnet
ENV HBASE_ZOOKEEPER_QUORUM        zookeeper-1.vnet

## Add user-specified CLASSPATH
ENV HBASE_CLASSPATH ${PHOENIX_HOME}/server/*


RUN set -x \
    && apk --no-cache add \
        bash \
        openjdk8-jre \
        nss \
        su-exec \ 
        python \
        wget \
    && mirror_url=$( \
        wget -q -O - "http://www.apache.org/dyn/closer.cgi/?as_json=1" \
        | grep "preferred" \
        | sed -n 's#.*"\(https\?://*[^"]*\)".*#\1#p' \
        # " to help quote matching
        ) \
    && wget -q -O - ${mirror_url}hbase/${HBASE_VERSION}/hbase-${HBASE_VERSION}-bin.tar.gz \
        | tar -xzf - -C /usr/local \
    && ln -s /usr/local/hbase-${HBASE_VERSION} /usr/local/hbase-${HBASE_VERSION:0:3} \
    ## user/dir/permmsion
    && adduser -D  -g '' -s /sbin/nologin -u 1000 docker \
    && for user in hadoop hbase; do \
         adduser -D -g '' -s /sbin/nologin ${user}; \
       done \
    && for user in root hbase docker; do \
         adduser ${user} hadoop; \
       done \       
    && mkdir -p \
        ${HBASE_TMP_DIR} \
        ${HBASE_LOG_DIR} \
    && chmod -R 755 \
        ${HBASE_TMP_DIR} \
        ${HBASE_LOG_DIR} \
    && chown -R hbase:hadoop \
        ${HBASE_TMP_DIR} \
        ${HBASE_LOG_DIR}  \      
    && rm -rf ${HBASE_HOME}/docs \
    && sed -i.bk -e 's/PermSize/MetaspaceSize/g' ${HBASE_CONF_DIR}/hbase-env.sh
RUN set -x \
    && mirror_url=$( \
        wget -q -O - "http://www.apache.org/dyn/closer.cgi/?as_json=1" \
        | grep "preferred" \
        | sed -n 's#.*"\(https\?://*[^"]*\)".*#\1#p' \
        # " to help quote matching
        ) \
    ## phoenix
    && package_dir="phoenix-${PHOENIX_VERSION}" \
    && package_name="phoenix-hbase-${HBASE_VER}-${PHOENIX_VERSION}-bin" \
    && wget -q -O - ${mirror_url}phoenix/${package_dir}/${package_name}.tar.gz \
        | tar -xzf - -C /usr/local \
    && mv /usr/local/${package_name} ${PHOENIX_HOME} \
    && cd ${PHOENIX_HOME} \  
    && mkdir -p ${PHOENIX_HOME}/server \
    && cd server \
    && ln -s ../phoenix-server-hbase-*.jar
COPY --from=jdk-builder /usr/local/phoenix-queryserver/phoenix-queryserver-bin.tar.gz /tmp/phoenix-queryserver-bin.tar.gz
RUN set -x \
    ## query server
    && tar zxf /tmp/phoenix-queryserver-bin.tar.gz -C ${PHOENIX_HOME} \
    && cd ${PHOENIX_HOME} \
    && mv phoenix-queryserver-* client \
    && cd client \
    && ln -s ../phoenix-client-hbase-*.jar

COPY etc/*  ${HBASE_CONF_DIR}/
COPY bin/*  /usr/local/bin/
COPY lib/*  /usr/local/lib/
    
VOLUME ["${HBASE_TMP_DIR}", "${HBASE_LOG_DIR}", "${HBASE_HOME}"]

ENTRYPOINT ["entrypoint.sh"]

WORKDIR ${PHOENIX_HOME}

LABEL \
    org.label-schema.build-date=$BUILD_DATE \
    org.label-schema.license="Apache License 2.0" \
    org.label-schema.vcs-ref=$VCS_REF

