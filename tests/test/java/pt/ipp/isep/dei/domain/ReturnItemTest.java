package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.ReturnItem;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ReturnItem}.
 * <p>
 * These tests verify the behavior of the ReturnItem class, including proper initialization,
 * validation of quantity, and the correct ordering based on timestamp.
 * </p>
 */
class ReturnItemTest {

    /**
     * Tests that a ReturnItem can be created with valid parameters
     * and that its getters return the expected values.
     */
    @Test
    void testValidReturnItem() {
        ReturnItem item = new ReturnItem(
                "RET001", "SKU123", 10, "customer-remorse",
                LocalDateTime.parse("2025-09-22T10:00:00"), null);

        assertEquals("RET001", item.getReturnId());
        assertEquals("SKU123", item.getSku());
        assertEquals(10, item.getQuantity());
        assertEquals("customer-remorse", item.getReason());
    }

    /**
     * Tests that creating a ReturnItem with a negative quantity
     * throws an IllegalArgumentException.
     */
    @Test
    void testInvalidQuantityThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new ReturnItem("RET002", "SKU123", -5, "damaged",
                        LocalDateTime.now(), null)
        );
    }

    /**
     * Tests that {@link ReturnItem#compareTo(ReturnItem)} orders items
     * in descending order of timestamp, so newer items come first.
     */
    @Test
    void testCompareToDescendingByTimestamp() {
        ReturnItem newer = new ReturnItem("RET003", "SKU1", 1, "cycle-count",
                LocalDateTime.parse("2025-09-22T17:00:00"), null);
        ReturnItem older = new ReturnItem("RET004", "SKU2", 1, "damaged",
                LocalDateTime.parse("2025-09-20T10:00:00"), null);

        assertTrue(newer.compareTo(older) < 0, "Newer items should come first");
    }
}
