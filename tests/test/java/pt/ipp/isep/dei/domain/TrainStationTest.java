package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.TrainStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TrainStation class
 * Tests constructors, getters, setters, distance calculations, and object methods
 */
public class TrainStationTest {

    private TrainStation station1;
    private TrainStation station2;
    private TrainStation station3;

    @BeforeEach
    public void setUp() {
        // Berlin Hauptbahnhof
        station1 = new TrainStation(
                "DE", "Europe/Berlin", "CET",
                "Berlin Hauptbahnhof",
                52.525, 13.369,
                true, true, false
        );

        // Paris Gare du Nord
        station2 = new TrainStation(
                "FR", "Europe/Paris", "CET",
                "Paris Gare du Nord",
                48.881, 2.355,
                true, true, false
        );

        // Lisboa Santa Apolónia
        station3 = new TrainStation(
                "PT", "Europe/Lisbon", "WET/GMT",
                "Lisboa Santa Apolónia",
                38.713, -9.125,
                true, true, false
        );
    }

    // ========== CONSTRUCTOR AND GETTER TESTS ==========

    @Test
    @DisplayName("Test 1: Constructor initializes all fields correctly")
    public void testConstructor() {
        TrainStation station = new TrainStation(
                "ES", "Europe/Madrid", "CET",
                "Madrid Atocha",
                40.407, -3.691,
                true, true, false
        );

        assertEquals("ES", station.getCountry());
        assertEquals("Europe/Madrid", station.getTimeZone());
        assertEquals("CET", station.getTimeZoneGroup());
        assertEquals("Madrid Atocha", station.getStation());
        assertEquals(40.407, station.getLatitude(), 0.001);
        assertEquals(-3.691, station.getLongitude(), 0.001);
        assertTrue(station.isCity());
        assertTrue(station.isMainStation());
        assertFalse(station.isAirport());
    }

    @Test
    @DisplayName("Test 2: Getters return correct values")
    public void testGetters() {
        assertEquals("DE", station1.getCountry());
        assertEquals("Europe/Berlin", station1.getTimeZone());
        assertEquals("CET", station1.getTimeZoneGroup());
        assertEquals("Berlin Hauptbahnhof", station1.getStation());
        assertEquals(52.525, station1.getLatitude(), 0.001);
        assertEquals(13.369, station1.getLongitude(), 0.001);
        assertTrue(station1.isCity());
        assertTrue(station1.isMainStation());
        assertFalse(station1.isAirport());
    }

    @Test
    @DisplayName("Test 3: Airport station initialization")
    public void testAirportStation() {
        TrainStation airport = new TrainStation(
                "FR", "Europe/Paris", "CET",
                "Charles de Gaulle Airport",
                49.0097, 2.5479,
                false, false, true
        );

        assertFalse(airport.isCity());
        assertFalse(airport.isMainStation());
        assertTrue(airport.isAirport());
    }

    // ========== SETTER TESTS ==========

    @Test
    @DisplayName("Test 4: Set country")
    public void testSetCountry() {
        station1.setCountry("AT");
        assertEquals("AT", station1.getCountry());
    }

    @Test
    @DisplayName("Test 5: Set time zone")
    public void testSetTimeZone() {
        station1.setTimeZone("Europe/Vienna");
        assertEquals("Europe/Vienna", station1.getTimeZone());
    }

    @Test
    @DisplayName("Test 6: Set time zone group")
    public void testSetTimeZoneGroup() {
        station1.setTimeZoneGroup("EET");
        assertEquals("EET", station1.getTimeZoneGroup());
    }

    @Test
    @DisplayName("Test 7: Set station name")
    public void testSetStation() {
        station1.setStation("Berlin Ostbahnhof");
        assertEquals("Berlin Ostbahnhof", station1.getStation());
    }

    @Test
    @DisplayName("Test 8: Set latitude")
    public void testSetLatitude() {
        station1.setLatitude(53.551);
        assertEquals(53.551, station1.getLatitude(), 0.001);
    }

    @Test
    @DisplayName("Test 9: Set longitude")
    public void testSetLongitude() {
        station1.setLongitude(10.000);
        assertEquals(10.000, station1.getLongitude(), 0.001);
    }

