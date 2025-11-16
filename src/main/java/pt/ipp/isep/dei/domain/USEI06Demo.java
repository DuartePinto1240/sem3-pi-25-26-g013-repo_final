package main.java.pt.ipp.isep.dei.domain;

import java.util.*;

/**
 * Demonstration class for USEI06 with 5 sample queries.
 * Shows practical usage of the TimeZoneIndexSystem.
 */
public class USEI06Demo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  USEI06 - Time-Zone Index and Windowed Queries Demo      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        try {
            long totalStart = System.currentTimeMillis();

            // Load stations from CSV
            System.out.println("📁 Loading European railway stations...");
            long loadStart = System.currentTimeMillis();

            TrainStationLoader loader = new TrainStationLoader();
            String csvPath = "train_station_dataset_v2/train_stations_europe.csv";
            List<TrainStation> stations = loader.load(csvPath);

            long loadTime = System.currentTimeMillis() - loadStart;
            System.out.println("✅ Loaded " + stations.size() + " stations in " + loadTime + "ms\n");

            // Build index system
            System.out.println("🏗️  Building index system...");
            long indexStart = System.currentTimeMillis();

            TimeZoneIndexSystem indexSystem = new TimeZoneIndexSystem(stations);

            long indexTime = System.currentTimeMillis() - indexStart;
            System.out.println("⚡ Index built in " + indexTime + "ms\n");

            // Run sample queries
            runSampleQuery1(indexSystem);
            runSampleQuery2(indexSystem);
            runSampleQuery3(indexSystem);
            runSampleQuery4(indexSystem);
            runSampleQuery5(indexSystem);

            // Print complexity analysis
            indexSystem.printComplexityAnalysis();

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sample Query 1: Find all stations at a specific latitude
     * Demonstrates: Coordinate-based search with duplicate handling
     */
    private static void runSampleQuery1(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 1: Find stations at specific latitude             │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        System.out.println("DEBUG: Query 1 started");

        double[] testLatitudes = {38.7223, 51.5074, 48.8566, 40.0, 45.0, 50.0};
        boolean found = false;

        for (double lat : testLatitudes) {
            System.out.println("DEBUG: Testing latitude " + lat);
            List<TrainStation> results = system.findByLatitude(lat);
            System.out.println("DEBUG: Got " + results.size() + " results");

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
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("⚠️ No stations found at tested latitudes");
            System.out.println("DEBUG: Total unique latitudes: " + system.getUniqueLatitudes());
        }

        System.out.println("\n⏱️  Complexity: O(log n)\n");
    }

    /**
     * Sample Query 2: Find all stations in CET time zone, grouped by country
     */
    private static void runSampleQuery2(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 2: All CET stations grouped by country            │");
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

            if (countriesShown < 10) {
                System.out.printf("   • %s: %d stations%n", country, count);

                if (count > 0 && countriesShown < 5) {
                    List<TrainStation> stations = entry.getValue();
                    int showCount = Math.min(3, count);
                    for (int i = 0; i < showCount; i++) {
                        System.out.printf("      - %s%n", stations.get(i).getStation());
                    }
                    if (count > 3) System.out.printf("      ... and %d more%n", count - 3);
                }

                countriesShown++;
            }
        }

        if (results.size() > 10)
            System.out.printf("   ... and %d more countries%n", results.size() - 10);

