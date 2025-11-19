package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader; // Correct loader
import main.java.pt.ipp.isep.dei.domain.TwoDTree;
import main.java.pt.ipp.isep.dei.domain.StationDistance;
import main.java.pt.ipp.isep.dei.dto.ProximitySearchResult;
import main.java.pt.ipp.isep.dei.service.ProximitySearchService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI for USEI09 - Proximity Search.
 *
 * This UI has an empty constructor and loads its own data
 * when run() is executed, following the project's standard.
 */
public class USEI09UI implements Runnable {

    private TwoDTree stationTree;
    private final ProximitySearchService searchService;
    private static final String STATION_FILE_PATH = "train_station_dataset_v2/train_stations_europe.csv";

    /**
     * Constructs a USEI09UI with no arguments.
     * Data will be loaded when run() is called.
     */
    public USEI09UI() {
        this.searchService = new ProximitySearchService();
        // this.stationTree is null until loadData() is called
    }

    @Override
    public void run() {
        // 1. Load data
        if (!loadData()) {
            System.out.println("Error: Failed to load station data for USEI09. Aborting.");
            System.out.println("Please ensure '" + STATION_FILE_PATH + "' is available.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- USEI09: Proximity Search (Nearest-N) ---");

        try {
            // 2. Get Inputs (from requirements Section 1.5)
            double lat = readDouble(scanner, "Enter target latitude [-90, 90]: ", -90, 90);
            double lon = readDouble(scanner, "Enter target longitude [-180, 180]: ", -180, 180);
            int n = readInt(scanner, "Enter N (number of stations to find): ", 1, Integer.MAX_VALUE);

            System.out.print("Enter optional timeZoneGroup filter (or press Enter for none): ");
            String timeZoneFilter = scanner.nextLine();

            // 3. Call Service
            ProximitySearchResult result = searchService.findNearestStations(
                    this.stationTree, lat, lon, n, timeZoneFilter
            );

            // 4. Display Results (from requirements Section 1.5 & 4)
            printResults(result);

        } catch (IllegalArgumentException e) {
            System.err.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prints the formatted results from the ProximitySearchResult DTO.
     */
    private void printResults(ProximitySearchResult result) {
        System.out.println("\n--- Search Results ---");
        if (result.getNearestStations().isEmpty()) {
            System.out.println("No stations found matching the criteria.");
        } else {
            System.out.println("Found " + result.getNearestStations().size() + " station(s):");

            // Header for output (matches requirements Section 1.5 fields)
            System.out.printf("%-25s | %-7s | %-10s | %-10s | %-8s | %s%n",
                    "Station Name", "Country", "Latitude", "Longitude", "Time Zone", "Distance (km)");
            System.out.println(new String(new char[85]).replace("\0", "-"));

            for (StationDistance sd : result.getNearestStations()) {
                TrainStation s = sd.getStation();
                // **ASSUMPTION**: TrainStation has getCountry() and getTimeZoneGroup()
                System.out.printf("%-25s | %-7s | %-10.6f | %-10.6f | %-8s | %.2f%n",
                        s.getStation(),     // stationName
                        s.getCountry(),     // country
                        s.getLatitude(),    // latitude
                        s.getLongitude(),   // longitude
                        s.getTimeZoneGroup(),// timeZoneGroup
                        sd.getDistanceKm()  // distanceKm
                );
            }
        }

        // 5. Display Temporal Analysis (from requirements Section 4 & AC6)
        System.out.println("\n--- Search Metrics ---");
        System.out.printf("Filter Applied:      %s%n", result.getFilterApplied());
        System.out.printf("Search Time (ms):    %d%n", result.getSearchTimeMs());
        System.out.printf("Visited KD-Tree Nodes: %d%n", result.getVisitedNodes());
        System.out.printf("Average Complexity:  %s%n", result.getAverageComplexity());
        System.out.printf("Worst-Case Complexity: %s%n", result.getWorstCaseComplexity());
    }

    /**
     * Loads the station data using the TrainStationLoader.
     * This is the correct pattern for your project.
     */
    private boolean loadData() {
        try {
            List<TrainStation> allStations = new TrainStationLoader().load(STATION_FILE_PATH);

            if (allStations == null || allStations.isEmpty()) {
                System.err.println("No stations loaded from file: " + STATION_FILE_PATH);
                return false;
            }

            this.stationTree = new TwoDTree(allStations);

            if (this.stationTree.isEmpty()) {
                System.err.println("Station 2D-Tree was created but is empty.");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Critical error loading data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Input Validation Helpers ---

    private double readDouble(Scanner scanner, String prompt, double min, double max) {
        double value;
        while (true) {
            try {
                System.out.print(prompt);
                value = scanner.nextDouble();
                if (value >= min && value <= max) {
                    break;
                } else {
                    System.out.printf("Error: Value must be between %.1f and %.1f.%n", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number.");
                scanner.next(); // Clear invalid input
            }
        }
        scanner.nextLine(); // Consume newline
        return value;
    }

    private int readInt(Scanner scanner, String prompt, int min, int max) {
        int value;
        while (true) {
            try {
                System.out.print(prompt);
                value = scanner.nextInt();
                if (value >= min && value <= max) {
                    break;
                } else {
                    System.out.printf("Error: Value must be between %d and %d.%n", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer.");
                scanner.next(); // Clear invalid input
            }
        }
        scanner.nextLine(); // Consume newline
        return value;
    }
}