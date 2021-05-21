package csv;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;

public interface CSVWriter {
    String write(String location) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException;
}
