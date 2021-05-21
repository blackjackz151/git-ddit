package git;

import com.fasterxml.jackson.databind.ObjectMapper;
import issueData.Issue;
import issueData.IssueNicknames;
import org.eclipse.jgit.lib.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class RepoTreeBuilder {

    private final ObjectInserter oi;
    private final Repository repo;

    RepoTreeBuilder(Repository repo) {
        this.repo = repo;
        this.oi = new ObjectInserter(repo);
    }

    Optional<ObjectId> getRepoTree(List<Issue> issues, IssueNicknames nicknames) throws IOException {
        TreeFormatter repoTreeFormatter = new TreeFormatter();
        Map<String, String> nicknamesTowrite = nicknames.getIssueNicknames();
        repoTreeFormatter.append(".nicknames", FileMode.REGULAR_FILE, writeNicknames(nicknamesTowrite));
        IssueTreeBuilder issueWriter = new IssueTreeBuilder(repo);

        for (Issue issue : issues) {
            ObjectId issueObj = issueWriter.buildIssueTree(issue);
            repoTreeFormatter.append(issue.getHash(), FileMode.TREE, issueObj);
        }

        return Optional.of(oi.insert(Constants.OBJ_TREE, repoTreeFormatter.toByteArray()));
    }

    ObjectId writeNicknames(Map<String, String> nicknames) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] issueNicknameJson = mapper.writeValueAsBytes(nicknames);
        return oi.insert(Constants.OBJ_BLOB, issueNicknameJson);
    }
}
