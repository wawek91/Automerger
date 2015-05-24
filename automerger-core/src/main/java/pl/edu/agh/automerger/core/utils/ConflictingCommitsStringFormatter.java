package pl.edu.agh.automerger.core.utils;

import pl.edu.agh.automerger.core.objects.ConflictingCommit;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Allows to show conflicting objects dependencies as a properly formatted String, which has a tree structure.
 */
public class ConflictingCommitsStringFormatter {

  private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private static final String EMPTY_SPACE = " ";

  private static final String COMMIT_LINE_FORMAT = "> [%s] %s %s";

  private static final String FILE_ITEM_MARKER = "  * ";

  private static final String LINE_NUMBERS_SEPARATOR = ", ";

  private static final String LINE_NUMBERS_MARKER = " - line";

  private static final String PLURAL_LINE_NUMBERS_SUFFIX = "s";

  private final StringBuilder builder = new StringBuilder();

  /**
   * Returns a well-formatted String containing data of given conflicting commits.
   */
  public String getFormattedStringOf(final Collection<ConflictingCommit> conflictingCommits) {
    builder.setLength(0);

    for (ConflictingCommit conflictingCommit : conflictingCommits) {
      appendConflictingCommit(conflictingCommit);
    }

    return builder.toString();
  }

  /**
   * Adds conflicting commit data to the StringBuilder, along with all it's conflicting files.
   */
  private void appendConflictingCommit(final ConflictingCommit commit) {
    builder.append(LINE_SEPARATOR)
        .append(String.format(COMMIT_LINE_FORMAT, sdf.format(commit.getDate()), commit.getName(), commit.getMessage()))
        .append(LINE_SEPARATOR);

    final Map<String, List<Long>> conflictingFilesLineNumbersMap = commit.getConflictingFilesLineNumbersMap();
    for (String filePath : conflictingFilesLineNumbersMap.keySet()) {
      builder.append(FILE_ITEM_MARKER).append(filePath);
      appendLineNumbers(conflictingFilesLineNumbersMap.get(filePath));
    }

    builder.append(LINE_SEPARATOR);
  }

  /**
   * Adds conflicting lines' numbers to the StringBuilder.
   */
  private void appendLineNumbers(final List<Long> lineNumbers) {
    builder.append(LINE_NUMBERS_MARKER);

    if (lineNumbers.size() > 1) {
      builder.append(PLURAL_LINE_NUMBERS_SUFFIX);
    }

    builder.append(EMPTY_SPACE).append(lineNumbers.get(0));

    for (int i = 1; i < lineNumbers.size(); i++) {
      builder.append(LINE_NUMBERS_SEPARATOR).append(lineNumbers.get(i));
    }
  }

}
