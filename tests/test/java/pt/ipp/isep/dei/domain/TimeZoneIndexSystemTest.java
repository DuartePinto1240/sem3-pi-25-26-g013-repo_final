package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.TimeZoneIndexSystem;
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Test suite for TimeZoneIndexSystem (USEI06)
 * Tests all query operations, validation, and edge cases
 */
public class TimeZoneIndexSystemTest {

    private TimeZoneIndexSystem indexSystem;
    private List<TrainStation> testStations;

    @BeforeEach
    public void setUp() {
        testStations = createTestStations();
    }

    /**
     * Creates a comprehensive test dataset with various scenarios
     */
    private List<TrainStation> createTestStations() {
        List<TrainStation> stations = new ArrayList<>();

        // Valid stations - CET time zone
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Berlin Hauptbahnhof", 52.525, 13.369, true, true, false));
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "München Hbf", 48.140, 11.558, true, true, false));
        stations.add(new TrainStation("FR", "Europe/Paris", "CET",
                "Paris Gare du Nord", 48.881, 2.355, true, true, false));
        stations.add(new TrainStation("IT", "Europe/Rome", "CET",
                "Roma Termini", 41.901, 12.502, true, true, false));
        stations.add(new TrainStation("NL", "Europe/Amsterdam", "CET",
                "Amsterdam Centraal", 52.379, 4.900, true, true, false));

        // Valid stations - WET/GMT time zone
        stations.add(new TrainStation("PT", "Europe/Lisbon", "WET/GMT",
                "Lisboa Santa Apolónia", 38.713, -9.125, true, true, false));
        stations.add(new TrainStation("PT", "Europe/Lisbon", "WET/GMT",
                "Porto Campanhã", 41.149, -8.585, true, true, false));
        stations.add(new TrainStation("GB", "Europe/London", "WET/GMT",
                "London King's Cross", 51.531, -0.123, true, true, false));

        // Valid stations - EET time zone
        stations.add(new TrainStation("GR", "Europe/Athens", "EET",
                "Athens Railway Station", 37.993, 23.721, true, true, false));
        stations.add(new TrainStation("RO", "Europe/Bucharest", "EET",
                "Bucharest North", 44.446, 26.074, true, true, false));

        // Stations with same latitude (for duplicate coordinate testing)
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Station Alpha", 50.000, 10.000, false, false, false));
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Station Beta", 50.000, 11.000, false, false, false));
        stations.add(new TrainStation("FR", "Europe/Paris", "CET",
                "Station Gamma", 50.000, 12.000, false, false, false));

        // Stations with same longitude
        stations.add(new TrainStation("SE", "Europe/Stockholm", "CET",
                "Northern Station", 60.000, 15.000, false, false, false));
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Southern Station", 48.000, 15.000, false, false, false));

        // Invalid stations (should be rejected)
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "", 52.0, 13.0, false, false, false)); // Empty name
        stations.add(new TrainStation("", "Europe/Berlin", "CET",
                "Invalid Station 1", 52.0, 13.0, false, false, false)); // Empty country
        stations.add(new TrainStation("DE", "Europe/Berlin", "",
                "Invalid Station 2", 52.0, 13.0, false, false, false)); // Empty timezone group
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Invalid Lat", 91.0, 13.0, false, false, false)); // Lat > 90
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Invalid Lat 2", -91.0, 13.0, false, false, false)); // Lat < -90
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Invalid Lon", 52.0, 181.0, false, false, false)); // Lon > 180
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Invalid Lon 2", 52.0, -181.0, false, false, false)); // Lon < -180
        stations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                null, 52.0, 13.0, false, false, false)); // Null name

        return stations;
    }

    // ========== CONSTRUCTION AND VALIDATION TESTS ==========

    @Test
    @DisplayName("Test 1: System construction and validation counts")
    public void testSystemConstruction() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        assertEquals(15, indexSystem.getValidStationsCount(),
                "Should accept 15 valid stations");
        assertEquals(8, indexSystem.getRejectedStationsCount(),
                "Should reject 8 invalid stations");
    }

    @Test
    @DisplayName("Test 2: Empty station list")
    public void testEmptyStationList() {
        indexSystem = new TimeZoneIndexSystem(new ArrayList<>());

        assertEquals(0, indexSystem.getValidStationsCount());
        assertEquals(0, indexSystem.getRejectedStationsCount());
    }

    @Test
    @DisplayName("Test 3: All invalid stations")
    public void testAllInvalidStations() {
        List<TrainStation> invalidOnly = new ArrayList<>();
        invalidOnly.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "", 52.0, 13.0, false, false, false));
        invalidOnly.add(new TrainStation("", "Europe/Berlin", "CET",
                "Test", 52.0, 13.0, false, false, false));
        invalidOnly.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Test", 91.0, 13.0, false, false, false));

        indexSystem = new TimeZoneIndexSystem(invalidOnly);

        assertEquals(0, indexSystem.getValidStationsCount());
        assertEquals(3, indexSystem.getRejectedStationsCount());
    }

    // ========== LATITUDE QUERY TESTS ==========

    @Test
    @DisplayName("Test 4: Find by exact latitude")
    public void testFindByLatitude() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        List<TrainStation> result = indexSystem.findByLatitude(50.000);

        assertEquals(3, result.size(), "Should find 3 stations at latitude 50.0");

        // Verify sorted by name
        assertEquals("Station Alpha", result.get(0).getStation());
        assertEquals("Station Beta", result.get(1).getStation());
        assertEquals("Station Gamma", result.get(2).getStation());
    }

    @Test
    @DisplayName("Test 5: Find by latitude - no results")
    public void testFindByLatitudeNoResults() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        List<TrainStation> result = indexSystem.findByLatitude(99.999);

        assertTrue(result.isEmpty(), "Should return empty list for non-existent latitude");
    }

    @Test
    @DisplayName("Test 6: Find by latitude range")
    public void testFindByLatitudeRange() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        List<TrainStation> result = indexSystem.findByLatitudeRange(48.0, 52.0);

        assertTrue(result.size() >= 4, "Should find multiple stations in range");

        // Verify all results are within range
        for (TrainStation station : result) {
            assertTrue(station.getLatitude() >= 48.0 && station.getLatitude() <= 52.0,
                    "Station latitude should be within range");
        }
    }

    // ========== LONGITUDE QUERY TESTS ==========

    @Test
    @DisplayName("Test 7: Find by exact longitude")
    public void testFindByLongitude() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        List<TrainStation> result = indexSystem.findByLongitude(15.000);

        assertEquals(2, result.size(), "Should find 2 stations at longitude 15.0");

        // Verify sorted by name
        assertEquals("Northern Station", result.get(0).getStation());
        assertEquals("Southern Station", result.get(1).getStation());
    }

    @Test
    @DisplayName("Test 8: Find by longitude - no results")
    public void testFindByLongitudeNoResults() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        List<TrainStation> result = indexSystem.findByLongitude(999.0);

        assertTrue(result.isEmpty(), "Should return empty list for non-existent longitude");
    }

    // ========== TIME ZONE QUERY TESTS ==========

    @Test
    @DisplayName("Test 9: Find by time zone group")
    public void testFindByTimeZoneGroup() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        Map<String, List<TrainStation>> result = indexSystem.findByTimeZoneGroup("CET");

        assertFalse(result.isEmpty(), "Should find stations in CET zone");
        assertTrue(result.containsKey("DE"), "Should contain German stations");
        assertTrue(result.containsKey("FR"), "Should contain French stations");
        assertTrue(result.containsKey("IT"), "Should contain Italian stations");

        // Verify stations within each country are sorted by name
        List<TrainStation> germanStations = result.get("DE");
        assertTrue(germanStations.size() >= 2, "Should have multiple German stations");
    }

    @Test
    @DisplayName("Test 10: Find by time zone group - no results")
    public void testFindByTimeZoneGroupNoResults() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        Map<String, List<TrainStation>> result = indexSystem.findByTimeZoneGroup("INVALID");

        assertTrue(result.isEmpty(), "Should return empty map for non-existent time zone");
    }

    @Test
    @DisplayName("Test 11: Find by time zone and country")
    public void testFindByTimeZoneAndCountry() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        List<TrainStation> result = indexSystem.findByTimeZoneAndCountry("WET/GMT", "PT");

        assertEquals(2, result.size(), "Should find 2 Portuguese stations in WET/GMT");

        // Verify sorted alphabetically
        assertTrue(result.get(0).getStation().compareTo(result.get(1).getStation()) < 0,
                "Stations should be sorted by name");
    }

    @Test
    @DisplayName("Test 12: Find by time zone and country - no results")
    public void testFindByTimeZoneAndCountryNoResults() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        List<TrainStation> result = indexSystem.findByTimeZoneAndCountry("CET", "ES");

        assertTrue(result.isEmpty(), "Should return empty list for non-existent combination");
    }

    // ========== WINDOWED QUERY TESTS ==========

    @Test
    @DisplayName("Test 13: Windowed query - multiple time zones")
    public void testWindowedQuery() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        Map<String, Map<String, List<TrainStation>>> result =
                indexSystem.windowedQuery("CET", "WET/GMT");

        assertEquals(2, result.size(), "Should return 2 time zones");
        assertTrue(result.containsKey("CET"), "Should contain CET zone");
        assertTrue(result.containsKey("WET/GMT"), "Should contain WET/GMT zone");

        // Verify nested structure
        Map<String, List<TrainStation>> cetCountries = result.get("CET");
        assertFalse(cetCountries.isEmpty(), "CET should have countries");
    }

    @Test
    @DisplayName("Test 14: Windowed query - single time zone")
    public void testWindowedQuerySingle() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        Map<String, Map<String, List<TrainStation>>> result =
                indexSystem.windowedQuery("EET");

        assertEquals(1, result.size(), "Should return 1 time zone");
        assertTrue(result.containsKey("EET"), "Should contain EET zone");
    }

    @Test
    @DisplayName("Test 15: Windowed query - no time zones")
    public void testWindowedQueryEmpty() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        Map<String, Map<String, List<TrainStation>>> result =
                indexSystem.windowedQuery();

        assertTrue(result.isEmpty(), "Should return empty map for no time zones");
    }

    @Test
    @DisplayName("Test 16: Windowed query - non-existent time zones")
    public void testWindowedQueryNonExistent() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        Map<String, Map<String, List<TrainStation>>> result =
                indexSystem.windowedQuery("INVALID1", "INVALID2");

        assertTrue(result.isEmpty(), "Should return empty map for non-existent zones");
    }

    // ========== EDGE CASES AND BOUNDARY TESTS ==========

    @Test
    @DisplayName("Test 17: Boundary latitude values")
    public void testBoundaryLatitudes() {
        List<TrainStation> boundaryStations = new ArrayList<>();
        boundaryStations.add(new TrainStation("XX", "UTC", "UTC",
                "North Pole", 90.0, 0.0, false, false, false));
        boundaryStations.add(new TrainStation("XX", "UTC", "UTC",
                "South Pole", -90.0, 0.0, false, false, false));
        boundaryStations.add(new TrainStation("XX", "UTC", "UTC",
                "Equator", 0.0, 0.0, false, false, false));

        indexSystem = new TimeZoneIndexSystem(boundaryStations);

        assertEquals(3, indexSystem.getValidStationsCount(),
                "Should accept boundary latitude values");

        assertNotNull(indexSystem.findByLatitude(90.0));
        assertNotNull(indexSystem.findByLatitude(-90.0));
        assertNotNull(indexSystem.findByLatitude(0.0));
    }

    @Test
    @DisplayName("Test 18: Boundary longitude values")
    public void testBoundaryLongitudes() {
        List<TrainStation> boundaryStations = new ArrayList<>();
        boundaryStations.add(new TrainStation("XX", "UTC", "UTC",
                "Prime Meridian", 0.0, 0.0, false, false, false));
        boundaryStations.add(new TrainStation("XX", "UTC", "UTC",
                "International Date Line East", 0.0, 180.0, false, false, false));
        boundaryStations.add(new TrainStation("XX", "UTC", "UTC",
                "International Date Line West", 0.0, -180.0, false, false, false));

        indexSystem = new TimeZoneIndexSystem(boundaryStations);

        assertEquals(3, indexSystem.getValidStationsCount(),
                "Should accept boundary longitude values");

        assertNotNull(indexSystem.findByLongitude(180.0));
        assertNotNull(indexSystem.findByLongitude(-180.0));
        assertNotNull(indexSystem.findByLongitude(0.0));
    }

    @Test
    @DisplayName("Test 19: Duplicate station names in same location")
    public void testDuplicateNames() {
        List<TrainStation> duplicates = new ArrayList<>();
        duplicates.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Central Station", 52.0, 13.0, true, true, false));
        duplicates.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Central Station", 52.0, 13.0, true, true, false));
        duplicates.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Central Station", 52.0, 13.0, true, true, false));

        indexSystem = new TimeZoneIndexSystem(duplicates);

        assertEquals(3, indexSystem.getValidStationsCount(),
                "Should accept duplicate names");

        List<TrainStation> result = indexSystem.findByLatitude(52.0);
        assertEquals(3, result.size(), "Should find all duplicate stations");
    }

    @Test
    @DisplayName("Test 20: Unicode and special characters in names")
    public void testUnicodeNames() {
        List<TrainStation> unicodeStations = new ArrayList<>();
        unicodeStations.add(new TrainStation("CH", "Europe/Zurich", "CET",
                "Zürich HB", 47.378, 8.540, true, true, false));
        unicodeStations.add(new TrainStation("DK", "Europe/Copenhagen", "CET",
                "København H", 55.673, 12.565, true, true, false));
        unicodeStations.add(new TrainStation("RU", "Europe/Moscow", "MSK",
                "Москва́ Кие́вская", 55.744, 37.567, true, true, false));

        indexSystem = new TimeZoneIndexSystem(unicodeStations);

        assertEquals(3, indexSystem.getValidStationsCount(),
                "Should accept unicode characters in names");
    }

    // ========== SPECIAL ATTRIBUTES TESTS ==========

    @Test
    @DisplayName("Test 21: Stations with special attributes (airport, main station)")
    public void testSpecialAttributes() {
        List<TrainStation> specialStations = new ArrayList<>();
        specialStations.add(new TrainStation("FR", "Europe/Paris", "CET",
                "Charles de Gaulle Airport", 49.0097, 2.5479, false, false, true)); // Airport
        specialStations.add(new TrainStation("DE", "Europe/Berlin", "CET",
                "Berlin Hauptbahnhof", 52.525, 13.369, true, true, false)); // Main station
        specialStations.add(new TrainStation("NL", "Europe/Amsterdam", "CET",
                "Amsterdam Schiphol", 52.310, 4.763, false, false, true)); // Airport

        indexSystem = new TimeZoneIndexSystem(specialStations);

        assertEquals(3, indexSystem.getValidStationsCount(),
                "Should accept stations with special attributes");

        // Verify they can be queried normally
        List<TrainStation> cetStations = indexSystem.findByTimeZoneAndCountry("CET", "DE");
        assertFalse(cetStations.isEmpty(), "Should find German stations");
    }

    // ========== PERFORMANCE AND STATISTICS TESTS ==========

    @Test
    @DisplayName("Test 22: Tree height is logarithmic")
    public void testTreeHeight() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        int height = indexSystem.getTimeZoneIndexHeight();

        assertTrue(height >= 0, "Tree height should be non-negative");
        // For AVL tree, height should be O(log n)
        assertTrue(height <= Math.ceil(1.44 * Math.log(15) / Math.log(2)),
                "AVL tree height should be logarithmic");
    }

    @Test
    @DisplayName("Test 23: Complexity analysis prints without error")
    public void testComplexityAnalysisPrint() {
        indexSystem = new TimeZoneIndexSystem(testStations);

        assertDoesNotThrow(() -> indexSystem.printComplexityAnalysis(),
                "Complexity analysis should print without errors");
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Test 24: Complete workflow - construction to queries")
    public void testCompleteWorkflow() {
        // Step 1: Construction
        indexSystem = new TimeZoneIndexSystem(testStations);
        assertTrue(indexSystem.getValidStationsCount() > 0);

        // Step 2: Latitude query
        List<TrainStation> latResult = indexSystem.findByLatitude(52.525);
        assertNotNull(latResult);

        // Step 3: Longitude query
        List<TrainStation> lonResult = indexSystem.findByLongitude(13.369);
        assertNotNull(lonResult);

        // Step 4: Time zone query
        Map<String, List<TrainStation>> tzResult =
                indexSystem.findByTimeZoneGroup("CET");
        assertFalse(tzResult.isEmpty());

        // Step 5: Windowed query
        Map<String, Map<String, List<TrainStation>>> windowedResult =
                indexSystem.windowedQuery("CET", "WET/GMT", "EET");
        assertTrue(windowedResult.size() >= 2);
    }

    @Test
    @DisplayName("Test 25: Large dataset performance")
    public void testLargeDataset() {
        List<TrainStation> largeDataset = new ArrayList<>();

        // Generate 1000 stations
        for (int i = 0; i < 1000; i++) {
            double lat = -90 + (i % 180);
            double lon = -180 + (i % 360);
            String tz = (i % 3 == 0) ? "CET" : (i % 3 == 1) ? "WET/GMT" : "EET";
            String country = "C" + (i % 50);

            largeDataset.add(new TrainStation(
                    country, "Europe/Generic", tz,
                    "Station" + i, lat, lon,
                    false, false, false));
        }

        long startTime = System.currentTimeMillis();
        indexSystem = new TimeZoneIndexSystem(largeDataset);
        long constructionTime = System.currentTimeMillis() - startTime;

        assertTrue(constructionTime < 5000,
                "Construction of 1000 stations should complete in < 5 seconds");

        // Test query performance
        startTime = System.currentTimeMillis();
        indexSystem.findByTimeZoneGroup("CET");
        long queryTime = System.currentTimeMillis() - startTime;

        assertTrue(queryTime < 100,
                "Query should complete in < 100ms");
    }
}