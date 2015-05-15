# Automerger

## automerger-ear
## automerger-ejb
## automerger-test
Tested repository configuration is stored in `repository.properties`.

* *LocalRepositoryPreparer.main()* - Runs a preparation of a local up-to-date copy of remote repository.
* *FeatureBranchesMergeTest.main()*  - Runs a test of non-conflicting and conflicting branches merge into the main one. Uses *LocalRepositoryPreparer* for a fresh repository copy.
