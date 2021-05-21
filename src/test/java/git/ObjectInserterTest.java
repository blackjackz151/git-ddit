package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TempDirectory.class)
class ObjectInserterTest {

    private ObjectInserter objectInserter;
    private Repository repository;

    @BeforeEach
    void setUp(@TempDirectory.TempDir Path tempDir) throws GitAPIException {
        Git git = Git.init().setDirectory(new File(tempDir.toString())).call();
        this.objectInserter = new ObjectInserter(git.getRepository());
        this.repository = git.getRepository();
        this.objectInserter = new ObjectInserter(repository);
    }

    @AfterEach
    void tearDown() {
        repository.close();
    }


    @Test
    void testInsertBytesBlob() throws IOException {
        ObjectId blob = objectInserter.insert(Constants.OBJ_BLOB, "test".getBytes());
        assertTrue(repository.hasObject(blob), "Repository does not get blob inserted");
    }

    @Test
    void testInsertBytesTree() throws IOException {
        ObjectId blob1 = objectInserter.insert(Constants.OBJ_BLOB, "a".getBytes());
        ObjectId blob2 = objectInserter.insert(Constants.OBJ_BLOB, "b".getBytes());
        ObjectId blob3 = objectInserter.insert(Constants.OBJ_BLOB, "c".getBytes());

        TreeFormatter issueTreeFormatter = new TreeFormatter();
        issueTreeFormatter.append("a", FileMode.REGULAR_FILE, blob1);
        issueTreeFormatter.append("b", FileMode.REGULAR_FILE, blob2);
        issueTreeFormatter.append("c", FileMode.REGULAR_FILE, blob3);

        ObjectId tree = objectInserter.insert(Constants.OBJ_TREE, issueTreeFormatter.toByteArray());

        assertTrue(repository.hasObject(tree), "Repository does not get tree inserted");
    }

    @Test
    void testInsertCommit() throws IOException {
        CommitBuilder commitBuilder = new CommitBuilder();
        commitBuilder.setMessage("test");
        commitBuilder.setTreeId(objectInserter.insert(Constants.OBJ_TREE, new byte[]{}));
        PersonIdent personIdent = new PersonIdent("test", "test");
        commitBuilder.setAuthor(personIdent);
        commitBuilder.setCommitter(personIdent);

        ObjectId newCommit = objectInserter.insert(commitBuilder);
        assertTrue(repository.hasObject(newCommit));
    }


}
