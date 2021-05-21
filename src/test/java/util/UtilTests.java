package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UtilTests {

    @Test
    void epochToStringDateTest() {
        long epoch = 1551143970402L;
        String timeString = "26/02/2019";
        assertEquals(Util.epochToStringDate(epoch), timeString, "Date not converted properly");
    }

    @Test
    void epochToStringDateAndTimeTest() {
        long epoch = 1551143970402L;
        String timeString = "26/02/2019 01:19:30";
        assertEquals(Util.epochToStringDateAndTime(epoch), timeString, "Date not converted properly");
    }

    @Test
    void eStringDateToEpochTest() {
        long epoch = 1551139200L;
        String timeString = "26/02/2019";
        assertEquals(Util.StringDateToEpoch(timeString), epoch, "Date not converted properly");
    }

//    @Test
//    void testReadFile() {
//        String string = "Test string";
//        File file = new File("/home/jack/Documents/ddit/src/test/resources/test.txt");
//        try {
//            FileUtils.writeStringToFile(file, string, "UTF-8");
//
//            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
//            System.setOut(new java.io.PrintStream(out));
//            Util.readFile(file.getPath());
//
//            assertEquals(string + System.lineSeparator(), out.toString());
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            FileUtils.deleteQuietly(file);
//        }
//    }

    @Test
    void testDeleteDirectory() {
        File file = new File("/home/jack/Documents/ddit/src/test/resources/test");
        try {
            Files.createDirectories(file.toPath());

            assertTrue(file.exists());

            Util.deleteDirectory(file.getPath());

            assertFalse(file.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInvalidReadFile() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(out));
        assertThrows(RuntimeException.class, () -> Util.readFile("Invalid"));
    }


}
