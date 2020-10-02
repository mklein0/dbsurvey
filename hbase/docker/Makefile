#
.PHONY: clean built/all

CONTAINER_VERSION := 1.0
CONTAINER_REPO := amastilovic

all: build

built:
	mkdir -p built

built/hbase-%: Dockerfile-% built
	docker build -f $< -t ${CONTAINER_REPO}/hbase-${*}:${CONTAINER_VERSION} .
	touch $@

built/%: Dockerfile-% built
	docker build -f $< -t ${CONTAINER_REPO}/${*}:${CONTAINER_VERSION} .
	touch $@


build: built/namenode-snappy built/datanode-snappy built/hbase-master-snappy built/hbase-region-snappy


clean:
	if ! type docker-compose 2>&- 1>&-; then \
		docker-compose -f hbase-standalone.yml down --remove-orphans; \
		docker-compose -f hbase-distributed-local.yml down --remove-orphans; \
	fi
	for i in namenode-snappy datanode-snappy hbase-master-snappy hbase-region-snappy; \
	do \
		docker rmi ${CONTAINER_REPO}/$${i}:${CONTAINER_VERSION} || true; \
	done
	rm -rf built/

list:
	docker images ${CONTAINER_REPO}/*

standalone: build
	docker-compose --compatibility -f hbase-standalone.yml up

cluster: build
	docker-compose --compatibility -f hbase-distributed-local.yml up

