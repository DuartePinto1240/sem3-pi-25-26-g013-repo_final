package test.java.pt.ipp.isep.dei.domain.USEI06;

import main.java.pt.ipp.isep.dei.domain.TimeZoneIndexSystem;
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the USEI06 TimeZoneIndexSystem functionality.
 *
 * Tests include:
 * - Loading train stations from CSV
 * - Valid/invalid station counts
 * - Queries by latitude, longitude, and time zone
 * - Windowed queries
 * - Range queries
 * - Distance calculations between stations
 * - Consistency and ordering in indexes
 */
class USEI06Test {

    private static TimeZoneIndexSystem tzSystem;
    private static List<TrainStation> allStations;

    /**
     * Initializes the TimeZoneIndexSystem by loading the CSV dataset.
     */
    @BeforeAll
    static void setup() {
        String csvPath = "C:\\Users\\duart\\IdeaProjects\\sem3-pi-25-26-g013-repo\\train_station_dataset_v2\\train_stations_europe.csv";

        TrainStationLoader loader = new TrainStationLoader();
        allStations = loader.load(csvPath);

        tzSystem = new TimeZoneIndexSystem(allStations);

        assertNotNull(tzSystem);
        assertTrue(tzSystem.getValidStationsCount() > 0, "Must have valid stations");
    }

    /**
     * Verifies that valid and rejected station counts are consistent.
     */
    @Test
    void testValidAndRejectedCounts() {
        int valid = tzSystem.getValidStationsCount();
        int rejected = tzSystem.getRejectedStationsCount();

        System.out.println("✅ Valid stations: " + valid);
        System.out.println("❌ Rejected stations: " + rejected);
        System.out.println("📊 Total loaded: " + allStations.size());

        assertTrue(valid > 0, "Should have valid stations");
        assertTrue(rejected >= 0, "Rejected count should be non-negative");
        assertEquals(valid + rejected, allStations.size(), "All stations must be counted");
    }

    /**
     * Tests finding stations by exact latitude and longitude.
     */
    @Test
    void testFindByLatitudeAndLongitude() {
        // Test with first 100 stations for performance
        int testCount = Math.min(100, allStations.size());

        for (int i = 0; i < testCount; i++) {
            TrainStation st = allStations.get(i);
            if (st.getStation() != null) {
                List<TrainStation> byLat = tzSystem.findByLatitude(st.getLatitude());
                List<TrainStation> byLon = tzSystem.findByLongitude(st.getLongitude());

                assertNotNull(byLat, "Latitude query should not return null");
                assertNotNull(byLon, "Longitude query should not return null");
                assertTrue(byLat.contains(st), "Station must be in latitude index");
                assertTrue(byLon.contains(st), "Station must be in longitude index");
            }
        }
    }

    /**
     * Verifies that invalid stations are rejected during indexing.
     */
    @Test
    void testInvalidStationsRejected() {
        // Empty country
        TrainStation invalidStation1 = new TrainStation(
                "", "('Europe/Paris',)", "CET", "StationX",
                50.0, 10.0, false, false, false
        );

        // Empty time zone group
        TrainStation invalidStation2 = new TrainStation(
                "FR", "('Europe/Paris',)", "", "StationY",
                50.0, 10.0, false, false, false
        );

        // Invalid latitude
        TrainStation invalidStation3 = new TrainStation(
                "FR", "('Europe/Paris',)", "CET", "StationZ",
                100.0, 10.0, false, false, false
        );

        TimeZoneIndexSystem tempSystem = new TimeZoneIndexSystem(
                List.of(invalidStation1, invalidStation2, invalidStation3)
        );

        assertEquals(0, tempSystem.getValidStationsCount(), "No valid stations should be indexed");
        assertEquals(3, tempSystem.getRejectedStationsCount(), "All invalid stations should be rejected");
    }

    /**
     * Tests the Haversine distance calculation between two stations.
     */
    @Test
    void testStationDistanceAccuracy() {
        if (allStations.size() >= 2) {
            TrainStation st1 = allStations.get(0);
            TrainStation st2 = allStations.get(1);

            double distance = st1.distanceTo(st2);
            assertTrue(distance >= 0, "Distance must be non-negative");
            assertTrue(distance < 10000, "Distance between European stations should be < 10,000 km");
        }
    }

    /**
     * Tests distance between same station should be 0.
     */
    @Test
    void testDistanceSameStation() {
        if (!allStations.isEmpty()) {
            TrainStation st = allStations.get(0);
            double distance = st.distanceTo(st);
            assertEquals(0.0, distance, 0.01, "Distance to same station should be 0");
        }
    }

