package pl.edu.agh.automerger.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import pl.edu.agh.automerger.core.config.RepositoryConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Core class performing basic operations.
 */
public abstract class Automerger {

  private final Logger logger = LogManager.getLogger();

  private RepositoryConfiguration repoConfig;

  private Git git;

  public Automerger() {
    repoConfig = getRepositoryConfiguration();
    initGit();
  }

  /**
   * Initializes Git object set on local repository.
   */
  private void initGit() {
    try {
      git = Git.open(new File(repoConfig.getLocalRepositoryPath()));
    }
    catch (IOException e) {
      logger.info("Git repository was not found under {}\n{}", repoConfig.getLocalRepositoryPath(), e);
    }
  }

  /**
   * Returns proper Configuration object.
   */
  protected abstract RepositoryConfiguration getRepositoryConfiguration();

  /**
   * Performs the merge operation.
   */
  public void merge() {
    logger.info("Merge invoked");
    try {
      checkoutMainBranch();
      final MergeResult mergeResult = performMerge();
      handleMergeResult(mergeResult);
    }
    catch (GitAPIException e) {
      logger.error("GitAPIException {}", e);
    }
    catch (IOException e) {
      logger.error("IOException {}", e);
    }
  }

  /**
   * Checks out a branch set as the main one.
   */
  private void checkoutMainBranch() throws GitAPIException {
    git.checkout().setName(repoConfig.getMainBranchName()).call(); //TODO should clone/pull first if not existing?
  }

  /**
   * Executes the merge command.
   */
  private MergeResult performMerge() throws GitAPIException, IOException {
    final Ref featureBranchRef = git.getRepository().getRef(repoConfig.getFeatureBranchName());
    return git.merge().include(featureBranchRef).call();
  }

  /**
   * Inspects merge operation result and handles it appropriately.
   */
  private void handleMergeResult(final MergeResult mergeResult) {
    if (mergeResult.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
      logger.info("Conflicts exist");
      conflict(mergeResult);
    } else {
      logger.info("No conflicts");
      // TODO should commit first?
      //git.push().call();
    }
  }

  /**
   * Executes actions proper for the given merge conflict.
   */
  private void conflict(final MergeResult mergeResult) {
    //getMailer().sendTo(""); //TODO
  }

  /**
   * Returns proper Mailer object.
   */
  protected abstract Mailer getMailer();

}
