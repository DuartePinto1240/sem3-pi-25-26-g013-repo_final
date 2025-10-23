package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RailwayLine}.
 * <p>
 * These tests verify the behavior of the RailwayLine class, including correct initialization,
 * segment list immutability, and validation of constructor arguments.
 * </p>
 */
public class RailwayLineTest {

    // ---- Helper methods for creating minimal objects ----

    /**
     * Creates a test {@link Station} with given name.
     *
     * @param name the station name
     * @return a new Station object
     */
    private Station createStation(String name) {
        return new Station(name, "City_" + name, 0.0, 0.0);
    }

    /**
     * Creates a test {@link LineSegment} between two stations with a specified distance
     * and a sequence number.
     *
     * @param sequenceNumber the sequence number of the segment in the line
     * @param origin         the origin station name
     * @param destination    the destination station name
     * @param dist           the distance of the segment
     * @return a new LineSegment object
     */
    private LineSegment createSegment(int sequenceNumber, String origin, String destination, double dist) {
        return new LineSegment(sequenceNumber, createStation(origin), createStation(destination), dist, 100.0, false);
    }

    /**
     * Tests that the RailwayLine constructor and getters return expected values.
     */
    @Test
    public void testConstructorAndGetters() {
        LineSegment seg1 = createSegment(1, "Lisboa", "Coimbra", 200);
        LineSegment seg2 = createSegment(2, "Coimbra", "Porto", 120);
        List<LineSegment> segments = List.of(seg1, seg2);

        RailwayLine line = new RailwayLine(segments);

        assertEquals(seg1.getOrigin(), line.getStartStation());
        assertEquals(seg2.getDestination(), line.getEndStation());
        assertEquals(2, line.getSegments().size());
        assertEquals(segments, line.getSegments());
    }

    /**
     * Tests that {@link RailwayLine#getSegments()} returns an unmodifiable list.
     */
    @Test
    public void testGetSegmentsReturnsUnmodifiableList() {
        LineSegment seg = createSegment(1, "A", "B", 10);
        RailwayLine line = new RailwayLine(List.of(seg));

        List<LineSegment> segs = line.getSegments();
        assertThrows(UnsupportedOperationException.class, () -> segs.add(createSegment(2, "B", "C", 15)));
    }

    /**
     * Tests that the constructor rejects an empty list of segments.
     */
    @Test
    public void testConstructorRejectsEmptyList() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new RailwayLine(List.of()));
        assertTrue(ex.getMessage().contains("must have at least one segment"));
    }

    /**
     * Tests that the constructor rejects a null list of segments.
     */
    @Test
    public void testConstructorRejectsNullList() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new RailwayLine(null));
        assertTrue(ex.getMessage().contains("must have at least one segment"));
    }

    /**
     * Tests that for a single-segment line, start and end stations are correctly returned.
     */
    @Test
    public void testSingleSegmentLineStartAndEndAreCorrect() {
        LineSegment seg = createSegment(1, "Origin", "Destination", 100);
        RailwayLine line = new RailwayLine(List.of(seg));

        assertEquals(seg.getOrigin(), line.getStartStation());
        assertEquals(seg.getDestination(), line.getEndStation());
    }
}
