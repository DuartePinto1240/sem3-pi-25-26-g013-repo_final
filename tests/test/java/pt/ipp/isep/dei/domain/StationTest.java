package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Station;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Station} class.
 * <p>
 * These tests cover the creation of Station objects, validation of input parameters,
 * equality and hashCode behavior, and the toString method formatting.
 * </p>
 */
public class StationTest {

    /**
     * Tests that a Station is created correctly with valid data.
     */
    @Test
    public void testConstructor_WithValidData_ShouldCreateStation() {
        Station s = new Station("ST001", "Central Station", 41.1496, -8.6109,0,0);

        assertEquals("ST001", s.getId());
        assertEquals("Central Station", s.getName());
        assertEquals(41.1496, s.getLatitude());
        assertEquals(-8.6109, s.getLongitude());
    }

    /**
     * Tests that if coordinates are not provided, they default to 0.0.
     */
    @Test
    public void testConstructor_WithoutCoordinates_ShouldDefaultToZero() {
        Station s = new Station("ST002", "North Station");

        assertEquals("ST002", s.getId());
        assertEquals("North Station", s.getName());
        assertEquals(0.0, s.getLatitude());
        assertEquals(0.0, s.getLongitude());
    }

    /**
     * Tests that creating a Station with an empty ID throws an exception.
     */
    @Test
    public void testConstructor_WithInvalidId_ShouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new Station("", "Test Station"));
        assertEquals("Station ID and Name must not be empty.", e.getMessage());
    }

    /**
     * Tests that creating a Station with an empty name throws an exception.
     */
    @Test
    public void testConstructor_WithInvalidName_ShouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new Station("ST003", ""));
        assertEquals("Station ID and Name must not be empty.", e.getMessage());
    }

    /**
     * Tests that two Station objects with the same ID are considered equal.
     */
    @Test
    public void testEquals_SameId_ShouldBeEqual() {
        Station s1 = new Station("ST001", "Central Station");
        Station s2 = new Station("ST001", "Central Station Copy");

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    /**
     * Tests that two Station objects with different IDs are not equal.
     */
    @Test
    public void testEquals_DifferentId_ShouldNotBeEqual() {
        Station s1 = new Station("ST001", "Central");
        Station s2 = new Station("ST002", "Central");

        assertNotEquals(s1, s2);
    }

    /**
     * Tests that a Station object is equal to itself.
     */
    @Test
    public void testEquals_SameObject_ShouldReturnTrue() {
        Station s = new Station("ST001", "Central");
        assertEquals(s, s);
    }

    /**
     * Tests that a Station object is not equal to null.
     */
    @Test
    public void testEquals_NullObject_ShouldReturnFalse() {
        Station s = new Station("ST001", "Central");
        assertNotEquals(s, null);
    }

    /**
     * Tests that a Station object is not equal to an object of a different class.
     */
    @Test
    public void testEquals_DifferentClass_ShouldReturnFalse() {
        Station s = new Station("ST001", "Central");
        assertNotEquals(s, "Central Station");
    }

    /**
     * Tests that the toString method returns the expected formatted string.
     */
    @Test
    public void testToString_ShouldReturnFormattedText() {
        Station s = new Station("ST001", "Central Station");
        assertEquals("Central Station (ST001)", s.toString());
    }
}
