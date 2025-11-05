package main.java.pt.ipp.isep.dei.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading data from CSV files.
 * This class provides a static method to read a CSV file and return its content
 * as a list of string arrays, where each array represents a row.
 */
public class CsvReaderUtil {

    /**
     * Reads a CSV file from a given path, skipping the header row.
     * <p>
     * This method reads all lines from the specified file, splits each line by commas
     * to get individual values, and returns a list of string arrays. Empty lines are ignored.
     *
     * @param filePath The path to the CSV file to be read.
     * @return A {@link List} of {@code String[]} where each array represents a row
     * of the CSV file. Returns an empty list if the file is empty (besides the header).
     * @throws RuntimeException if the file is not found at the specified path or if an
     * I/O error occurs during the reading process.
     */
    public static List<String[]> readCsvFile(String filePath) {
        List<String[]> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            // Pular o cabeçalho (header) do CSV
            br.readLine();

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] values = line.split(",");
                    records.add(values);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Ficheiro não encontrado no caminho especificado: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler o ficheiro CSV: " + filePath, e);
        }

        return records;
    }
}