package commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import util.Util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SelectorTest {

    @Test
    void testNoArgs() {
        CommandSelector selector = new CommandSelector(new String[]{});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandHelp);
    }

    @Test
    void testMainMenuHelp() {
        CommandSelector selector = new CommandSelector(new String[]{"-h"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandHelp);
    }

    @Test
    void testMainMenuHelp2() {
        CommandSelector selector = new CommandSelector(new String[]{"--help"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandHelp);
    }

    @Test
    void testCommandHelp() {
        CommandSelector selector = new CommandSelector(new String[]{"init", "--help"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandHelp);
    }

    @Test
    void testCommandHelp2() {
        CommandSelector selector = new CommandSelector(new String[]{"init", "-h"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandHelp);
    }

    @Test
    void invalidcreateTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"new", "-t", "title"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "new" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void createTest() {
        CommandSelector selector = new CommandSelector(new String[]{"new", "-t", "title", "-d", "desc"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandCreate);
    }

    @Test
    void initTest() {
        CommandSelector selector = new CommandSelector(new String[]{"init"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandInit);
    }

    @Test
    void invalidDeleteTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"delete"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "delete" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void DeleteTest() {
        CommandSelector selector = new CommandSelector(new String[]{"delete", "-id", "hash"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandDelete);
    }


    @Test
    void invalidLsTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"ls", "-invalid"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "ls" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void lsTestCommentCSV() {
        CommandSelector selector = new CommandSelector(new String[]{"ls", "--csv-comments", "path", "-csv", "path", "-v", "-q", "title:title"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandLs);
    }

    @Test
    void lsTestCSV() {
        CommandSelector selector = new CommandSelector(new String[]{"ls", "-csv", "path", "-v", "-q", "title:title"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandLs);
    }

    @Test
    void lsTest() {
        CommandSelector selector = new CommandSelector(new String[]{"ls", "-v", "-q", "title:title"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandLs);
    }

    @Test
    void showTest() {
        CommandSelector selector = new CommandSelector(new String[]{"show", "-id", "hash"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandShow);
    }

    @Test
    void invalidShowTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"show"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "show" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void pullTest() {
        CommandSelector selector = new CommandSelector(new String[]{"pull", "-r", "hash"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandPull);
    }

    @Test
    void invalidPullTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"pull"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "pull" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void pushTest() {
        CommandSelector selector = new CommandSelector(new String[]{"push", "-r", "hash"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandPush);
    }

    @Test
    void invalidPushTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"push"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "push" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void assignTest() {
        CommandSelector selector = new CommandSelector(new String[]{"assign", "-id", "hash", "-n", "name", "-e", "email", "-a"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandAssign);
    }

    @Test
    void invalidAssignTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"assign"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "assign" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void invalidAssignTest2() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"assign", "-id", "hash", "-n", "name", "-a"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Missing name or email"));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void watchTest() {
        CommandSelector selector = new CommandSelector(new String[]{"watch", "-id", "hash", "-n", "name", "-e", "email", "-a"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandWatcher);
    }

    @Test
    void watchTest2() {
        CommandSelector selector = new CommandSelector(new String[]{"watch", "-id", "hash", "-ls"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandWatcher);
    }

    @Test
    void watchTest3() {
        CommandSelector selector = new CommandSelector(new String[]{"watch", "-id", "hash", "-n", "name", "-e", "email", "-rm"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandWatcher);
    }

    @Test
    void invalidWatchTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"watch"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "watch" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void invalidWatchTest2() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"watch", "-id", "hash", "-n", "name", "-a"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Missing name or email"));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void attachAddTest() {
        CommandSelector selector = new CommandSelector(new String[]{"attach", "-id", "hash", "-a", "path"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandAttachment);
    }

    @Test
    void attachRmTest() {
        CommandSelector selector = new CommandSelector(new String[]{"attach", "-id", "hash", "-rm", "path"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandAttachment);
    }

    @Test
    void invalidAttachTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"attach"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "attach" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void testExecutor() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        Util.readFile("help/MainMenuHelp");
        String helpText = out.toString();
        out.reset();
        CommandExecutor executor = new CommandExecutor(new String[]{"help"});
        executor.start();
        assertEquals(helpText, out.toString());
    }

    @Test
    void commentTest() {
        CommandSelector selector = new CommandSelector(new String[]{"comment", "-id", "hash", "-m", "msg"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandComment);
    }

    @Test
    void invalidCommentTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"comment"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "comment" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void tagTest() {
        CommandSelector selector = new CommandSelector(new String[]{"tag", "-id", "hash", "-a", "tag"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandTag);
    }

    @Test
    void tagRMTest() {
        CommandSelector selector = new CommandSelector(new String[]{"tag", "-id", "hash", "-rm", "tag"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandTag);
    }

    @Test
    void invalidTagTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"tag"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "tag" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void editTitleTest() {
        CommandSelector selector = new CommandSelector(new String[]{"edit", "-id", "hash", "-t", "titl"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandEdit);
    }

    @Test
    void editDescTest() {
        CommandSelector selector = new CommandSelector(new String[]{"edit", "-id", "hash", "-d", "desc"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandEdit);
    }


    @Test
    void editStatusTest() {
        CommandSelector selector = new CommandSelector(new String[]{"edit", "-id", "hash", "-s", "close"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandEdit);
    }

    @Test
    void invalidEditTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"edit"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "edit" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void invalidTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"asdasd"});
        Command command = selector.getCommand();
        assertEquals("Unrecognized command: " + "asdasd" + System.lineSeparator(), out.toString());
        assertTrue(command instanceof CommandNull);
    }

    @Test
    void nicknameAddTest() {
        CommandSelector selector = new CommandSelector(new String[]{"nickname", "--add", "hash", "nickname"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandNickname);
    }

    @Test
    void nicknameEditTest() {
        CommandSelector selector = new CommandSelector(new String[]{"nickname", "--update", "hash", "nickname"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandNickname);
    }

    @Test
    void nicknameRmTest() {
        CommandSelector selector = new CommandSelector(new String[]{"nickname", "--remove", "hash"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandNickname);
    }

    @Test
    void nicknameListTest() {
        CommandSelector selector = new CommandSelector(new String[]{"nickname", "--list"});
        Command command = selector.getCommand();
        assertTrue(command instanceof CommandNickname);
    }

    @Test
    void invalidNicknameTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(out));
        CommandSelector selector = new CommandSelector(new String[]{"nickname"});
        Command command = selector.getCommand();
        assertTrue(out.toString().contains("Command \"" + "nickname" + "\" failed - "));
        assertTrue(command instanceof CommandNull);
    }


}
