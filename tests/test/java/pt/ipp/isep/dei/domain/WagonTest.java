package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Wagon;
import main.java.pt.ipp.isep.dei.domain.Box;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Wagon} class.
 * <p>
 * These tests verify the correct behavior of the {@code Wagon} class, including
 * its constructors, box management (adding boxes and maintaining order),
 * and textual representation.
 * </p>
 */
public class WagonTest {

    /**
     * Verifies that the constructor initializes an empty {@link Wagon} correctly
     * when no boxes are provided.
     * The wagon should have a valid ID and an empty box list.
     */
    @Test
    public void testConstructorWithEmptyList() {
        // Arrange
        Wagon wagon = new Wagon("WGN001");

        // Assert
        assertEquals("WGN001", wagon.getWagonId());
        assertNotNull(wagon.getBoxes());
        assertTrue(wagon.getBoxes().isEmpty());
    }

    /**
     * Verifies that boxes can be added to a {@link Wagon} after construction
     * and that they are correctly stored.
     */
    @Test
    public void testConstructorWithBoxesAdded() {
        // Arrange
        Box box1 = new Box("BOX001", "SKU001", 10, null, LocalDateTime.now());
        Box box2 = new Box("BOX002", "SKU002", 20, LocalDate.of(2025, 12, 31), LocalDateTime.now());

        // Act
        Wagon wagon = new Wagon("WGN002");
        wagon.addBox(box1);
        wagon.addBox(box2);

        // Assert
        assertEquals("WGN002", wagon.getWagonId());
        assertEquals(2, wagon.getBoxes().size());
        assertTrue(wagon.getBoxes().contains(box1));
        assertTrue(wagon.getBoxes().contains(box2));
    }

    /**
     * Ensures that adding a {@link Box} to a {@link Wagon}
     * increases the number of boxes in the list.
     */
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

    /**
     * Verifies that boxes are stored in the same order they were added to the {@link Wagon}.
     */
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

    /**
     * Tests that the {@link Wagon#toString()} method includes
     * relevant details such as the wagon ID and the number of boxes.
     */
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

    /**
     * Verifies that {@link Wagon#getBoxes()} returns an unmodifiable list
     * to prevent external modification of the wagon's box collection.
     */
    @Test
    public void testGetBoxesReturnsUnmodifiableList() {
        // Arrange
        Wagon wagon = new Wagon("WGN005");
        Box box = new Box("BOX500", "SKU500", 10, null, LocalDateTime.now());
        wagon.addBox(box);

        // Act & Assert
        List<Box> boxes = wagon.getBoxes();
        assertThrows(UnsupportedOperationException.class, () ->
                boxes.add(new Box("BOX501", "SKU501", 5, null, LocalDateTime.now()))
        );
    }

    /**
     * Verifies that adding the same box multiple times is allowed.
     */
    @Test
    public void testAddSameBoxMultipleTimes() {
        // Arrange
        Wagon wagon = new Wagon("WGN006");
        Box box = new Box("BOX600", "SKU600", 10, null, LocalDateTime.now());

        // Act
        wagon.addBox(box);
        wagon.addBox(box);

        // Assert
        assertEquals(2, wagon.getBoxes().size());
        // Both entries should reference the same box object
        assertSame(wagon.getBoxes().get(0), wagon.getBoxes().get(1));
    }

    /**
     * Tests that a wagon with no boxes has a toString that reflects zero boxes.
     */
    @Test
    public void testToStringWithNoBoxes() {
        // Arrange
        Wagon wagon = new Wagon("WGN007");

        // Act
        String text = wagon.toString();

        // Assert
        assertTrue(text.contains("WGN007"));
        assertTrue(text.contains("boxes=0"));
    }

    /**
     * Tests that adding a null box is safely ignored.
     */
    @Test
    public void testAddNullBoxIsIgnored() {
        // Arrange
        Wagon wagon = new Wagon("WGN008");

        // Act
        wagon.addBox(null);

        // Assert
        assertEquals(0, wagon.getBoxes().size());
    }

    /**
     * Verifies that getNumber() returns the same value as getWagonId().
     */
    @Test
    public void testGetNumberReturnsSameAsGetWagonId() {
        // Arrange
        Wagon wagon = new Wagon("WGN009");

        // Assert
        assertEquals(wagon.getWagonId(), wagon.getNumber());
    }
}