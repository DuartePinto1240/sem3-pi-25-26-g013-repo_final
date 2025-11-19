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
        System.out.println("CSV path: " + csvPath);
        System.out.println("Stations loaded: " + allStations.size());

        controller = new USEI10Controller();
    }

    private List<TrainStation> naiveRadiusScan(double lat, double lon, double radiusKm) {

        List<TrainStation> result = new ArrayList<>();

        for (TrainStation s : allStations) {
            double d = haversineKm(lat, lon, s.getLatitude(), s.getLongitude());
            if (d <= radiusKm) {
                result.add(s);
            }
        }
        return result;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0088;

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Test
    void testRadiusSmall() {

        double lat = 38.7223;
        double lon = -9.1393;
        double radiusKm = 10.0;

        System.out.println("\n--- TEST 1: Radius 10 km around Lisbon ---");

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        List<TrainStation> naive = naiveRadiusScan(lat, lon, radiusKm);

        assertEquals(naive.size(), result.matchedCount);

        for (TrainStation s : result.tree.inOrder()) {
            assertTrue(naive.contains(s));
        }
    }


    @Test
    void testRadiusPortugalWide() {

        double lat = 39.5;
        double lon = -8.0;
        double radiusKm = 180.0;

        System.out.println("\n--- TEST 2: Radius ~180 km around Portugal ---");

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        List<TrainStation> naive = naiveRadiusScan(lat, lon, radiusKm);

        assertEquals(naive.size(), result.matchedCount);

        List<Map.Entry<DistanceNameKey, TrainStation>> list =
                result.tree.inOrder();

        for (int i = 1; i < list.size(); i++) {
            DistanceNameKey prev = list.get(i - 1).getKey();
            DistanceNameKey curr = list.get(i).getKey();
            assertTrue(prev.compareTo(curr) <= 0);
        }
    }

    @Test
    void testRadiusEurope() {

        double lat = 48.0;
        double lon = 9.0; // mais ou menos no centro da Europa
        double radiusKm = 1000.0;

        System.out.println("\n--- TEST 3: Radius 1000 km covering Europe ---");

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        List<TrainStation> naive = naiveRadiusScan(lat, lon, radiusKm);

        assertEquals(naive.size(), result.matchedCount);

        Map<String, Long> naiveByCountry = new HashMap<>();
        for (TrainStation s : naive) {
            naiveByCountry.merge(s.getCountry(), 1L, Long::sum);
        }

        assertEquals(naiveByCountry, result.countByCountry);
    }

    @Test
    void testNoStationsInOcean() {

        System.out.println("\n--- TEST 4: Ocean radius test (0 results expected) ---");

        // Middle of the Atlantic
        double lat = 0.0;
        double lon = -40.0;
        double radiusKm = 50.0;

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        assertEquals(0, result.matchedCount);
        assertTrue(result.tree.isEmpty());
    }

    @Test
    void testResultsSubsetOfDataset() {

        System.out.println("\n--- TEST 5: All returned stations must be from dataset ---");

        double lat = 47.0;
        double lon = 2.0;
        double radiusKm = 500.0;

        var result = controller.radiusSearch(
                "train_station_dataset_v2/train_stations_europe.csv",
                lat, lon, radiusKm
        );

        for (TrainStation s : result.tree.inOrder()) {
            assertTrue(allStations.contains(s));
        }
    }
}
