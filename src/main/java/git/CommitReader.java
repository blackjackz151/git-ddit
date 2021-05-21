package git;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import issueData.Comment;
import issueData.Issue;
import issueData.User;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

class CommitReader {

    private final Repository repo;
    private final List<Issue> issues;
    private final ObjectMapper mapper;
    private Map<String, String> nicknames;

    CommitReader(Repository repo, RevTree commitTree) {
        this.repo = repo;
        this.issues = new ArrayList<>();
        this.nicknames = new HashMap<>();
        mapper = new ObjectMapper();
        buildIssues(commitTree);
    }

    private void buildIssues(RevTree commitTree) {
        try (TreeWalk treeWalk = new TreeWalk(repo)) {
            treeWalk.addTree(commitTree);
            treeWalk.setRecursive(true);

            while (treeWalk.next()) {

                ObjectId blob = treeWalk.getObjectId(0);


                if (treeWalk.getNameString().equals(".nicknames")) {
                    String content = getContent(blob);
                    parseNicknames(content);
                    continue;
                }

                String hash = split(treeWalk.getPathString(), 0);

                if (issues.isEmpty() || getIssue(hash) == null) {
                    Issue issue = new Issue();
                    issue.setHash(hash);
                    issues.add(issue);
                    addFieldData(issue, treeWalk.getPathString(), blob);
                } else {
                    addFieldData(getIssue(hash), treeWalk.getPathString(), blob);
                }
            }

        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Make sure no non-issue files have been created on issue branch");

        }

    }

    private void parseNicknames(String content) throws IOException {
        this.nicknames = mapper.readValue(content, new TypeReference<Map<String, String>>() {
        });
    }

    private Issue getIssue(String hash) {
        for (Issue issue : issues) {
            if (issue.getHash().equals((hash))) {
                return issue;
            }
        }
        return null;
    }


    private String getContent(ObjectId blob) throws IOException {
        try (ObjectReader objectReader = repo.newObjectReader()) {
            ObjectLoader objectLoader = objectReader.open(blob);
            byte[] bytes = objectLoader.getBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    private byte[] getByteContent(ObjectId blob) throws IOException {
        try (ObjectReader objectReader = repo.newObjectReader()) {
            ObjectLoader objectLoader = objectReader.open(blob);
            return objectLoader.getBytes();
        }
    }

    private void addFieldData(Issue issue, String path, ObjectId blob) throws IOException {
        String field = split(path, 1);
        String data = getContent(blob);

        switch (field) {
            case "title":
                issue.setTitle(mapper.readValue(data, String.class));
                break;
            case "description":
                issue.setDescription(mapper.readValue(data, String.class));
                break;
            case "creator":
                issue.setCreator(mapper.readValue(data, User.class));
                break;
            case "status":
                issue.setStatus(mapper.readValue(data, boolean.class));
                break;
            case "creationTime":
                issue.setCreationTime(mapper.readValue(data, long.class));
                break;
            case "editTime":
                issue.setEdited(mapper.readValue(data, long.class));
                break;
            case "comments":
                buildComment(issue, path, data);
                break;
            case "tags":
                issue.setTags(mapper.readValue(data, new TypeReference<Set<String>>() {
                }));
                break;
            case "watchers":
                issue.setWatchers(mapper.readValue(data, new TypeReference<Set<User>>() {
                }));
                break;
            case "assignees":
                issue.setAssignees(mapper.readValue(data, new TypeReference<Set<User>>() {
                }));
                break;
            case "attachments":
                String attachmentName = split(path, 2);
                issue.addAttachment(attachmentName, getByteContent(blob));
                break;
            default:
                //ignore
                break;
        }
    }

    private void buildComment(Issue issue, String path, String data) throws IOException {
        String commentHash = split(path, 2);
        String commentField = split(path, 3);

        if (issue.getComments().isEmpty() || getComment(issue, commentHash) == null) {
            Comment comment = new Comment();
            comment.setHash(commentHash);
            issue.addComment(comment);
            addCommentFields(comment, commentField, data);
        } else {
            addCommentFields(getComment(issue, commentHash), commentField, data);
        }
    }

    private void addCommentFields(Comment comment, String field, String data) throws IOException {
        switch (field) {
            case "author":
                comment.setAuthor(mapper.readValue(data, User.class));
                break;
            case "message":
                comment.setMessage(mapper.readValue(data, String.class));
                break;
            case "commentTime":
                comment.setCreationTime(mapper.readValue(data, long.class));
                break;
            default:
                break;
        }

    }

    private Comment getComment(Issue issue, String hash) {
        for (Comment comment : issue.getComments()) {
            if (comment.getHash().equals((hash))) {
                return comment;
            }
        }
        return null;
    }

    List<Issue> getIssues() {
        return issues;
    }

    Map<String, String> getNicknames() {
        return nicknames;
    }

    private String split(String path, int index) {
        return path.split("/")[index];
    }

}
