package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QualityOperator}.
 * <p>
 * These tests verify the behavior of the QualityOperator when processing returned items
 * from quarantine, ensuring that items are correctly restocked or discarded based on
 * their conditions, and that the audit log records these operations.
 * </p>
 */
class QualityOperatorTest {

    private static final String LOG_PATH = "quality_operator_test_log.txt";

    /**
     * Cleans up the log file before each test to ensure isolation.
     */
    @BeforeEach
    void cleanup() {
        new File(LOG_PATH).delete();
    }

    /**
     * Tests that the QualityOperator correctly processes returns.
     * <ul>
     *     <li>Items eligible for restocking are added to the inventory.</li>
     *     <li>Items marked for discard are not added to the inventory.</li>
     *     <li>The audit log records both restock and discard operations.</li>
     * </ul>
     *
     * @throws Exception if file I/O fails.
     */
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
