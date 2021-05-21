package queries;

import issueData.Comment;
import issueData.Issue;
import issueData.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilterIssueTests {
    private List<Issue> issues;

    @BeforeEach
    void setUp() {
        this.issues = new ArrayList<>();

        long epoch1 = 1551143970402L; //"26/02/2019 01:19:30";
        long epoch2 = 1551052800000L; //25/02/2019
        long epoch3 = 1551398400000L; //01/03/2019

        Issue issue1 = new Issue("issue1", "desc1");
        Issue issue2 = new Issue("issue2", "desc2");
        Issue issue3 = new Issue("issue3", "desc3");

        issue1.setCreationTime(epoch1);
        issue2.setCreationTime(epoch2);
        issue3.setCreationTime(epoch3);

        issue1.setEdited(epoch1);
        issue2.setEdited(epoch2);
        issue3.setEdited(epoch3);

        User user1 = new User("user1", "user1@email.com");
        User user2 = new User("user2", "user2@email.com");
        User user3 = new User("user3", "user3@email.com");

        issue1.setCreator(user1);
        issue2.setCreator(user2);
        issue3.setCreator(user3);

        issue1.addTags("issue1, cat, dog");
        issue2.addTags("issue2, cat, dog");
        issue3.addTags("issue3, cat, dog");

        issue1.addAssignee(user3);
        issue1.addWatcher(user2);

        issue2.addAssignee(user1);
        issue2.addWatcher(user3);

        issue3.addAssignee(user2);
        issue3.addWatcher(user1);

        issue2.setStatus(false);

        issue1.addComment(new Comment(user1, "comment1"));
        issue1.addComment(new Comment(user1, "onall"));
        issue2.addComment(new Comment(user2, "comment2"));
        issue2.addComment(new Comment(user1, "onall"));
        issue3.addComment(new Comment(user3, "comment3"));
        issue3.addComment(new Comment(user1, "onall"));

        issues.add(issue1);
        issues.add(issue2);
        issues.add(issue3);

    }

    @Test
    void testTitleFilter() {
        String[] query = new String[]{"title:issue"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testTitleFilter2() {
        String[] query = new String[]{"title:issue2"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testTitleFilter3() {
        String[] query = new String[]{"title:issue2", "title:issue3"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testDescFilter1() {
        String[] query = new String[]{"desc:desc"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testDescFilter2() {
        String[] query = new String[]{"desc:desc2"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testDescFilter3() {
        String[] query = new String[]{"desc:desc2", "desc:desc3"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void invalidQualifierTest() {
        String[] query = new String[]{"invalid:qualifier"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testStatusFilter1() {
        String[] query = new String[]{"status:open"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(2, filteredIssues.size());
    }

    @Test
    void testStatusFilter2() {
        String[] query = new String[]{"status:closed"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testCommentFilter1() {
        String[] query = new String[]{"comment:onall"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testCommentFilter2() {
        String[] query = new String[]{"comment:comment"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testCommentFilter3() {
        String[] query = new String[]{"comment:comment1"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testCommentFilter4() {
        String[] query = new String[]{"comment:comment1", "comment:comment2"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testTagFilter1() {
        String[] query = new String[]{"tag:cat"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testTagFilter2() {
        String[] query = new String[]{"tag:issue1"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testAssigneeFilter1() {
        String[] query = new String[]{"assignee:user"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testAssigneeFilter2() {
        String[] query = new String[]{"assignee:user1"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testAssigneeFilter3() {
        String[] query = new String[]{"assignee:email.com"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }


    @Test
    void testWatcherFilter1() {
        String[] query = new String[]{"watcher:user"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testWatcherFilter2() {
        String[] query = new String[]{"watcher:user1"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testWatcherFilter3() {
        String[] query = new String[]{"watcher:email.com"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testCreatorFilter1() {
        String[] query = new String[]{"creator:user"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testCreatorFilter2() {
        String[] query = new String[]{"creator:user1"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testCreatedFilter1() {
        String[] query = new String[]{"created:25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testCreatedFilter2() {
        String[] query = new String[]{"created:>25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(2, filteredIssues.size());
    }

    @Test
    void testCreatedFilter3() {
        String[] query = new String[]{"created:>=25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testCreatedFilter4() {
        String[] query = new String[]{"created:<25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(0, filteredIssues.size());
    }

    @Test
    void testCreatedFilter5() {
        String[] query = new String[]{"created:<=25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testEditedFilter1() {
        String[] query = new String[]{"edited:25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testEditedFilter2() {
        String[] query = new String[]{"edited:>25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(2, filteredIssues.size());
    }

    @Test
    void testEditedFilter3() {
        String[] query = new String[]{"edited:>=25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

    @Test
    void testEditedFilter4() {
        String[] query = new String[]{"edited:<25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(0, filteredIssues.size());
    }

    @Test
    void testEditedFilter5() {
        String[] query = new String[]{"edited:<=25/02/2019"};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(1, filteredIssues.size());
    }

    @Test
    void testNoQuery() {
        String[] query = new String[]{};
        FilterIssues issueFilter = new FilterIssues(query, issues);
        List<Issue> filteredIssues = issueFilter.getFilteredIssues();
        assertEquals(3, filteredIssues.size());
    }

}
