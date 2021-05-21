package git;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(TempDirectory.class)
class GitCommandsTest {

    private Repository repository;
    private Git git;
    private String localPath;

    @BeforeEach
    void setUp(@TempDirectory.TempDir Path tempDir) throws GitAPIException {
        this.localPath = tempDir + "/local";
        this.git = Git.init().setDirectory(new File(localPath)).call();
        git.commit().setMessage("init").call();
        git.checkout().setOrphan(true).setCreateBranch(true).setName("issues").call();
        this.repository = git.getRepository();
    }

    @AfterEach
    void tearDown() {
        repository.close();
    }

    @Test
    void testPullEmptyRemote(@TempDirectory.TempDir Path tempRemote) throws IOException, GitAPIException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        String remotePath = tempRemote.toString() + "/remote";
        Git remote = initTestRemote(remotePath);
        GitCommands commands = new GitCommands(repository);
        commands.pull(remote.getRepository().getDirectory().getPath());
        assertEquals("Issues pulled" + System.lineSeparator(), out.toString());
        assertTrue(isMergedInto());
        assertTrue(git.status().call().isClean());
    }

    @Test
    void testPullIssuesNoConflicts(@TempDirectory.TempDir Path tempRemote) throws IOException, GitAPIException {
        git.checkout().setName("master").call();
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        String remotePath = tempRemote.toString() + "/remote";
        Git remote = initTestRemote(remotePath);
        commitFakeissues(remote);
        GitCommands commands = new GitCommands(repository);
        commands.pull(remotePath);
        assertEquals("Issues pulled" + System.lineSeparator(), out.toString());
        assertTrue(isMergedInto());
        assertTrue(git.status().call().isClean());
    }

    @Test
    void testCommitNoParentAfterInit() throws IOException {
        ObjectId tree = mock(ObjectId.class);
        GitCommands commands = new GitCommands(repository);
        assertFalse(commands.commit("test commit", tree));
    }

    @Test
    void testPullIssuesConflictsAbandon(@TempDirectory.TempDir Path tempRemote) throws IOException, GitAPIException {

        String remotePath = tempRemote.toString() + "/remote";
        Git remote = initTestRemote(remotePath);
        commitFakeissues(remote);

        GitCommands commands = new GitCommands(repository);
        commands.pull(remotePath);

        remote.checkout().setName("issues").call();
        FileUtils.writeStringToFile(new File(remotePath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description"), "this is conflict", "UTF-8", false);
        remote.add().addFilepattern(".").call();
        remote.commit().setMessage("remote repo conflict").call();
        remote.checkout().setName("master").call();

        git.checkout().setName("issues").call();
        FileUtils.writeStringToFile(new File(localPath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description"), "to create conflict", "UTF-8", false);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("local repo conflict").call();
        git.checkout().setName("master").call();


        String input = "abort";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        commands.pull(remotePath);
        assertTrue(out.toString().contains("Merge abandoned"));

        assertTrue(git.status().call().isClean());

    }

    @Test
    void testPullissuesConflicts(@TempDirectory.TempDir Path tempRemote) throws IOException, GitAPIException {

        String remotePath = tempRemote.toString() + "/remote";
        Git remote = initTestRemote(remotePath);
        commitFakeissues(remote);

        GitCommands commands = new GitCommands(repository);
        commands.pull(remotePath);

        remote.checkout().setName("issues").call();
        FileUtils.writeStringToFile(new File(remotePath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description"), "this is conflict", "UTF-8", false);
        FileUtils.writeStringToFile(new File(remotePath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/editTime"), String.valueOf(System.currentTimeMillis() - 100), "UTF-8", false);
        remote.add().addFilepattern(".").call();
        remote.commit().setMessage("remote repo conflict").call();
        remote.checkout().setName("master").call();

        git.checkout().setName("issues").call();
        FileUtils.writeStringToFile(new File(localPath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description"), "to create conflict", "UTF-8", false);
        FileUtils.writeStringToFile(new File(localPath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/editTime"), String.valueOf(System.currentTimeMillis()), "UTF-8", false);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("local repo conflict").call();
        git.checkout().setName("master").call();


        String input = "resolved";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        commands.pull(remotePath);
        assertEquals("Conflicts exist in:\n" +
                "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/editTime\n" +
                "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description\n" +
                "Enter \"abort\" to stop merge or \"resolved\" if finished resolving - any other input will be treated as abort\n" +
                "Issues pulled\n", out.toString());

        assertTrue(git.status().call().isClean());
        assertTrue(isMergedInto());

    }

    @Test
    void testPullissuesConflictsNicknames(@TempDirectory.TempDir Path tempRemote) throws IOException, GitAPIException {

        String remotePath = tempRemote.toString() + "/remote";
        Git remote = initTestRemote(remotePath);
        commitFakeissues(remote);

        GitCommands commands = new GitCommands(repository);
        commands.pull(remotePath);

        remote.checkout().setName("issues").call();
        FileUtils.writeStringToFile(new File(remotePath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description"), "this is conflict", "UTF-8", false);
        FileUtils.writeStringToFile(new File(remotePath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/editTime"), String.valueOf(System.currentTimeMillis() - 100), "UTF-8", false);
        FileUtils.writeStringToFile(new File(remotePath, ".nicknames"), "nickname-collision1", "UTF-8", false);
        remote.add().addFilepattern(".").call();
        remote.commit().setMessage("remote repo conflict").call();
        remote.checkout().setName("master").call();

        git.checkout().setName("issues").call();
        FileUtils.writeStringToFile(new File(localPath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description"), "to create conflict", "UTF-8", false);
        FileUtils.writeStringToFile(new File(localPath, "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/editTime"), String.valueOf(System.currentTimeMillis()), "UTF-8", false);
        FileUtils.writeStringToFile(new File(localPath, ".nicknames"), "nickname-collision2", "UTF-8", false);
        git.add().addFilepattern(".").call();
        git.commit().setMessage("local repo conflict").call();
        git.checkout().setName("master").call();


        String input = "resolved";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        commands.pull(remotePath);
        assertEquals("Conflicts exist in:\n" +
                "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/editTime\n" +
                "7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db/description\n" +
                ".nicknames\n" +
                "Merge conflict in .nicknames - users are strongly advised not to change names of issues - advised to resolve with a social process\n" +
                "Enter \"abort\" to stop merge or \"resolved\" if finished resolving - any other input will be treated as abort\n" +
                "Issues pulled\n", out.toString());

        assertTrue(git.status().call().isClean());
        assertTrue(isMergedInto());

    }


    private void commitFakeissues(Git remote) throws IOException, GitAPIException {
        remote.checkout().setName("issues").call();
        FileUtils.copyDirectory(new File("src/test/resources/fake-issues"), remote.getRepository().getDirectory().getParentFile());
        remote.add().addFilepattern(".").call();
        remote.commit().setMessage("fakeissues").call();
        remote.checkout().setName("master").call();
    }

    private boolean isMergedInto() throws IOException {
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit repoHead = revWalk.parseCommit(repository.resolve("refs/heads/issues"));
            RevCommit remoteHead = revWalk.parseCommit(repository.resolve("refs/remotes/remIssues"));
            return revWalk.isMergedInto(remoteHead, repoHead);
        }
    }

    private Git initTestRemote(String tempRemote) throws GitAPIException {
        Git remoteGit = Git.init().setDirectory(new File(tempRemote)).call();
        remoteGit.commit().setMessage("init").call();
        remoteGit.checkout().setOrphan(true).setCreateBranch(true).setName("issues").call();
        remoteGit.checkout().setName("master").call();

        return remoteGit;
    }

}
