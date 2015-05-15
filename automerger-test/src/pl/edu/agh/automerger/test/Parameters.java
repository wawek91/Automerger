package pl.edu.agh.automerger.test;

import java.io.IOException;
import java.util.Properties;

/**
 * Serves test repository configuration parameters.
 */
public class Parameters {

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

  // name of a branch, which should merge into main branch without any conflicts
  private static final String NON_CONFLICTING_BRANCH_NAME = "branch.non-conflicting_feature.name";

  // name of a remote of a branch, which should merge into main branch without any conflicts
  private static final String NON_CONFLICTING_BRANCH_REMOTE = "branch.non-conflicting_feature.remote";

  // name of a branch, which should merge into main branch with some conflicts
  private static final String CONFLICTING_BRANCH_NAME = "branch.conflicting_feature.name";

  // name of a remote of a branch, which should merge into main branch with some conflicts
  private static final String CONFLICTING_BRANCH_REMOTE = "branch.conflicting_feature.remote";

  // pattern of a remote reference
  private static final String REMOTE_REF_PATTERN = "refs/remotes/%s/%s";

  // .properties config file name
  private static final String FILE_PATH = "repository.properties";

  private static Parameters instance;

  private Properties properties;

  /**
   * Initializes properties store and loads the .properties file config in.
   */
  private Parameters() {
    try {
      properties = new Properties();
      properties.load(Parameters.class.getClassLoader().getResourceAsStream(FILE_PATH));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  /**
   * Returns property value for given property name. Initializes the store if it's not yet done.
   */
  private static String getProperty(final String key) {
    if (instance == null) {
      instance = new Parameters();
    }
    return instance.properties.getProperty(key);
  }

  /**
   * Returns the local repository path.
   */
  public static String getLocalRepositoryPath() {
    return getProperty(LOCAL_REPOSITORY_PATH);
  }

  /**
   * Returns the remote repository path.
   */
  public static String getRemoteRepositoryPath() {
    return getProperty(REMOTE_REPOSITORY_PATH);
  }

  /**
   * Returns the Git username required to clone private repository.
   */
  public static String getGitUsername() {
    return getProperty(GIT_USERNAME);
  }

  /**
   * Returns the Git password required to clone private repository.
   */
  public static String getGitPassword() {
    return getProperty(GIT_PASSWORD);
  }

  /**
   * Returns the main branch name.
   */
  public static String getMainBranchName() {
    return getProperty(MAIN_BRANCH_NAME);
  }

  /**
   * Returns a reference to the remote main branch.
   */
  public static String getMainBranchRef() {
    return String.format(REMOTE_REF_PATTERN, getProperty(MAIN_BRANCH_REMOTE), getProperty(MAIN_BRANCH_NAME));
  }

  /**
   * Returns the name of a branch which should merge into main branch without conflicts.
   */
  public static String getNonConflictingFeatureBranchName() {
    return getProperty(NON_CONFLICTING_BRANCH_NAME);
  }

  /**
   * Returns a reference to a remote branch, which should merge into main branch without conflicts.
   */
  public static String getNonConflictingFeatureBranchRef() {
    return String.format(REMOTE_REF_PATTERN, getProperty(NON_CONFLICTING_BRANCH_REMOTE), getNonConflictingFeatureBranchName());
  }

  /**
   * Returns the name of a branch which should merge into main branch with at least one conflict.
   */
  public static String getConflictingFeatureBranchName() {
    return getProperty(CONFLICTING_BRANCH_NAME);
  }

  /**
   * Returns a reference to a remote branch, which should merge into main branch with at least one conflict.
   */
  public static String getConflictingFeatureBranchRef() {
    return String.format(REMOTE_REF_PATTERN, getProperty(CONFLICTING_BRANCH_REMOTE), getConflictingFeatureBranchName());
  }

}
