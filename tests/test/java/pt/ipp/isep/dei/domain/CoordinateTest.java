package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Coordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinateTest {

    @Test
    void testDistanceTo_SameAisle() {
        Coordinate c1 = new Coordinate(1, 2);
        Coordinate c2 = new Coordinate(1, 5);

        int distance = c1.distanceTo(c2);
        assertEquals(3, distance);
    }

    @Test
    void testDistanceTo_DifferentAisles() {
        Coordinate c1 = new Coordinate(1, 2);
        Coordinate c2 = new Coordinate(3, 4);

        // Fórmula: this.bay + |aisle diff|*3 + other.bay = 2 + (4-2)*3 + 4 = 12
        int distance = c1.distanceTo(c2);
        assertEquals(12, distance);
    }

    @Test
    void testDistanceTo_DifferentAisles_ReversedOrder() {
        Coordinate c1 = new Coordinate(3, 4);
        Coordinate c2 = new Coordinate(1, 2);

        // Fórmula: 4 + (|3-1|*3) + 2 = 4 + 6 + 2 = 12
        int distance = c1.distanceTo(c2);
        assertEquals(12, distance);
    }

    @Test
    void testDistanceTo_SameCoordinate() {
        Coordinate c1 = new Coordinate(2, 3);
        Coordinate c2 = new Coordinate(2, 3);

        assertEquals(0, c1.distanceTo(c2));
    }

}
