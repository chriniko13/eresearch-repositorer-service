# ERESEARCH REPOSITORER SERVICE #

### Description

This is the backbone service of eresearch repository platform.
It integrates with external systems, collects --> aggregates based on a correlation id --> transforms and stores them
in a document based database which in our case is MongoDB.


This service integrates with the following external systems:
* [eresearch-author-matcher](https://github.com/chriniko13/eresearch-author-matcher)
* [eresearch-dblp-consumer](https://github.com/chriniko13/eresearch-dblp-consumer)
* [eresearch-scidir-consumer](https://github.com/chriniko13/eresearch-sciencedirect-consumer)
* [eresearch-author-finder](https://github.com/chriniko13/eresearch-author-finder)
* [eresearch-scopus-consumer](https://github.com/chriniko13/eresearch-scopus-consumer)


It consists of two workflows in order to collect and store the data.
* `repositorer-first-step-workflow.xml` TODO
* `repositorer-second-step-workflow.xml` TODO

Also it handles errors with the following workflows:
* `repositorer-error-handling-aggregators-workflow.xml` TODO
* `repositorer-error-handling-workflow.xml` TODO


### External Dependencies needed in order to run service (Other services && Infrastructure)

Build docker images for the following services (detailed info on how to build docker image can be found on README.md
of each one of the following described services):

* Dependencies
    * [eresearch-author-matcher](https://github.com/chriniko13/eresearch-author-matcher)
    * [eresearch-dblp-consumer](https://github.com/chriniko13/eresearch-dblp-consumer)
    * [eresearch-scidir-consumer](https://github.com/chriniko13/eresearch-sciencedirect-consumer)
    * [eresearch-author-finder](https://github.com/chriniko13/eresearch-author-finder)
    * [eresearch-scopus-consumer](https://github.com/chriniko13/eresearch-scopus-consumer)
    * MongoDB && ActiveMQ

* How to init dependencies
    * Execute: `docker-compose up`
    * Execute: `docker-compose down`


### Unit Tests (written in Groovy with Spock Framework)

* Execute: `mvn clean test`


### Integration Tests (run docker-compose first)

* Execute: `mvn clean verify`
TODO