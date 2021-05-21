package issueData;

import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

public class Issue {

    private final List<Comment> comments;
    private Map<String, byte[]> attachments;

    @CsvBindAndSplitByPosition(position = 7, writeDelimiter = ", ", elementType = String.class, collectionType = HashSet.class)
    private Set<String> tags;

    @CsvBindAndSplitByPosition(position = 8, writeDelimiter = ", ", elementType = User.class, collectionType = HashSet.class)
    private Set<User> assignees;

    @CsvBindAndSplitByPosition(position = 9, writeDelimiter = ", ", elementType = User.class, collectionType = HashSet.class)
    private Set<User> watchers;

    @CsvBindByPosition(position = 2)
    private String title;

    @CsvBindByPosition(position = 3)
    private String description;

    @CsvBindByPosition(position = 4)
    private User creator;

    @CsvBindByPosition(position = 0)
    private String hash;

    @CsvBindByPosition(position = 1)
    private boolean status;

    @CsvBindByPosition(position = 5)
    private long creationTime;

    @CsvBindByPosition(position = 6)
    private long edited;

    public Issue(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = true;
        long time = System.currentTimeMillis();
        this.creationTime = time;
        this.edited = time;
        this.hash = DigestUtils.sha256Hex(title + description + creationTime);
        this.comments = new ArrayList<>();
        this.tags = new HashSet<>();
        this.assignees = new HashSet<>();
        this.watchers = new HashSet<>();
        this.attachments = new HashMap<>();
    }

    public Issue() {
        comments = new ArrayList<>();
        tags = new HashSet<>();
        assignees = new HashSet<>();
        watchers = new HashSet<>();
        this.attachments = new HashMap<>();
    }

    public void edited() {
        edited = System.currentTimeMillis();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addWatcher(User user) {
        watchers.add(user);
    }

    public Set<User> getWatchers() {
        return watchers;
    }

    public void setWatchers(Set<User> watchers) {
        this.watchers = watchers;
    }

    public void removeWatcher(User user) {
        watchers.remove(user);
    }

    public void addAttachment(String name, byte[] data) {
        attachments.put(name, data);
    }

    public Map<String, byte[]> getAttachments() {
        return attachments;
    }

//    public void setAttachments(Map<String, byte[]> attachments) {
//        this.attachments = attachments;
//    }

    public void addAssignee(User user) {
        assignees.add(user);
    }

    public Set<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<User> assignees) {
        this.assignees = assignees;
    }

    public void removeAssignee(User user) {
        assignees.remove(user);
    }

    public void addTags(String tag) {
        List<String> strings = Arrays.asList(tag.split("\\s*,\\s*"));
        strings.forEach(t -> {
            if (!t.equals("")) {
                tags.add(t);
            }
        });
    }

//    public void addTag(String tag) {
//        tags.add(tag);
//    }

    public void removeTag(String tag) {
        List<String> strings = Arrays.asList(tag.split("\\s*,\\s*"));
        strings.forEach(t -> {
            if (!t.equals("")) {
                tags.remove(t);
            }
        });
    }


    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public long getEdited() {
        return edited;
    }

    public void setEdited(long edited) {
        this.edited = edited;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return Objects.equals(hash, issue.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    @Override
    public String toString() {
        return "Hash: " + hash + " Title: " + title;
    }

}
