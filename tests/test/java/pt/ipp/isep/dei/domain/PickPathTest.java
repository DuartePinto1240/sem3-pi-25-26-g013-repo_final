package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PickPath}.
 * <p>
 * These tests validate the behavior of the PickPath class, which represents
 * a sequence of pick stops along with the total distance traveled.
 * </p>
 */
public class PickPathTest {

    // 🔹 Helper to create PickStop for testing
    private PickStop mockStop(String sku, int aisle, int bay) {
        Coordinate coordinate = new Coordinate(aisle, bay);
        Allocation allocation = new Allocation("ORD1", 1, sku, 10, 5.0, "BOX1", aisle, bay);
        return new PickStop(coordinate, allocation);
    }

    /**
     * Tests the constructor and getters of PickPath.
     * Ensures that the sequence and total distance are correctly stored.
     */
    @Test
    public void testConstructorAndGetters() {
        List<PickStop> seq = List.of(
                mockStop("SKU001", 1, 1),
                mockStop("SKU002", 2, 2),
                mockStop("SKU003", 3, 3)
        );
        double totalDistance = 42.7;

        PickPath path = new PickPath(seq, totalDistance);

        assertEquals(seq, path.getSequence());
        assertEquals(totalDistance, path.getTotalDistance());
        assertEquals(3, path.getSequence().size());
    }

    /**
     * Tests that the toString method includes all stops and the total distance.
     */
    @Test
    public void testToStringIncludesStopsAndDistance() {
        PickStop s1 = mockStop("SKU100", 1, 2);
        PickStop s2 = mockStop("SKU200", 3, 4);
        PickPath path = new PickPath(List.of(s1, s2), 10.5);

        String text = path.toString();
        System.out.println("toString() => " + text);

        assertTrue(text.contains("SKU100"));
        assertTrue(text.contains("SKU200"));
        assertTrue(text.contains("Total distance"));
        assertTrue(text.contains("10.5"));
    }

    /**
     * Tests that an empty PickPath sequence produces a readable string.
     */
    @Test
    public void testEmptySequenceProducesReadableString() {
        PickPath path = new PickPath(List.of(), 0.0);

        String text = path.toString();

        assertTrue(text.contains("Total distance"));
        assertTrue(text.endsWith("0.0"));
    }

    /**
     * Tests that the order of pick stops is preserved in the string representation.
     */
    @Test
    public void testToStringOrderOfStopsPreserved() {
        PickStop s1 = mockStop("SKU_A", 1, 1);
        PickStop s2 = mockStop("SKU_B", 2, 2);
        PickStop s3 = mockStop("SKU_C", 3, 3);
        PickPath path = new PickPath(List.of(s1, s2, s3), 30.0);

        String text = path.toString();

        int indexA = text.indexOf("SKU_A");
        int indexB = text.indexOf("SKU_B");
        int indexC = text.indexOf("SKU_C");

        assertTrue(indexA < indexB && indexB < indexC, "The order of stops should be preserved");
    }
}
