package test.java.pt.ipp.isep.dei.domain.USEI08;

import main.java.pt.ipp.isep.dei.controller.USEI08Controller;
import main.java.pt.ipp.isep.dei.domain.KDTreeStations;
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for USEI08 - Search by Geographical Area.
 *
 * The tests load the real CSV dataset, build a KD-tree and verify
 * that the range-search with optional filters produces results
 * consistent with a naive O(N) scan over the dataset.
 */
class USEI08Test {

    private static List<TrainStation> allStations;
    private static KDTreeStations kdTree;
    private static USEI08Controller controller;

    /**
     * Loads the CSV dataset and builds the KD-tree.
     */
    @BeforeAll
    static void setup() {
        String csvPath = "train_station_dataset_v2/train_stations_europe.csv";

        TrainStationLoader loader = new TrainStationLoader();
        allStations = loader.load(csvPath);

        System.out.println("\n=== USEI08 TEST INITIALIZATION ===");
        System.out.println("CSV path: " + csvPath);
        System.out.println("Stations loaded: " + allStations.size());

        kdTree = new KDTreeStations();

        long start = System.nanoTime();
        kdTree.buildFromStations(allStations);
        long end = System.nanoTime();

        System.out.println("KD-tree built in " + ((end - start) / 1_000_000) + " ms\n");

        controller = new USEI08Controller(kdTree);
    }

    /**
     * Performs a naive O(N) linear scan applying the same filters
     * used by the KD-tree search, for correctness comparison.
     *
     * @return a list of stations matching the query
     */
    private List<TrainStation> naiveSearch(double latMin, double latMax,
                                           double lonMin, double lonMax,
                                           Boolean isCity, Boolean isMainStation,
                                           String country) {

        return allStations.stream()
                .filter(s -> s.getLatitude() >= latMin && s.getLatitude() <= latMax)
                .filter(s -> s.getLongitude() >= lonMin && s.getLongitude() <= lonMax)
                .filter(s -> isCity == null || s.isCity() == isCity)
                .filter(s -> isMainStation == null || s.isMainStation() == isMainStation)
                .filter(s -> country == null || country.equalsIgnoreCase(s.getCountry()))
                .toList();
    }

    /**
     * Tests the query for Portuguese cities inside the bounding box
     * lat[36,43] × lon[-10,-5], matching Query 1 of the USEI08 UI.
     */
    @Test
    void testPortugueseCitiesBoundingBox() {
        double latMin = 36.0, latMax = 43.0;
        double lonMin = -10.0, lonMax = -5.0;

        System.out.println("\n--- TEST 1: Portuguese Cities in Bounding Box ---");

        List<TrainStation> kdResult = controller.searchStationsInArea(
                latMin, latMax, lonMin, lonMax,
                true, null, "PT"
        );

        System.out.println("KD-tree returned: " + kdResult.size() + " stations");
        kdResult.stream().limit(5).forEach(s ->
                System.out.println(" • " + s.getStation() + " (" + s.getCountry() + ")")
        );

        List<TrainStation> naive = naiveSearch(
                latMin, latMax, lonMin, lonMax,
                true, null, "PT"
        );

        assertEquals(naive.size(), kdResult.size());
    }

    /**
     * Tests the query for Spanish main stations inside the Iberian
     * bounding box lat[36,44] × lon[-10,4], matching Query 2 of the UI.
     */
    @Test
    void testSpanishMainStationsIberianBoundingBox() {
        double latMin = 36.0, latMax = 44.0;
        double lonMin = -10.0, lonMax = 4.0;

        System.out.println("\n--- TEST 2: Spanish Main Stations ---");

        List<TrainStation> kdResult = controller.searchStationsInArea(
                latMin, latMax, lonMin, lonMax,
                null, true, "ES"
        );

        System.out.println("KD-tree returned: " + kdResult.size() + " stations");
        kdResult.stream().limit(5).forEach(s ->
                System.out.println(" • " + s.getStation() + " (" + s.getCountry() + ")")
        );

        List<TrainStation> naive = naiveSearch(
                latMin, latMax, lonMin, lonMax,
                null, true, "ES"
        );

        assertEquals(naive.size(), kdResult.size());
    }

    /**
     * Tests the query for cities in the latitude band [37,39] with ALL countries,
     * matching Query 3 of the USEI08 UI.
     */
    @Test
    void testCitiesLatitudeBandAllCountries() {
        double latMin = 37.0, latMax = 39.0;
        double lonMin = -10.0, lonMax = 3.0;

        System.out.println("\n--- TEST 3: Cities in Latitude Band [37,39] ---");

        List<TrainStation> kdResult = controller.searchStationsInArea(
                latMin, latMax, lonMin, lonMax,
                true, null, null
        );

        System.out.println("KD-tree returned: " + kdResult.size() + " stations");
        kdResult.stream().limit(5).forEach(s ->
                System.out.println(" • " + s.getStation() + " (" + s.getCountry() + ")")
        );

        List<TrainStation> naive = naiveSearch(
                latMin, latMax, lonMin, lonMax,
                true, null, null
        );

        assertEquals(naive.size(), kdResult.size());
    }

    /**
     * Tests a rectangle that cannot contain Portuguese stations (lon ∈ [50,100]),
     * ensuring that the KD-tree returns an empty result.
     */
    @Test
    void testNoPortugueseStationsInFarEast() {
        System.out.println("\n--- TEST 4: No PT Stations in Far East Rectangle ---");

        List<TrainStation> result = controller.searchStationsInArea(
                -90, 90,
                50, 100,
                null, null, "PT"
        );

        System.out.println("KD-tree returned: " + result.size());
        assertTrue(result.isEmpty());
    }

    /**
     * Ensures that all returned stations are always part of the original CSV dataset
     * for any general-purpose rectangle.
     */
    @Test
    void testKDTreeResultsAreSubsetOfDataset() {
        System.out.println("\n--- TEST 5: KD-tree Results Subset of Dataset ---");

        List<TrainStation> result = controller.searchStationsInArea(
                30, 60,
                -15, 10,
                null, null, null
        );

        System.out.println("KD-tree returned: " + result.size() + " stations");

        for (TrainStation s : result) {
            assertTrue(allStations.contains(s));
        }
    }
}
