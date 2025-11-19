package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USEI10Controller;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class USEI10UI implements Runnable{
    private final USEI10Controller controller;

    public USEI10UI() {
        this.controller = new USEI10Controller();
    }

    @Override
    public void run() {
        System.out.println("=== USEI10 - Radius search and density summary ===");

        String csvPath = "train_station_dataset_v2/train_stations_europe.csv";

        double lat = readDouble("Target latitude (-90 a 90): ");
        double lon = readDouble("Target longitude (-180 a 180): ");
        double radius = readDouble("Radius (km): ");

        System.out.println("\n Running radius search...");

        USEI10Controller.Result res = controller.radiusSearch(csvPath, lat, lon, radius);

        System.out.printf("  Total stations indexed: %d%n", res.totalIndexedStations);
        System.out.printf("  Stations within %.3f km: %d%n%n", radius, res.matchedCount);

        System.out.println("List (distance ASC, name DESC):");
        AtomicInteger idx = new AtomicInteger(1);
        res.tree.inOrder((k, v) -> {
            System.out.printf("%3d) %s | %.3f km | %s | %s%n",
                    idx.getAndIncrement(),
                    v.getStation(),
                    k.getDistance(),
                    v.getCountry(),
                    v.isCity() ? "City" : "NotCity");
        });

        System.out.println("\nSummary by country:");
        for (Map.Entry<String, Long> e : res.countByCountry.entrySet()) {
            System.out.printf("  %s: %d%n", e.getKey(), e.getValue());
        }

        System.out.println("\nSummary by isCity:");
        for (Map.Entry<Boolean, Long> e : res.countByIsCity.entrySet()) {
            System.out.printf("  isCity=%s: %d%n", e.getKey(), e.getValue());
        }

        System.out.println("\n--- Temporal Complexity ---");
        System.out.println("  1) Load stations (TrainStationLoader): O(N)");
        System.out.println("  2) Bounding-box filter: O(N)");
        System.out.println("  3) Haversine on candidates (k): O(k)");
        System.out.println("  4) AVL insertions (k log k)");
        System.out.println("Overall: O(N + k log k) where k = #candidates inside bounding box.");
    }

    private double readDouble(String prompt) {
        while (true) {
            String line = Utils.readLineFromConsole(prompt);
            try {
                return Double.parseDouble(line.trim());
            } catch (Exception ex) {
                System.out.println("Invalid number, try again.");
            }
        }
    }
}
