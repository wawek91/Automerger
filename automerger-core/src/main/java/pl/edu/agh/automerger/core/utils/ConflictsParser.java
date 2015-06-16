package pl.edu.agh.automerger.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import pl.edu.agh.automerger.core.objects.ConflictAuthor;
import pl.edu.agh.automerger.core.objects.ConflictingCommit;

import java.io.IOException;
import java.util.*;

/**
 * Allows to transform hardly readable JGit merge result data structure into perspicuous one.
 */
public class ConflictsParser {

  private static final long MILLISECONDS_IN_SECOND = 1000;

  private final Logger logger = LogManager.getLogger();

  private final Map<String, ConflictAuthor> conflictAuthorsByEmailsMap = new HashMap<>();

  private final Repository gitRepository;

  private final String mergedBranchName;

  private Map<String, int[][]> allConflicts;

  private ObjectId[] mergedCommits;

  public ConflictsParser(final Repository gitRepository, final String mergedBranchName) {
    this.gitRepository = gitRepository;
    this.mergedBranchName = mergedBranchName;
  }

  /**
   * Creates convenient tree structure, which groups conflicting files by commits, which caused the conflicts,
   * and those in turn by their authors.
   */
  public Collection<ConflictAuthor> parse(final MergeResult mergeResult) {
    allConflicts = mergeResult.getConflicts();
    mergedCommits = mergeResult.getMergedCommits();

    if (allConflicts != null && mergedCommits != null) {
      for (String filePath : allConflicts.keySet()) {
        parseConflictsForFile(filePath);
      }
    }

    return conflictAuthorsByEmailsMap.values();
  }

  /**
   * Processes merge conflict for a single file with given path.
   */
  private void parseConflictsForFile(final String filePath) {
    final int[][] conflictsInFile = allConflicts.get(filePath);
    RevCommit commit;
    int conflictingLineNumber;

    for (int conflictNumber = 0; conflictNumber < conflictsInFile.length; conflictNumber++) {
      for (int commitNumber = 0; commitNumber < conflictsInFile[conflictNumber].length - 1; commitNumber++) {
        // line numbers are counted from 0, thus incrementation
        conflictingLineNumber = conflictsInFile[conflictNumber][commitNumber] + 1;
        if (conflictingLineNumber <= 0) {
          continue;
        }

        commit = (RevCommit) mergedCommits[commitNumber];
        if (isCommitFromFeatureBranch(commit.getId())) {
          registerConflictingLine(commit, filePath, conflictingLineNumber);
        }
      }
    }
  }

  /**
   * Checks if given ObjectId relates to a commit belonging to the merged branch.
   */
  private boolean isCommitFromFeatureBranch(final ObjectId commitId) {
    final RevWalk walk = new RevWalk(gitRepository);
    try {
      final Ref featureBranchRef = gitRepository.getRef(Constants.R_HEADS.concat(mergedBranchName));
      final RevCommit headCommit = walk.parseCommit(featureBranchRef.getObjectId());
      final RevCommit searchedCommit = walk.parseCommit(commitId);
      return walk.isMergedInto(searchedCommit, headCommit);
    }
    catch (IOException e) {
      logger.error(e);
    }
    return false;
  }

  /**
   * Adds information about conflicting line to proper file item from list in the commit, in which it was changed.
   */
  private void registerConflictingLine(final RevCommit commit, final String filePath, final int lineNumber) {
    final PersonIdent commitAuthor = commit.getAuthorIdent();
    final Map<String, ConflictingCommit> conflictingCommitsByName =
        getConflictingCommitsOfAuthor(commitAuthor.getName(), commitAuthor.getEmailAddress());
    final String commitName = commit.getName();
    ConflictingCommit conflictingCommit;

    if (conflictingCommitsByName.containsKey(commitName)) {
      conflictingCommit = conflictingCommitsByName.get(commitName);
    } else {
      conflictingCommit = new ConflictingCommit(commit.getName(), parseDate(commit.getCommitTime()), commit.getShortMessage());
      conflictingCommitsByName.put(commitName, conflictingCommit);
    }

    conflictingCommit.addConflictingFileLineNumber(filePath, lineNumber);
  }

  /**
   * Returns a map of all conflicting commits of the author with given name and email address.
   */
  private Map<String, ConflictingCommit> getConflictingCommitsOfAuthor(final String authorName, final String authorEmailAddress) {
    ConflictAuthor conflictAuthor;

    if (conflictAuthorsByEmailsMap.containsKey(authorEmailAddress)) {
      conflictAuthor = conflictAuthorsByEmailsMap.get(authorEmailAddress);
    } else {
      conflictAuthor = new ConflictAuthor(authorName, authorEmailAddress);
      conflictAuthorsByEmailsMap.put(authorEmailAddress, conflictAuthor);
    }

    return conflictAuthor.getConflictingCommitsByName();
  }

  /**
   * Returns Date object created from specific JGit time value (seconds since epoch).
   */
  private Date parseDate(final int secondsFromEpoch) {
    return new Date(secondsFromEpoch * MILLISECONDS_IN_SECOND);
  }

}
