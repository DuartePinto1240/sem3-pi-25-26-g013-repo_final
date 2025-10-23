package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PickStop}.
 * <p>
 * These tests verify the behavior of the PickStop class, which represents a stop
 * in a picking path associated with a specific allocation and coordinate.
 * </p>
 */
public class PickStopTest {

    // 🔹 Helper to create a valid PickStop for testing
    private PickStop createPickStop(String sku, int aisle, int bay) {
        Coordinate coordinate = new Coordinate(aisle, bay);
        Allocation allocation = new Allocation("ORD001", 1, sku, 10, 5.0, "BOX001", aisle, bay);
        return new PickStop(coordinate, allocation);
    }

    /**
     * Tests the constructor and getters of PickStop.
     * Ensures that the coordinate and allocation are correctly stored.
     */
    @Test
    public void testConstructorAndGetters() {
        Coordinate coord = new Coordinate(2, 5);
        Allocation alloc = new Allocation("ORD1", 1, "SKU123", 20, 15.0, "BOX9", 2, 5);

        PickStop stop = new PickStop(coord, alloc);

        assertEquals(coord, stop.getCoordinate());
        assertEquals(alloc, stop.getAllocation());
    }

    /**
     * Tests that the toString method produces a string containing the SKU and coordinate.
     */
    @Test
    public void testToStringFormat() {
        PickStop stop = createPickStop("SKU100", 3, 7);

        String text = stop.toString();
        System.out.println("PickStop toString() => " + text);

        assertTrue(text.contains("SKU100"));
        assertTrue(text.contains("@"));
        assertTrue(text.contains("("));
        assertTrue(text.contains(","));
        assertTrue(text.contains(")"));
    }

    /**
     * Tests that the toString method matches the expected format "SKU @ (aisle,bay)".
     */
    @Test
    public void testToStringMatchesExpectedFormat() {
        Coordinate coord = new Coordinate(1, 2);
        Allocation alloc = new Allocation("ORD2", 2, "SKU55", 5, 3.0, "BOX2", 1, 2);
        PickStop stop = new PickStop(coord, alloc);

        String expected = "SKU55 @ (1,2)";
        String actual = stop.toString();

        assertEquals(expected, actual);
    }

    /**
     * Tests that different PickStop objects with different SKUs or coordinates
     * produce different toString outputs.
     */
    @Test
    public void testDifferentStopsHaveDifferentToString() {
        PickStop s1 = createPickStop("SKU001", 1, 1);
        PickStop s2 = createPickStop("SKU002", 2, 2);

        String str1 = s1.toString();
        String str2 = s2.toString();

        assertNotEquals(str1, str2);
    }

    /**
     * Tests that constructing a PickStop with a null coordinate or allocation
     * throws a NullPointerException when accessed.
     */
    @Test
    public void testNullCoordinateOrAllocationThrowsException() {
        Coordinate coord = new Coordinate(1, 1);
        Allocation alloc = new Allocation("ORDX", 1, "SKU_X", 5, 2.0, "BOX_X", 1, 1);

        // Coordinate null
        assertThrows(NullPointerException.class, () -> {
            PickStop invalid = new PickStop(null, alloc);
            invalid.toString(); // forces access to field
        });

        // Allocation null
        assertThrows(NullPointerException.class, () -> {
            PickStop invalid = new PickStop(coord, null);
            invalid.toString(); // forces access to allocation.getSku()
        });
    }
}
