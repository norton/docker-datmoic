.PHONY: all build start test stop m2-cache clean realclean

DATOMIC_VERSION = 0.9.5350

all: start

build: build/datomic-base/datomic-pro-$(DATOMIC_VERSION).zip build/datomic-base/.license-key build/datomic-tester/.m2
	docker-compose build

start: build var/tester-data
	docker-compose up -d

test:
	docker-compose run tester lein test

stop:
	docker-compose stop

m2-cache:
	rm -rf build/datomic-tester/.m2
	(cd build/datomic-tester/jepsen.datomic && lein deps && lein install)
	cp -r ~/.m2 build/datomic-tester

clean: stop
	-docker-compose down

realclean: clean
	@docker rm -v $(shell docker ps -a -q -f status=exited) 2>/dev/null || true
	@docker rmi $(shell docker images -q) 2>/dev/null || true
	@docker rm $(shell docker ps -a -q) 2>/dev/null || true
	@rm -f priv/datomic-pro-$(DATOMIC_VERSION).zip
	@rm -f build/datomic-base/datomic-pro-$(DATOMIC_VERSION).zip
	@rm -f build/datomic-base/.license-key
	@rm -rf build/datomic-base/.m2
	@rm -rf var/tester-data

priv/datomic-pro-$(DATOMIC_VERSION).zip: priv/.credentials
	curl -u $(shell cat priv/.credentials) -SL https://my.datomic.com/repo/com/datomic/datomic-pro/$(DATOMIC_VERSION)/datomic-pro-$(DATOMIC_VERSION).zip -o $@

build/datomic-base/datomic-pro-$(DATOMIC_VERSION).zip: priv/datomic-pro-$(DATOMIC_VERSION).zip
	cp -f priv/datomic-pro-$(DATOMIC_VERSION).zip $@

build/datomic-base/.license-key: priv/.license-key
	cp -f priv/.license-key $@

build/datomic-tester/.m2:
	mkdir -p $@

var/tester-data:
	mkdir -p $@
