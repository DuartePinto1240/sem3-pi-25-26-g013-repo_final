package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.AuditLogger;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AuditLogger} class.
 * <p>
 * Verifies the creation of the log file and the correct appending of log messages.
 * </p>
 */
class AuditLoggerTest {

    private static final String TEST_FILE = "audit_test_log.txt";

    /**
     * Ensures that the test file is deleted before each test execution,
     * preventing interference between test runs.
     */
    @BeforeEach
    void cleanup() {
        new File(TEST_FILE).delete();
    }

    /**
     * Tests that the {@link AuditLogger#log(String)} method creates the log file
     * if it does not exist and correctly appends multiple log entries to it.
     *
     * @throws Exception if an error occurs while reading the file
     */
    @Test
    void testLogCreatesFileAndAppends() throws Exception {
        AuditLogger logger = new AuditLogger(TEST_FILE);
        logger.log("returnId=RET001 | sku=SKU1 | action=Restocked | qty=5");
        logger.log("returnId=RET002 | sku=SKU2 | action=Discarded | qty=3");

        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "Log file should be created");

        String content = Files.readString(file.toPath());
        assertTrue(content.contains("RET001"), "Log should contain the first entry");
        assertTrue(content.contains("RET002"), "Log should contain the second entry");
    }
}
