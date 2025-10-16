package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Wagon;
import main.java.pt.ipp.isep.dei.domain.Box;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WagonTest {

    @Test
    public void testConstructorWithEmptyList() {
        // Arrange
        Wagon wagon = new Wagon("WGN001");

        // Assert
        assertEquals("WGN001", wagon.getWagonId());
        assertNotNull(wagon.getBoxes());
        assertTrue(wagon.getBoxes().isEmpty());
    }

    @Test
    public void testConstructorWithBoxesList() {
        // Arrange
        Box box1 = new Box("BOX001", "SKU001", 10, null, LocalDateTime.now());
        Box box2 = new Box("BOX002", "SKU002", 20, LocalDate.of(2025, 12, 31), LocalDateTime.now());
        List<Box> boxes = List.of(box1, box2);

        // Act
        Wagon wagon = new Wagon("WGN002", boxes);

        // Assert
        assertEquals("WGN002", wagon.getWagonId());
        assertEquals(2, wagon.getBoxes().size());
        assertTrue(wagon.getBoxes().contains(box1));
        assertTrue(wagon.getBoxes().contains(box2));
    }

    @Test
    public void testAddBoxIncreasesListSize() {
        // Arrange
        Wagon wagon = new Wagon("WGN003");
        Box box = new Box("BOX100", "SKU999", 15, null, LocalDateTime.now());

        // Act
        wagon.addBox(box);

        // Assert
        assertEquals(1, wagon.getBoxes().size());
        assertEquals(box, wagon.getBoxes().get(0));
    }

    @Test
    public void testAddMultipleBoxesMaintainsOrder() {
        // Arrange
        Wagon wagon = new Wagon("WGN004");
        Box box1 = new Box("BOX_A", "SKU_A", 5, null, LocalDateTime.now());
        Box box2 = new Box("BOX_B", "SKU_B", 8, null, LocalDateTime.now());

        // Act
        wagon.addBox(box1);
        wagon.addBox(box2);

        // Assert
        assertEquals(2, wagon.getBoxes().size());
        assertEquals(box1, wagon.getBoxes().get(0));
        assertEquals(box2, wagon.getBoxes().get(1));
    }

    @Test
    public void testToStringContainsImportantInfo() {
        // Arrange
        Wagon wagon = new Wagon("WGN999");
        Box box = new Box("BOX900", "SKU900", 10, null, LocalDateTime.now());
        wagon.addBox(box);

        // Act
        String text = wagon.toString();

        // Assert
        assertTrue(text.contains("WGN999"));
        assertTrue(text.contains("boxes=1"));
    }
}

