package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Bay;
import main.java.pt.ipp.isep.dei.domain.Box;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Bay} class.
 * <p>
 * Verifies behavior related to the bay's storage capacity
 * and the addition of boxes into the bay.
 * </p>
 */
public class BayTest {

    /**
     * Tests that the methods {@link Bay#canStoreBox(Box)} and {@link Bay#addBox(Box)}
     * correctly enforce the bay's maximum capacity.
     * <p>
     * Ensures that a bay cannot store more boxes than its defined limit.
     * </p>
     */
    @Test
    public void testBayCapacity() {
        Bay bay = new Bay("W1", 1, 1, 2); // Bay with capacity for 2 boxes

        assertTrue(bay.canStoreBox(new Box("BOX001", "SKU001", 10, null, null)));
        bay.addBox(new Box("BOX001", "SKU001", 10, null, null));
        assertEquals(1, bay.getStoredBoxes().size());

        // Attempt to add another box (should not exceed capacity)
        bay.addBox(new Box("BOX002", "SKU002", 5, null, null));
        assertEquals(2, bay.getStoredBoxes().size(), "Should not exceed the maximum capacity of 2 boxes");
    }

    /**
     * Tests that adding a box to the bay correctly updates
     * the internal list of stored boxes.
     * <p>
     * Ensures that the added box is properly reflected in the bay's stored boxes collection.
     * </p>
     */
    @Test
    public void testAddBoxToBay() {
        Bay bay = new Bay("W1", 1, 1, 10);
        Box box = new Box("BOX001", "SKU001", 10, null, null);

        bay.addBox(box);

        assertEquals(1, bay.getStoredBoxes().size());
        assertEquals(box, bay.getStoredBoxes().get(0));
    }
}
