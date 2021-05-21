package csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import issueData.Comment;
import issueData.Issue;
import issueData.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirectory.class)
class CommentsToCSVTests {

    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        User user = new User("jack", "jack");
        Issue issue = new Issue("issue1", "desc1");
        issue.setCreator(user);

        issue.addComment(new Comment(user, "message1"));
        issue.addComment(new Comment(user, "message2"));

        this.comments = issue.getComments();
    }

    @Test
    void testFileCorrect(@TempDirectory.TempDir Path tempDir) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        CSVWriter commentsToCSV = new CommentsToCSV(comments);
        String path = commentsToCSV.write(tempDir.toString());

        CSVReader reader = new CSVReader(new FileReader(path));
        List<String[]> rows = reader.readAll();

        assertEquals(Arrays.toString(rows.get(0)), Arrays.toString(new String[]{"HASH", "AUTHOR", "MESSAGE", "CREATIONTIME"}));


        Comment comment1 = comments.get(0);
        String[] comment1data = new String[]{comment1.getHash(), comment1.getAuthor().toString(), comment1.getMessage(), String.valueOf(comment1.getCreationTime())};
        assertEquals(Arrays.toString(rows.get(1)), Arrays.toString(comment1data));
    }


    @Test
    void testValidLocation(@TempDirectory.TempDir Path tempDir) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        CSVWriter commentsToCSV = new CommentsToCSV(comments);
        String path = commentsToCSV.write(tempDir.toString());
        assertTrue(new File(path).exists());
    }

    @Test
    void testInvalidLocation() {
        CSVWriter commentsToCSV = new CommentsToCSV(comments);
        assertThrows(IOException.class, () -> commentsToCSV.write("invalid"));
    }
}
