package commands;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelpTest {

    @Test
    void testMainMenu() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("help");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/MainMenuHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testInit() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("init");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/InitHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testNew() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("new");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/NewHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testDelete() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("delete");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/DeleteHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testLs() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("ls");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/LsHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testShow() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("show");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/ShowHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testPull() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("pull");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/PullHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testPush() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("push");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/PushHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testAssign() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("assign");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/AssignHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testAttach() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("attach");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/AttachHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testComment() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("comment");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/CommentHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testTag() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("tag");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/TagHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testEdit() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("edit");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/EditHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testWatch() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("watch");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/WatchHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

    @Test
    void testNickname() throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        Command command = new CommandHelp("nickname");
        command.execute();
        assertEquals(FileUtils.readFileToString(new File("src/main/resources/help/NicknameHelp"), "UTF-8") + System.lineSeparator(), out.toString());
    }

}
