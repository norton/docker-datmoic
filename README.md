# docker-datomic
Docker musings for a Datomic installation and jepsen test automation.

## Quickstart

```
$ make m2-cache
$ make
$ make test
```

Results are written to the './var/tester-data' directory.

The 'test' target can be executed multiple times.

See [Makefile](./Makefile) for details.

## Prerequisites
- Place your Datomic credentials for downloading the software in the file
  'priv/.credentials'. The format of the file is 'username:password' where
  'username' is replaced by your registered email address and 'password' is
  replaced by your Datomic Download Key.
- Place your Datomic license key in the file 'priv/.license-key'. The format of
  the file is '...' where '...' is replaced by by your Datomic license key.

## Resources
- http://www.datomic.com
- https://github.com/aphyr/jepsen
- https://github.com/aphyr/jepsen/blob/master/zookeeper/src/jepsen/zookeeper.clj (Sample Jepsen test for Zookeeper)
- https://github.com/aphyr/jepsen/blob/master/doc/scaffolding.md (Tutorial for writing a Jepsen test)
- https://github.com/norton/docker-machine

## Prerequisites (Mac OS X)

- Install and setup [homebrew](http://brew.sh).
- Install virtualbox, docker, docker-compose, docker-machine, and maven.

```
$ brew cask install virtualbox
$ brew install docker docker-compose docker-machine
$ brew install leiningen maven
```

- Create your own 'dev' docker machine.

```
$ git clone https://github.com/norton/docker-machine.git
$ cd docker-machine
$ make
```

NOTE: This test probably needs at least 4-5 GB of physical memory and at least 2 cpus.

- Add 'dev' docker machine to your environment.

```
$ eval "$(docker-machine env dev)"
```

## Bugs
- Until Jepsen version 0.1.1 is released, one must download and locally install
  Jepsen into your own private '.m2/repository' before executing the 'm2-cache'
  make target.
- Implementation for the pause nemesis appears not to be working as expected.
  Needs further investigation.

## ToDo
- Implement checker for jespen.datomic test.
- Add console to jepsen.datomic test.
- Add postgres to jepsen.datomic test.
- Add assertions and passing of entity to setup-schema.clj script.
- Add checksums for curl downloads.
- BONUS ... implement a postgres "active" and "standby" node to jepsen.datomic test.
