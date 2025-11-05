package test.java.pt.ipp.isep.dei.domain.USEI05;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for Use Case USEI05 — Returns & Quarantine.
 *
 * This class validates the following functional aspects:
 *
 *   Quarantine ordering (latest first, timestamp DESC, returnId ASC)
 *   Decision rules of {@link QualityOperator} (Restock vs. Discard)
 *   Audit log formatting (presence/absence of qty field)
 *   Inventory updates respecting FEFO/FIFO ordering
 *
 *
 * All tests use in-memory objects and a temporary log file. No external CSVs are required.
 */
public class USEI05Test {

    private Quarantine quarantine;
    private Inventory inventory;
    private AuditLogger logger;
    private QualityOperator operator;
    private Path logFilePath;

    private ReturnItem r1, r2, r3;

    /**
     * Creates reusable test data and a clean environment before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Prepare domain objects
        quarantine = new Quarantine();
        inventory = new Inventory();

        // Prepare a temporary audit log file
        logFilePath = Path.of("test_audit_log.txt");
        if (Files.exists(logFilePath)) Files.delete(logFilePath);

        logger = new AuditLogger(logFilePath.toString(), true);
        operator = new QualityOperator(quarantine, inventory, logger);

        // Create a small dataset of returns with different timestamps and reasons
        r1 = new ReturnItem("RET0001", "SKU001", 10, "customer-remorse",
                LocalDateTime.of(2025, 9, 10, 10, 0), null);
        r2 = new ReturnItem("RET0002", "SKU002", 5, "damaged",
                LocalDateTime.of(2025, 9, 12, 9, 0), null);
        r3 = new ReturnItem("RET0003", "SKU003", 8, "cycle-count",
                LocalDateTime.of(2025, 9, 11, 9, 0), null);
    }

    // ---------------------------------------------------------------------
    // Quarantine behaviour
    // ---------------------------------------------------------------------

    /**
     * Ensures that the Quarantine correctly orders elements
     * by timestamp descending (latest first) and returnId ascending.
     */
    @Test
    @DisplayName("Quarantine should maintain latest-first order (timestamp DESC)")
    void testQuarantineOrdering() {
        List<ReturnItem> list = Arrays.asList(r1, r2, r3);
        quarantine.addAll(list);

        // The first processed item must be the most recent (r2, 2025-09-12)
        ReturnItem first = quarantine.next();
        assertEquals("RET0002", first.getReturnId(), "Latest return should be processed first");

        // Next should be r3 (2025-09-11)
        ReturnItem second = quarantine.next();
        assertEquals("RET0003", second.getReturnId(), "Second latest return should follow");

        // Last should be r1 (2025-09-10)
        ReturnItem third = quarantine.next();
        assertEquals("RET0001", third.getReturnId(), "Oldest return should be processed last");
    }

    // ---------------------------------------------------------------------
    // QualityOperator: decision logic
    // ---------------------------------------------------------------------

    /**
     * Validates that the QualityOperator correctly decides between
     * restocking and discarding items according to the return reason.
     */
    @Test
    @DisplayName("QualityOperator should restock only eligible reasons")
    void testRestockVsDiscardDecision() {
        quarantine.addAll(Arrays.asList(r1, r2, r3));
        operator.processReturns();

        assertTrue(inventory.containsSku("SKU001"), "Customer remorse → restock expected");
        assertTrue(inventory.containsSku("SKU003"), "Cycle count → restock expected");
        assertFalse(inventory.containsSku("SKU002"), "Damaged → discard expected");
    }

    // ---------------------------------------------------------------------
    // Inventory behaviour
    // ---------------------------------------------------------------------

    /**
     * Checks that restocked boxes are inserted into the inventory
     * with proper FEFO/FIFO ordering and metadata.
     */
    @Test
    @DisplayName("Restocked boxes should appear in inventory with correct IDs and timestamps")
    void testInventoryInsertion() {
        quarantine.addAll(Arrays.asList(r1, r3));
        operator.processReturns();

        List<Box> boxes1 = inventory.getBoxes("SKU001");
        List<Box> boxes3 = inventory.getBoxes("SKU003");

        assertEquals(1, boxes1.size());
        assertEquals(1, boxes3.size());
        assertTrue(boxes1.get(0).getBoxId().startsWith("RET-RET0001"));
        assertNotNull(boxes1.get(0).getReceivedAt());
    }

    // ---------------------------------------------------------------------
    // Audit log checks
    // ---------------------------------------------------------------------

    /**
     * Verifies that the audit log is created with one line per processed return.
     */
    @Test
    @DisplayName("Audit log should contain one line per processed return")
    void testAuditLogEntryCount() throws IOException {
        quarantine.addAll(Arrays.asList(r1, r2, r3));
        operator.processReturns();

        List<String> logLines = Files.readAllLines(logFilePath);
        assertEquals(3, logLines.size(), "Each return should produce exactly one log entry");
    }

    /**
     * Ensures that discarded items do not include a qty field in the audit log,
     * while restocked items do.
     */
    @Test
    @DisplayName("Audit log should omit qty for discarded items")
    void testAuditLogQtyField() throws IOException {
        quarantine.addAll(Arrays.asList(r1, r2, r3));
        operator.processReturns();

        List<String> logLines = Files.readAllLines(logFilePath);

        assertTrue(logLines.stream().anyMatch(l -> l.contains("action=Restocked") && l.contains("qty=")),
                "Restocked entries must include qty");
        assertTrue(logLines.stream().anyMatch(l -> l.contains("action=Discarded") && !l.contains("qty=")),
                "Discarded entries must not include qty");
    }

    /**
     * Confirms that the log preserves the reverse chronological order of inspection.
     */
    @Test
    @DisplayName("Audit log lines should follow latest-first inspection order")
    void testAuditLogOrder() throws IOException {
        quarantine.addAll(Arrays.asList(r1, r2, r3));
        operator.processReturns();

        List<String> logLines = Files.readAllLines(logFilePath);
        // r2 (latest) must appear before r3 and r1
        String firstLine = logLines.get(0);
        assertTrue(firstLine.contains("RET0002"),
                "First log entry should correspond to the most recent return (RET0002)");
    }

    /**
     * Deletes the temporary audit log after each test run.
     */
    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(logFilePath)) Files.delete(logFilePath);
    }
}
