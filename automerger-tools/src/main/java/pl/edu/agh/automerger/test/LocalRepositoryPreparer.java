package pl.edu.agh.automerger.test;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;

/**
 * Preparation of an up-to-date copy of remote repository with all branches.
 */
public class LocalRepositoryPreparer {

  private static final String GIT_REPO_DIR = "/.git";

  private Git git;

  private boolean clonePerformed;

  /**
   * Runs a preparation of an up-to-date copy of remote repository.
   */
  public static void main(String[] args) {
    new LocalRepositoryPreparer().prepareRepository().close();
  }

  /**
   * Prepares an up-to-date copy of remote repository and returns a Git repository object.
   */
  public Git prepareRepository() {
    try {
      prepareCleanRepository();
    }
    catch (GitAPIException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (LocalRepositoryException e) {
      System.err.print(e.getMessage());
      System.exit(0);
    }
    return git;
  }

  /**
   * Prepares an up-to-date copy of remote repository, leaving it on the main branch.
   */
  private void prepareCleanRepository() throws GitAPIException, IOException, LocalRepositoryException {
    if (repositoryDoesNotExist()) {
      cloneRepository();
      clonePerformed = true;
    }

    init();

    if (erroneousRepository()) {
      throw new LocalRepositoryException();
    }

    resetBranches();
    checkoutMainBranch();
  }

  /**
   * Performs a local repository existence check.
   */
  private boolean repositoryDoesNotExist() {
    return !RepositoryCache.FileKey.isGitRepository(new File(Parameters.getLocalRepositoryPath() + GIT_REPO_DIR), FS.DETECTED);
  }

  /**
   * Clones the remote repository.
   */
  private void cloneRepository() throws GitAPIException {
    final CloneCommand cloneCommand = Git.cloneRepository()
        .setDirectory(new File(Parameters.getLocalRepositoryPath()))
        .setURI(Parameters.getRemoteRepositoryPath())
        .setCloneAllBranches(true);

    final String gitUsername = Parameters.getGitUsername();
    final String gitPassword = Parameters.getGitPassword();
    if (isNotEmpty(gitUsername) && isNotEmpty(gitPassword)) {
      cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUsername, gitPassword));
    }

    cloneCommand.call();
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
    git = Git.open(new File(Parameters.getLocalRepositoryPath()));
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
      resetBranch(Parameters.getMainBranchName(), Parameters.getMainBranchRef());
      resetBranch(Parameters.getNonConflictingFeatureBranchName(), Parameters.getNonConflictingFeatureBranchRef());
      resetBranch(Parameters.getConflictingFeatureBranchName(), Parameters.getConflictingFeatureBranchRef());
    }
    catch (GitAPIException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks out the branch with given name and, if repository clone wasn't made, performs the hard reset to a given remotes state.
   */
  private void resetBranch(final String branchName, final String remoteBranchRef) throws GitAPIException {
    final boolean isMainBranch = Parameters.getMainBranchName().equals(branchName);

    checkoutBranch(branchName, isMainBranch);

    if (!clonePerformed || !isMainBranch) {
      resetCurrentBranchToRemoteState(remoteBranchRef);
    }
  }

  /**
   * Checks out a branch with given name.
   */
  private void checkoutBranch(final String branchName, final boolean isMainBranch) throws GitAPIException {
    git.checkout().setName(branchName).setCreateBranch(clonePerformed && !isMainBranch).call();
  }

  /**
   * Performs the hard reset on current branch, bringing it back to the HEAD state of given remote.
   */
  private void resetCurrentBranchToRemoteState(final String remoteBranchRef) throws GitAPIException {
    git.reset().setMode(ResetCommand.ResetType.HARD).setRef(remoteBranchRef).call();
  }

  /**
   * Checks out the main branch.
   */
  private void checkoutMainBranch() throws GitAPIException {
    checkoutBranch(Parameters.getMainBranchName(), true);
  }

  /**
   * An exception connected with repository structure failure (e.g. when clone operation was not successful).
   */
  class LocalRepositoryException extends Exception {

    private static final String MESSAGE = "Local repository problem. Clean selected directory and try again.";

    public LocalRepositoryException() {
      super(MESSAGE);
    }

  }

}
