package main.java.pt.ipp.isep.dei.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderUtil {

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