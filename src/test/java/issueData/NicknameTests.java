package issueData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class NicknameTests {

    private IssueNicknames nicknames;

    //    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
//            System.setOut(new java.io.PrintStream(out));
    @BeforeEach
    void setUp() {
        this.nicknames = new IssueNicknames(new HashMap<>());
    }

    @Test
    void addValidNickname() {
        assertTrue(nicknames.addNickname("123", "nickname"));
    }

    @Test
    void addInvalidNicknameHashUsed() {
        nicknames.addNickname("123", "nickname");
        assertFalse(nicknames.addNickname("123", "another"));
    }

    @Test
    void addInvalidNicknameNicknameUsed() {
        nicknames.addNickname("123", "nickname");
        assertFalse(nicknames.addNickname("aaa", "nickname"));
    }

    @Test
    void addInvalidNicknameNicknameIsHashInUse() {
        nicknames.addNickname("123", "nickname");
        assertFalse(nicknames.addNickname("aaa", "123"));
    }

    @Test
    void addInvalidNicknameHashIsANicknameInUse() {
        nicknames.addNickname("123", "nickname");
        assertFalse(nicknames.addNickname("nickname", "test"));
    }


    @Test
    void addInvalidNicknameHashUsedOut() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        nicknames.addNickname("123", "nickname");
        nicknames.addNickname("123", "another");
        assertEquals("Hash already has a nickname" + System.lineSeparator(), out.toString());
    }

    @Test
    void addInvalidNicknameNicknameUsedOut() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        nicknames.addNickname("123", "nickname");
        nicknames.addNickname("aaa", "nickname");
        assertEquals("Nickname already exists" + System.lineSeparator(), out.toString());
    }

    @Test
    void addInvalidNicknameNicknameIsHashInUseOut() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        nicknames.addNickname("123", "nickname");
        nicknames.addNickname("aaa", "123");
        assertEquals("This nickname is an existing hash, choose another" + System.lineSeparator(), out.toString());
    }

    @Test
    void addInvalidNicknameHashIsANicknameInUseOut() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        nicknames.addNickname("123", "nickname");
        nicknames.addNickname("nickname", "test");
        assertEquals("This hash/nickname is already in use as a nickname" + System.lineSeparator(), out.toString());
    }


    @Test
    void removeValidNickname() {
        nicknames.addNickname("123", "nickname");
        assertTrue(nicknames.removeNickname("123"));
    }

    @Test
    void removeInvalidNickname() {
        nicknames.addNickname("123", "nickname");
        assertFalse(nicknames.removeNickname("invalid"));
    }

    @Test
    void updateValidNickname() {
        nicknames.addNickname("123", "nickname");
        assertTrue(nicknames.updateNickname("123", "newnickname"));
    }

    @Test
    void updateInvalidNickname() {
        assertFalse(nicknames.updateNickname("non-existing", "newnickname"));
    }

    @Test
    void updateInvalidNicknameAlreadyInUse() {
        nicknames.addNickname("123", "nickname");
        nicknames.addNickname("321", "another");
        assertFalse(nicknames.updateNickname("123", "another"));
    }

    @Test
    void updateInvalidNicknameAlreadyInUseOut() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        nicknames.addNickname("123", "nickname");
        nicknames.addNickname("321", "another");
        nicknames.updateNickname("123", "another");
        assertEquals("This hash/nickname is already in use as a nickname" + System.lineSeparator(), out.toString());
    }

    @Test
    void testGetter() {
        assertNotNull(nicknames.getIssueNicknames());
    }


}
