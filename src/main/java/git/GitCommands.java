package git;

import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.Merger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import util.Util;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;

class GitCommands {

    private final Repository repo;
    private final ObjectInserter oi;


    GitCommands(Repository repo) {
        this.repo = repo;
        this.oi = new ObjectInserter(repo);

    }

    Optional<Iterable<PushResult>> push(String remote) {
        try (Git git = new Git(repo)) {
            Iterable<PushResult> results = git.push()
                    .setTransportConfigCallback(getSSHConfig())
                    .setRemote(remote)
                    .add("issues")
                    .call();
            return Optional.of(results);
        } catch (GitAPIException e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }

    void pull(String remote) {
        String clone = null;
        try {
            clone = localClone();
            mergeOnClone(clone, remote);
        } catch (IOException | GitAPIException e) {
            System.err.println(e.getMessage());
        } finally {
            Util.deleteDirectory(clone);
        }
    }

    boolean commit(String message, ObjectId tree, ObjectId... parent) throws IOException {
        CommitBuilder commitBuilder = new CommitBuilder();
        commitBuilder.setMessage(message);
        PersonIdent personIdent = new PersonIdent(Util.getUserName(repo), Util.getUserEmail(repo));
        commitBuilder.setAuthor(personIdent);
        commitBuilder.setCommitter(personIdent);

        if (tree != null && parent != null) {
            commitBuilder.setTreeId(tree);
            commitBuilder.setParentIds(parent);
        } else {
            commitBuilder.setTreeId(oi.insert(Constants.OBJ_TREE, new byte[]{}));
        }

        ObjectId newCommit = oi.insert(commitBuilder);

        RevCommit commit;

        try (RevWalk walk = new RevWalk(repo)) {
            commit = walk.parseCommit(newCommit);
            walk.dispose();
        }

        RefUpdate update = repo.updateRef("refs/heads/issues");
        update.setNewObjectId(commit);
        RefUpdate.Result result = update.update();

        switch (result) {
            case NEW:
            case FAST_FORWARD:
                return true;
            default:
                return false;
        }

    }

    private String localClone() throws IOException, GitAPIException {
        Path tempDir = Files.createTempDirectory("ddit");
        tempDir.toFile().deleteOnExit();
        Git.cloneRepository()
                .setURI(repo.getDirectory().getParent())
                .setDirectory(tempDir.toFile())
                .setBranchesToClone(Collections.singleton("refs/heads/issues"))
                .setBranch("refs/heads/issues")
                .call();
        return tempDir.toString();
    }

    private void mergeOnClone(String clone, String remote) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(clone))) {
            fetchRemote(remote, git);
            ObjectId mergeBase = git.getRepository().resolve("remIssues");

            MergeCommand merge = git.merge()
                    .include(mergeBase)
                    .setCommit(true)
                    .setMessage("Merged");
            MergeResult mergeResult = merge.call();

            if (!mergeResult.getMergeStatus().isSuccessful() && mergeResult.getConflicts() != null) {
                System.out.println("Conflicts exist in:");
                mergeResult.getConflicts().forEach((k, v) -> System.out.println(k));

                if (Desktop.isDesktopSupported()) {
                    mergeResult.getConflicts().forEach((k, v) -> {
                        try {
                            if (k.contains("editTime")) {
                                FileOutputStream stream = new FileOutputStream(new File(clone + "/" + k), false);
                                byte[] bytes = String.valueOf(System.currentTimeMillis()).getBytes();
                                stream.write(bytes);
                                stream.close();
                            } else {
                                if (k.contains(".nicknames")) {
                                    System.out.println("Merge conflict in .nicknames - users are strongly advised not to change names of issues - advised to resolve with a social process");
                                }
                                Desktop.getDesktop().open(new File(clone + "/" + k));
                            }
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    });

                    System.out.println("Enter \"abort\" to stop merge or \"resolved\" if finished resolving - any other input will be treated as abort");
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine();
                    scanner.close();

                    if (input.equals("resolved")) {
                        git.add().addFilepattern(".").call();
                        git.commit().setMessage("resolved merge conflict").call();
                        pullClone(clone);
                    } else {
                        git.reset().setMode(ResetCommand.ResetType.HARD).call();
                        System.out.println("Merge abandoned");
                    }
                }

            } else if (mergeResult.getMergeStatus().isSuccessful()) {
                pullClone(clone);
            }

        }
    }

    private void fetchRemote(String remote, Git git) throws IOException, GitAPIException {
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "remIssues", "url", remote);
        config.save();

        git.fetch()
                .setTransportConfigCallback(getSSHConfig())
                .setRemote("remIssues")
                .setRefSpecs(new RefSpec("refs/heads/issues:refs/remotes/remIssues"))
                .call();

    }

    private TransportConfigCallback getSSHConfig() {
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                // do nothing
            }
        };

        return transport -> {
            if (transport instanceof SshTransport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        };
    }


    private void pullClone(String clone) throws IOException, GitAPIException {

        try (Git git = new Git(repo)) {
            fetchRemote(clone, git);

            Ref localRef = repo.findRef("refs/heads/issues");
            Ref remoteRef = repo.findRef("refs/remotes/remIssues");

            RevCommit localCommit = null;
            RevCommit remoteCommit = null;

            try (RevWalk revWalk = new RevWalk(repo)) {
                localCommit = revWalk.parseCommit(localRef.getObjectId());
                remoteCommit = revWalk.parseCommit(remoteRef.getObjectId());
                revWalk.dispose();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            Merger merger = MergeStrategy.THEIRS.newMerger(repo);

            if (merger.merge(localCommit, remoteCommit)) {
                String commit = "Merged " + localRef.getName() + " and " + remoteRef.getName();
                commit(commit, merger.getResultTreeId(), localCommit, remoteCommit);
                System.out.println("Issues pulled");
            }

        }

    }

}