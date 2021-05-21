package git;

import com.fasterxml.jackson.databind.ObjectMapper;
import issueData.Comment;
import issueData.Issue;
import org.eclipse.jgit.lib.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

class IssueTreeBuilder {

    private final ObjectInserter oi;
    private final ObjectMapper mapper;

    IssueTreeBuilder(Repository repository) {
        this.oi = new ObjectInserter(repository);
        mapper = new ObjectMapper();
    }

    ObjectId buildIssueTree(Issue issue) throws IOException {

        TreeFormatter issueTreeFormatter = new TreeFormatter();

        byte[] assigneesJson = mapper.writeValueAsBytes(issue.getAssignees());
        byte[] creationTimeJson = mapper.writeValueAsBytes(issue.getCreationTime());
        byte[] creatorJson = mapper.writeValueAsBytes(issue.getCreator());
        byte[] descriptionJson = mapper.writeValueAsBytes(issue.getDescription());
        byte[] editTimeJson = mapper.writeValueAsBytes(issue.getEdited());
        byte[] statusJson = mapper.writeValueAsBytes(issue.getStatus());
        byte[] tagsJsonBytes = mapper.writeValueAsBytes(issue.getTags());
        byte[] titleJson = mapper.writeValueAsBytes(issue.getTitle());
        byte[] watchersJson = mapper.writeValueAsBytes(issue.getWatchers());


        ObjectId assignees = oi.insert(Constants.OBJ_BLOB, assigneesJson);
        ObjectId attachments = buildAttachmentTree(issue.getAttachments());
        ObjectId comments = buildCommentTree(issue.getComments());
        ObjectId creationTime = oi.insert(Constants.OBJ_BLOB, creationTimeJson);
        ObjectId creator = oi.insert(Constants.OBJ_BLOB, creatorJson);
        ObjectId editTime = oi.insert(Constants.OBJ_BLOB, editTimeJson);
        ObjectId description = oi.insert(Constants.OBJ_BLOB, descriptionJson);
        ObjectId status = oi.insert(Constants.OBJ_BLOB, statusJson);
        ObjectId tags = oi.insert(Constants.OBJ_BLOB, tagsJsonBytes);
        ObjectId title = oi.insert(Constants.OBJ_BLOB, titleJson);
        ObjectId watchers = oi.insert(Constants.OBJ_BLOB, watchersJson);

        issueTreeFormatter.append("assignees", FileMode.REGULAR_FILE, assignees);
        issueTreeFormatter.append("attachments", FileMode.TREE, attachments);
        issueTreeFormatter.append("comments", FileMode.TREE, comments);
        issueTreeFormatter.append("creationTime", FileMode.REGULAR_FILE, creationTime);
        issueTreeFormatter.append("creator", FileMode.REGULAR_FILE, creator);
        issueTreeFormatter.append("description", FileMode.REGULAR_FILE, description);
        issueTreeFormatter.append("editTime", FileMode.REGULAR_FILE, editTime);
        issueTreeFormatter.append("status", FileMode.REGULAR_FILE, status);
        issueTreeFormatter.append("tags", FileMode.REGULAR_FILE, tags);
        issueTreeFormatter.append("title", FileMode.REGULAR_FILE, title);
        issueTreeFormatter.append("watchers", FileMode.REGULAR_FILE, watchers);


        return oi.insert(Constants.OBJ_TREE, issueTreeFormatter.toByteArray());
    }

    private ObjectId buildCommentTree(List<Comment> comments) throws IOException {
        TreeFormatter allCommentTreeFormatter = new TreeFormatter();
        for (Comment comment : comments) {

            TreeFormatter commentTreeFormatter = new TreeFormatter();

            byte[] authorJson = mapper.writeValueAsBytes(comment.getAuthor());
            byte[] commentTimeJson = mapper.writeValueAsBytes(comment.getCreationTime());
            byte[] messageJson = mapper.writeValueAsBytes(comment.getMessage());

            ObjectId author = oi.insert(Constants.OBJ_BLOB, authorJson);
            ObjectId commentTime = oi.insert(Constants.OBJ_BLOB, commentTimeJson);
            ObjectId message = oi.insert(Constants.OBJ_BLOB, messageJson);


            commentTreeFormatter.append("author", FileMode.REGULAR_FILE, author);
            commentTreeFormatter.append("commentTime", FileMode.REGULAR_FILE, commentTime);
            commentTreeFormatter.append("message", FileMode.REGULAR_FILE, message);


            ObjectId commentTree = oi.insert(Constants.OBJ_TREE, commentTreeFormatter.toByteArray());

            allCommentTreeFormatter.append(comment.getHash(), FileMode.TREE, commentTree);
        }

        return oi.insert(Constants.OBJ_TREE, allCommentTreeFormatter.toByteArray());
    }

    private ObjectId buildAttachmentTree(Map<String, byte[]> attachments) throws IOException {

        SortedSet<String> sortedAttachments = new TreeSet<>(attachments.keySet());

        TreeFormatter attachmentTree = new TreeFormatter();
        for (String file : sortedAttachments) {
            ObjectId fileBlob = oi.insert(Constants.OBJ_BLOB, attachments.get(file));
            attachmentTree.append(file, FileMode.REGULAR_FILE, fileBlob);
        }

        return oi.insert(Constants.OBJ_TREE, attachmentTree.toByteArray());
    }

}
