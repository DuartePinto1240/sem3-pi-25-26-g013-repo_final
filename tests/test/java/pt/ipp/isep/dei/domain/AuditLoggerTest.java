package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.AuditLogger;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class AuditLoggerTest {

    private static final String TEST_FILE = "audit_test_log.txt";

    @BeforeEach
    void cleanup() {
        new File(TEST_FILE).delete();
    }

    @Test
    void testLogCreatesFileAndAppends() throws Exception {
        AuditLogger logger = new AuditLogger(TEST_FILE);
        logger.log("returnId=RET001 | sku=SKU1 | action=Restocked | qty=5");
        logger.log("returnId=RET002 | sku=SKU2 | action=Discarded | qty=3");

        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "Log file should be created");

        String content = Files.readString(file.toPath());
        assertTrue(content.contains("RET001"));
        assertTrue(content.contains("RET002"));
    }
}
