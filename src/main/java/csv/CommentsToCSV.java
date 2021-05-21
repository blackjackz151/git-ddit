package csv;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import issueData.Comment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CommentsToCSV implements CSVWriter {
    private final List<Comment> comments;

    public CommentsToCSV(List<Comment> comments) {
        this.comments = comments;
    }

    public String write(String location) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        Path filePath = Paths.get(location);
        String csvFile = filePath.toString() + "/comments-" + System.currentTimeMillis() + ".csv";
        Writer writer = new FileWriter(csvFile);

        CustomMappingStrategy<Comment> strategy = new CustomMappingStrategy<>();
        strategy.setType(Comment.class);
        strategy.setColumnMapping("HASH", "AUTHOR", "MESSAGE", "CREATIONTIME");

        StatefulBeanToCsv<Comment> beanWriter = new StatefulBeanToCsvBuilder<Comment>(writer).withMappingStrategy(strategy).build();
        beanWriter.write(comments);

        writer.close();

        return csvFile;
    }


    private static class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
        private static final String[] HEADER = new String[]{"HASH", "AUTHOR", "MESSAGE", "CREATIONTIME"};

        @Override
        public String[] generateHeader(T bean) {
            return HEADER;
        }
    }

}
