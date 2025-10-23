package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit tests for the {@link WagonUnloader} class.
 * <p>
 * These tests verify that the {@code WagonUnloader} correctly unloads boxes
 * from wagons into warehouse bays following the appropriate storage policy:
 * FIFO (First-In-First-Out) for non-perishable boxes and FEFO (First-Expired-First-Out)
 * for perishable boxes.
 * </p>
 */
public class WagonUnloaderTest {

    /**
     * Tests that the {@link WagonUnloader#unload(List)} method correctly unloads
     * non-perishable boxes using the FIFO (First-In-First-Out) policy.
     * <p>
     * The boxes should be stored in the warehouse bay in ascending order of their
     * arrival date and time — the oldest box first.
     * </p>
     */
    @Test
    public void testUnloadFIFO() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay1 = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay1);

        // Creating non-perishable boxes (FIFO)
        Box box1 = new Box("BOX001", "SKU001", 10, null, LocalDateTime.of(2025, 7, 5, 15, 42, 0));
        Box box2 = new Box("BOX002", "SKU002", 15, null, LocalDateTime.of(2025, 6, 10, 10, 30, 0));
        Wagon wagon = new Wagon("WGN001", List.of(box1, box2));

        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(List.of(wagon));

        // Boxes should be stored in FIFO order
        assertEquals(box2, bay1.getStoredBoxes().get(0));  // The oldest box should come first
        assertEquals(box1, bay1.getStoredBoxes().get(1));
    }

    /**
     * Tests that the {@link WagonUnloader#unload(List)} method correctly unloads
     * perishable boxes using the FEFO (First-Expired-First-Out) policy.
     * <p>
     * The boxes should be stored in the warehouse bay in ascending order of their
     * expiration date — the one expiring soonest first.
     * </p>
     */
    @Test
    public void testUnloadFEFO() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay1 = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay1);

        // Creating perishable boxes (FEFO)
        Box box1 = new Box("BOX001", "SKU001", 10, LocalDate.of(2025, 12, 31), LocalDateTime.of(2025, 7, 5, 15, 42, 0));
        Box box2 = new Box("BOX002", "SKU002", 15, LocalDate.of(2025, 6, 30), LocalDateTime.of(2025, 6, 10, 10, 30, 0));
        Wagon wagon = new Wagon("WGN001", List.of(box1, box2));

        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(List.of(wagon));

        // Boxes should be stored in FEFO order (earliest expiration date first)
        assertEquals(box2, bay1.getStoredBoxes().get(0));  // The box expiring soonest should come first
        assertEquals(box1, bay1.getStoredBoxes().get(1));  // The box with the later expiration date should come second
    }
}
