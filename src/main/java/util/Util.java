package util;

import commands.CommandHelp;
import issueData.Issue;
import issueData.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Stateless Utility class
 */
public final class Util {

    private Util() {
    }

    public static String epochToStringDate(long epoch) {
        ZonedDateTime dateTime = Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String epochToStringDateAndTime(long epoch) {
        ZonedDateTime dateTime = Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public static long StringDateToEpoch(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(time, formatter);
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    public static void readFile(String path) {
        ClassLoader classLoader = CommandHelp.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(path);

        if (stream == null) {
            throw new RuntimeException("Cannot find file " + path);
        }

        try {
            System.out.println(IOUtils.toString(stream, "UTF-8"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public static void deleteDirectory(String path) {
        if (path != null) {
            try {
                FileUtils.forceDelete(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printIssue(Issue issue) {
        String status = issue.getStatus() ? "Open" : "Closed";
        User creator = issue.getCreator();

        System.out.println("Hash: " + issue.getHash());
        System.out.println("Issue title: " + issue.getTitle() + "\n" +
                "Issue Description: " + issue.getDescription() + "\n" +
                "Status: " + status + "\n" +
                "Creator: " + "name: " + creator.name + " email: " + creator.email + "\n" +
                "Last edited: " + Util.epochToStringDateAndTime(issue.getEdited()) + "\n" +
                "Creation time: " + Util.epochToStringDateAndTime(issue.getCreationTime()) + "\n" +
                "Tags: ");

        issue.getTags().forEach(System.out::println);

        System.out.println("Watchers: ");
        issue.getWatchers().forEach(user -> System.out.println("User Name: " + user.name + " Email: " + user.email));
        System.out.println();

        System.out.println("Assignees: ");
        issue.getAssignees().forEach(user -> System.out.println("User Name: " + user.name + " Email: " + user.email));
        System.out.println();

        System.out.println("Attachments: ");
        issue.getAttachments().keySet().forEach(System.out::println);
        System.out.println();
    }

    public static String getUserName(Repository repository) {
        String name = repository.getConfig().getString("user", null, "name");
        return Objects.requireNonNullElse(name, "Username unknown ");
    }

    public static String getUserEmail(Repository repository) {
        String email = repository.getConfig().getString("user", null, "email");
        return Objects.requireNonNullElse(email, "Email unknown");
    }

}
