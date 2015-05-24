package pl.edu.agh.automerger.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import pl.edu.agh.automerger.core.config.RepositoryConfiguration;
import pl.edu.agh.automerger.core.objects.ConflictAuthor;
import pl.edu.agh.automerger.core.utils.ConflictingCommitsStringFormatter;
import pl.edu.agh.automerger.core.utils.ConflictsParser;
import pl.edu.agh.automerger.core.utils.LocalRepositoryPreparer;

import java.io.IOException;
import java.util.Collection;

/**
 * Core class performing basic operations.
 */
public abstract class Automerger {

  private final Logger logger = LogManager.getLogger();

  private RepositoryConfiguration repoConfig;

  private Git git;

  public Automerger() {
    repoConfig = getRepositoryConfiguration();
    prepareLocalRepository();
  }

  /**
   * Prepares Git object set on an up-to-date local repository.
   */
  private void prepareLocalRepository() {
    try {
      git = new LocalRepositoryPreparer(repoConfig).prepareRepository();
    }
    catch (Exception e) {
      logger.error(e);
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
    final MergeStatus mergeStatus = mergeResult.getMergeStatus();
    logger.info("Merge status: {}", mergeStatus);

    if (mergeStatus.isSuccessful()) {
      pushToRemote();
    } else if (MergeStatus.CONFLICTING.equals(mergeStatus)) {
      informAboutConflicts(mergeResult);
    }
  }

  /**
   * Pushes merged changes to the remote branch.
   */
  private void pushToRemote() {
    //git.push().call();
  }

  /**
   * Prepares human readable information about occurred merge conflicts and sends them to conflicting commits authors.
   */
  private void informAboutConflicts(final MergeResult mergeResult) {
    //getMailer().sendTo(""); //TODO
    final Collection<ConflictAuthor> conflictsByAuthors = new ConflictsParser().parse(mergeResult);

    final ConflictingCommitsStringFormatter formatter = new ConflictingCommitsStringFormatter();
    for (ConflictAuthor conflictAuthor : conflictsByAuthors) {
      logger.info("Conflicts:\n{}", formatter.getFormattedStringOf(conflictAuthor.getConflictingCommitsByName().values()));
    }
  }

  /**
   * Returns proper Mailer object.
   */
  protected abstract Mailer getMailer();

}
