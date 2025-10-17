package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RailwayLineTest {

    // 🔹 Helpers para criar objetos mínimos
    private Station createStation(String name) {
        // Adapta conforme a tua implementação de Station
        return new Station(name, "City_" + name, 0.0, 0.0);
    }

    private LineSegment createSegment(String origin, String destination, double dist) {
        return new LineSegment(createStation(origin), createStation(destination), dist, 100.0, false);
    }

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        LineSegment seg1 = createSegment("Lisboa", "Coimbra", 200);
        LineSegment seg2 = createSegment("Coimbra", "Porto", 120);
        List<LineSegment> segments = List.of(seg1, seg2);

        // Act
        RailwayLine line = new RailwayLine(segments);

        // Assert
        assertEquals(seg1.getOrigin(), line.getStartStation());
        assertEquals(seg2.getDestination(), line.getEndStation());
        assertEquals(2, line.getSegments().size());
        assertEquals(segments, line.getSegments());
    }

    @Test
    public void testGetSegmentsReturnsUnmodifiableList() {
        // Arrange
        LineSegment seg = createSegment("A", "B", 10);
        RailwayLine line = new RailwayLine(List.of(seg));

        // Act & Assert
        List<LineSegment> segs = line.getSegments();
        assertThrows(UnsupportedOperationException.class, () -> segs.add(createSegment("B", "C", 15)));
    }

    @Test
    public void testConstructorRejectsEmptyList() {
        // Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new RailwayLine(List.of()));
        assertTrue(ex.getMessage().contains("must have at least one segment"));
    }

    @Test
    public void testConstructorRejectsNullList() {
        // Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new RailwayLine(null));
        assertTrue(ex.getMessage().contains("must have at least one segment"));
    }



    @Test
    public void testSingleSegmentLineStartAndEndAreCorrect() {
        // Arrange
        LineSegment seg = createSegment("Origin", "Destination", 100);

        // Act
        RailwayLine line = new RailwayLine(List.of(seg));

        // Assert
        assertEquals(seg.getOrigin(), line.getStartStation());
        assertEquals(seg.getDestination(), line.getEndStation());
    }
}

