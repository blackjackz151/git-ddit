package git;

import issueData.Issue;
import issueData.User;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirectory.class)
class IssueRepoTests {

    private Repository repository;
    private Git git;
    private Path path;

    @BeforeEach
    void setUp(@TempDirectory.TempDir Path tempDir) throws GitAPIException {
        this.path = Paths.get(tempDir + "/local");
        this.git = Git.init().setDirectory(new File(tempDir.toString() + "/local")).call();
        git.commit().setMessage("init").call();
        this.repository = git.getRepository();
    }

    @AfterEach
    void tearDown() {
        repository.close();
    }

    @Test
    void testIssueBranchNotExist() throws IOException {
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        assertFalse(issueRepo.issueBranchExists());
    }

    @Test
    void testIssueBranchExist() throws IOException {
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        issueRepo.init();
        assertTrue(issueRepo.issueBranchExists());
    }

    @Test
    void testGetIssues() throws IOException {
        populateRepoWithFakeData();
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        assertEquals(2, issueRepo.getIssues().size());
    }

    @Test
    void testGetNicknames() throws IOException {
        populateRepoWithFakeData();
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        assertEquals(1, issueRepo.getNicknames().getIssueNicknames().size());
    }

    @Test
    void testWriteRepo() throws IOException {
        populateRepoWithFakeData();
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        issueRepo.getIssues().get(0).setStatus(false);
        assertTrue(issueRepo.writeRepo("test writerepo"));
    }

    @Test
    void testGetIssueWithHash() throws IOException {
        populateRepoWithFakeData();
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        assertTrue(issueRepo.getIssue("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db").isPresent());
    }

    @Test
    void testGetIssueWithNickname() throws IOException {
        populateRepoWithFakeData();
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        assertTrue(issueRepo.getIssue("issue").isPresent());
    }

    @Test
    void testTwoIssueSameHash() throws IOException {
        populateRepoWithFakeData();
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        Issue duplciate = new Issue();
        duplciate.setHash("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db");
        issueRepo.getIssues().add(duplciate);
        assertThrows(IllegalStateException.class, () -> issueRepo.getIssue("7a37cedc949c7be72767e9b7b008f33ba4b5136ff4a8bd4c6b00ca69b6ce51db"));
    }

    @Test
    void verifyUserCorrect() throws IOException {
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        String name = repository.getConfig().getString("user", null, "name");
        String email = repository.getConfig().getString("user", null, "email");
        User user = new User(name, email);
        assertEquals(user, issueRepo.getUser());
    }

    private void populateRepoWithFakeData() {
        try {
            git.checkout().setOrphan(true).setName("issues").setCreateBranch(true).call();
            FileUtils.copyDirectory(new File("src/test/resources/fake-issues"), new File(path.toString()));
            git.add().addFilepattern(".").call();
            git.commit().setMessage("testData").call();
            git.checkout().setName("master").call();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void invalidRepo() {
        assertThrows(GitRepoNotFoundException.class, () -> new IssueRepo("/invalid"));
    }


    @Test
    void testInit() throws IOException {
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        assertTrue(issueRepo.init());

        Ref issueRepoRef = repository.findRef("refs/heads/issues");

        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(issueRepoRef.getObjectId());
            assertEquals("Issue (orphan) branch created", commit.getFullMessage());
            walk.dispose();
        }
    }

    @Test
    void pullTestInvalidRemote() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        issueRepo.pull("invalidRemote");
        assertFalse(out.toString().isEmpty());
    }

    @Test
    void pushTestInvalidRemote() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        Optional<Iterable<PushResult>> pr = issueRepo.push("invalidRemote");
        assertFalse(pr.isPresent());
        assertFalse(out.toString().isEmpty());
    }


    @Test
    void pushTestRemote(@TempDirectory.TempDir Path tempDir) throws IOException, GitAPIException {
        populateRepoWithFakeData();
        String remotePath = tempDir.toString() + "/remote";
        Git remoteGit = Git.init().setDirectory(new File(remotePath)).call();
        remoteGit.commit().setMessage("init").call();
        remoteGit.checkout().setOrphan(true).setName("issues").setCreateBranch(true).call();
        FileUtils.copyDirectory(new File("src/test/resources/fake-issue"), new File(remotePath));
        remoteGit.add().addFilepattern(".").call();
        remoteGit.commit().setMessage("testData").call();
        remoteGit.checkout().setName("master").call();

        IssueRepo issueRepo = new IssueRepo(path.toAbsolutePath() + "/.git");
        Optional<Iterable<PushResult>> pr = issueRepo.push(remotePath);
        assertTrue(pr.isPresent());
        pr.ifPresent(pushResults -> pushResults.forEach(r -> {
            r.getRemoteUpdates().forEach(refUpdate ->
                    assertEquals(RemoteRefUpdate.Status.REJECTED_NONFASTFORWARD, refUpdate.getStatus()));
        }));
    }
}
