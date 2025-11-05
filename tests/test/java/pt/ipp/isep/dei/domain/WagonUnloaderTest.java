package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WagonUnloaderTest {

    private Warehouse warehouse;
    private WagonUnloader unloader;
    private Bay bay1;
    private Bay bay2;
    private Bay bay3;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse("W1");

        bay1 = new Bay("W1", 1, 1, 2);
        bay2 = new Bay("W1", 1, 2, 2);
        bay3 = new Bay("W1", 2, 1, 2);

        warehouse.addBay(bay1);
        warehouse.addBay(bay2);
        warehouse.addBay(bay3);

        unloader = new WagonUnloader(warehouse);
    }

    @Test
    void ensureBoxesAreGroupedBySKU() {
        Wagon wagon = new Wagon("WG1");
        wagon.addBox(new Box("B1", "SKU1", 1, LocalDate.now(), LocalDateTime.now()));
        wagon.addBox(new Box("B2", "SKU2", 1, null, LocalDateTime.now()));
        wagon.addBox(new Box("B3", "SKU1", 1, LocalDate.now().plusDays(3), LocalDateTime.now()));

        unloader.unload(List.of(wagon));

        assertEquals(2, bay1.getStoredBoxes().size());
        assertTrue(bay1.getStoredBoxes().stream().allMatch(b -> b.getSku().equals("SKU1")));
    }

    @Test
    void ensureFEFOOrderIsRespected() {
        LocalDateTime now = LocalDateTime.now();
        Wagon wagon = new Wagon("WG1");

        wagon.addBox(new Box("B1", "SKU1", 1, LocalDate.now().plusDays(5), now));
        wagon.addBox(new Box("B2", "SKU1", 1, LocalDate.now().plusDays(1), now));

        unloader.unload(List.of(wagon));

        List<Box> stored = bay1.getStoredBoxes();
        assertEquals("B2", stored.get(0).getBoxId());
        assertEquals("B1", stored.get(1).getBoxId());
    }

    @Test
    void ensureNextBayIsUsedWhenCapacityIsExceeded() {
        LocalDateTime now = LocalDateTime.now();
        Wagon wagon = new Wagon("WG1");

        wagon.addBox(new Box("B1", "SKU1", 1, LocalDate.now(), now));
        wagon.addBox(new Box("B2", "SKU1", 1, LocalDate.now(), now));
        wagon.addBox(new Box("B3", "SKU1", 1, LocalDate.now(), now));

        unloader.unload(List.of(wagon));

        assertEquals(2, bay1.getStoredBoxes().size());
        assertEquals(1, bay2.getStoredBoxes().size());
    }

    @Test
    void ensureEachBayContainsOnlyOneSKU() {
        LocalDateTime now = LocalDateTime.now();
        Wagon wagon = new Wagon("WG1");

        wagon.addBox(new Box("B1", "SKU1", 1, LocalDate.now(), now));
        wagon.addBox(new Box("B2", "SKU2", 1, LocalDate.now(), now));

        unloader.unload(List.of(wagon));

        assertEquals("SKU1", bay1.getStoredBoxes().get(0).getSku());
        assertEquals("SKU2", bay2.getStoredBoxes().get(0).getSku());
    }

    @Test
    void ensureFIFOWhenNoExpiryDate() {
        LocalDateTime now = LocalDateTime.now();
        Wagon wagon = new Wagon("WG1");

        wagon.addBox(new Box("B1", "SKU1", 1, null, now.minusDays(5)));
        wagon.addBox(new Box("B2", "SKU1", 1, null, now.minusDays(10)));

        unloader.unload(List.of(wagon));

        List<Box> stored = bay1.getStoredBoxes();
        assertEquals("B2", stored.get(0).getBoxId()); // mais antiga primeiro → FIFO
        assertEquals("B1", stored.get(1).getBoxId());
    }
}
