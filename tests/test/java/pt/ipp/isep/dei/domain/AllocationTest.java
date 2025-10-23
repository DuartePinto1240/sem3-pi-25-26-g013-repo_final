package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Allocation} class.
 * <p>
 * Verifies instance creation, getter behavior, and consistency of stored values.
 * </p>
 */
public class AllocationTest {

    /**
     * Tests the creation of an {@link Allocation} with all fields defined
     * and checks that the getters return the correct values.
     */
    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String orderId = "ORD0001";
        int lineNo = 2;
        String sku = "SKU0005";
        int qty = 25;
        double weight = 120.5;
        String boxId = "BOX099";
        int aisle = 3;
        int bay = 7;

        // Act
        Allocation alloc = new Allocation(orderId, lineNo, sku, qty, weight, boxId, aisle, bay);

        // Assert
        assertEquals(orderId, alloc.getOrderId());
        assertEquals(lineNo, alloc.getLineNo());
        assertEquals(sku, alloc.getSku());
        assertEquals(qty, alloc.getQty());
        assertEquals(weight, alloc.getWeight());
        assertEquals(boxId, alloc.getBoxId());
        assertEquals(aisle, alloc.getAisle());
        assertEquals(bay, alloc.getBay());
    }

    /**
     * Tests whether the class allows negative values and maintains internal consistency.
     */
    @Test
    public void testNegativeValuesAllowedButConsistent() {
        // Arrange
        Allocation alloc = new Allocation("ORD999", 1, "SKU999", -5, -10.0, "BOXNEG", 1, 1);

        // Assert
        assertEquals(-5, alloc.getQty());
        assertEquals(-10.0, alloc.getWeight());
    }

    /**
     * Verifies that two different {@link Allocation} objects with the same data
     * have equal values when accessed via getters.
     * <p>
     * Note: {@link Allocation} does not override {@code equals()}, so this test
     * compares individual fields instead.
     * </p>
     */
    @Test
    public void testEqualityBetweenDifferentObjectsSameData() {
        // Arrange
        Allocation a1 = new Allocation("ORD100", 3, "SKU777", 5, 10, "BOX777", 2, 4);
        Allocation a2 = new Allocation("ORD100", 3, "SKU777", 5, 10, "BOX777", 2, 4);

        // Even without equals() defined, individual field values are the same
        assertEquals(a1.getOrderId(), a2.getOrderId());
        assertEquals(a1.getSku(), a2.getSku());
        assertEquals(a1.getQty(), a2.getQty());
    }
}