    @Test
    @DisplayName("Test 10: Set isCity flag")
    public void testSetCity() {
        station1.setCity(false);
        assertFalse(station1.isCity());

        station1.setCity(true);
        assertTrue(station1.isCity());
    }

    @Test
    @DisplayName("Test 11: Set isMainStation flag")
    public void testSetMainStation() {
        station1.setMainStation(false);
        assertFalse(station1.isMainStation());

        station1.setMainStation(true);
        assertTrue(station1.isMainStation());
    }

    @Test
    @DisplayName("Test 12: Set isAirport flag")
    public void testSetAirport() {
        station1.setAirport(true);
        assertTrue(station1.isAirport());

        station1.setAirport(false);
        assertFalse(station1.isAirport());
    }

    // ========== DISTANCE CALCULATION TESTS (HAVERSINE) ==========

    @Test
    @DisplayName("Test 13: Distance between Berlin and Paris (~878 km)")
    public void testDistanceBerlinParis() {
        double distance = station1.distanceTo(station2);

        // Berlin to Paris is approximately 878 km
        assertEquals(878, distance, 10,
                "Distance should be approximately 878 km with ±10 km tolerance");
    }

    @Test
    @DisplayName("Test 14: Distance between Paris and Lisbon (~1450 km)")
    public void testDistanceParisLisbon() {
        double distance = station2.distanceTo(station3);

        // Paris to Lisbon is approximately 1450 km
        assertEquals(1450, distance, 20,
                "Distance should be approximately 1450 km with ±20 km tolerance");
    }

    @Test
    @DisplayName("Test 15: Distance between Berlin and Lisbon (~2300 km)")
    public void testDistanceBerlinLisbon() {
        double distance = station1.distanceTo(station3);

        // Berlin to Lisbon is approximately 2300 km
        assertEquals(2300, distance, 30,
                "Distance should be approximately 2300 km with ±30 km tolerance");
    }

    @Test
    @DisplayName("Test 16: Distance from station to itself is 0")
    public void testDistanceToSelf() {
        double distance = station1.distanceTo(station1);

        assertEquals(0, distance, 0.001,
                "Distance from a station to itself should be 0");
    }

    @Test
    @DisplayName("Test 17: Distance is symmetric (A to B = B to A)")
    public void testDistanceSymmetry() {
        double distance1 = station1.distanceTo(station2);
        double distance2 = station2.distanceTo(station1);

        assertEquals(distance1, distance2, 0.001,
                "Distance should be symmetric");
    }

    @Test
    @DisplayName("Test 18: Distance with same latitude, different longitude")
    public void testDistanceSameLatitude() {
        TrainStation west = new TrainStation(
                "XX", "UTC", "UTC", "West",
                45.0, -10.0, false, false, false
        );
        TrainStation east = new TrainStation(
                "XX", "UTC", "UTC", "East",
                45.0, 10.0, false, false, false
        );

        double distance = west.distanceTo(east);

        // At 45° latitude, 20° longitude ≈ 1570 km
        assertTrue(distance > 1400 && distance < 1700,
                "Distance should be approximately 1570 km");
    }

    @Test
    @DisplayName("Test 19: Distance with same longitude, different latitude")
    public void testDistanceSameLongitude() {
        TrainStation north = new TrainStation(
                "XX", "UTC", "UTC", "North",
                60.0, 10.0, false, false, false
        );
        TrainStation south = new TrainStation(
                "XX", "UTC", "UTC", "South",
                40.0, 10.0, false, false, false
        );

        double distance = north.distanceTo(south);

        // 20° latitude ≈ 2222 km
        assertEquals(2222, distance, 50,
                "Distance should be approximately 2222 km");
    }

    @Test
    @DisplayName("Test 20: Distance across equator")
    public void testDistanceAcrossEquator() {
        TrainStation northernHemisphere = new TrainStation(
                "XX", "UTC", "UTC", "North",
                10.0, 0.0, false, false, false
        );
        TrainStation southernHemisphere = new TrainStation(
                "XX", "UTC", "UTC", "South",
                -10.0, 0.0, false, false, false
        );

        double distance = northernHemisphere.distanceTo(southernHemisphere);

        // 20° latitude ≈ 2222 km
        assertEquals(2222, distance, 50,
                "Distance across equator should be calculated correctly");
    }

