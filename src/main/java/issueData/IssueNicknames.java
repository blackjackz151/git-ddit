package issueData;

import java.util.Map;

public class IssueNicknames {

    private final Map<String, String> issueNicknames;

    public IssueNicknames(Map<String, String> issueNicknames) {
        this.issueNicknames = issueNicknames;
    }

    public boolean addNickname(String hash, String nickname) {
        if (validateInput(hash, nickname)) {
            issueNicknames.put(hash, nickname);
            return true;
        } else {
            return false;
        }
    }


    private boolean validateInput(String hash, String nickname) {
        if (issueNicknames.containsKey(hash)) {
            System.out.println("Hash already has a nickname");
            return false;
        } else if (issueNicknames.containsValue(nickname)) {
            System.out.println("Nickname already exists");
            return false;
        } else if (issueNicknames.containsKey(nickname)) {
            System.out.println("This nickname is an existing hash, choose another");
            return false;
        } else if (issueNicknames.containsValue(hash)) {
            System.out.println("This hash/nickname is already in use as a nickname");
            return false;
        } else {
            return true;
        }
    }

    public boolean removeNickname(String identifier) {
        String oldVal = issueNicknames.remove(identifier);
        return oldVal != null;
    }

    public boolean updateNickname(String hash, String newNickname) {
        if (!issueNicknames.containsValue(newNickname)) {
            String oldVal = issueNicknames.replace(hash, newNickname);
            return oldVal != null;
        } else {
            System.out.println("This hash/nickname is already in use as a nickname");
            return false;
        }

    }


    public Map<String, String> getIssueNicknames() {
        return issueNicknames;
    }

}