    /**
     * Ensures the time zone AVL tree height is reasonable for balanced tree.
     */
    @Test
    void testTimeZoneIndexTreeHeight() {
        int height = tzSystem.getTimeZoneIndexHeight();
        int tzCount = tzSystem.getTimeZoneCount();

        System.out.println("Time zone tree height: " + height);
        System.out.println("Time zone count: " + tzCount);

        assertTrue(height >= 0, "Tree height should be non-negative");

        if (tzCount > 0) {
            // For AVL tree: height <= 1.44 * log2(n+2) - 0.328
            double maxHeight = 1.44 * Math.log(tzCount + 2) / Math.log(2);
            assertTrue(height <= maxHeight + 1, "AVL tree height should be balanced");
        }
    }

    /**
     * Confirms that stations are consistently indexed across all trees.
     */
    @Test
    void testIndexConsistencyAcrossTrees() {
        // Test with sample of stations for performance
        int testCount = Math.min(100, allStations.size());

        for (int i = 0; i < testCount; i++) {
            TrainStation st = allStations.get(i);
            if (st.getStation() != null) {
                List<TrainStation> byLat = tzSystem.findByLatitude(st.getLatitude());
                List<TrainStation> byLon = tzSystem.findByLongitude(st.getLongitude());
                List<TrainStation> byTzCountry = tzSystem.findByTimeZoneAndCountry(
                        st.getTimeZoneGroup(), st.getCountry()
                );

                assertTrue(byLat.contains(st), "Station should be in latitude index");
                assertTrue(byLon.contains(st), "Station should be in longitude index");
                assertTrue(byTzCountry.contains(st), "Station should be in timezone/country index");
            }
        }
    }

