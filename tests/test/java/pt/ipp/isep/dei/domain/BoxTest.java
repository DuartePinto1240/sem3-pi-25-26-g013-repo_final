package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Box;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit tests for the {@link Box} class.
 * <p>
 * Verifies the correct creation of boxes and proper storage of their attributes,
 * including behavior for both perishable and non-perishable boxes.
 * </p>
 */
public class BoxTest {

    /**
     * Tests the creation of a box with all attributes provided,
     * including expiry date and received date.
     * <p>
     * Ensures that all field values are stored and retrieved correctly.
     * </p>
     */
    @Test
    public void testBoxCreation() {
        Box box = new Box("BOX001", "SKU001", 10, LocalDate.of(2025, 12, 31),
                LocalDateTime.of(2025, 7, 5, 15, 42, 0));

        assertEquals("BOX001", box.getBoxId());
        assertEquals("SKU001", box.getSku());
        assertEquals(10, box.getQuantity());
        assertEquals(LocalDate.of(2025, 12, 31), box.getExpiryDate());
        assertEquals(LocalDateTime.of(2025, 7, 5, 15, 42, 0), box.getReceivedAt());
    }

    /**
     * Tests the creation of a non-perishable box.
     * <p>
     * Verifies that the expiry date is {@code null} while the received date is properly set.
     * </p>
     */
    @Test
    public void testNonPerishableBox() {
        Box box = new Box("BOX002", "SKU002", 5, null,
                LocalDateTime.of(2025, 8, 22, 12, 30, 0));

        assertNull(box.getExpiryDate(), "Non-perishable box should have null expiryDate");
        assertNotNull(box.getReceivedAt(), "Received date must be defined");
    }
}
