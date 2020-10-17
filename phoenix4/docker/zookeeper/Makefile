.PHONY: default all build clean clobber
default: all

BUILD_DATE ?= $(shell date -u +"%Y-%m-%dT%H:%M:%SZ")
VCS_REF ?= $(shell git rev-parse HEAD)
ZOOKEEPER_VERSION ?= 3.4.10
ZOOKEEPER_VER ?= $(shell echo $(ZOOKEEPER_VERSION) | sed -e 's/\.[0-9]*$$//')
TAG ?= $(ZOOKEEPER_VERSION)-alpine
CONTAINER_REPO ?= dbsurvey/zookeeper

all: build

clobber: clean
	docker rmi -f $(CONTAINER_REPO) || :

clean:
	rm -f built.container

build: built.container
	docker images $(CONTAINER_REPO)

built.container: Dockerfile zoo.cfg entrypoint.sh
	docker build \
		--build-arg BUILD_DATE=${BUILD_DATE} \
		--build-arg VCS_REF=${VCS_REF} \
		--build-arg ZOOKEEPER_VERSION=${ZOOKEEPER_VERSION} \
		--build-arg ZOOKEEPER_VER=${ZOOKEEPER_VER} \
		--rm -t $(CONTAINER_REPO):${TAG} .
	touch $@
