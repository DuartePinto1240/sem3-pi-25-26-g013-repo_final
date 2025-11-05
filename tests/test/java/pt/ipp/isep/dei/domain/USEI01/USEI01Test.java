package test.java.pt.ipp.isep.dei.domain.USEI01;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for Use Case USEI01 — Wagons Unloading (Inventory Replenishment).
 *
 * This class validates the following functional aspects:
 *
 *   - SKU grouping: Boxes of the same SKU must be allocated to the same bay(s)
 *   - FEFO ordering: Perishable goods with earlier expiry dates are stored first
 *   - FIFO ordering: Non-perishable goods (null expiry) follow receivedAt ordering
 *   - Bay capacity: No bay exceeds its maximum capacity
 *   - Multi-bay allocation: When a bay fills up, allocation continues to the next bay
 *   - Deterministic ordering: BoxId is used as a tie-breaker
 *
 * All tests use in-memory objects. No external CSV files are required.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class USEI01Test {

    private Warehouse warehouse;
    private WagonUnloader unloader;
    private List<Wagon> wagons;

    /**
     * Creates a clean test environment with a fresh warehouse and wagons before each test.
     */
    @BeforeEach
    void setUp() {
        warehouse = new Warehouse("W1");

        // Create bays with different capacities
        for (int aisle = 1; aisle <= 3; aisle++) {
            for (int bay = 1; bay <= 5; bay++) {
                int capacity = (aisle == 1) ? 5 : 10; // Aisle 1 has smaller capacity
                warehouse.addBay(new Bay("W1", aisle, bay, capacity));
            }
        }

        unloader = new WagonUnloader(warehouse);
        wagons = new ArrayList<>();
    }

    // =========================================================================
    // FEFO Ordering Tests (Perishable goods)
    // =========================================================================

    /**
     * Validates that boxes with earlier expiry dates are placed first in the bay.
     */
    @Test
    @Order(1)
    @DisplayName("FEFO: Boxes with earlier expiry dates should be stored first")
    void testFEFO_EarliestExpiryFirst() {
        Wagon wagon = new Wagon("WGN001");

        // Same SKU, different expiry dates
        Box box1 = new Box("BOX001", "SKU001", 10,
                LocalDate.of(2025, 12, 31), LocalDateTime.of(2025, 9, 1, 10, 0));
        Box box2 = new Box("BOX002", "SKU001", 15,
                LocalDate.of(2025, 10, 15), LocalDateTime.of(2025, 9, 1, 11, 0));
        Box box3 = new Box("BOX003", "SKU001", 20,
                LocalDate.of(2026, 3, 20), LocalDateTime.of(2025, 9, 1, 9, 0));

        wagon.addBox(box1);
        wagon.addBox(box2);
        wagon.addBox(box3);
        wagons.add(wagon);

        unloader.unload(wagons);

        Bay bay = warehouse.getBay("W1", 1, 1);
        List<Box> stored = bay.getStoredBoxes();

        assertEquals(3, stored.size());
        assertEquals("BOX002", stored.get(0).getBoxId(), "Earliest expiry (2025-10-15) should be first");
        assertEquals("BOX001", stored.get(1).getBoxId(), "Second expiry (2025-12-31) should be second");
        assertEquals("BOX003", stored.get(2).getBoxId(), "Latest expiry (2026-03-20) should be last");
    }

    /**
     * Ensures that perishable goods (with expiry date) are stored before non-perishable goods (null expiry).
     */
    @Test
    @Order(2)
    @DisplayName("FEFO: Perishable goods should be stored before non-perishable goods")
    void testFEFO_PerishableBeforeNonPerishable() {
        Wagon wagon = new Wagon("WGN001");

        // Mix of perishable and non-perishable
        Box box1 = new Box("BOX001", "SKU001", 10,
                null, LocalDateTime.of(2025, 8, 1, 10, 0));
        Box box2 = new Box("BOX002", "SKU001", 15,
                LocalDate.of(2025, 11, 30), LocalDateTime.of(2025, 9, 1, 10, 0));
        Box box3 = new Box("BOX003", "SKU001", 20,
                null, LocalDateTime.of(2025, 7, 1, 10, 0));

        wagon.addBox(box1);
        wagon.addBox(box2);
        wagon.addBox(box3);
        wagons.add(wagon);

        unloader.unload(wagons);

        Bay bay = warehouse.getBay("W1", 1, 1);
        List<Box> stored = bay.getStoredBoxes();

        assertEquals(3, stored.size());
        assertEquals("BOX002", stored.get(0).getBoxId(), "Perishable box should be first");
        assertNotNull(stored.get(0).getExpiryDate(), "First box should have expiry date");
        assertNull(stored.get(1).getExpiryDate(), "Non-perishable boxes should follow");
        assertNull(stored.get(2).getExpiryDate(), "Non-perishable boxes should follow");
    }

    // =========================================================================
    // FIFO Ordering Tests (Non-perishable goods)
    // =========================================================================

    /**
     * Validates that non-perishable goods are ordered by receivedAt (oldest first).
     */
    @Test
    @Order(3)
    @DisplayName("FIFO: Non-perishable goods should be ordered by receivedAt (oldest first)")
    void testFIFO_OldestReceivedFirst() {
        Wagon wagon = new Wagon("WGN001");

        // Same SKU, no expiry, different receivedAt
        Box box1 = new Box("BOX001", "SKU001", 10,
                null, LocalDateTime.of(2025, 9, 10, 10, 0));
        Box box2 = new Box("BOX002", "SKU001", 15,
                null, LocalDateTime.of(2025, 9, 5, 10, 0));
        Box box3 = new Box("BOX003", "SKU001", 20,
                null, LocalDateTime.of(2025, 9, 15, 10, 0));

        wagon.addBox(box1);
        wagon.addBox(box2);
        wagon.addBox(box3);
        wagons.add(wagon);

        unloader.unload(wagons);

        Bay bay = warehouse.getBay("W1", 1, 1);
        List<Box> stored = bay.getStoredBoxes();

        assertEquals(3, stored.size());
        assertEquals("BOX002", stored.get(0).getBoxId(), "Oldest receivedAt should be first");
        assertEquals("BOX001", stored.get(1).getBoxId(), "Second oldest should be second");
        assertEquals("BOX003", stored.get(2).getBoxId(), "Newest should be last");
    }

    /**
     * Ensures that boxId is used as a tie-breaker when expiry dates and receivedAt are equal.
     */
    @Test
    @Order(4)
    @DisplayName("FIFO: BoxId should be used as tie-breaker")
    void testFIFO_BoxIdTieBreaker() {
        Wagon wagon = new Wagon("WGN001");

        LocalDateTime sameTime = LocalDateTime.of(2025, 9, 1, 10, 0);

        Box box1 = new Box("BOX003", "SKU001", 10, null, sameTime);
        Box box2 = new Box("BOX001", "SKU001", 15, null, sameTime);
        Box box3 = new Box("BOX002", "SKU001", 20, null, sameTime);

        wagon.addBox(box1);
        wagon.addBox(box2);
        wagon.addBox(box3);
        wagons.add(wagon);

        unloader.unload(wagons);

        Bay bay = warehouse.getBay("W1", 1, 1);
        List<Box> stored = bay.getStoredBoxes();

        assertEquals(3, stored.size());
        assertEquals("BOX001", stored.get(0).getBoxId(), "BoxId should determine order");
        assertEquals("BOX002", stored.get(1).getBoxId());
        assertEquals("BOX003", stored.get(2).getBoxId());
    }

    // =========================================================================
    // SKU Grouping Tests
    // =========================================================================

    /**
     * Ensures that boxes of the same SKU are allocated to the same bay.
     */
    @Test
    @Order(5)
    @DisplayName("SKU Grouping: All boxes of the same SKU should be in the same bay(s)")
    void testSKUGrouping_SameSKUSameBay() {
        Wagon wagon = new Wagon("WGN001");

        // Three boxes of SKU001
        wagon.addBox(new Box("BOX001", "SKU001", 10, null, LocalDateTime.now()));
        wagon.addBox(new Box("BOX002", "SKU001", 15, null, LocalDateTime.now()));
        wagon.addBox(new Box("BOX003", "SKU001", 20, null, LocalDateTime.now()));

        // Three boxes of SKU002
        wagon.addBox(new Box("BOX004", "SKU002", 10, null, LocalDateTime.now()));
        wagon.addBox(new Box("BOX005", "SKU002", 15, null, LocalDateTime.now()));
        wagon.addBox(new Box("BOX006", "SKU002", 20, null, LocalDateTime.now()));

        wagons.add(wagon);
        unloader.unload(wagons);

        // Verify SKU001 is in one bay
        Bay bay1 = warehouse.getBay("W1", 1, 1);
        Set<String> skusInBay1 = bay1.getStoredBoxes().stream()
                .map(Box::getSku)
                .collect(Collectors.toSet());

        assertEquals(1, skusInBay1.size(), "Bay should contain only one SKU");
        assertTrue(skusInBay1.contains("SKU001"));

        // Verify SKU002 is in another bay
        Bay bay2 = warehouse.getBay("W1", 1, 2);
        Set<String> skusInBay2 = bay2.getStoredBoxes().stream()
                .map(Box::getSku)
                .collect(Collectors.toSet());

        assertEquals(1, skusInBay2.size(), "Bay should contain only one SKU");
        assertTrue(skusInBay2.contains("SKU002"));
    }

    /**
     * Validates that different SKUs are never mixed in the same bay.
     */
    @Test
    @Order(6)
    @DisplayName("SKU Grouping: Different SKUs should never be mixed in the same bay")
    void testSKUGrouping_NoMixing() {
        Wagon wagon = new Wagon("WGN001");

        // Create multiple SKUs
        for (int i = 1; i <= 5; i++) {
            wagon.addBox(new Box("BOX00" + i, "SKU00" + i, 10, null, LocalDateTime.now()));
        }

        wagons.add(wagon);
        unloader.unload(wagons);

        // Check all bays
        for (Bay bay : warehouse.getBays().values()) {
            if (!bay.getStoredBoxes().isEmpty()) {
                Set<String> skus = bay.getStoredBoxes().stream()
                        .map(Box::getSku)
                        .collect(Collectors.toSet());

                assertEquals(1, skus.size(),
                        "Bay " + bay.getAisle() + "-" + bay.getBay() + " should contain only one SKU");
            }
        }
    }

    // =========================================================================
    // Capacity Tests
    // =========================================================================

    /**
     * Ensures that no bay exceeds its maximum capacity.
     */
    @Test
    @Order(7)
    @DisplayName("Capacity: No bay should exceed its maximum capacity")
    void testCapacity_NoBayExceedsLimit() {
        Wagon wagon = new Wagon("WGN001");

        // Add 10 boxes of the same SKU (Aisle 1 has capacity 5)
        for (int i = 1; i <= 10; i++) {
            wagon.addBox(new Box("BOX00" + i, "SKU001", 10, null, LocalDateTime.now()));
        }

        wagons.add(wagon);
        unloader.unload(wagons);

        // Check all bays respect capacity
        for (Bay bay : warehouse.getBays().values()) {
            assertTrue(bay.getStoredBoxes().size() <= bay.getCapacityBoxes(),
                    "Bay " + bay.getAisle() + "-" + bay.getBay() + " exceeded capacity");
        }
    }

    /**
     * Validates that when a bay fills up, allocation continues to the next bay.
     */
    @Test
    @Order(8)
    @DisplayName("Capacity: Multi-bay allocation when first bay is full")
    void testCapacity_MultiBayAllocation() {
        Wagon wagon = new Wagon("WGN001");

        // Add 8 boxes of SKU001 (Aisle 1, Bay 1 has capacity 5)
        for (int i = 1; i <= 8; i++) {
            wagon.addBox(new Box("BOX00" + i, "SKU001", 10, null,
                    LocalDateTime.of(2025, 9, i, 10, 0)));
        }

        wagons.add(wagon);
        unloader.unload(wagons);

        Bay bay1 = warehouse.getBay("W1", 1, 1);
        Bay bay2 = warehouse.getBay("W1", 1, 2);

        assertEquals(5, bay1.getStoredBoxes().size(), "First bay should be full");
        assertEquals(3, bay2.getStoredBoxes().size(), "Overflow should go to second bay");

        // Verify all boxes are SKU001
        assertEquals("SKU001", bay1.getStoredBoxes().get(0).getSku());
        assertEquals("SKU001", bay2.getStoredBoxes().get(0).getSku());
    }

    // =========================================================================
    // Integration Tests
    // =========================================================================

    /**
     * Complex scenario: Multiple wagons with mixed perishable/non-perishable goods.
     */
    @Test
    @Order(9)
    @DisplayName("Integration: Multiple wagons with mixed goods")
    void testIntegration_MultipleWagonsMixedGoods() {
        // Wagon 1: Perishable SKU001
        Wagon wagon1 = new Wagon("WGN001");
        wagon1.addBox(new Box("BOX001", "SKU001", 10,
                LocalDate.of(2025, 10, 15), LocalDateTime.of(2025, 9, 1, 10, 0)));
        wagon1.addBox(new Box("BOX002", "SKU001", 15,
                LocalDate.of(2025, 11, 30), LocalDateTime.of(2025, 9, 2, 10, 0)));

        // Wagon 2: Non-perishable SKU001
        Wagon wagon2 = new Wagon("WGN002");
        wagon2.addBox(new Box("BOX003", "SKU001", 20,
                null, LocalDateTime.of(2025, 8, 1, 10, 0)));

        // Wagon 3: Different SKU
        Wagon wagon3 = new Wagon("WGN003");
        wagon3.addBox(new Box("BOX004", "SKU002", 25,
                null, LocalDateTime.of(2025, 9, 1, 10, 0)));

        wagons.add(wagon1);
        wagons.add(wagon2);
        wagons.add(wagon3);

        unloader.unload(wagons);

        Bay bay1 = warehouse.getBay("W1", 1, 1);
        List<Box> stored = bay1.getStoredBoxes();

        // All SKU001 should be in bay1, ordered by FEFO/FIFO
        assertEquals(3, stored.size());
        assertEquals("SKU001", stored.get(0).getSku());
        assertEquals("SKU001", stored.get(1).getSku());
        assertEquals("SKU001", stored.get(2).getSku());

        // BOX001 (2025-10-15) should be first
        assertEquals("BOX001", stored.get(0).getBoxId());
        // BOX002 (2025-11-30) should be second
        assertEquals("BOX002", stored.get(1).getBoxId());
        // BOX003 (null expiry) should be last
        assertEquals("BOX003", stored.get(2).getBoxId());

        // SKU002 should be in bay2
        Bay bay2 = warehouse.getBay("W1", 1, 2);
        assertEquals(1, bay2.getStoredBoxes().size());
        assertEquals("SKU002", bay2.getStoredBoxes().get(0).getSku());
    }

    /**
     * Validates that all boxes from multiple wagons are successfully allocated.
     */
    @Test
    @Order(10)
    @DisplayName("Integration: All boxes should be allocated")
    void testIntegration_AllBoxesAllocated() {
        int totalBoxes = 0;

        // Create 3 wagons with different SKUs
        for (int w = 1; w <= 3; w++) {
            Wagon wagon = new Wagon("WGN00" + w);
            for (int b = 1; b <= 5; b++) {
                wagon.addBox(new Box("BOX" + w + "0" + b, "SKU00" + w, 10,
                        null, LocalDateTime.of(2025, 9, b, 10, 0)));
                totalBoxes++;
            }
            wagons.add(wagon);
        }

        unloader.unload(wagons);

        // Count allocated boxes
        int allocatedBoxes = warehouse.getBays().values().stream()
                .mapToInt(bay -> bay.getStoredBoxes().size())
                .sum();

        assertEquals(totalBoxes, allocatedBoxes, "All boxes should be allocated");
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    /**
     * Tests behavior with empty wagons.
     */
    @Test
    @Order(11)
    @DisplayName("Edge Case: Empty wagons should not cause errors")
    void testEdgeCase_EmptyWagons() {
        wagons.add(new Wagon("WGN001"));
        wagons.add(new Wagon("WGN002"));

        assertDoesNotThrow(() -> unloader.unload(wagons));

        // All bays should be empty
        for (Bay bay : warehouse.getBays().values()) {
            assertTrue(bay.getStoredBoxes().isEmpty());
        }
    }

    /**
     * Tests behavior with single box.
     */
    @Test
    @Order(12)
    @DisplayName("Edge Case: Single box should be allocated correctly")
    void testEdgeCase_SingleBox() {
        Wagon wagon = new Wagon("WGN001");
        wagon.addBox(new Box("BOX001", "SKU001", 10, null, LocalDateTime.now()));
        wagons.add(wagon);

        unloader.unload(wagons);

        Bay bay = warehouse.getBay("W1", 1, 1);
        assertEquals(1, bay.getStoredBoxes().size());
        assertEquals("BOX001", bay.getStoredBoxes().get(0).getBoxId());
    }

    /**
     * Cleanup after each test.
     */
    @AfterEach
    void tearDown() {
        warehouse = null;
        unloader = null;
        wagons = null;
    }
}
