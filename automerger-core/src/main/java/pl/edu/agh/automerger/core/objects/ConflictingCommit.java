package pl.edu.agh.automerger.core.objects;

import java.util.*;

/**
 * Holds all conflicting files data per single conflicting commit as well as information about itself.
 */
public class ConflictingCommit {

  private final String name;

  private final Date date;

  private final String message;

  private final Map<String, List<Long>> conflictingFilesLineNumbersMap = new HashMap<>();

  public ConflictingCommit(final String name, final Date date, final String message) {
    this.name = name;
    this.date = date;
    this.message = message;
  }

  public String getName() {
    return name;
  }

  public Date getDate() {
    return date;
  }

  public String getMessage() {
    return message;
  }

  public Map<String, List<Long>> getConflictingFilesLineNumbersMap() {
    return conflictingFilesLineNumbersMap;
  }

  /**
   * Adds conflicting line number to the given file path data.
   */
  public void addConflictingFileLineNumber(final String filePath, final long lineNumber) {
    if (!conflictingFilesLineNumbersMap.containsKey(filePath)) {
      conflictingFilesLineNumbersMap.put(filePath, new ArrayList<>(Arrays.asList(lineNumber)));
    } else {
      conflictingFilesLineNumbersMap.get(filePath).add(lineNumber);
    }
  }

}
