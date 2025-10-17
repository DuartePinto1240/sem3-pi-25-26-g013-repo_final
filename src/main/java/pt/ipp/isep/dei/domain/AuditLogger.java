package main.java.pt.ipp.isep.dei.domain;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {

    private final String filePath;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AuditLogger(String filePath) {
        this.filePath = filePath;
    }

    public synchronized void log(String message) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(LocalDateTime.now().format(FORMATTER) + " | " + message + System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write log: " + e.getMessage(), e);
        }
    }
}

