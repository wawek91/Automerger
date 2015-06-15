package pl.edu.agh.automerger.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import pl.edu.agh.automerger.core.config.RepositoryConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Preparation of an up-to-date copy of remote repository with requested branches.
 */
public class LocalRepositoryPreparer {

  private static final String REPOSITORY_ERROR_MESSAGE = "Local repository problem. Clean selected directory and try again.";

  private static final String GIT_REPO_DIR = "/.git";

  private final Logger logger = LogManager.getLogger();

  private RepositoryConfiguration repoConfig;

  private Git git;

  private boolean clonePerformed;

  public LocalRepositoryPreparer(final RepositoryConfiguration repoConfig) {
    this.repoConfig = repoConfig;
  }

  /**
   * Prepares an up-to-date copy of remote repository and returns a Git repository object.
   */
  public Git prepareRepository() throws GitAPIException, IOException {
    logger.info("Preparing local repository");

    if (repositoryDoesNotExist()) {
      logger.info("Local repository does not exist - cloning");
      cloneRepository();
      clonePerformed = true;
    }

    init();

    if (erroneousRepository()) {
      throw new WrongRepositoryStateException(REPOSITORY_ERROR_MESSAGE);
    }

    resetBranches();

    logger.info("Local repository is up-to-date");
    return git;
  }

  /**
   * Performs a local repository existence check.
   */
  private boolean repositoryDoesNotExist() {
    return !RepositoryCache.FileKey.isGitRepository(new File(repoConfig.getLocalRepositoryPath() + GIT_REPO_DIR), FS.DETECTED);
  }

  /**
   * Clones the remote repository.
   */
  private void cloneRepository() throws GitAPIException {
    final CloneCommand cloneCommand = Git.cloneRepository()
        .setDirectory(new File(repoConfig.getLocalRepositoryPath()))
        .setURI(repoConfig.getRemoteRepositoryPath())
        .setCloneAllBranches(false)
        .setBranchesToClone(Arrays.asList(repoConfig.getMainBranchRef(), repoConfig.getFeatureBranchRef()));

    final UsernamePasswordCredentialsProvider credentialsProvider = getCredentialsProvider();
    if (credentialsProvider != null) {
      cloneCommand.setCredentialsProvider(credentialsProvider);
    }

    cloneCommand.call();
  }

  /**
   * Returns CredentialsProvider object if Git username and password are configured, null otherwise.
   */
  private UsernamePasswordCredentialsProvider getCredentialsProvider() {
    final String gitUsername = repoConfig.getGitUsername();
    final String gitPassword = repoConfig.getGitPassword();

    if (isNotEmpty(gitUsername) && isNotEmpty(gitPassword)) {
      return new UsernamePasswordCredentialsProvider(gitUsername, gitPassword);
    }

    return null;
  }

  /**
   * Checks if given string contains any characters.
   */
  private boolean isNotEmpty(final String string) {
    return string != null && !"".equals(string);
  }

  /**
   * Initializes Git repository object.
   */
  private void init() throws GitAPIException, IOException {
    git = Git.open(new File(repoConfig.getLocalRepositoryPath()));
  }

  /**
   * Performs a repository validation check (at least one valid object reference).
   */
  private boolean erroneousRepository() {
    for (Ref ref : git.getRepository().getAllRefs().values()) {
      if (ref.getObjectId() != null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Resets all local branches to the remotes' stages.
   */
  private void resetBranches() {
    try {
      resetBranch(repoConfig.getMainBranchName(), repoConfig.getMainBranchRef());
      resetBranch(repoConfig.getFeatureBranchName(), repoConfig.getFeatureBranchRef());
    }
    catch (GitAPIException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks out the branch with given name and, if repository clone wasn't made, performs the hard reset to a given remotes state.
   */
  private void resetBranch(final String branchName, final String remoteBranchRef) throws GitAPIException {
    final boolean isMainBranch = repoConfig.getMainBranchName().equals(branchName);

    checkoutBranch(branchName, isMainBranch);

    if (!clonePerformed || !isMainBranch) {
      logger.info("Hard reset on branch \"{}\" from ref \"{}\"", branchName, remoteBranchRef);
      resetCurrentBranchToRemoteState(remoteBranchRef);
    }

    if (!clonePerformed) {
      logger.info("Pulling branch \"{}\" from ref \"{}\"", branchName, remoteBranchRef);
      pullBranch(branchName);
    }
  }

  /**
   * Checks out a branch with given name.
   */
  private void checkoutBranch(final String branchName, final boolean isMainBranch) throws GitAPIException {
    git.checkout().setName(branchName).setCreateBranch(clonePerformed && !isMainBranch).call();
  }

  /**
   * Performs a hard reset on current branch, bringing it back to the HEAD state of given remote.
   */
  private void resetCurrentBranchToRemoteState(final String remoteBranchRef) throws GitAPIException {
    git.reset().setMode(ResetCommand.ResetType.HARD).setRef(remoteBranchRef).call();
  }

  /**
   * Performs a pull on current branch from given remote branch, making the local one up-to-date.
   */
  private void pullBranch(final String branchName) throws GitAPIException {
    final PullCommand pullCommand = git.pull().setRemoteBranchName(branchName);

    final UsernamePasswordCredentialsProvider credentialsProvider = getCredentialsProvider();
    if (credentialsProvider != null) {
      pullCommand.setCredentialsProvider(credentialsProvider);
    }

    pullCommand.call();
  }

}
