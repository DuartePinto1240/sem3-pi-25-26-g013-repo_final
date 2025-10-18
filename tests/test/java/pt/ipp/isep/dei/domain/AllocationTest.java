package test.java.pt.ipp.isep.dei.domain;



import main.java.pt.ipp.isep.dei.domain.Allocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AllocationTest {

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

    @Test
    public void testToStringContainsAllFields() {
        // Arrange
        Allocation alloc = new Allocation("ORD002", 1, "SKU0010", 10, 50.0, "BOX123", 4, 8);

        // Act
        String text = alloc.toString();

        // Assert
        assertTrue(text.contains("ORD002"));
        assertTrue(text.contains("SKU0010"));
        assertTrue(text.contains("BOX123"));
        assertTrue(text.contains("aisle=4"));
        assertTrue(text.contains("bay=8"));
    }

    @Test
    public void testNegativeValuesAllowedButConsistent() {
        // Arrange
        Allocation alloc = new Allocation("ORD999", 1, "SKU999", -5, -10.0, "BOXNEG", 1, 1);

        // Assert
        assertEquals(-5.0, alloc.getQty());
        assertEquals(-10.0, alloc.getWeight());
    }

    @Test
    public void testEqualityBetweenDifferentObjectsSameData() {
        // Arrange
        Allocation a1 = new Allocation("ORD100", 3, "SKU777", 5, 10, "BOX777", 2, 4);
        Allocation a2 = new Allocation("ORD100", 3, "SKU777", 5, 10, "BOX777", 2, 4);

        // Mesmo sem equals() definido, os valores individuais são iguais
        assertEquals(a1.getOrderId(), a2.getOrderId());
        assertEquals(a1.getSku(), a2.getSku());
        assertEquals(a1.getQty(), a2.getQty());
    }
}

