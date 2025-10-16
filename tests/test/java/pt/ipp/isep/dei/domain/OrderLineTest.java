package test.java.pt.ipp.isep.dei.domain;



import main.java.pt.ipp.isep.dei.domain.OrderLine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderLineTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int lineNo = 1;
        String sku = "SKU0005";
        int qtyRequested = 25;

        // Act
        OrderLine orderLine = new OrderLine(lineNo, sku, qtyRequested);

        // Assert
        assertEquals(lineNo, orderLine.getLineNo());
        assertEquals(sku, orderLine.getSku());
        assertEquals(qtyRequested, orderLine.getQtyRequested());
    }

    @Test
    public void testDifferentObjectsSameValues() {
        // Arrange
        OrderLine l1 = new OrderLine(2, "SKU0010", 40);
        OrderLine l2 = new OrderLine(2, "SKU0010", 40);

        // Assert
        assertEquals(l1.getLineNo(), l2.getLineNo());
        assertEquals(l1.getSku(), l2.getSku());
        assertEquals(l1.getQtyRequested(), l2.getQtyRequested());
    }

    @Test
    public void testZeroQuantityAllowed() {
        // Arrange
        OrderLine line = new OrderLine(3, "SKU0009", 0);

        // Assert
        assertEquals(0, line.getQtyRequested());
    }

    @Test
    public void testNegativeQuantityAllowedButConsistent() {
        // Arrange
        OrderLine line = new OrderLine(4, "SKU9999", -10);

        // Assert
        assertEquals(-10, line.getQtyRequested());
    }
}

