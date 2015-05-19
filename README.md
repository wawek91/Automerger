# Automerger
###### Git periodical merging application

## automerger-core
Core merging module, included by standalone version and EJB module. 

## automerger-ear
EAR building module, allowing to deploy an EJB onto an application server.

## automerger-ejb
EJB building module, which uses core merging functionality adjusted to work in JEE environment.  
Repository configuration is stored in `repository.properties`, mailing configuration is stored in `mailing.properties`.

## automerger-standalone
Standalone version of Automerger, allowing to immediately test core module work.  
Repository configuration is stored in `repository.properties`, mailing configuration is stored in `mailing.properties`.

## automerger-tools
Repository configuration is stored in `repository.properties`.

* *LocalRepositoryPreparer.main()* - Runs a preparation of a local up-to-date copy of remote repository.
* *FeatureBranchesMergeTest.main()*  - Runs a test of non-conflicting and conflicting branches merge into the main one. Uses *LocalRepositoryPreparer* for a fresh repository copy.
