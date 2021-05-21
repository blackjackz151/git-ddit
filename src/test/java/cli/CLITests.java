package cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CLITests {
    @Test
    void newCLIValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("new");
        CommandLine input = cli.getInput(new String[]{"-t", "title", "-d", "desc"});
        assertTrue(input.hasOption("title") && input.hasOption("desc"));
    }

    @Test
    void newCLIInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("new");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-t", "title"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-d", "desc"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-d", "-t"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }


    @Test
    void deleteCLIValidOptionsTests() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("delete");
        CommandLine input = cli.getInput(new String[]{"-id", "hash"});
        assertTrue(input.hasOption("id"));
    }

    @Test
    void deleteCLIInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("delete");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void lsValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("ls");
        CommandLine input = cli.getInput(new String[]{});
        assertEquals(0, input.getOptions().length);
    }

    @Test
    void lsValidOptionsTest2() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("ls");
        CommandLine input = cli.getInput(new String[]{"-v"});
        assertTrue(input.hasOption("verbose"));
    }

    @Test
    void lsValidOptionsTest3() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("ls");
        CommandLine input = cli.getInput(new String[]{"-csv", "path"});
        assertTrue(input.hasOption("csv") && input.getOptionValue("csv").equals("path"));
    }

    @Test
    void lsValidOptionsTest4() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("ls");
        CommandLine input = cli.getInput(new String[]{"--csv-comments", "path"});
        assertTrue(input.hasOption("csv-comments") && input.getOptionValue("csv-comments").equals("path"));
    }

    @Test
    void lsValidOptionsTest5() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("ls");
        CommandLine input = cli.getInput(new String[]{"-csv", "path", "--query", "title:hello", "desc:world"});
        assertTrue(input.hasOption("csv") && input.getOptionValue("csv").equals("path"));
        assertTrue(input.hasOption("query") && input.getOptionValues("query").length == 2);
    }

    @Test
    void lsValidOptionsTest6() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("ls");
        CommandLine input = cli.getInput(new String[]{"-csvc", "path", "--query", "title:hello", "desc:world"});
        assertTrue(input.hasOption("csvc") && input.getOptionValue("csvc").equals("path"));
        assertTrue(input.hasOption("query") && input.getOptionValues("query").length == 2);
    }

    @Test
    void lsValidOptionsTest7() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("ls");
        CommandLine input = cli.getInput(new String[]{"--query", "title:hello", "desc:world"});
        assertTrue(input.hasOption("query") && input.getOptionValues("query").length == 2);
    }

    @Test
    void lsInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("ls");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-csv"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-csvc"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void showValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("show");
        CommandLine input = cli.getInput(new String[]{"-id", "hash"});
        assertTrue(input.hasOption("id"));
    }

    @Test
    void showInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("show");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void pullValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("pull");
        CommandLine input = cli.getInput(new String[]{"-r", "path"});
        assertTrue(input.hasOption("remote"));
    }

    @Test
    void pullInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("pull");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void pushValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("push");
        CommandLine input = cli.getInput(new String[]{"-r", "path"});
        assertTrue(input.hasOption("remote"));
    }

    @Test
    void pushInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("push");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void assignValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("assign");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "-ls"});
        assertTrue(input.hasOption("ls") && input.hasOption("id"));
    }

    @Test
    void assignValidOptionsTest2() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("assign");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--add", "--name", "name", "--email", "email"});
        assertTrue(input.hasOption("add") && input.hasOption("id") && input.hasOption("name") && input.hasOption("email"));
    }

    @Test
    void assignValidOptionsTest3() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("assign");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--remove", "--name", "name", "--email", "email"});
        assertTrue(input.hasOption("remove") && input.hasOption("id") && input.hasOption("name") && input.hasOption("email"));
    }

    @Test
    void assignInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("assign");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--name"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--email"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--email", "--name"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--remove"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void watchValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("watch");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "-ls"});
        assertTrue(input.hasOption("ls") && input.hasOption("id"));
    }

    @Test
    void watchValidOptionsTest2() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("watch");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--add", "--name", "name", "--email", "email"});
        assertTrue(input.hasOption("add") && input.hasOption("id") && input.hasOption("name") && input.hasOption("email"));
    }

    @Test
    void watchValidOptionsTest3() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("watch");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--remove", "--name", "name", "--email", "email"});
        assertTrue(input.hasOption("remove") && input.hasOption("id") && input.hasOption("name") && input.hasOption("email"));
    }

    @Test
    void watchInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("watch");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--name"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--email"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--email", "--name"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--remove"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }


    @Test
    void attachValidOptionTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("attach");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--add", "path"});
        assertTrue(input.hasOption("add") && input.hasOption("id"));
    }

    @Test
    void attachValidOptionTest2() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("attach");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--remove", "path"});
        assertTrue(input.hasOption("remove") && input.hasOption("id"));
    }

    @Test
    void attachInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("attach");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--remove"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--remove"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void commentValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("comment");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--message", "message"});
        assertTrue(input.hasOption("message") && input.hasOption("id"));
    }

    @Test
    void commentInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("comment");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--message"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void tagValidOptionTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("tag");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--add", "tag"});
        assertTrue(input.hasOption("add") && input.hasOption("id"));
    }

    @Test
    void tagValidOptionTest2() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("tag");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--remove", "tag"});
        assertTrue(input.hasOption("remove") && input.hasOption("id"));
    }

    @Test
    void tagInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("tag");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--add", "--remove"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--remove"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void initValidOptionTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("init");
        CommandLine input = cli.getInput(new String[]{});
        assertEquals(0, input.getOptions().length);
    }

    @Test
    void initInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("init");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void invalidCommandTest() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        CommandLineInterface cli = new CommandLineInterface("invalid");
        assertEquals("Unrecognized command: invalid" + System.lineSeparator(), out.toString());
    }

    @Test
    void editInvalidTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("edit");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--title", "title"});
        assertTrue(input.hasOption("title") && input.hasOption("id"));
    }

    @Test
    void editInvalidTest2() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("edit");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--desc", "desc"});
        assertTrue(input.hasOption("desc") && input.hasOption("id"));
    }

    @Test
    void editInvalidTest3() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("edit");
        CommandLine input = cli.getInput(new String[]{"-id", "hash", "--status", "status"});
        assertTrue(input.hasOption("status") && input.hasOption("id"));
    }

    @Test
    void editInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("tag");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--title"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--title", "--status"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--title", "title", "--status", "close"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--status"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-id", "hash", "--desc"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }

    @Test
    void nicknameValidOptionsTest() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("nickname");
        CommandLine input = cli.getInput(new String[]{"--add", "hash", "nickname"});
        assertTrue(input.hasOption("add") && input.getOptionValues("add").length == 2);
    }

    @Test
    void nicknameValidOptionsTest2() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("nickname");
        CommandLine input = cli.getInput(new String[]{"--update", "hash", "nickname"});
        assertTrue(input.hasOption("update") && input.getOptionValues("update").length == 2);
    }

    @Test
    void nicknameValidOptionsTest3() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("nickname");
        CommandLine input = cli.getInput(new String[]{"--remove", "hash"});
        assertTrue(input.hasOption("remove"));
    }

    @Test
    void nicknameValidOptionsTest4() throws ParseException {
        CommandLineInterface cli = new CommandLineInterface("nickname");
        CommandLine input = cli.getInput(new String[]{"--list"});
        assertTrue(input.hasOption("list"));
    }

    @Test
    void nicknameInvalidOptionsTest() {
        CommandLineInterface cli = new CommandLineInterface("nickname");
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"--add", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"--update", "hash"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"--remove"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"--remove", "--add"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"--remove hash sadasi", "--add hash asdjh", "--list"}));
        assertThrows(ParseException.class, () -> cli.getInput(new String[]{"-fake", "-args"}));
    }


}
