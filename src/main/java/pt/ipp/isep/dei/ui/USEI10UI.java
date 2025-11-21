package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USEI10Controller;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User interface for executing the USEI10 use case:
 * <strong>Radius-based station search and density summary.</strong>
 *
 * <p>This UI is console-driven and interacts with {@link USEI10Controller}
 * to perform the spatial search and display results, including sorted listings,
 * country summary, and city/non-city summary.</p>
 */

public class USEI10UI implements Runnable{

    /** Controller responsible for executing the radius search logic. */
    private final USEI10Controller controller;

    /** Constructs the UI and initializes its controller.*/
    public USEI10UI() {
        this.controller = new USEI10Controller();
    }

    /**
     * Entry point for running the console-based UI.
     * <p>Prompts the user for coordinates and search radius, runs the search,
     * and prints formatted results including totals, sorted listing, and summaries.</p>
     */
    @Override
    public void run() {
        System.out.println("=== USEI10 - Radius search and density summary ===");

        String csvPath = "train_station_dataset_v2/train_stations_europe.csv";

        double lat = readDouble("Target latitude (-90 a 90): ");
        double lon = readDouble("Target longitude (-180 a 180): ");
        double radius = readDouble("Radius (km): ");

        System.out.println("\n Running radius search...");

        USEI10Controller.Result res = controller.radiusSearch(csvPath, lat, lon, radius);

        System.out.println("");
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
        System.out.println(" Build KD-Tree : O(N log N)");
        System.out.println(" KD-Tree rangeSearch (bounding box): O(log N + k)");
        System.out.println(" Haversine distance filtering (exact circle): O(k)");
        System.out.println(" Sorting k results: O(k log k)");
        System.out.println(" AVL insertions (k log k): O(k log k)");
        System.out.println("Overall: O(log N + k log k), where k = #stations inside the bounding box.");
    }

    /**
     * Reads a double value from the console, repeatedly prompting the user
     * until a valid number is entered.
     *
     * @param prompt text displayed to the user
     * @return the parsed double value
     */
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
