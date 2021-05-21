package issueData;

import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;

public class Comment {

    @CsvBindByPosition(position = 0)
    private String hash;

    @CsvBindByPosition(position = 1)
    private User author;

    @CsvBindByPosition(position = 2)
    private String message;

    @CsvBindByPosition(position = 3)
    private long creationTime;

    public Comment(User author, String message) {
        this.author = author;
        this.message = message;
        this.creationTime = System.currentTimeMillis();
        this.hash = DigestUtils.sha256Hex(author + message + creationTime);
    }

    public Comment() {
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(hash, comment.hash);

    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

}
