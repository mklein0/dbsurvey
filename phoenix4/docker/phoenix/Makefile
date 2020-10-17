.PHONY: default all build clean clobber

default: all

BUILD_DATE ?= $(shell date -u +"%Y-%m-%dT%H:%M:%SZ")
VCS_REF ?= $(shell git rev-parse HEAD)
HADOOP_VERSION ?= 2.10.1
HBASE_VERSION ?= 1.4.13
PHOENIX_VERSION ?= 4.15.0
HADOOP_VER ?= $(shell echo $(HADOOP_VERSION) | sed -e 's/\.[0-9]*$$//')
HBASE_VER ?= $(shell echo $(HBASE_VERSION) | sed -e 's/\.[0-9]*$$//')
PHOENIX_VER ?= $(shell echo $(PHOENIX_VERSION) | sed -e 's/\.[0-9]*$$//')
TAG ?= $(PHOENIX_VERSION)-hbase$(HBASE_VERSION)-alpine
CONTAINER_REPO ?= dbsurvey/phoenix

all: build

clobber: clean
	docker rmi -f $(CONTAINER_REPO):${TAG} || :

clean:
	rm -f built.container

build: built.container
	docker images $(CONTAINER_REPO)

built.container: Dockerfile bin/* etc/* lib/*
	docker build \
		--build-arg BUILD_DATE=${BUILD_DATE} \
		--build-arg VCS_REF=${VCS_REF} \
		--build-arg HADOOP_VERSION=${HADOOP_VERSION} \
		--build-arg HBASE_VERSION=${HBASE_VERSION} \
		--build-arg HBASE_VER=${HBASE_VER} \
		--build-arg PHOENIX_VERSION=${PHOENIX_VERSION} \
		--build-arg PHOENIX_VER=${PHOENIX_VER} \
		-t $(CONTAINER_REPO):${TAG} .
	touch $@
