package pl.edu.agh.automerger.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
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

  private boolean localRepositoryError;

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
      localRepositoryError = false;
    }
    catch (Exception e) {
      logger.error(e);
      localRepositoryError = true;
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
    if (localRepositoryError) {
      logger.warn("Merge aborted - local repository is not in a correct state.");
      return;
    }

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
    git.checkout().setName(repoConfig.getMainBranchName()).call();
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
  private void handleMergeResult(final MergeResult mergeResult) throws GitAPIException {
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
  private void pushToRemote() throws GitAPIException {
    logger.info("Publishing merged changes via push");
    git.push()
        .setRemote(repoConfig.getMainBranchRemote())
        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(repoConfig.getGitUsername(), repoConfig.getGitPassword()))
        .call();
  }

  /**
   * Prepares human readable information about occurred merge conflicts and sends them to conflicting commits authors.
   */
  private void informAboutConflicts(final MergeResult mergeResult) {
    final ConflictsParser conflictsParser = new ConflictsParser(git.getRepository(), repoConfig.getFeatureBranchName());
    final Collection<ConflictAuthor> conflictsByAuthors = conflictsParser.parse(mergeResult);
    final ConflictingCommitsStringFormatter formatter = new ConflictingCommitsStringFormatter();
    final Mailer mailer = getMailer();
    String conflictsPerAuthor;

    for (ConflictAuthor conflictAuthor : conflictsByAuthors) {
      conflictsPerAuthor = formatter.getFormattedStringOf(conflictAuthor.getConflictingCommitsByName().values());
      mailer.sendMessage(conflictAuthor.getName(), conflictAuthor.getEmailAddress(), conflictsPerAuthor);
      logger.info("Conflicts of '{}':\n{}", conflictAuthor.getName(), conflictsPerAuthor);
    }
  }

  /**
   * Returns proper Mailer object.
   */
  protected abstract Mailer getMailer();

}
