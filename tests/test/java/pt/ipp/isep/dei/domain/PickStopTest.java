package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PickStopTest {

    // 🔹 Helper para criar um PickStop completo e válido
    private PickStop createPickStop(String sku, int aisle, int bay) {
        Coordinate coordinate = new Coordinate(aisle, bay);
        Allocation allocation = new Allocation("ORD001", 1, sku, 10, 5.0, "BOX001", aisle, bay);
        return new PickStop(coordinate, allocation);
    }

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        Coordinate coord = new Coordinate(2, 5);
        Allocation alloc = new Allocation("ORD1", 1, "SKU123", 20, 15.0, "BOX9", 2, 5);

        // Act
        PickStop stop = new PickStop(coord, alloc);

        // Assert
        assertEquals(coord, stop.getCoordinate());
        assertEquals(alloc, stop.getAllocation());
    }

    @Test
    public void testToStringFormat() {
        // Arrange
        PickStop stop = createPickStop("SKU100", 3, 7);

        // Act
        String text = stop.toString();
        System.out.println("PickStop toString() => " + text);

        // Assert
        assertTrue(text.contains("SKU100"));
        assertTrue(text.contains("@"));
        assertTrue(text.contains("("));
        assertTrue(text.contains(","));
        assertTrue(text.contains(")"));
    }

    @Test
    public void testToStringMatchesExpectedFormat() {
        // Arrange
        Coordinate coord = new Coordinate(1, 2);
        Allocation alloc = new Allocation("ORD2", 2, "SKU55", 5, 3.0, "BOX2", 1, 2);
        PickStop stop = new PickStop(coord, alloc);

        // Act
        String expected = "SKU55 @ (1,2)";
        String actual = stop.toString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testDifferentStopsHaveDifferentToString() {
        // Arrange
        PickStop s1 = createPickStop("SKU001", 1, 1);
        PickStop s2 = createPickStop("SKU002", 2, 2);

        // Act
        String str1 = s1.toString();
        String str2 = s2.toString();

        // Assert
        assertNotEquals(str1, str2);
    }

    @Test
    public void testNullCoordinateOrAllocationThrowsException() {
        Coordinate coord = new Coordinate(1, 1);
        Allocation alloc = new Allocation("ORDX", 1, "SKU_X", 5, 2.0, "BOX_X", 1, 1);

        // Coordinate null
        assertThrows(NullPointerException.class, () -> {
            PickStop invalid = new PickStop(null, alloc);
            invalid.toString(); // força acesso a campo
        });

        // Allocation null
        assertThrows(NullPointerException.class, () -> {
            PickStop invalid = new PickStop(coord, null);
            invalid.toString(); // força o uso de allocation.getSku()
        });
    }
}

