package main.java.pt.ipp.isep.dei.domain;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Responsible for recording events or actions to an audit log file.
 *
 * <p>The {@code AuditLogger} allows the creation of chronological entries with timestamps
 * to track relevant events, such as user operations, system failures,
 * or other critical actions.</p>
 *
 * <p>Each log line contains a timestamp in the format {@code yyyy-MM-dd HH:mm},
 * followed by a descriptive message.</p>
 */
public class AuditLogger {

    /** Path to the file where logs will be stored. */
    private final String filePath;

    /**
     * Constructs an {@code AuditLogger} that writes to the specified file.
     * The existing file, if present, is preserved (append mode).
     *
     * @param filePath full path to the log file
     */
    public AuditLogger(String filePath) {
        this(filePath, false);
    }

    /**
     * Constructs an {@code AuditLogger} with the option to overwrite the existing file.
     *
     * @param filePath  full path to the log file
     * @param overwrite if {@code true}, the file is cleared on startup;
     *                  if {@code false}, new entries are appended to the existing file
     */
    public AuditLogger(String filePath, boolean overwrite) {
        this.filePath = filePath;
        if (overwrite) clearFile();
    }

    /**
     * Clears the content of the log file if it exists.
     * This method is automatically called only if the {@code overwrite} option
     * is enabled in the constructor.
     */
    private void clearFile() {
        try (FileWriter fw = new FileWriter(filePath, false)) {
            // just opens and closes — clears existing content
        } catch (IOException e) {
            System.err.println("⚠️ Could not clear audit log: " + e.getMessage());
        }
    }

    /**
     * Records a new entry in the log file with the current date and time.
     *
     * @param message message to be recorded in the log
     */
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
