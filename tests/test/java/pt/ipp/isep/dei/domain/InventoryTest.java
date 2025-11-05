package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Box;
import main.java.pt.ipp.isep.dei.domain.Inventory;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Inventory}.
 * <p>
 * These tests verify adding boxes to the inventory, checking SKU existence,
 * and ensuring boxes are sorted by FEFO (First Expired, First Out) order.
 * </p>
 */
class InventoryTest {

    /**
     * Verifies that adding a box to the inventory allows the SKU to be found
     * and the box can be retrieved.
     */
    @Test
    void testAddBoxAndContainsSku() {
        Inventory inv = new Inventory();
        Box b1 = new Box("B001", "SKU1", 5, LocalDate.parse("2025-12-01"), LocalDateTime.now());
        inv.addBox(b1);

        assertTrue(inv.containsSku("SKU1"));
        assertEquals(1, inv.getBoxes("SKU1").size());
    }

    /**
     * Verifies that boxes for the same SKU are sorted by FEFO (earliest expiry first).
     */
    @Test
    void testBoxesAreSortedByFEFO() {
        Inventory inv = new Inventory();
        Box b1 = new Box("B001", "SKU1", 5, LocalDate.parse("2025-12-01"), LocalDateTime.now());
        Box b2 = new Box("B002", "SKU1", 5, LocalDate.parse("2025-11-01"), LocalDateTime.now());
        inv.addBox(b1);
        inv.addBox(b2);

        assertEquals("B002", inv.getBoxes("SKU1").get(0).getBoxId(),
                "Box with earlier expiry should be first (FEFO)");
    }
}

