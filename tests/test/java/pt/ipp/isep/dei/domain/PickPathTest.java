package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PickPathTest {

    // 🔹 Helper para criar PickStop de teste
    private PickStop mockStop(String sku, int aisle, int bay) {
        Coordinate coordinate = new Coordinate(aisle, bay);
        Allocation allocation = new Allocation("ORD1", 1, sku, 10, 5.0, "BOX1", aisle, bay);
        return new PickStop(coordinate, allocation);
    }

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        List<PickStop> seq = List.of(
                mockStop("SKU001", 1, 1),
                mockStop("SKU002", 2, 2),
                mockStop("SKU003", 3, 3)
        );
        double totalDistance = 42.7;

        // Act
        PickPath path = new PickPath(seq, totalDistance);

        // Assert
        assertEquals(seq, path.getSequence());
        assertEquals(totalDistance, path.getTotalDistance());
        assertEquals(3, path.getSequence().size());
    }

    @Test
    public void testToStringIncludesStopsAndDistance() {
        // Arrange
        PickStop s1 = mockStop("SKU100", 1, 2);
        PickStop s2 = mockStop("SKU200", 3, 4);
        PickPath path = new PickPath(List.of(s1, s2), 10.5);

        // Act
        String text = path.toString();
        System.out.println("toString() => " + text);

        // Assert
        assertTrue(text.contains("SKU100"));
        assertTrue(text.contains("SKU200"));
        assertTrue(text.contains("Total distance"));
        assertTrue(text.contains("10.5"));
    }

    @Test
    public void testEmptySequenceProducesReadableString() {
        // Arrange
        PickPath path = new PickPath(List.of(), 0.0);

        // Act
        String text = path.toString();

        // Assert
        assertTrue(text.contains("Total distance"));
        assertTrue(text.endsWith("0.0"));
    }

    @Test
    public void testToStringOrderOfStopsPreserved() {
        // Arrange
        PickStop s1 = mockStop("SKU_A", 1, 1);
        PickStop s2 = mockStop("SKU_B", 2, 2);
        PickStop s3 = mockStop("SKU_C", 3, 3);
        PickPath path = new PickPath(List.of(s1, s2, s3), 30.0);

        // Act
        String text = path.toString();

        // Assert
        int indexA = text.indexOf("SKU_A");
        int indexB = text.indexOf("SKU_B");
        int indexC = text.indexOf("SKU_C");

        assertTrue(indexA < indexB && indexB < indexC, "A ordem dos stops deve ser preservada");
    }
}

