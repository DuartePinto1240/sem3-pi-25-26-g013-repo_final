package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Bay;
import main.java.pt.ipp.isep.dei.domain.Warehouse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Warehouse} class.
 * <p>
 * These tests verify that warehouse bays can be correctly added, retrieved,
 * and listed within a {@code Warehouse} instance.
 * </p>
 */
public class WarehouseTest {

    /**
     * Tests that a {@link Bay} can be successfully added to the {@link Warehouse},
     * and then retrieved using its warehouse ID, aisle, and bay number.
     * <p>
     * The retrieved bay should be the same instance as the one added.
     * </p>
     */
    @Test
    public void testAddBay() {
        Warehouse warehouse = new Warehouse("W1");

        Bay bay1 = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay1);

        Bay retrievedBay = warehouse.getBay("W1", 1, 1);
        assertEquals(bay1, retrievedBay);  // The bay added should be the same as the one retrieved
    }

    /**
     * Tests that the {@link Warehouse#getBays()} method returns all bays
     * that were previously added to the warehouse.
     * <p>
     * The total number of bays returned should match the number added.
     * </p>
     */
    @Test
    public void testGetBays() {
        Warehouse warehouse = new Warehouse("W1");

        Bay bay1 = new Bay("W1", 1, 1, 10);
        Bay bay2 = new Bay("W1", 1, 2, 15);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);

        assertEquals(2, warehouse.getBays().size());  // Should return 2 bays
    }
}
