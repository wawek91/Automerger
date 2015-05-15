package pl.edu.agh.automerger.test;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * Test of non-conflicting and conflicting branches merge into the main one.
 */
public class FeatureBranchesMergeTest {

  private static final String HEAD_REF = "HEAD";

  private Git git;

  /**
   * Runs the test.
   */
  public static void main(String[] args) {
    try {
      new FeatureBranchesMergeTest().runTest();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Performs the test by preparing the local repository and performing the two merges.
   */
  private void runTest() throws IOException {
    git = new LocalRepositoryPreparer().prepareRepository();

    if (isGitRepositoryInvalid()) {
      return;
    }

    mergeOkFeature();
    mergeConflictingFeature();

    git.close();
  }

  /**
   * Checks if the repository was properly initialized and is valid.
   */
  public boolean isGitRepositoryInvalid() throws IOException {
    return git == null || git.getRepository().getRef(HEAD_REF) == null;
  }

  /**
   * Merges non-conflicting feature branch into current branch.
   */
  private void mergeOkFeature() {
    final MergeResult result = merge(Parameters.getNonConflictingFeatureBranchRef());

    if (result != null) {
      System.out.println("OK feature branch merge result: " + result.getMergeStatus());
    } else {
      System.out.println("OK feature branch merge failed");
    }
  }

  /**
   * Merges branch with given ref into current branch.
   */
  private MergeResult merge(final String ref) {
    try {
      return git.merge().include(git.getRepository().getRef(ref)).call();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (GitAPIException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Merges conflicting feature branch into current branch.
   */
  private void mergeConflictingFeature() {
    final MergeResult result = merge(Parameters.getConflictingFeatureBranchRef());

    if (result != null) {
      System.out.println("Conflicting feature branch merge result: " + result.getMergeStatus());
    } else {
      System.out.println("Conflicting feature branch merge failed");
    }
  }

}
