package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class QualityOperatorTest {

    private static final String LOG_PATH = "quality_operator_test_log.txt";

    @BeforeEach
    void cleanup() {
        new File(LOG_PATH).delete();
    }

    @Test
    void testProcessReturnsRestocksAndDiscardsCorrectly() throws Exception {
        Quarantine quarantine = new Quarantine();
        Inventory inventory = new Inventory();
        AuditLogger logger = new AuditLogger(LOG_PATH);
        QualityOperator operator = new QualityOperator(quarantine, inventory, logger);

        // Restock case
        quarantine.add(new ReturnItem("RET001", "SKU1", 10, "customer-remorse",
                LocalDateTime.parse("2025-09-22T10:00:00"), null));

        // Discard case
        quarantine.add(new ReturnItem("RET002", "SKU2", 5, "damaged",
                LocalDateTime.parse("2025-09-20T10:00:00"), null));

        operator.processReturns();

        // Assertions
        assertTrue(inventory.containsSku("SKU1"), "Restocked SKU should be in inventory");
        assertFalse(inventory.containsSku("SKU2"), "Discarded SKU should not be in inventory");

        // Log file verification
        String content = Files.readString(new File(LOG_PATH).toPath());
        assertTrue(content.contains("RET001"));
        assertTrue(content.contains("RET002"));
        assertTrue(content.contains("Restocked"));
        assertTrue(content.contains("Discarded"));
    }
}
