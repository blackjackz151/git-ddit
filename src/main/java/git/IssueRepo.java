package git;

import issueData.Issue;
import issueData.IssueNicknames;
import issueData.User;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.util.FS;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IssueRepo {

    private Repository repository;
    private IssueNicknames nicknames;
    private List<Issue> issues;
    private GitCommands git;

    public IssueRepo(String path) throws IOException {
        setGitDir(path);
    }

    private void setGitDir(String gitDirPath) throws IOException {
        if (RepositoryCache.FileKey.isGitRepository(new File(gitDirPath), FS.DETECTED)) {
            this.repository = new FileRepositoryBuilder()
                    .setGitDir(new File(gitDirPath))
                    .build();
            this.git = new GitCommands(repository);
            readTree(getCurrentCommit());
        } else {
            throw new GitRepoNotFoundException("Not in a valid Git repository");
        }
    }

    public boolean writeRepo(String commitMessage) throws IOException {
        RepoTreeBuilder repoTreeBuilder = new RepoTreeBuilder(repository);

        Optional<ObjectId> repoTree = repoTreeBuilder.getRepoTree(sortIssues(issues), nicknames);

        if (repoTree.isPresent()) {
            return git.commit(commitMessage, repoTree.get(), getCurrentCommit());
        } else {
            return false;
        }
    }

    public boolean init() throws IOException {
        String commitMessage = "Issue (orphan) branch created";
        return git.commit(commitMessage, null);
    }

    public Optional<Iterable<PushResult>> push(String remote) {
        return git.push(remote);
    }

    public void pull(String remote) {
        git.pull(remote);
    }

    private void readTree(RevCommit commit) {
        if (commit == null) {
            this.issues = new ArrayList<>();
            this.nicknames = new IssueNicknames(new HashMap<>());
        } else {
            CommitReader commitReader = new CommitReader(repository, commit.getTree());
            this.nicknames = new IssueNicknames(commitReader.getNicknames());
            this.issues = sortIssues(commitReader.getIssues());
        }
    }

    private List<Issue> sortIssues(List<Issue> issues) {
        issues.sort(Comparator.comparing(Issue::getHash));
        return issues;
    }

    public boolean issueBranchExists() {
        try (Git git = new Git(repository)) {
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().equals("refs/heads/issues")) return true;
            }
            return false;
        } catch (GitAPIException e) {
            return false;
        }
    }

    private RevCommit getCurrentCommit() throws IOException {
        RevCommit commit;
        Ref issueRef = repository.findRef("refs/heads/issues");

        if (issueRef == null) {
            return null;
        }

        try (RevWalk walk = new RevWalk(repository)) {
            commit = walk.parseCommit(issueRef.getObjectId());
            walk.dispose();
            return commit;
        }
    }

    public User getUser() {
        return new User(Util.getUserName(repository), Util.getUserEmail(repository));
    }

    public IssueNicknames getNicknames() {
        return nicknames;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public Optional<Issue> getIssue(String identifier) {
        return getIssues().stream()
                .filter((issue) -> issue.getHash().equals(identifier) || issue.getHash().equals(getHash(identifier)))
                .reduce((u, v) -> {
                    throw new IllegalStateException("More than one issue with same hash");
                });
    }

    private String getHash(String nickname) {
        Map<String, String> nicknames = getNicknames().getIssueNicknames();
        String key = "";
        for (Map.Entry<String, String> entry : nicknames.entrySet()) {
            if (nickname.equals(entry.getValue())) {
                key = entry.getKey();
                break;
            }
        }

        return key;
    }

}
