# Adapted from https://github.com/druid-io/docker-druid/blob/master/Dockerfile
# & http://druid.io/docs/latest/tutorials/index.html#start-up-druid-services
FROM ubuntu:18.04

# Set version and github repo which you want to build from
ENV DRUID_VERSION "0.13.0-incubating"
ENV ZOOKEEPER_VERSION 3.4.14

# Java 8
RUN apt-get update \
      && apt-get install -y software-properties-common \
      && apt-add-repository -y ppa:webupd8team/java \
      && apt-get purge --auto-remove -y software-properties-common \
      && apt-get update \
      && echo oracle-java-8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections \
      && apt-get install -y oracle-java8-installer oracle-java8-set-default \
           perl python \
      && apt-get clean \
      && rm -rf /var/cache/oracle-jdk8-installer \
      && rm -rf /var/lib/apt/lists/*

# Zookeeper (from tarball)
RUN mirror_url=$( \
        wget -q -O - "http://www.apache.org/dyn/closer.cgi/?as_json=1" \
        | grep "preferred" \
        | sed -n 's#.*"\(http://*[^"]*\)".*#\1#p' \
        ) \
    && wget --progress=dot -O - ${mirror_url}/zookeeper/zookeeper-${ZOOKEEPER_VERSION}/zookeeper-${ZOOKEEPER_VERSION}.tar.gz \
        | tar -xzf - -C /usr/local \
    && ln -s /usr/local/zookeeper-$ZOOKEEPER_VERSION /usr/local/zookeeper \
    && cp /usr/local/zookeeper/conf/zoo_sample.cfg /usr/local/zookeeper/conf/zoo.cfg

# Druid system user
RUN adduser --system --group --no-create-home druid \
      && mkdir -p /var/lib/druid \
      && chown druid:druid /var/lib/druid

# Druid (from tarball)
RUN mirror_url=$( \
        wget -q -O - "http://www.apache.org/dyn/closer.cgi/?as_json=1" \
        | grep "preferred" \
        | sed -n 's#.*"\(http://*[^"]*\)".*#\1#p' \
        ) \
    && wget --progress=dot -O - ${mirror_url}/incubator/druid/${DRUID_VERSION}/apache-druid-${DRUID_VERSION}-bin.tar.gz \
        | tar -xzf - -C /usr/local \
    && ln -s /usr/local/apache-druid-${DRUID_VERSION} /usr/local/druid \
    && ln -s /usr/local/druid/extensions /var/lib/druid/extensions
RUN ln -s /usr/local/zookeeper /usr/local/druid/zk

# Expose ports:
# - 8081: HTTP (coordinator)
# - 8082: HTTP (broker)
# - 8083: HTTP (historical)
# - 8090: HTTP (overlord)
# - 2181 2888 3888: ZooKeeper
EXPOSE 8081
EXPOSE 8082
EXPOSE 8083
EXPOSE 8090
EXPOSE 2181 2888 3888

WORKDIR /var/lib/druid
ENTRYPOINT exec /usr/local/druid/bin/supervise -c /usr/local/druid/quickstart/tutorial/conf/tutorial-cluster.conf
#export HOSTIP="$(resolveip -s $HOSTNAME)" && find /var/lib/mysql -type f -exec touch {} \; && exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
