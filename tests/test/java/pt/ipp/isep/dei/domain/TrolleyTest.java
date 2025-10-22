package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import main.java.pt.ipp.isep.dei.domain.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Trolley} class.
 * <p>
 * These tests verify that a {@code Trolley} correctly manages its capacity,
 * weight utilization, and allocation of items.
 * It ensures that weight calculations, utilization percentages, and storage
 * behaviors are accurate under various scenarios.
 * </p>
 */
public class TrolleyTest {

    private Trolley trolley;

    /**
     * Sets up a fresh {@link Trolley} instance before each test.
     * The trolley has an ID of {@code 1} and a total capacity of {@code 100.0 kg}.
     */
    @BeforeEach
    void setUp() {
        trolley = new Trolley(1, 100.0); // capacity 100 kg
    }

    /**
     * Helper method for creating a sample {@link Allocation} with the given SKU and weight.
     *
     * @param sku    the SKU of the allocated item
     * @param weight the total weight of the allocation
     * @return a new {@link Allocation} instance
     */
    private Allocation createAllocation(String sku, double weight) {
        return new Allocation(
                "ORDER001",   // orderId
                1,            // lineNo
                sku,          // sku
                10,           // quantity
                weight,       // weight
                "BOX01",      // boxId
                1,            // aisle
                1             // bay
        );
    }

    /**
     * Tests that a newly created {@link Trolley} starts with the correct default values:
     * <ul>
     *   <li>ID and capacity are correctly set</li>
     *   <li>Used weight is initially 0</li>
     *   <li>No allocations are present</li>
     * </ul>
     */
    @Test
    void testInitialState() {
        assertEquals(1, trolley.getId());
        assertEquals(100.0, trolley.getCapacity());
        assertEquals(0.0, trolley.getUsedWeight());
        assertTrue(trolley.getAllocations().isEmpty());
    }

    /**
     * Verifies that {@link Trolley#canFit(double)} returns {@code true}
     * when the new weight fits within the remaining capacity.
     */
    @Test
    void testCanFitWhenEnoughCapacity() {
        assertTrue(trolley.canFit(50.0), "Should fit when weight is below capacity.");
    }

    /**
     * Verifies that {@link Trolley#canFit(double)} returns {@code true}
     * when the new weight exactly matches the remaining capacity.
     */
    @Test
    void testCanFitWhenAtLimit() {
        trolley.addAllocation(createAllocation("SKU001", 60.0));
        assertTrue(trolley.canFit(40.0), "Should fit exactly up to the capacity limit.");
    }

    /**
     * Verifies that {@link Trolley#canFit(double)} returns {@code false}
     * when the new weight exceeds the total capacity.
     */
    @Test
    void testCanFitWhenExceedsCapacity() {
        trolley.addAllocation(createAllocation("SKU001", 90.0));
        assertFalse(trolley.canFit(20.0), "Should not fit if total weight exceeds capacity.");
    }

    /**
     * Ensures that adding allocations properly increases the used weight
     * and that all allocations are stored in the trolley.
     */
    @Test
    void testAddAllocationIncreasesUsedWeight() {
        Allocation a1 = createAllocation("SKU001", 30.0);
        Allocation a2 = createAllocation("SKU002", 20.0);

        trolley.addAllocation(a1);
        trolley.addAllocation(a2);

        assertEquals(50.0, trolley.getUsedWeight(), 0.0001);
        assertEquals(2, trolley.getAllocations().size());
        assertTrue(trolley.getAllocations().contains(a1));
        assertTrue(trolley.getAllocations().contains(a2));
    }

    /**
     * Verifies that {@link Trolley#getUtilization()} correctly computes
     * the percentage of capacity used.
     */
    @Test
    void testGetUtilization() {
        trolley.addAllocation(createAllocation("SKU001", 25.0)); // 25/100 = 25%
        trolley.addAllocation(createAllocation("SKU002", 25.0)); // 50/100 = 50%

        assertEquals(50.0, trolley.getUtilization(), 0.0001, "Utilization should be 50%.");
    }

    /**
     * Verifies that {@link Trolley#getUtilization()} returns {@code 100.0}
     * when the trolley is fully loaded.
     */
    @Test
    void testGetUtilizationAtFullCapacity() {
        trolley.addAllocation(createAllocation("SKU001", 100.0));
        assertEquals(100.0, trolley.getUtilization(), 0.0001, "Should be 100% when full.");
    }

    /**
     * Verifies that {@link Trolley#getUtilization()} returns {@code 0.0}
     * when no allocations have been added.
     */
    @Test
    void testGetUtilizationWhenEmpty() {
        assertEquals(0.0, trolley.getUtilization(), 0.0001, "Should be 0% when empty.");
    }
}
