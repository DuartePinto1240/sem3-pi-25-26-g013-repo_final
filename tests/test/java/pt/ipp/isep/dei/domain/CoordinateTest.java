package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Coordinate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        Coordinate c = new Coordinate(2, 5);

        // Assert
        assertEquals(2, c.getAisle());
        assertEquals(5, c.getBay());
    }

    @Test
    public void testToStringFormat() {
        // Arrange
        Coordinate c = new Coordinate(3, 7);

        // Act
        String text = c.toString();

        // Assert
        assertEquals("(3,7)", text);
    }

    @Test
    public void testDistanceSameAisle() {
        // Arrange
        Coordinate c1 = new Coordinate(2, 3);
        Coordinate c2 = new Coordinate(2, 8);

        // Act
        int distance = c1.distanceTo(c2);

        // Assert
        assertEquals(5, distance, "Deve calcular diferença absoluta no mesmo corredor");
    }

    @Test
    public void testDistanceDifferentAisles() {
        // Arrange
        Coordinate c1 = new Coordinate(1, 2);
        Coordinate c2 = new Coordinate(3, 4);

        // Act
        int distance = c1.distanceTo(c2);

        // Formula: this.bay + |aisle diff| * 3 + other.bay = 2 + 6 + 4 = 12
        assertEquals(12, distance, "Deve aplicar fórmula correta quando os corredores são diferentes");
    }



    @Test
    public void testDistanceToItselfIsZero() {
        // Arrange
        Coordinate c = new Coordinate(5, 9);

        // Act
        int distance = c.distanceTo(c);

        // Assert
        assertEquals(0, distance);
    }

    @Test
    void testDistanceTo_DifferentAisles_ReversedOrder() {
        Coordinate c1 = new Coordinate(3, 4);
        Coordinate c2 = new Coordinate(1, 2);

        // Fórmula: 4 + (|3-1|*3) + 2 = 4 + 6 + 2 = 12
        int distance = c1.distanceTo(c2);
        assertEquals(12, distance);
    }
}

