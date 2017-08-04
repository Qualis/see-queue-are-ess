# see-queue-are-ess

A CQRS library for Clojure.

NOTE: this project uses git submodules so you will want to clone recursively to have all expected behaviours.

* `git clone --recursive git@git.aconex.com:see-queue-are-ess`

NOTE: The submodules are hosted on GitHub, ensure you have setup your GitHub access.

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

## Starting the application

Start the application in:

1. developer mode (automatically detects code changes): `lein run-dev`
2. production mode: `lein run`

Navigate to [vagrant-see-queue-are-ess.local](http://vagrant-see-queue-are-ess.local).

## Continuous Integration with Vagrant

Navigate to [vagrant-see-queue-are-ess-ci.local:8080](http://vagrant-see-queue-are-ess-ci.local:8080).

## Links
* [Clojure](https://clojure.org)
* [Leiningen](http://leiningen.org)
* [Midje](https://github.com/marick/Midje)
