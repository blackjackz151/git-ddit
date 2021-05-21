package csv;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import issueData.Issue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class IssuesToCSV implements CSVWriter {
    private final List<Issue> issues;

    public IssuesToCSV(List<Issue> issues) {
        this.issues = issues;
    }

    public String write(String location) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        Path filePath = Paths.get(location);
        String csvFile = filePath.toString() + "/issues-" + System.currentTimeMillis() + ".csv";
        Writer writer = new FileWriter(csvFile);

        CustomMappingStrategy<Issue> strategy = new CustomMappingStrategy<>();
        strategy.setType(Issue.class);
        strategy.setColumnMapping("HASH", "STATUS", "TITLE", "DESCRIPTION", "CREATOR", "CREATIONTIME", "EDITED", "TAGS", "ASSIGNEES", "WATCHERS");

        StatefulBeanToCsv<Issue> beanWriter = new StatefulBeanToCsvBuilder<Issue>(writer).withMappingStrategy(strategy).build();
        beanWriter.write(issues);

        writer.close();

        return csvFile;
    }


    private static class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
        private static final String[] HEADER = new String[]{"HASH", "STATUS", "TITLE", "DESCRIPTION", "CREATOR", "CREATIONTIME", "EDITED", "TAGS", "ASSIGNEES", "WATCHERS"};

        @Override
        public String[] generateHeader(T bean) {
            return HEADER;
        }
    }

}