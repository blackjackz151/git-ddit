package queries;

import issueData.Issue;
import issueData.User;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterIssues {

    private final Map<String, String> conditions;
    private final List<Issue> issueList;
    private final List<Issue> filteredIssues;

    public FilterIssues(String[] query, List<Issue> issueList) {
        ParseQuery parseQuery = new ParseQuery(query);
        this.conditions = parseQuery.getQualifierAndValue();
        this.issueList = issueList;
        this.filteredIssues = filter();
    }

    private List<Issue> filter() {

        List<Issue> issues = issueList;
        conditions.forEach((qualifier, value) -> issues.removeIf(issue -> {
            switch (qualifier) {
                case "title":
                    return !issue.getTitle().toLowerCase().contains(value);
                case "comment":
                    return !commentContains(issue, value);
                case "tag":
                    return !issue.getTags().contains(value);
                case "assignee":
                    return !userExistInSet(issue.getAssignees(), value);
                case "watcher":
                    return !userExistInSet(issue.getWatchers(), value);
                case "creator":
                    User user = issue.getCreator();
                    return !user.name.contains(value) || !user.email.contains(value);
                case "desc":
                    return !issue.getDescription().toLowerCase().contains(value);
                case "status":
                    String status = issue.getStatus() ? "open" : "closed";
                    return !status.equals(value);
                case "created":
                    return !filterDate(issue.getCreationTime(), value);
                case "edited":
                    return !filterDate(issue.getEdited(), value);
                default:
                    return false;
            }
        }));

        return issues;
    }

    private boolean filterDate(long time, String value) {
        String date;
        if (value.contains("<=")) {
            date = value.replace("<=", "");
            String issueTime = Util.epochToStringDate(time);
            long dayTime = Util.StringDateToEpoch(issueTime);
            long valueTime = Util.StringDateToEpoch(date);
            return dayTime <= valueTime;

        } else if (value.contains(">=")) {
            date = value.replace(">=", "");
            String issueTime = Util.epochToStringDate(time);
            long dayTime = Util.StringDateToEpoch(issueTime);
            long valueTime = Util.StringDateToEpoch(date);
            return dayTime >= valueTime;

        } else if (value.contains("<")) {
            date = value.replace("<", "");
            String issueTime = Util.epochToStringDate(time);
            long dayTime = Util.StringDateToEpoch(issueTime);
            long valueTime = Util.StringDateToEpoch(date);
            return dayTime < valueTime;

        } else if (value.contains(">")) {
            date = value.replace(">", "");
            String issueTime = Util.epochToStringDate(time);
            long dayTime = Util.StringDateToEpoch(issueTime);
            long valueTime = Util.StringDateToEpoch(date);
            return dayTime > valueTime;
        } else {
            return Util.epochToStringDate(time).equals(value) || Util.epochToStringDateAndTime(time).equals(value);
        }

    }

    private boolean userExistInSet(Set<User> users, String value) {
        return users.stream().anyMatch(user -> user.name.toLowerCase().contains(value) || user.email.toLowerCase().contains(value));
    }

    private boolean commentContains(Issue issue, String value) {
        return issue.getComments().stream().anyMatch(comment
                -> comment.getMessage().toLowerCase().contains(value));
    }

    public List<Issue> getFilteredIssues() {
        return filteredIssues;
    }
}
