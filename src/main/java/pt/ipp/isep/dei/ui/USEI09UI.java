package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;
import main.java.pt.ipp.isep.dei.domain.TwoDTree;
import main.java.pt.ipp.isep.dei.domain.StationDistance;
import main.java.pt.ipp.isep.dei.dto.ProximitySearchResult;
import main.java.pt.ipp.isep.dei.service.ProximitySearchService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Scanner;

/**
 * Console UI for USEI09 - Proximity Search (Nearest-N with Filters).
 *
 * This version includes pre-search validation for the timeZoneGroup filter.
 */
public class USEI09UI implements Runnable {

    private TwoDTree stationTree;
    private Set<String> validTimeZones; // NEW: Stores unique time zones for validation
    private final ProximitySearchService searchService;
    private static final String STATION_FILE_PATH = "train_station_dataset_v2/train_stations_europe.csv";

    /**
     * Constructs a USEI09UI with no arguments.
     */
    public USEI09UI() {
        this.searchService = new ProximitySearchService();
    }

    @Override
    public void run() {
        // 1. Load data and extract available time zones
        if (!loadData()) {
            System.out.println("Error: Failed to load station data for USEI09. Aborting.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- USEI09: Proximity Search (Nearest-N) ---");

        try {
            // 2. Get Coordinates and N (AC0 validation is handled by helper methods)
            double lat = readDouble(scanner, "Enter target latitude [-90, 90]: ", -90, 90);
            double lon = readDouble(scanner, "Enter target longitude [-180, 180]: ", -180, 180);
            int n = readInt(scanner, "Enter N (number of stations to find): ", 1, Integer.MAX_VALUE);

            // 3. Get Time Zone Filter with Validation Loop (FIX)
            String timeZoneFilter = readTimeZoneFilter(scanner);

            // 4. Call Service
            ProximitySearchResult result = searchService.findNearestStations(
                    this.stationTree, lat, lon, n, timeZoneFilter
            );

            // 5. Display Results
            printResults(result);

        } catch (IllegalArgumentException e) {
            System.err.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads and validates the timeZoneGroup filter input.
     * @param scanner The Scanner object.
     * @return A valid time zone string (matching case of the set) or null if empty.
     */
    private String readTimeZoneFilter(Scanner scanner) {
        String filterInput;
        String normalizedFilter = null;
        boolean isValid = false;

        System.out.println("\nAvailable Time Zone Groups: " + this.validTimeZones.stream().collect(Collectors.joining(", ")));

        do {
            System.out.print("Enter optional timeZoneGroup filter (or press Enter for none): ");
            filterInput = scanner.nextLine().trim();

            if (filterInput.isEmpty()) {
                // Case 1: Empty input (No filter)
                normalizedFilter = null;
                isValid = true;
            } else {
                // Case 2: Check if input matches any valid time zone group (case-insensitive)
                String upperInput = filterInput.toUpperCase();

                // Find the exact (correctly cased) match in the valid set
                normalizedFilter = this.validTimeZones.stream()
                        .filter(tz -> tz.equals(upperInput))
                        .findFirst()
                        .orElse(null);

                if (normalizedFilter != null) {
                    isValid = true;
                } else {
                    System.out.println("⚠️ Invalid time zone group: '" + filterInput + "'. Please enter one of the available options or press Enter.");
                }
            }
        } while (!isValid);

        // We return the value in the correct case for the KD-Tree (e.g., "CET" instead of "cet")
        // Note: The KD-Tree's isFilterMatch converts this back to upper case, but consistency is best.
        // Since the UI normalized the input case, we return the user's input, or null if empty.
        return filterInput.isEmpty() ? null : filterInput;
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
                System.out.printf("%-25s | %-7s | %-10.6f | %-10.6f | %-8s | %.2f%n",
                        s.getStation(),
                        s.getCountry(),
                        s.getLatitude(),
                        s.getLongitude(),
                        s.getTimeZoneGroup(),
                        sd.getDistanceKm()
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
     * Loads the station data, builds the TwoDTree, and extracts valid time zones.
     */
    private boolean loadData() {
        try {
            // Correctly calling the 'load' method on TrainStationLoader
            List<TrainStation> allStations = new TrainStationLoader().load(STATION_FILE_PATH);

            if (allStations == null || allStations.isEmpty()) {
                System.err.println("No stations loaded from file: " + STATION_FILE_PATH);
                return false;
            }

            // NEW: Extract all unique time zone groups (case-normalized to upper)
            this.validTimeZones = allStations.stream()
                    .map(s -> s.getTimeZoneGroup().toUpperCase())
                    .collect(Collectors.toSet());

            // Builds the TwoDTree
            this.stationTree = new TwoDTree(allStations);

            // Checks if the inherited BST/AVL base class is empty
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
    // (Ensure these methods are available, or use the Utils class if it exists elsewhere)

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
                    System.out.printf("Error: Value must be between %d and %d (N > 0 is enforced here).%n", min, max);
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