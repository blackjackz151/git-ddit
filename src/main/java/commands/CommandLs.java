package commands;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import csv.CSVWriter;
import csv.CommentsToCSV;
import csv.IssuesToCSV;
import git.IssueRepo;
import issueData.Comment;
import issueData.Issue;
import queries.FilterIssues;
import util.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CommandLs implements Command {

    private final IssueRepo issueRepo;
    private final String[] args;
    private final boolean verbose;
    private String path;
    private String commentPath;

    CommandLs(IssueRepo issueRepo, String[] args, boolean verbose) {
        this.issueRepo = issueRepo;
        this.args = args;
        this.verbose = verbose;
    }

    CommandLs(IssueRepo issueRepo, boolean verbose, String[] args, String path) {
        this(issueRepo, args, verbose);
        this.path = path;
    }

    CommandLs(IssueRepo issueRepo, boolean verbose, String issuePath, String[] args, String commentPath) {
        this(issueRepo, verbose, args, issuePath);
        this.commentPath = commentPath;
    }

    @Override
    public void execute() {

        if (!issueRepo.issueBranchExists()) {
            System.out.println("Issue branch does not exist - need to run git ddit init");
            return;
        }


        List<Issue> issues = issueRepo.getIssues();

        if (args != null) {
            FilterIssues filterIssues = new FilterIssues(args, issues);
            issues = filterIssues.getFilteredIssues();
        }

        if (verbose) {
            issues.forEach(Util::printIssue);
        } else {
            issues.forEach(System.out::println);
        }

        if (path != null) {
            CSVWriter csvWriter = new IssuesToCSV(issues);
            writeCSV(csvWriter, path);
        }

        if (commentPath != null) {
            List<Comment> comments = new ArrayList<>();
            issues.forEach(issue -> comments.addAll(issue.getComments()));

            CSVWriter csvWriter = new CommentsToCSV(comments);
            writeCSV(csvWriter, commentPath);
        }

    }

    private void writeCSV(CSVWriter csvWriter, String location) {
        try {
            String file = csvWriter.write(location);
            if (file != null) {
                System.out.println("Created CSV at: " + file);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File path not found, make sure path exists - CSV not created");
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            System.err.println(e.getMessage());
        }
    }


}
