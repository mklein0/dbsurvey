
SUBDIRS = zookeeper hadoop phoenix

.PHONY: default all build clean clobber
default: all

$(SUBDIRS)::
	$(MAKE) -C $@ $(MAKECMDGOALS)

all build clean clobber: $(SUBDIRS)
