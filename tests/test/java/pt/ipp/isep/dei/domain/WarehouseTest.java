package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Bay;
import main.java.pt.ipp.isep.dei.domain.Warehouse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTest {

    @Test
    public void testAddBay() {
        Warehouse warehouse = new Warehouse("W1");

        Bay bay1 = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay1);

        Bay retrievedBay = warehouse.getBay("W1", 1, 1);
        assertEquals(bay1, retrievedBay);  // O bay que foi adicionado deve ser o mesmo recuperado
    }

    @Test
    public void testGetBays() {
        Warehouse warehouse = new Warehouse("W1");

        Bay bay1 = new Bay("W1", 1, 1, 10);
        Bay bay2 = new Bay("W1", 1, 2, 15);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);

        assertEquals(2, warehouse.getBays().size());  // Deve retornar 2 bays
    }
}