    @Test
    @DisplayName("Test 21: Distance with negative coordinates")
    public void testDistanceNegativeCoordinates() {
        TrainStation station = new TrainStation(
                "XX", "UTC", "UTC", "Station",
                -33.865, 151.209, false, false, false
        );

        double distance = station.distanceTo(station1);

        // Sydney to Berlin is approximately 16,000+ km
        assertTrue(distance > 15000,
                "Long distance calculation should work with negative coordinates");
    }

    // ========== EQUALS AND HASHCODE TESTS ==========

    @Test
    @DisplayName("Test 22: Equals - same object")
    public void testEqualsSameObject() {
        assertEquals(station1, station1,
                "A station should equal itself");
    }

    @Test
    @DisplayName("Test 23: Equals - identical stations")
    public void testEqualsIdenticalStations() {
        TrainStation duplicate = new TrainStation(
                "DE", "Europe/Berlin", "CET",
                "Berlin Hauptbahnhof",
                52.525, 13.369,
                true, true, false
        );

        assertEquals(station1, duplicate,
                "Stations with same country, name, and coordinates should be equal");
    }

    @Test
    @DisplayName("Test 24: Equals - different stations")
    public void testEqualsDifferentStations() {
        assertNotEquals(station1, station2,
                "Different stations should not be equal");
    }

    @Test
    @DisplayName("Test 25: Equals - null comparison")
    public void testEqualsNull() {
        assertNotEquals(null, station1,
                "Station should not equal null");
    }

    @Test
    @DisplayName("Test 26: Equals - different class")
    public void testEqualsDifferentClass() {
        String notAStation = "Not a station";
        assertNotEquals(station1, notAStation,
                "Station should not equal object of different class");
    }

    @Test
    @DisplayName("Test 27: Equals - same coordinates but different name")
    public void testEqualsSameCoordinatesDifferentName() {
        TrainStation sameLocation = new TrainStation(
                "DE", "Europe/Berlin", "CET",
                "Berlin Different Station",
                52.525, 13.369,
                true, true, false
        );

        assertNotEquals(station1, sameLocation,
                "Stations with same coordinates but different names should not be equal");
    }

    @Test
    @DisplayName("Test 28: Equals - same name but different coordinates")
    public void testEqualsSameNameDifferentCoordinates() {
        TrainStation sameNameDifferentLocation = new TrainStation(
                "DE", "Europe/Berlin", "CET",
                "Berlin Hauptbahnhof",
                52.999, 13.999,
                true, true, false
        );

        assertNotEquals(station1, sameNameDifferentLocation,
                "Stations with same name but different coordinates should not be equal");
    }

    @Test
    @DisplayName("Test 29: Equals - different country")
    public void testEqualsDifferentCountry() {
        TrainStation differentCountry = new TrainStation(
                "FR", "Europe/Berlin", "CET",
                "Berlin Hauptbahnhof",
                52.525, 13.369,
                true, true, false
        );

        assertNotEquals(station1, differentCountry,
                "Stations with different countries should not be equal");
    }

    @Test
    @DisplayName("Test 30: HashCode - identical stations have same hash")
    public void testHashCodeIdenticalStations() {
        TrainStation duplicate = new TrainStation(
                "DE", "Europe/Berlin", "CET",
                "Berlin Hauptbahnhof",
                52.525, 13.369,
                true, true, false
        );

        assertEquals(station1.hashCode(), duplicate.hashCode(),
                "Equal stations should have the same hash code");
    }

    @Test
    @DisplayName("Test 31: HashCode - different stations likely have different hash")
    public void testHashCodeDifferentStations() {
        assertNotEquals(station1.hashCode(), station2.hashCode(),
                "Different stations should likely have different hash codes");
    }

    @Test
    @DisplayName("Test 32: HashCode consistency")
    public void testHashCodeConsistency() {
        int hash1 = station1.hashCode();
        int hash2 = station1.hashCode();
        int hash3 = station1.hashCode();

        assertEquals(hash1, hash2, "Multiple calls should return same hash");
        assertEquals(hash2, hash3, "Multiple calls should return same hash");
    }

    // ========== TOSTRING TESTS ==========



