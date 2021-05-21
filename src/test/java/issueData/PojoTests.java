package issueData;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PojoTests {

    @Test
    void testAddSingleTagToIssue() {
        Issue issue = new Issue();
        issue.addTags("one tag");
        assertEquals(1, issue.getTags().size());
    }

    @Test
    void testAddMultipleTagsToIssue() {
        Issue issue = new Issue();
        issue.addTags("one tag, two tag, three tag");
        assertEquals(3, issue.getTags().size());
    }

    @Test
    void testRemoveSingleTag() {
        Issue issue = new Issue();
        issue.addTags("one tag, two tag, three tag");
        assertEquals(3, issue.getTags().size());
        issue.removeTag("one tag");
        assertEquals(2, issue.getTags().size());
    }

    @Test
    void testRemoveMultipleTags() {
        Issue issue = new Issue();
        issue.addTags("one tag, two tag, three tag");
        assertEquals(3, issue.getTags().size());
        issue.removeTag("one tag, two tag");
        assertEquals(1, issue.getTags().size());
    }

    @Test
    void testUserUnEquals() {
        User user = new User("test", "test");
        User user1 = new User("different", "user");
        User user2 = new User();
        assertNotEquals(user, user1);
        assertNotEquals(user, user2);
    }

    @Test
    void testUserUnEquals2() {
        User user = new User("test", "test");
        Issue issue = new Issue("title1", "desc1");
        assertNotEquals(user, issue);
    }

    @Test
    void testUserUnEquals3() {
        User user = new User("test", "test");
        assertNotEquals(user, null);
    }

    @Test
    void testUserEquals() {
        User user = new User("test", "test");
        User user1 = new User("test", "test");
        assertEquals(user, user1);
    }

    @Test
    void testUserHashCode() {
        User user = new User("test", "test");
        User user1 = new User("test", "test");
        assertEquals(user.hashCode(), user1.hashCode());
    }

    @Test
    void testUserHashCode2() {
        User user = new User("test", "test");
        User user1 = new User("test", "another");
        assertNotEquals(user.hashCode(), user1.hashCode());
    }

    @Test
    void testUserEquals2() {
        User user = new User("test", "test");
        assertEquals(user, user);
    }

    @Test
    void testIssueUnequal() {
        Issue issue = new Issue("title1", "desc1");
        Issue issue1 = new Issue("title2", "desc2");
        assertNotEquals(issue, issue1);
    }

    @Test
    void testIssueUnequal2() {
        Issue issue = new Issue("title1", "desc1");
        User user = new User("test", "test");
        assertNotEquals(issue, user);
    }

    @Test
    void testIssueUnequal3() {
        Issue issue = new Issue("title1", "desc1");
        assertNotEquals(issue, null);
    }

    @Test
    void testIssueEquals() {
        String title = "title";
        String desc = "desc";
        long time = System.currentTimeMillis();

        String hash = DigestUtils.sha256Hex(title + desc + time);

        Issue issue = new Issue();
        issue.setHash(hash);

        Issue issue1 = new Issue();
        issue1.setHash(hash);

        assertEquals(issue, issue1);

    }


    @Test
    void testIssueEquals2() {
        String title = "title";
        String desc = "desc";
        long time = System.currentTimeMillis();
        String hash = DigestUtils.sha256Hex(title + desc + time);
        Issue issue = new Issue();
        issue.setHash(hash);
        assertEquals(issue, issue);
    }

    @Test
    void testIssueToString() {
        String title = "title";
        String desc = "desc";
        long time = System.currentTimeMillis();

        String hash = DigestUtils.sha256Hex(title + desc + time);

        Issue issue = new Issue();
        issue.setHash(hash);

        Issue issue1 = new Issue();
        issue1.setHash(hash);

        assertEquals(issue.toString(), issue1.toString());

    }

    @Test
    void testIssueToString2() {
        String title = "title";
        String desc = "desc";
        long time = System.currentTimeMillis();

        String hash = DigestUtils.sha256Hex(title + desc + time);

        Issue issue = new Issue();
        issue.setHash(hash);

        Issue issue1 = new Issue();
        issue1.setHash("different");

        assertNotEquals(issue.toString(), issue1.toString());

    }


    @Test
    void testIssueHashCode() {
        String title = "title";
        String desc = "desc";
        long time = System.currentTimeMillis();

        String hash = DigestUtils.sha256Hex(title + desc + time);

        Issue issue = new Issue();
        issue.setHash(hash);

        Issue issue1 = new Issue();
        issue1.setHash(hash);

        assertEquals(issue.hashCode(), issue1.hashCode());

    }

    @Test
    void testIssueHashCode2() {
        String title = "title";
        String desc = "desc";
        long time = System.currentTimeMillis();

        String hash = DigestUtils.sha256Hex(title + desc + time);

        Issue issue = new Issue();
        issue.setHash(hash);

        Issue issue1 = new Issue();
        issue1.setHash("different");

        assertNotEquals(issue.hashCode(), issue1.hashCode());

    }


    @Test
    void testCommentUnequal() {
        User author = new User("test", "test");
        Comment comment = new Comment(author, "Message1");
        Comment comment1 = new Comment(author, "Message2");
        assertNotEquals(comment, comment1);
    }

    @Test
    void testCommentUnequal2() {
        User author = new User("test", "test");
        Comment comment = new Comment(author, "Message1");
        Issue issue = new Issue("title1", "desc1");
        assertNotEquals(comment, issue);
    }

    @Test
    void testCommentUnequal3() {
        User author = new User("test", "test");
        Comment comment = new Comment(author, "Message1");
        assertNotEquals(comment, null);
    }


    @Test
    void testCommentEquals() {
        User author = new User("test", "test");
        String message = "message";
        long time = System.currentTimeMillis();
        String hash = DigestUtils.sha256Hex(author + message + time);
        Comment comment = new Comment();
        comment.setHash(hash);
        Comment comment1 = new Comment();
        comment1.setHash(hash);
        assertEquals(comment, comment1);
    }

    @Test
    void testCommentEquals2() {
        User author = new User("test", "test");
        String message = "message";
        long time = System.currentTimeMillis();
        String hash = DigestUtils.sha256Hex(author + message + time);
        Comment comment = new Comment();
        comment.setHash(hash);
        assertEquals(comment, comment);
    }

    @Test
    void testCommentHashcode() {
        User author = new User("test", "test");
        String message = "message";
        long time = System.currentTimeMillis();
        String hash = DigestUtils.sha256Hex(author + message + time);
        Comment comment = new Comment();
        comment.setHash(hash);
        assertEquals(comment.hashCode(), comment.hashCode());
    }

    @Test
    void testCommentHashcode2() {
        User author = new User("test", "test");
        String message = "message";
        long time = System.currentTimeMillis();
        String hash = DigestUtils.sha256Hex(author + message + time);
        Comment comment = new Comment();
        comment.setHash(hash);
        Comment comment1 = new Comment();
        comment1.setHash("different");
        assertNotEquals(comment.hashCode(), comment1.hashCode());
    }

}
