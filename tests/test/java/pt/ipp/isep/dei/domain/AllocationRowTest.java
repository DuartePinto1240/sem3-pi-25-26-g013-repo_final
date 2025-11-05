package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.AllocationRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AllocationRow} class.
 * <p>
 * Verifies correct instance creation and attribute access via getters.
 * </p>
 */
public class AllocationRowTest {

    /**
     * Tests the creation of an {@link AllocationRow} with all fields defined
     * and checks that the getters return the correct values.
     */
    @Test
    public void testAllocationRowCreation() {
        AllocationRow row = new AllocationRow("ORD01", 2, "SKU002", 15, "BOX001", 3, 5);

        assertEquals("ORD01", row.getOrderId());
        assertEquals(2, row.getLineNo());
        assertEquals("SKU002", row.getSku());
        assertEquals(15, row.getQty());
        assertEquals("BOX001", row.getBoxId());
        assertEquals(3, row.getAisle());
        assertEquals(5, row.getBay());
    }
}
