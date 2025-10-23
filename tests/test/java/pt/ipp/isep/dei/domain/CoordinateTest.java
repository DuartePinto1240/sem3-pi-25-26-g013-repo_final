package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Coordinate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Coordinate} class.
 * <p>
 * Verifies the correct storage of aisle and bay values, the calculation of distances between coordinates,
 * and the proper string representation format.
 * </p>
 */
public class CoordinateTest {

    /**
     * Tests the constructor and getter methods of {@link Coordinate}.
     * Ensures that aisle and bay values are correctly stored and returned.
     */
    @Test
    public void testConstructorAndGetters() {
        Coordinate c = new Coordinate(2, 5);

        assertEquals(2, c.getAisle());
        assertEquals(5, c.getBay());
    }

    /**
     * Verifies that the {@link Coordinate#toString()} method
     * returns the expected string format.
     */
    @Test
    public void testToStringFormat() {
        Coordinate c = new Coordinate(3, 7);
        String text = c.toString();
        assertEquals("(3,7)", text);
    }

    /**
     * Tests the distance calculation between two coordinates
     * that are located within the same aisle.
     */
    @Test
    public void testDistanceSameAisle() {
        Coordinate c1 = new Coordinate(2, 3);
        Coordinate c2 = new Coordinate(2, 8);

        int distance = c1.distanceTo(c2);

        assertEquals(5, distance, "Should calculate absolute difference when in the same aisle");
    }

    /**
     * Tests the distance calculation between coordinates located in different aisles.
     * <p>
     * The formula used is: {@code this.bay + |aisle difference| * 3 + other.bay}.
     * Example: 2 + 6 + 4 = 12.
     * </p>
     */
    @Test
    public void testDistanceDifferentAisles() {
        Coordinate c1 = new Coordinate(1, 2);
        Coordinate c2 = new Coordinate(3, 4);

        int distance = c1.distanceTo(c2);
        assertEquals(12, distance, "Should apply correct formula when aisles differ");
    }

    /**
     * Verifies that the distance from a coordinate to itself is zero.
     */
    @Test
    public void testDistanceToItselfIsZero() {
        Coordinate c = new Coordinate(5, 9);

        int distance = c.distanceTo(c);

        assertEquals(0, distance);
    }

    /**
     * Tests the distance calculation between coordinates in different aisles
     * when the order of the coordinates is reversed.
     * <p>
     * The expected distance is the same regardless of direction.
     * Formula: 4 + (|3-1| * 3) + 2 = 12.
     * </p>
     */
    @Test
    void testDistanceTo_DifferentAisles_ReversedOrder() {
        Coordinate c1 = new Coordinate(3, 4);
        Coordinate c2 = new Coordinate(1, 2);

        int distance = c1.distanceTo(c2);
        assertEquals(12, distance);
    }
}
