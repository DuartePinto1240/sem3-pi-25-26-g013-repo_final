package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.OrderLine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OrderLine}.
 * <p>
 * These tests validate the construction and basic behavior of OrderLine objects,
 * including getter methods and handling of zero or negative quantities.
 * </p>
 */
public class OrderLineTest {

    /**
     * Tests that the constructor correctly sets all properties and getters return the expected values.
     */
    @Test
    public void testConstructorAndGetters() {
        int lineNo = 1;
        String sku = "SKU0005";
        int qtyRequested = 25;

        OrderLine orderLine = new OrderLine(lineNo, sku, qtyRequested);

        assertEquals(lineNo, orderLine.getLineNo());
        assertEquals(sku, orderLine.getSku());
        assertEquals(qtyRequested, orderLine.getQtyRequested());
    }

    /**
     * Tests that two different OrderLine objects with the same data have identical field values.
     */
    @Test
    public void testDifferentObjectsSameValues() {
        OrderLine l1 = new OrderLine(2, "SKU0010", 40);
        OrderLine l2 = new OrderLine(2, "SKU0010", 40);

        assertEquals(l1.getLineNo(), l2.getLineNo());
        assertEquals(l1.getSku(), l2.getSku());
        assertEquals(l1.getQtyRequested(), l2.getQtyRequested());
    }

    /**
     * Tests that an OrderLine can have zero quantity.
     */
    @Test
    public void testZeroQuantityAllowed() {
        OrderLine line = new OrderLine(3, "SKU0009", 0);

        assertEquals(0, line.getQtyRequested());
    }

    /**
     * Tests that an OrderLine can have a negative quantity and that it is stored consistently.
     */
    @Test
    public void testNegativeQuantityAllowedButConsistent() {
        OrderLine line = new OrderLine(4, "SKU9999", -10);

        assertEquals(-10, line.getQtyRequested());
    }
}
