package csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirectory.class)
class IssueToCSVTests {

    private List<Issue> issues;

    @BeforeEach
    void setUp() {
        this.issues = new ArrayList<>();

        Issue issue = new Issue("issue1", "desc1");
        issue.setCreator(new User("jack", "jack"));

        Issue issue2 = new Issue("issue2", "desc2");
        issue2.setCreator(new User("jack", "jack"));

        issues.add(issue);
        issues.add(issue2);
    }

    @Test
    void testFileCorrect(@TempDirectory.TempDir Path tempDir) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        CSVWriter issuesToCSV = new IssuesToCSV(issues);
        String path = issuesToCSV.write(tempDir.toString());

        CSVReader reader = new CSVReader(new FileReader(path));
        List<String[]> rows = reader.readAll();

        assertEquals(Arrays.toString(rows.get(0)), Arrays.toString(new String[]{"HASH", "STATUS", "TITLE", "DESCRIPTION", "CREATOR", "CREATIONTIME", "EDITED", "TAGS", "ASSIGNEES", "WATCHERS"}));

        Issue issue1 = issues.get(0);
        String[] issue1Data = new String[]{issue1.getHash(), String.valueOf(issue1.getStatus()), issue1.getTitle(), issue1.getDescription(), issue1.getCreator().toString(), String.valueOf(issue1.getCreationTime()), String.valueOf(issue1.getEdited()), "", "", ""};
        assertEquals(Arrays.toString(rows.get(1)), Arrays.toString(issue1Data));

    }


    @Test
    void testValidLocation(@TempDirectory.TempDir Path tempDir) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        CSVWriter issuesToCSV = new IssuesToCSV(issues);
        String path = issuesToCSV.write(tempDir.toString());
        assertTrue(new File(path).exists());
    }

    @Test
    void testInvalidLocation() {
        CSVWriter issuesToCSV = new IssuesToCSV(issues);
        assertThrows(IOException.class, () -> issuesToCSV.write("invalid"));
    }


}
