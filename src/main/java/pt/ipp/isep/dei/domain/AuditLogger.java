package main.java.pt.ipp.isep.dei.domain;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {

    private final String filePath;

    public AuditLogger(String filePath) {
        this(filePath, false);
    }

    public AuditLogger(String filePath, boolean overwrite) {
        this.filePath = filePath;
        if (overwrite) clearFile();
    }

    private void clearFile() {
        try (FileWriter fw = new FileWriter(filePath, false)) {
            // just open and close — clears file
        } catch (IOException e) {
            System.err.println("⚠️ Could not clear audit log: " + e.getMessage());
        }
    }

    public void log(String message) {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            fw.write(timestamp + " | " + message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("❌ Error writing to audit log: " + e.getMessage());
        }
    }
}
