package test.java.pt.ipp.isep.dei.domain.USEI09;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.service.ProximitySearchService;
import main.java.pt.ipp.isep.dei.dto.ProximitySearchResult;

import main.java.pt.ipp.isep.dei.util.GeoUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for USEI09 – Proximity Search (Nearest-N with Filters).
 *
 * This test:
 *  • Loads the CSV dataset
 *  • Builds the TwoDTree
 *  • Calls ProximitySearchService
 *  • Validates N results, ordering, distance correctness, filters, and metrics
 */
public class USEI09Test {

    private static final String CSV_PATH = "train_station_dataset_v2/train_stations_europe.csv";

    private static TwoDTree tree;
    private static ProximitySearchService service;

    @BeforeAll
    public static void setup() throws Exception {

        // Load station dataset
        TrainStationLoader loader = new TrainStationLoader();
        List<TrainStation> allStations = loader.load(CSV_PATH);

        assertNotNull(allStations, "Stations list must not be null");
        assertFalse(allStations.isEmpty(), "CSV file must load stations");

        // Build KD-Tree
        tree = new TwoDTree(allStations);
        assertFalse(tree.isEmpty(), "KD-Tree must not be empty");

        // Prepare service
        service = new ProximitySearchService();
    }

    @Test
    @DisplayName("USEI09 – Find nearest 5 stations without filter")
    public void testNearestStationsWithoutFilter() {

        double lat = 48.8566;  // Paris
        double lon = 2.3522;
        int N = 5;

        ProximitySearchResult result =
                service.findNearestStations(tree, lat, lon, N, null);

        List<StationDistance> list = result.getNearestStations();

        assertNotNull(list);
        assertEquals(N, list.size(), "Must return exactly N results");

        // Distances must be sorted ASC (AC4)
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getDistanceKm()
                            <= list.get(i + 1).getDistanceKm(),
                    "Results must be sorted by ascending distance");
        }

        // Metrics must be recorded (AC6)
        assertTrue(result.getVisitedNodes() > 0, "Visited nodes must be > 0");
        assertTrue(result.getSearchTimeMs() >= 0);
    }


    @Test
    @DisplayName("USEI09 – Nearest N with timeZoneGroup filter (CET)")
    public void testNearestWithFilter() {

        double lat = 41.1579; // Porto
        double lon = -8.6291;
        int N = 7;

        ProximitySearchResult result =
                service.findNearestStations(tree, lat, lon, N, "CET");

        List<StationDistance> list = result.getNearestStations();

        // N results or fewer (if dataset has fewer matching)
        assertNotNull(list);
        assertTrue(list.size() <= N);

        // Every station returned must satisfy the filter (AC3)
        for (StationDistance sd : list) {
            assertEquals("CET",
                    sd.getStation().getTimeZoneGroup().toUpperCase(),
                    "All stations must match CET filter");
        }

        // Distances must be ascending
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getDistanceKm()
                            <= list.get(i + 1).getDistanceKm(),
                    "Results must be sorted by ascending distance");
        }
    }


    @Test
    @DisplayName("USEI09 – Validates HAVERSINE correctness on a known example")
    public void testHaversineDistanceSanity() {

        // Example: Paris → London (approx 343 km)
        double dist = GeoUtils.haversineDistance(
                48.8566, 2.3522,     // Paris
                51.5074, -0.1278     // London
        );

        assertTrue(dist > 300 && dist < 400,
                "Haversine must return correct real-world distance (~343 km)");
    }
}
