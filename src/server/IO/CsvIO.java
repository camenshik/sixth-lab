package server.IO;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class CsvIO {
    private CSVWriter csvWriter;
    private CSVReader csvReader;
    private String nameFile;

    public CsvIO(String nameFile) {
        this.nameFile = nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    private void setCsvWriter() throws IOException {
        this.csvWriter = new CSVWriter(
                new FileWriter(Paths.get(System.getProperty("user.dir"), nameFile).toFile())
        );
    }

    private void setCsvReader() throws IOException {
        this.csvReader = new CSVReader(
                new FileReader(Paths.get(System.getProperty("user.dir"), nameFile).toFile())
        );
    }

    public void writeAll(List<String[]> data) throws IOException {
        this.setCsvWriter();
        this.csvWriter.writeAll(data);
        this.csvWriter.flush();
        this.csvWriter.close();
    }

    public List<String[]> readAll() throws IOException, CsvException {
        this.setCsvReader();
        List<String[]> result = this.csvReader.readAll();
        this.csvReader.close();
        return result;
    }
}
