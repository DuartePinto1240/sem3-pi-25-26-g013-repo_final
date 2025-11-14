package main.java.pt.ipp.isep.dei.domain;

import java.util.*;

/**
 * Demonstration class for USEI06 with 5 sample queries.
 * Shows practical usage of the TimeZoneIndexSystem.
 */
public class USEI06Demo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  USEI06 - Time-Zone Index and Windowed Queries Demo        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Load stations from CSV
        System.out.println("📁 Loading European railway stations...");
        TrainStationLoader loader = new TrainStationLoader();

        // Adjust path to your CSV location
        String csvPath = "C:\\Users\\duart\\IdeaProjects\\sem3-pi-25-26-g013-repo\\train_station_dataset_v2\\train_stations_europe.csv"; // or full path
        List<TrainStation> stations = loader.load(csvPath);

        System.out.println("✅ Loaded " + stations.size() + " stations from CSV\n");

        // Build index system
        System.out.println("🏗️  Building index system...");
        TimeZoneIndexSystem indexSystem = new TimeZoneIndexSystem(stations);
        System.out.println();

        // Run sample queries
        runSampleQuery1(indexSystem);
        runSampleQuery2(indexSystem);
        runSampleQuery3(indexSystem);
        runSampleQuery4(indexSystem);
        runSampleQuery5(indexSystem);

        // Print complexity analysis
        indexSystem.printComplexityAnalysis();
    }

    /**
     * Sample Query 1: Find all stations at a specific latitude
     * Demonstrates: Coordinate-based search with duplicate handling
     */
    private static void runSampleQuery1(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 1: Find stations at specific latitude              │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        // Try common latitudes
        double[] testLatitudes = {38.7223, 51.5074, 48.8566};

        for (double lat : testLatitudes) {
            List<TrainStation> results = system.findByLatitude(lat);

            if (!results.isEmpty()) {
                System.out.printf("\n📍 Latitude: %.4f°%n", lat);
                System.out.println("✅ Found " + results.size() + " station(s):");

                int showCount = Math.min(5, results.size());
                for (int i = 0; i < showCount; i++) {
                    TrainStation station = results.get(i);
                    System.out.printf("   • %s (%s) - [%.5f, %.5f]%n",
                            station.getStation(),
                            station.getCountry(),
                            station.getLatitude(),
                            station.getLongitude());
                }

                if (results.size() > 5) {
                    System.out.printf("   ... and %d more%n", results.size() - 5);
                }
                break; // Show only first found
            }
        }

        System.out.println("\n⏱️  Complexity: O(log n)\n");
    }

    /**
     * Sample Query 2: Find all stations in CET time zone, grouped by country
     * Demonstrates: Time zone indexing with country ordering
     */
    private static void runSampleQuery2(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 2: All CET stations grouped by country             │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        String timeZone = "CET";
        Map<String, List<TrainStation>> results = system.findByTimeZoneGroup(timeZone);

        System.out.println("🌍 Time Zone: " + timeZone);
        System.out.println("✅ Found stations in " + results.size() + " countries:");

        int totalStations = 0;
        int countriesShown = 0;

        for (Map.Entry<String, List<TrainStation>> entry : results.entrySet()) {
            String country = entry.getKey();
            int count = entry.getValue().size();
            totalStations += count;

            if (countriesShown < 10) { // Show first 10 countries
                System.out.printf("   • %s: %d stations%n", country, count);

                // Show first 3 stations as sample
                if (count > 0 && countriesShown < 5) { // Detailed for first 5
                    List<TrainStation> stations = entry.getValue();
                    int showCount = Math.min(3, count);
                    for (int i = 0; i < showCount; i++) {
                        System.out.printf("      - %s%n", stations.get(i).getStation());
                    }
                    if (count > 3) {
                        System.out.printf("      ... and %d more%n", count - 3);
                    }
                }
                countriesShown++;
            }
        }

        if (results.size() > 10) {
            System.out.printf("   ... and %d more countries%n", results.size() - 10);
        }

        System.out.println("\n📊 Total stations in CET: " + totalStations);
        System.out.println("⏱️  Complexity: O(log n + m) where m = countries\n");
    }

    /**
     * Sample Query 3: Find stations in specific time zone and country
     * Demonstrates: Combined time zone + country filtering
     */
    private static void runSampleQuery3(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 3: Stations by time zone and country               │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        // Try different combinations
        String[][] queries = {
                {"WET/GMT", "PT"}, // Portugal
                {"CET", "FR"},     // France
                {"CET", "DE"}      // Germany
        };

        for (String[] query : queries) {
            String timeZone = query[0];
            String country = query[1];
            List<TrainStation> results = system.findByTimeZoneAndCountry(timeZone, country);

            if (!results.isEmpty()) {
                System.out.printf("\n🌍 Query: %s, %s%n", timeZone, country);
                System.out.println("✅ Found " + results.size() + " station(s):");

                // Show first 10 stations
                int showCount = Math.min(10, results.size());
                for (int i = 0; i < showCount; i++) {
                    TrainStation station = results.get(i);
                    System.out.printf("   %2d. %s", i + 1, station.getStation());
                    if (station.isMainStation()) System.out.print(" 🚉");
                    if (station.isCity()) System.out.print(" 🏙️");
                    if (station.isAirport()) System.out.print(" ✈️");
                    System.out.println();
                }

                if (results.size() > 10) {
                    System.out.printf("   ... and %d more stations%n", results.size() - 10);
                }
                break; // Show only first successful query
            }
        }

        System.out.println("\n⏱️  Complexity: O(log n + log m)\n");
    }

    /**
     * Sample Query 4: Windowed query across multiple time zones
     * Demonstrates: Multi-zone search (e.g., for route planning)
     */
    private static void runSampleQuery4(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 4: Windowed query - Multiple time zones            │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        String[] zones = {"CET", "WET/GMT", "EET"};
        Map<String, Map<String, List<TrainStation>>> results =
                system.windowedQuery(zones);

        System.out.println("🔍 Searching zones: " + Arrays.toString(zones));
        System.out.println("✅ Results by time zone:\n");

        int grandTotal = 0;
        for (Map.Entry<String, Map<String, List<TrainStation>>> tzEntry : results.entrySet()) {
            String tz = tzEntry.getKey();
            Map<String, List<TrainStation>> countries = tzEntry.getValue();

            int tzTotal = countries.values().stream()
                    .mapToInt(List::size)
                    .sum();
            grandTotal += tzTotal;

            System.out.printf("   🌍 %s (%d countries, %d stations):%n",
                    tz, countries.size(), tzTotal);

            // Show top 3 countries by station count
            countries.entrySet().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                    .limit(3)
                    .forEach(entry -> {
                        System.out.printf("      • %s: %d stations%n",
                                entry.getKey(), entry.getValue().size());
                    });

            if (countries.size() > 3) {
                System.out.printf("      ... and %d more countries%n", countries.size() - 3);
            }
            System.out.println();
        }

        System.out.println("📊 Grand total: " + grandTotal + " stations across " +
                zones.length + " time zones");
        System.out.println("⏱️  Complexity: O(k·log n + m) where k = " + zones.length + "\n");
    }

    /**
     * Sample Query 5: Find stations in latitude range
     * Demonstrates: Range query capabilities
     */
    private static void runSampleQuery5(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 5: Stations in latitude range                      │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        // Different regions to try
        double[][] ranges = {
                {37.0, 39.0},  // Southern Europe (Portugal/Spain)
                {51.0, 52.0},  // Central Europe (London/Amsterdam)
                {48.0, 49.0}   // Central Europe (Paris/Munich)
        };

        for (double[] range : ranges) {
            double minLat = range[0];
            double maxLat = range[1];
            List<TrainStation> results = system.findByLatitudeRange(minLat, maxLat);

            if (!results.isEmpty()) {
                System.out.printf("\n📐 Range: %.1f°N to %.1f°N%n", minLat, maxLat);
                System.out.println("✅ Found " + results.size() + " station(s):");

                // Group by country for better visualization
                Map<String, Long> byCountry = new HashMap<>();
                for (TrainStation station : results) {
                    byCountry.merge(station.getCountry(), 1L, Long::sum);
                }

                System.out.println("\n   Distribution by country:");
                byCountry.entrySet().stream()
                        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                        .limit(5) // Top 5 countries
                        .forEach(entry -> {
                            System.out.printf("   • %s: %d stations%n",
                                    entry.getKey(), entry.getValue());
                        });

                // Show sample stations
                System.out.println("\n   Sample stations:");
                results.stream()
                        .limit(5)
                        .forEach(station -> {
                            System.out.printf("   • %s (%s) - %.4f°N%n",
                                    station.getStation(),
                                    station.getCountry(),
                                    station.getLatitude());
                        });

                if (results.size() > 5) {
                    System.out.printf("   ... and %d more%n", results.size() - 5);
                }
                break; // Show only first successful range
            }
        }

        System.out.println("\n⏱️  Complexity: O(n) - full traversal required\n");
    }
}