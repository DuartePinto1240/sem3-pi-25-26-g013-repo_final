package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WagonUnloaderTest {

    @Test
    public void testUnloadFIFO() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay1 = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay1);

        // Criando caixas não perecíveis (FIFO)
        Box box1 = new Box("BOX001", "SKU001", 10, null, LocalDateTime.of(2025, 7, 5, 15, 42, 0));
        Box box2 = new Box("BOX002", "SKU002", 15, null, LocalDateTime.of(2025, 6, 10, 10, 30, 0));
        Wagon wagon = new Wagon("WGN001", List.of(box1, box2));

        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(List.of(wagon));

        // As caixas devem ser armazenadas em ordem FIFO
        assertEquals(box2, bay1.getStoredBoxes().get(0));  // A caixa mais antiga deve vir primeiro
        assertEquals(box1, bay1.getStoredBoxes().get(1));
    }

    @Test
    public void testUnloadFEFO() {
        Warehouse warehouse = new Warehouse("W1");
        Bay bay1 = new Bay("W1", 1, 1, 10);
        warehouse.addBay(bay1);

        // Criando caixas perecíveis (FEFO)
        Box box1 = new Box("BOX001", "SKU001", 10, LocalDate.of(2025, 12, 31), LocalDateTime.of(2025, 7, 5, 15, 42, 0));
        Box box2 = new Box("BOX002", "SKU002", 15, LocalDate.of(2025, 6, 30), LocalDateTime.of(2025, 6, 10, 10, 30, 0));
        Wagon wagon = new Wagon("WGN001", List.of(box1, box2));

        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(List.of(wagon));

        // As caixas devem ser armazenadas em ordem FEFO (expiração mais próxima primeiro)
        assertEquals(box2, bay1.getStoredBoxes().get(0));  // A caixa com data de expiração mais próxima deve vir primeiro
        assertEquals(box1, bay1.getStoredBoxes().get(1));  // A caixa com data de expiração mais distante vem depois
    }




}