    @Test
    @DisplayName("Test 34: toString format is consistent")
    public void testToStringFormat() {
        String result = station1.toString();

        assertTrue(result.startsWith("TrainStation{"),
                "toString should start with 'TrainStation{'");
        assertTrue(result.endsWith("}"),
                "toString should end with '}'");
    }

    // ========== EDGE CASES AND BOUNDARY TESTS ==========

    @Test
    @DisplayName("Test 35: Extreme latitude values (poles)")
    public void testExtremeLatitudes() {
        TrainStation northPole = new TrainStation(
                "XX", "UTC", "UTC", "North Pole",
                90.0, 0.0, false, false, false
        );
        TrainStation southPole = new TrainStation(
                "XX", "UTC", "UTC", "South Pole",
                -90.0, 0.0, false, false, false
        );

        double distance = northPole.distanceTo(southPole);

        // Distance between poles ≈ 20,015 km (half circumference)
        assertEquals(20015, distance, 50,
                "Distance between poles should be approximately half Earth's circumference");
    }

    @Test
    @DisplayName("Test 36: Extreme longitude values")
    public void testExtremeLongitudes() {
        TrainStation west = new TrainStation(
                "XX", "UTC", "UTC", "West",
                0.0, -180.0, false, false, false
        );
        TrainStation east = new TrainStation(
                "XX", "UTC", "UTC", "East",
                0.0, 180.0, false, false, false
        );

        double distance = west.distanceTo(east);

        // At equator, opposite sides should be approximately 0 km (same meridian)
        assertEquals(0, distance, 1,
                "Stations at -180° and 180° longitude are at the same meridian");
    }

    @Test
    @DisplayName("Test 37: Zero coordinates (Gulf of Guinea)")
    public void testZeroCoordinates() {
        TrainStation origin = new TrainStation(
                "XX", "UTC", "UTC", "Null Island",
                0.0, 0.0, false, false, false
        );

        assertEquals(0.0, origin.getLatitude());
        assertEquals(0.0, origin.getLongitude());

        double distanceToSelf = origin.distanceTo(origin);
        assertEquals(0, distanceToSelf, 0.001);
    }

    @Test
    @DisplayName("Test 38: Empty strings")
    public void testEmptyStrings() {
        TrainStation emptyStation = new TrainStation(
                "", "", "",
                "", 0.0, 0.0,
                false, false, false
        );

        assertEquals("", emptyStation.getCountry());
        assertEquals("", emptyStation.getTimeZone());
        assertEquals("", emptyStation.getTimeZoneGroup());
        assertEquals("", emptyStation.getStation());
    }

    @Test
    @DisplayName("Test 39: Unicode station names")
    public void testUnicodeNames() {
        TrainStation unicode = new TrainStation(
                "RU", "Europe/Moscow", "MSK",
                "Москва́ Кие́вская",
                55.744, 37.567,
                true, true, false
        );

        assertEquals("Москва́ Кие́вская", unicode.getStation());
        assertTrue(unicode.toString().contains("Москва́ Кие́вская"));
    }

    @Test
    @DisplayName("Test 40: Very long station names")
    public void testLongStationName() {
        String longName = "Station with a very very very very very very long name that exceeds normal length";
        TrainStation longNameStation = new TrainStation(
                "XX", "UTC", "UTC",
                longName,
                0.0, 0.0,
                false, false, false
        );

        assertEquals(longName, longNameStation.getStation());
    }

    // ========== COMBINED FLAGS TESTS ==========

    @Test
    @DisplayName("Test 41: All flags true")
    public void testAllFlagsTrue() {
        TrainStation special = new TrainStation(
                "XX", "UTC", "UTC", "Special Station",
                0.0, 0.0,
                true, true, true
        );

        assertTrue(special.isCity());
        assertTrue(special.isMainStation());
        assertTrue(special.isAirport());
    }

    @Test
    @DisplayName("Test 42: All flags false")
    public void testAllFlagsFalse() {
        TrainStation basic = new TrainStation(
                "XX", "UTC", "UTC", "Basic Station",
                0.0, 0.0,
                false, false, false
        );

        assertFalse(basic.isCity());
        assertFalse(basic.isMainStation());
        assertFalse(basic.isAirport());
    }
}
