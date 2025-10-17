package main.java.pt.ipp.isep.dei.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderUtil {

    /**
     * Lê todas as linhas de um ficheiro CSV do classpath (ex: pasta 'res').
     * @param filePath O caminho para o ficheiro a partir da raiz do classpath (ex: "/stations.csv").
     * @return Uma lista de arrays de String, onde cada array representa uma linha dividida por vírgulas.
     */
    public static List<String[]> readCsvFile(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (InputStream is = CsvReaderUtil.class.getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IOException("Ficheiro não encontrado no classpath: " + filePath);
            }
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {

                String line;
                // Pular o cabeçalho (header) do CSV
                br.readLine();

                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] values = line.split(",");
                        records.add(values);
                    }
                }
            }
        } catch (IOException e) {
            // Lança uma exceção para que a camada superior possa lidar com o erro.
            throw new RuntimeException("Falha ao ler o ficheiro CSV: " + filePath, e);
        }
        return records;
    }
}