    /**
     * Tests a latitude range query that matches no stations.
     */
    @Test
    void testLatitudeRangeNoMatches() {
        double minLat = 100.0;  // Invalid range
        double maxLat = 200.0;

        List<TrainStation> result = tzSystem.findByLatitudeRange(minLat, maxLat);
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "No stations should match this out-of-range latitude");
    }

    /**
     * Tests a windowed query using non-existing timezone groups.
     */
    @Test
    void testWindowedQueryNonExistingGroups() {
        Map<String, Map<String, List<TrainStation>>> result =
                tzSystem.windowedQuery("NON_EXIST_TZ", "FAKE_ZONE");

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Windowed query for non-existing groups must return empty map");
    }

    /**
     * Tests windowed queries across existing timezone groups.
     */
    @Test
    void testWindowedQueryExistingZones() {
        // Get all available time zones
        List<String> allTimeZones = tzSystem.getAllTimeZones();

        if (allTimeZones.size() >= 2) {
            // Use first 2 time zones
            String tz1 = allTimeZones.get(0);
            String tz2 = allTimeZones.get(1);

            Map<String, Map<String, List<TrainStation>>> windowed =
                    tzSystem.windowedQuery(tz1, tz2);

            assertNotNull(windowed, "Windowed result should not be null");
            assertFalse(windowed.isEmpty(), "Should have results for existing zones");

            // Verify data consistency
            windowed.forEach((tz, countryMap) -> {
                countryMap.forEach((country, stations) -> {
                    for (TrainStation st : stations) {
                        assertEquals(tz, st.getTimeZoneGroup(),
                                "Station timezone must match query");
                        assertEquals(country, st.getCountry(),
                                "Station country must match query");
                    }
                });
            });
        }
    }

    /**
     * Verifies latitude range query returns stations within expected bounds.
     */
    @Test
    void testLatitudeRangeQuery() {
        double minLat = 40.0;
        double maxLat = 60.0;
        List<TrainStation> range = tzSystem.findByLatitudeRange(minLat, maxLat);

        assertNotNull(range, "Range result should not be null");

        for (TrainStation st : range) {
            assertTrue(st.getLatitude() >= minLat && st.getLatitude() <= maxLat,
                    "Station latitude must be within range [" + minLat + ", " + maxLat + "]");
        }
    }

    /**
     * Tests finding stations by time zone and country.
     */
    @Test
    void testFindByTimeZoneAndCountry() {
        List<String> timeZones = tzSystem.getAllTimeZones();

        if (!timeZones.isEmpty()) {
            String tz = timeZones.get(0);
            Map<String, List<TrainStation>> byCountry = tzSystem.findByTimeZoneGroup(tz);

            assertNotNull(byCountry, "Result should not be null");

            if (!byCountry.isEmpty()) {
                String country = byCountry.keySet().iterator().next();
                List<TrainStation> stations = tzSystem.findByTimeZoneAndCountry(tz, country);

                assertNotNull(stations, "Stations list should not be null");
                assertFalse(stations.isEmpty(), "Should find stations for existing TZ/country");

                // Verify all stations match criteria
                for (TrainStation st : stations) {
                    assertEquals(tz, st.getTimeZoneGroup());
                    assertEquals(country, st.getCountry());
                }
            }
        }
    }

    /**
     * Ensures stations in indexes are alphabetically sorted by name.
     */
    @Test
    void testSortedOrderInIndexes() {
        List<String> timeZones = tzSystem.getAllTimeZones();

        if (!timeZones.isEmpty()) {
            String tz = timeZones.get(0);
            Map<String, List<TrainStation>> tzGroupMap = tzSystem.findByTimeZoneGroup(tz);

            tzGroupMap.values().forEach(list -> {
                for (int i = 1; i < list.size(); i++) {
                    String prev = list.get(i-1).getStation();
                    String curr = list.get(i).getStation();
                    assertTrue(prev.compareTo(curr) <= 0,
                            "Stations must be sorted by name: " + prev + " vs " + curr);
                }
            });
        }
    }

    /**
     * Tests that duplicate coordinates maintain sorted order by name.
     */
    @Test
    void testDuplicateCoordinatesSorted() {
        // Find stations with duplicate coordinates
        for (int i = 0; i < Math.min(100, allStations.size()); i++) {
            TrainStation st = allStations.get(i);
            List<TrainStation> atSameCoord = tzSystem.findByLatitude(st.getLatitude());

            if (atSameCoord.size() > 1) {
                // Verify they are sorted by name
                for (int j = 1; j < atSameCoord.size(); j++) {
                    String prev = atSameCoord.get(j-1).getStation();
                    String curr = atSameCoord.get(j).getStation();
                    assertTrue(prev.compareTo(curr) <= 0,
                            "Stations at same coordinate must be sorted by name");
                }
                break; // Found and tested one case
            }
        }
    }

    /**
     * Tests index statistics methods.
     */
    @Test
    void testIndexStatistics() {
        int uniqueLat = tzSystem.getUniqueLatitudes();
        int uniqueLon = tzSystem.getUniqueLongitudes();
        int tzCount = tzSystem.getTimeZoneCount();

        System.out.println("Unique latitudes: " + uniqueLat);
        System.out.println("Unique longitudes: " + uniqueLon);
        System.out.println("Time zones: " + tzCount);

        assertTrue(uniqueLat > 0, "Should have unique latitudes");
        assertTrue(uniqueLon > 0, "Should have unique longitudes");
        assertTrue(tzCount > 0, "Should have time zones");
        assertTrue(uniqueLat <= allStations.size(), "Unique lats <= total stations");
        assertTrue(uniqueLon <= allStations.size(), "Unique lons <= total stations");
    }

    /**
     * Tests time zone statistics method.
     */
    @Test
    void testTimeZoneStatistics() {
        List<String> timeZones = tzSystem.getAllTimeZones();

        for (String tz : timeZones) {
            String stats = tzSystem.getTimeZoneStats(tz);
            assertNotNull(stats, "Stats should not be null");
            assertTrue(stats.contains(tz), "Stats should contain timezone name");
        }

        // Test non-existing timezone
        String nonExist = tzSystem.getTimeZoneStats("NON_EXISTENT_TZ");
        assertTrue(nonExist.contains("not found"), "Should indicate timezone not found");
    }

    /**
     * Performance test: measure query times.
     */
    @Test
    void testQueryPerformance() {
        if (allStations.isEmpty()) return;

        TrainStation testStation = allStations.get(0);

        long start = System.nanoTime();
        List<TrainStation> byLat = tzSystem.findByLatitude(testStation.getLatitude());
        long latTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<TrainStation> byTz = tzSystem.findByTimeZoneAndCountry(
                testStation.getTimeZoneGroup(), testStation.getCountry()
        );
        long tzTime = System.nanoTime() - start;

        System.out.println("Latitude query: " + latTime + " ns");
        System.out.println("TZ/Country query: " + tzTime + " ns");

        // Queries should be fast (< 1ms for 64k dataset)
        assertTrue(latTime < 1_000_000, "Latitude query should be fast");
        assertTrue(tzTime < 1_000_000, "TZ/Country query should be fast");
    }
}