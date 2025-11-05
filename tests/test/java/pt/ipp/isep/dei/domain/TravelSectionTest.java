package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TravelSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link TravelSection} class.
 * <p>
 * These tests verify that a {@code TravelSection} correctly stores and returns
 * its associated {@link LineSegment}, effective speed, and travel time.
 * It also ensures object reference integrity and consistent data behavior.
 * </p>
 */
public class TravelSectionTest {

    private Station origin;
    private Station destination;
    private LineSegment segment;

    /**
     * Sets up shared test data before each test.
     * Creates two {@link Station} instances and a connecting {@link LineSegment}.
     */
    @BeforeEach
    public void setUp() {
        origin = new Station("ST001", "Porto", 41.15, -8.61);
        destination = new Station("ST002", "Lisboa", 38.72, -9.14);
        // The LineSegment includes distance, max speed, and electrification flag.
        segment = new LineSegment(1, origin, destination, 313.0, 160.0, true);

    }


    /**
     * Verifies that the constructor correctly initializes all fields
     * and that the getter methods return the expected values.
     */
    @Test
    public void testConstructorAndGetters_ShouldReturnCorrectValues() {
        TravelSection section = new TravelSection(segment, 120.0, 156.5);

        assertEquals(segment, section.getSegment());
        assertEquals(120.0, section.getEffectiveSpeedKmH());
        assertEquals(156.5, section.getSectionTimeMinutes());
    }

    /**
     * Ensures that the {@link LineSegment} reference stored inside
     * the {@link TravelSection} is the same instance passed to the constructor.
     */
    @Test
    public void testSegmentReferenceIntegrity_ShouldBeSameInstance() {
        TravelSection section = new TravelSection(segment, 100.0, 188.0);
        assertSame(segment, section.getSegment());
    }
}

