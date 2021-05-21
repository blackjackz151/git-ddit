package git;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TempDirectory.class)
class ExceptionalCommitReaderTests {

    private Repository repository;
    private Git git;
    private Path tempDir;

    @BeforeEach
    void setUp(@TempDirectory.TempDir Path tempDir) throws GitAPIException {
        this.tempDir = tempDir;
        this.git = Git.init().setDirectory(new File(tempDir.toString())).call();
        this.repository = git.getRepository();
    }

    @AfterEach
    void tearDown() {
        repository.close();
    }

    @Test
    void testNoIssues() throws GitAPIException {
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();
        CommitReader commitReader = new CommitReader(repository, commit.getTree());
        assertEquals(0, commitReader.getIssues().size());
    }

    @Test
    void testNoNicknames() throws GitAPIException {
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();
        CommitReader commitReader = new CommitReader(repository, commit.getTree());
        assertEquals(0, commitReader.getNicknames().size());
    }

    @Test
    void testNoIssuesWithNickname() throws GitAPIException, IOException {
        FileUtils.copyFileToDirectory(new File("src/test/resources/fake-issues/.nicknames"), tempDir.toFile());
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();
        CommitReader commitReader = new CommitReader(repository, commit.getTree());
        assertEquals(0, commitReader.getIssues().size());
        assertEquals(1, commitReader.getNicknames().size());
    }

    @Test
    void testIssuesWithNoNickname() throws GitAPIException, IOException {
        FileUtils.copyDirectory(new File("src/test/resources/fake-issues/03548c2a70ca9e294202779ce037a1ec9f7b4910fd602f1bffcf4d7732e9a69c"), new File(tempDir.toString()));
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();
        CommitReader commitReader = new CommitReader(repository, commit.getTree());
        assertEquals(1, commitReader.getIssues().size());
        assertEquals(0, commitReader.getNicknames().size());
    }


    @Test
    void testAddingInvalidFile() throws GitAPIException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(out));
        String string = "Test string";
        File file = new File(tempDir.toString(), "text.txt");
        FileUtils.writeStringToFile(file, string, "UTF-8");
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();
        new CommitReader(repository, commit.getTree());
        assertEquals("Make sure no non-issue files have been created on issue branch" + System.lineSeparator(), out.toString());
    }


    @Test
    void testNullRepo() throws GitAPIException {
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();
        assertThrows(NullPointerException.class, () -> {
            CommitReader commitReader = new CommitReader(null, commit.getTree());
        });
    }


    @Test
    void testNullCommit() {
        assertThrows(NullPointerException.class, () -> {
            CommitReader commitReader = new CommitReader(repository, null);
        });

    }


    @Test
    void testInvalidFilesInRepo(@TempDirectory.TempDir Path dir) throws IOException, GitAPIException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(out));

        Git git = Git.init().setDirectory(new File(dir.toString())).call();

        FileUtils.copyDirectory(new File("src/test/resources/fake-issues"), new File(dir.toString()));
        FileUtils.writeStringToFile(new File(dir.toString(), "invalid"), "data", "UTF-8");
        git.add().addFilepattern(".").call();
        RevCommit commit = git.commit().setMessage("testData").call();
        CommitReader commitReader = new CommitReader(git.getRepository(), commit.getTree());
        assertEquals("Make sure no non-issue files have been created on issue branch" + System.lineSeparator(), out.toString());
    }


}
