package test.java.pt.ipp.isep.dei.domain.USEI10;

import main.java.pt.ipp.isep.dei.controller.USEI10Controller;
import main.java.pt.ipp.isep.dei.domain.DistanceNameKey;
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class USEI10Test {
    private static List<TrainStation> allStations;
    private static USEI10Controller controller;

    @BeforeAll
    static void setup() {
        String csvPath = "train_station_dataset_v2/train_stations_europe.csv";

        TrainStationLoader loader = new TrainStationLoader();
        allStations = loader.load(csvPath);

        System.out.println("\n=== USEI10 TEST INITIALIZATION ===");
        System.out.println("Stations loaded: " + allStations.size());

        controller = new USEI10Controller();
    }

    // Auxiliary naive radius scan for correctness checking
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0088;

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    private List<TrainStation> naiveScan(double lat, double lon, double radiusKm) {
        List<TrainStation> res = new ArrayList<>();

        for (TrainStation s : allStations) {
            if (haversineKm(lat, lon, s.getLatitude(), s.getLongitude()) <= radiusKm) {
                res.add(s);
            }
        }
        return res;
    }

    @Test
    void testSmallRadius() {
        double lat = 38.7223;
        double lon = -9.1393;
        double radiusKm = 10;

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        List<TrainStation> naive = naiveScan(lat, lon, radiusKm);

        assertEquals(naive.size(), result.matchedCount);

        List<TrainStation> avlStations = new ArrayList<>();
        result.tree.inOrder((k, v) -> avlStations.add(v));

        for (TrainStation s : avlStations) {
            assertTrue(naive.contains(s));
        }
    }

    @Test
    void testOrderingCorrectness() {

        double lat = 39.5;
        double lon = -8.0;
        double radiusKm = 200;

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        List<DistanceNameKey> keys = new ArrayList<>();
        result.tree.inOrder((k, v) -> keys.add(k));

        for (int i = 1; i < keys.size(); i++) {
            assertTrue(keys.get(i - 1).compareTo(keys.get(i)) <= 0);
        }
    }

    @Test
    void testCountryCounts() {

        double lat = 48.0;
        double lon = 9.0;
        double radiusKm = 1000;

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        List<TrainStation> naive = naiveScan(lat, lon, radiusKm);

        Map<String, Long> naiveMap = new HashMap<>();
        for (TrainStation s : naive) {
            naiveMap.merge(s.getCountry(), 1L, Long::sum);
        }

        assertEquals(naiveMap, result.countByCountry);
    }

    @Test
    void testNoStations() {

        double lat = 0;
        double lon = -40;
        double radiusKm = 30;

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        assertEquals(0, result.matchedCount);

        List<TrainStation> avl = new ArrayList<>();
        result.tree.inOrder((k, v) -> avl.add(v));
        assertTrue(avl.isEmpty());
    }

    @Test
    void testResultsSubsetOfDataset() {

        double lat = 47.0;
        double lon = 6.0;
        double radiusKm = 500;

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        List<TrainStation> avl = new ArrayList<>();
        result.tree.inOrder((k, v) -> avl.add(v));

        for (TrainStation s : avl) {
            assertTrue(allStations.contains(s));
        }
    }
}
