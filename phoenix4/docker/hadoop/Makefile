.PHONY: default all build clean clobber
default: all

BUILD_DATE ?= $(shell date -u +"%Y-%m-%dT%H:%M:%SZ")
VCS_REF ?= $(shell git rev-parse HEAD)
HADOOP_VERSION ?= 2.10.1
HADOOP_VER ?= $(shell echo $(HADOOP_VERSION) | sed -e 's/\.[0-9]*$$//')

TAG ?= $(HADOOP_VERSION)-alpine
CONTAINER_REPO ?= dbsurvey/hadoop


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
		-t $(CONTAINER_REPO):${TAG} .

