package test.java.pt.ipp.isep.dei.domain.USEI06;

import main.java.pt.ipp.isep.dei.domain.AVLTree;
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
 * <p>
 * Tests include:
 * - Loading train stations from CSV
 * - Valid/invalid station counts
 * - Queries by latitude, longitude, and time zone
 * - Windowed queries
 * - Range queries
 * - Distance calculations between stations
 * - Consistency and ordering in AVL trees
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

        assertTrue(valid > 0);
        assertTrue(rejected >= 0);
        assertEquals(valid + rejected, allStations.size(), "All stations counted");
    }

    /**
     * Tests finding stations by exact latitude and longitude.
     */
    @Test
    void testFindByLatitudeAndLongitude() {
        for (TrainStation st : allStations) {
            if (st.getStation() != null) {
                List<TrainStation> byLat = tzSystem.findByLatitude(st.getLatitude());
                List<TrainStation> byLon = tzSystem.findByLongitude(st.getLongitude());

                assertNotNull(byLat);
                assertNotNull(byLon);
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
        TrainStation invalidStation1 = new TrainStation("", "CET", "CET", "StationX", 50, 10, false, false, false);
        TrainStation invalidStation2 = new TrainStation("FR", "CET", "", "StationY", 50, 10, false, false, false);

        TimeZoneIndexSystem tempSystem = new TimeZoneIndexSystem(List.of(invalidStation1, invalidStation2));

        assertEquals(0, tempSystem.getValidStationsCount(), "No valid stations");
        assertEquals(2, tempSystem.getRejectedStationsCount(), "All invalid stations rejected");
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
            assertTrue(distance < 5000, "Distance between European stations must be reasonable");
        }
    }

    /**
     * Ensures the time zone AVL tree height is reasonable.
     */
    @Test
    void testTimeZoneIndexTreeHeight() {
        int height = tzSystem.getTimeZoneIndexHeight();
        assertTrue(height > 0, "Tree height should be positive");
        assertTrue(height < allStations.size(), "Tree height should be less than total stations for balanced AVL");
    }

    /**
     * Confirms that stations are consistently indexed across all trees.
     */
    @Test
    void testIndexConsistencyAllStations() {
        for (TrainStation st : allStations) {
            List<TrainStation> byLat = tzSystem.findByLatitude(st.getLatitude());
            List<TrainStation> byLon = tzSystem.findByLongitude(st.getLongitude());
            List<TrainStation> byTzCountry = tzSystem.findByTimeZoneAndCountry(st.getTimeZoneGroup(), st.getCountry());

            assertTrue(byLat.contains(st));
            assertTrue(byLon.contains(st));
            assertTrue(byTzCountry.contains(st));
        }
    }

    /**
     * Tests a latitude range query that matches no stations.
     */
    @Test
    void testLatitudeRangeNoMatches() {
        double minLat = 100.0;
        double maxLat = 200.0;

        List<TrainStation> result = tzSystem.findByLatitudeRange(minLat, maxLat);
        assertNotNull(result);
        assertTrue(result.isEmpty(), "No stations should match this out-of-range latitude");
    }

    /**
     * Tests a windowed query using non-existing timezone groups.
     */
    @Test
    void testWindowedQueryNonExistingGroups() {
        Map<String, Map<String, List<TrainStation>>> result = tzSystem.windowedQuery("NON_EXIST", "XYZ");

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Windowed query for non-existing groups must return empty map");
    }

    /**
     * Tests windowed queries across existing timezone groups.
     */
    @Test
    void testWindowedQuery() {
        Map<String, Map<String, List<TrainStation>>> windowed = tzSystem.windowedQuery("CET", "WET");

        assertNotNull(windowed);
        windowed.forEach((tz, countryMap) -> {
            countryMap.forEach((country, stations) -> {
                for (TrainStation st : stations) {
                    assertEquals(tz, st.getTimeZoneGroup());
                    assertEquals(country, st.getCountry());
                }
            });
        });
    }

    /**
     * Verifies latitude range query returns stations within expected bounds.
     */
    @Test
    void testLatitudeRangeQuery() {
        double minLat = 40.0;
        double maxLat = 60.0;
        List<TrainStation> range = tzSystem.findByLatitudeRange(minLat, maxLat);

        assertNotNull(range);
        for (TrainStation st : range) {
            assertTrue(st.getLatitude() >= minLat && st.getLatitude() <= maxLat,
                    "Station latitude must be within range");
        }
    }

    /**
     * Confirms that all stations are consistently indexed across latitude, longitude, and timezone-country trees.
     */
    @Test
    void testConsistencyAcrossTrees() {
        for (TrainStation st : allStations) {
            if (st.getStation() != null) {
                List<TrainStation> byLat = tzSystem.findByLatitude(st.getLatitude());
                List<TrainStation> byLon = tzSystem.findByLongitude(st.getLongitude());
                List<TrainStation> byTzCountry = tzSystem.findByTimeZoneAndCountry(st.getTimeZoneGroup(), st.getCountry());

                assertTrue(byLat.contains(st));
                assertTrue(byLon.contains(st));
                assertTrue(byTzCountry.contains(st));
            }
        }
    }

    /**
     * Tests the distanceTo method of TrainStation for non-negative values.
     */
    @Test
    void testStationDistanceMethod() {
        if (allStations.size() >= 2) {
            TrainStation st1 = allStations.get(0);
            TrainStation st2 = allStations.get(1);
            double distance = st1.distanceTo(st2);

            assertTrue(distance >= 0, "Distance must be non-negative");
        }
    }

    /**
     * Tests AVLTree min/max values and tree height.
     */
    @Test
    void testAVLTreeHeightAndMinMax() {
        int tzHeight = tzSystem.getTimeZoneIndexHeight();
        System.out.println("TimeZoneIndexTree height: " + tzHeight);
        assertTrue(tzHeight > 0);

        AVLTree<Double, List<TrainStation>> latTree = new AVLTree<>();
        for (TrainStation st : allStations) {
            latTree.insert(st.getLatitude(), List.of(st));
        }

        assertNotNull(latTree.min());
        assertNotNull(latTree.max());
    }

    /**
     * Confirms in-order traversal of AVLTree preserves key-value consistency.
     */
    @Test
    void testInOrderTraversal() {
        AVLTree<Double, List<TrainStation>> latTree = new AVLTree<>();
        for (TrainStation st : allStations) {
            latTree.insert(st.getLatitude(), List.of(st));
        }

        latTree.inOrder((lat, list) -> {
            for (TrainStation st : list) {
                assertEquals(lat, st.getLatitude(), "In-order traversal must match latitude");
            }
        });
    }

    /**
     * Ensures stations in timezone groups are alphabetically sorted by name.
     */
    @Test
    void testSortedOrderInIndexes() {
        Map<String, List<TrainStation>> tzGroupMap = tzSystem.findByTimeZoneGroup("CET");
        tzGroupMap.values().forEach(list -> {
            for (int i = 1; i < list.size(); i++) {
                assertTrue(list.get(i-1).getStation().compareTo(list.get(i).getStation()) <= 0,
                        "Stations must be sorted by name");
            }
        });
    }
}