        System.out.println("\n📊 Total stations in CET: " + totalStations);
        System.out.println("⏱️  Complexity: O(log n + m)\n");
    }

    /**
     * Sample Query 3: Combined time zone + country filtering
     */
    private static void runSampleQuery3(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 3: Stations by time zone and country              │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        String[][] queries = {
                {"WET/GMT", "PT"},
                {"CET", "FR"},
                {"CET", "DE"}
        };

        for (String[] query : queries) {
            String timeZone = query[0];
            String country = query[1];
            List<TrainStation> results = system.findByTimeZoneAndCountry(timeZone, country);

            if (!results.isEmpty()) {
                System.out.printf("\n🌍 Query: %s, %s%n", timeZone, country);
                System.out.println("✅ Found " + results.size() + " station(s):");

                int showCount = Math.min(10, results.size());
                for (int i = 0; i < showCount; i++) {
                    TrainStation station = results.get(i);
                    System.out.printf("   %2d. %s", i + 1, station.getStation());
                    if (station.isMainStation()) System.out.print(" 🚉");
                    if (station.isCity()) System.out.print(" 🏙️");
                    if (station.isAirport()) System.out.print(" ✈️");
                    System.out.println();
                }

                if (results.size() > 10)
                    System.out.printf("   ... and %d more%n", results.size() - 10);

                break;
            }
        }

        System.out.println("\n⏱️  Complexity: O(log n + log m)\n");
    }

    /**
     * Sample Query 4: Windowed query across multiple time zones
     */
    private static void runSampleQuery4(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 4: Windowed query - Multiple time zones           │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        String[] zones = {"CET", "WET/GMT", "EET"};
        Map<String, Map<String, List<TrainStation>>> results = system.windowedQuery(zones);

        System.out.println("🔍 Searching zones: " + Arrays.toString(zones));
        System.out.println("✅ Results by time zone:\n");

        int grandTotal = 0;

        for (Map.Entry<String, Map<String, List<TrainStation>>> tzEntry : results.entrySet()) {
            String tz = tzEntry.getKey();
            Map<String, List<TrainStation>> countries = tzEntry.getValue();

            int tzTotal = countries.values().stream().mapToInt(List::size).sum();
            grandTotal += tzTotal;

            System.out.printf("   🌍 %s (%d countries, %d stations):%n",
                    tz, countries.size(), tzTotal);

            countries.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                    .limit(3)
                    .forEach(e ->
                            System.out.printf("      • %s: %d stations%n", e.getKey(), e.getValue().size())
                    );

            if (countries.size() > 3)
                System.out.printf("      ... and %d more%n", countries.size() - 3);

            System.out.println();
        }

        System.out.println("📊 Grand total: " + grandTotal +
                " stations across " + zones.length + " time zones");
        System.out.println("⏱️  Complexity: O(k·log n + m)\n");
    }

    /**
     * Sample Query 5: Range query for latitude
     */
    private static void runSampleQuery5(TimeZoneIndexSystem system) {
        System.out.println("┌──────────────────────────────────────────────────────────┐");
        System.out.println("│ QUERY 5: Stations in latitude range                     │");
        System.out.println("└──────────────────────────────────────────────────────────┘");

        double[][] ranges = {
                {37.0, 39.0},
                {51.0, 52.0},
                {48.0, 49.0}
        };

        for (double[] range : ranges) {
            double minLat = range[0];
            double maxLat = range[1];

            List<TrainStation> results = system.findByLatitudeRange(minLat, maxLat);

            if (!results.isEmpty()) {
                System.out.printf("\n📐 Range: %.1f°N to %.1f°N%n", minLat, maxLat);
                System.out.println("✅ Found " + results.size() + " station(s):");

                Map<String, Long> byCountry = new HashMap<>();
                for (TrainStation station : results) {
                    byCountry.merge(station.getCountry(), 1L, Long::sum);
                }

                System.out.println("\n   Distribution by country:");
                byCountry.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .limit(5)
                        .forEach(e ->
                                System.out.printf("   • %s: %d stations%n", e.getKey(), e.getValue())
                        );

                System.out.println("\n   Sample stations:");
                results.stream().limit(5).forEach(station ->
                        System.out.printf("   • %s (%s) - %.4f°N%n",
                                station.getStation(),
                                station.getCountry(),
                                station.getLatitude())
                );

                if (results.size() > 5)
                    System.out.printf("   ... and %d more%n", results.size() - 5);

                break;
            }
        }

        System.out.println("\n⏱️  Complexity: O(n) - full traversal required\n");
    }
}
