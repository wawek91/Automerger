package pl.edu.agh.automerger.core.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all conflicting commits data per single commit author as well as information about himself.
 */
public class ConflictAuthor {

  private final String name;

  private final String emailAddress;

  private final Map<String, ConflictingCommit> conflictingCommitsByName = new HashMap<>();

  public ConflictAuthor(final String name, final String emailAddress) {
    this.name = name;
    this.emailAddress = emailAddress;
  }

  public String getName() {
    return name;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public Map<String, ConflictingCommit> getConflictingCommitsByName() {
    return conflictingCommitsByName;
  }

}
