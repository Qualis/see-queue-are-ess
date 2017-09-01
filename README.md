# charlie-quebec-romeo-sierra

A CQRS library for Clojure.

NOTE: this project uses git submodules so you will want to clone recursively to have all expected behaviours.

* `git clone --recursive git@github.com:svo/charlie-quebec-romeo-sierra.git`

## Development environment

Requirements:

1. Ansible (2.1.1.0)
2. Vagrant (1.7.4)
3. VirtualBox (5.0.4 r102546)

To perform provisioning and start using the development environment run:

1. `vagrant up`
2. `vagrant ssh`
3. `cd /vagrant`

## Testing the application

To monitor tests as you edit the source code run the following from the `/vagrant` directory:

1. `lein repl`
2. `(use 'midje.repl)`
3. `(midje.repl/autotest)`

To run tests and generate reports:

* `./pre-commit.sh`

## Continuous Integration with Vagrant

Navigate to [vagrant-charlie-quebec-romeo-sierra-ci.local:8080](http://vagrant-charlie-quebec-romeo-sierra-ci.local:8080).

## Links
* [Clojure](https://clojure.org)
* [Leiningen](http://leiningen.org)
* [Midje](https://github.com/marick/Midje)
