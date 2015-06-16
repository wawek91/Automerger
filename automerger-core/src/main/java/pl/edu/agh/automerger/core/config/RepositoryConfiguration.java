package pl.edu.agh.automerger.core.config;

/**
 * Repository configuration class.
 */
public abstract class RepositoryConfiguration extends Configuration {

  // repository path
  private static final String LOCAL_REPOSITORY_PATH = "repository.local_path";

  // remote repository path
  private static final String REMOTE_REPOSITORY_PATH = "repository.remote_path";

  // Git username required to clone remote repository
  private static final String GIT_USERNAME = "git.username";

  // Git password required to clone remote repository
  private static final String GIT_PASSWORD = "git.password";

  // main branch name
  private static final String MAIN_BRANCH_NAME = "branch.main.name";

  // main branch remote name
  private static final String MAIN_BRANCH_REMOTE = "branch.main.remote";

  // feature branch name
  private static final String FEATURE_BRANCH_NAME = "branch.feature.name";

  // feature branch remote name
  private static final String FEATURE_BRANCH_REMOTE = "branch.feature.remote";

  // pattern of a remote reference
  private static final String REMOTE_REF_PATTERN = "refs/remotes/%s/%s";

  /**
   * Returns the local repository path.
   */
  public String getLocalRepositoryPath() {
    return getProperty(LOCAL_REPOSITORY_PATH);
  }

  /**
   * Returns the remote repository path.
   */
  public String getRemoteRepositoryPath() {
    return getProperty(REMOTE_REPOSITORY_PATH);
  }

  /**
   * Returns the Git username required to clone private repository.
   */
  public String getGitUsername() {
    return getProperty(GIT_USERNAME);
  }

  /**
   * Returns the Git password required to clone private repository.
   */
  public String getGitPassword() {
    return getProperty(GIT_PASSWORD);
  }

  /**
   * Returns the main branch name.
   */
  public String getMainBranchName() {
    return getProperty(MAIN_BRANCH_NAME);
  }

  /**
   * Returns the main branch remote name.
   */
  public String getMainBranchRemote() {
    return getProperty(MAIN_BRANCH_REMOTE);
  }

  /**
   * Returns a reference to the remote main branch.
   */
  public String getMainBranchRef() {
    return String.format(REMOTE_REF_PATTERN, getMainBranchRemote(), getMainBranchName());
  }

  /**
   * Returns the name of a branch which should merge into main branch without conflicts.
   */
  public String getFeatureBranchName() {
    return getProperty(FEATURE_BRANCH_NAME);
  }

  /**
   * Returns a reference to a remote branch, which should merge into main branch without conflicts.
   */
  public String getFeatureBranchRef() {
    return String.format(REMOTE_REF_PATTERN, getProperty(FEATURE_BRANCH_REMOTE), getFeatureBranchName());
  }

}
