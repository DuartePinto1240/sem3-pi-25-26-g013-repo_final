package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.Station;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LineSegment}.
 * <p>
 * These tests cover constructor validation, getters, toString formatting,
 * and behavior for electrification, distance, and max speed.
 * </p>
 */
public class LineSegmentTest {

    /**
     * Helper method to create mock Station objects for testing.
     *
     * @param name the name of the station
     * @return a Station instance with dummy coordinates
     */
    private Station createStation(String name) {
        return new Station(name, "City_" + name, 0, 0);
    }

    /**
     * Verifies that constructor initializes fields correctly
     * and getters return the expected values.
     */
    @Test
    public void testConstructorAndGetters() {
        Station s1 = createStation("A");
        Station s2 = createStation("B");

        LineSegment segment = new LineSegment(1, s1, s2, 12.5, 120.0, true);

        assertEquals(s1, segment.getOrigin());
        assertEquals(s2, segment.getDestination());
        assertEquals(12.5, segment.getDistanceKm());
        assertEquals(120.0, segment.getMaxSpeedKmH());
        assertTrue(segment.isElectrified());
    }

    /**
     * Ensures that the {@link LineSegment#toString()} method
     * contains origin, destination, distance, speed, and electrification info.
     */
    @Test
    public void testToStringFormat() {
        Station origin = createStation("Lisboa");
        Station destination = createStation("Porto");
        LineSegment seg = new LineSegment(1, origin, destination, 313.0, 200.0, true);

        String text = seg.toString();
        System.out.println("toString() => " + text);

        assertTrue(text.contains("Lisboa"));
        assertTrue(text.contains("Porto"));
        assertTrue(text.contains("km"));
        assertTrue(text.contains("200"));
        assertTrue(text.contains("electrified=true"));
    }

    /**
     * Verifies that constructor throws exception when origin or destination is null.
     */
    @Test
    public void testConstructorRejectsNullStations() {
        Station s1 = createStation("Valid");

        Exception ex1 = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(1, null, s1, 10.0, 100.0, true));
        assertTrue(ex1.getMessage().contains("Origin"));

        Exception ex2 = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(1, s1, null, 10.0, 100.0, true));
        assertTrue(ex2.getMessage().contains("Destination"));
    }

    /**
     * Verifies that constructor throws exception for non-positive distance.
     */
    @Test
    public void testConstructorRejectsNonPositiveDistance() {
        Station s1 = createStation("A");
        Station s2 = createStation("B");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(1, s1, s2, 0, 100.0, false));
        assertTrue(ex.getMessage().contains("Distance"));
    }

    /**
     * Verifies that constructor throws exception for non-positive max speed.
     */
    @Test
    public void testConstructorRejectsNonPositiveSpeed() {
        Station s1 = createStation("A");
        Station s2 = createStation("B");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(1, s1, s2, 10.0, -50.0, false));
        assertTrue(ex.getMessage().contains("Max Speed"));
    }

    /**
     * Ensures that the isElectrified flag works when false.
     */
    @Test
    public void testElectrifiedFalse() {
        Station s1 = createStation("X");
        Station s2 = createStation("Y");
        LineSegment segment = new LineSegment(2, s1, s2, 8.0, 80.0, false);

        assertFalse(segment.isElectrified());
    }
}

