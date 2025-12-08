package main.java.pt.ipp.isep.dei.domain;

import java.io.*;
import java.util.*;

/**
 * USEI11 - Directed Line Upgrade Plan
 *
 * Computes a topological ordering of Belgian railway stations based on
 * directed track dependencies for upgrade planning.
 *
 * Context: Each directed edge A → B means "Before station B can be fully upgraded,
 * its incoming neighbors (like A) must be upgraded first" to ensure compatibility,
 * signaling, or control dependencies flow in the direction of traffic.
 */
public class DirectedLineUpgradePlan {

    /**
     * Represents a railway station node with all required attributes
     */
    static class Station {
        int stationId;
        String name;
        double lat;          // Geographic latitude
        double lon;          // Geographic longitude
        double coordX;       // X coordinate for GRAPHVIZ
        double coordY;       // Y coordinate for GRAPHVIZ

        public Station(int stationId, String name, double lat, double lon,
                       double coordX, double coordY) {
            this.stationId = stationId;
            this.name = name;
            this.lat = lat;
            this.lon = lon;
            this.coordX = coordX;
            this.coordY = coordY;
        }

        @Override
        public String toString() {
            return String.format("Station{id=%d, name='%s', lat=%.6f, lon=%.6f, x=%.2f, y=%.2f}",
                    stationId, name, lat, lon, coordX, coordY);
        }
    }

    /**
     * Represents a directed track segment with all measures
     */
    static class TrackSegment {
        int fromStationId;
        int toStationId;
        double distance;     // Distance in kilometers
        int capacity;        // Maximum flow of trains per planning day
        double cost;         // Metric combining distance, capacity, congestion, penalties/bonuses

        public TrackSegment(int from, int to, double distance, int capacity, double cost) {
            this.fromStationId = from;
            this.toStationId = to;
            this.distance = distance;
            this.capacity = capacity;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return String.format("Track{%d → %d, dist=%.2f km, capacity=%d, cost=%.2f}",
                    fromStationId, toStationId, distance, capacity, cost);
        }
    }

    /**
     * Result class for the upgrade plan computation
     */
    public static class UpgradePlanResult {
        public boolean hasCycle;
        public List<Integer> topologicalOrder;      // Order of all vertices (if no cycle)
        public Set<Integer> stationsInCycle;        // Stations involved in cycles
        public List<TrackSegment> linksInCycle;     // Links involved in cycles
        public long computationTimeNanos;
        public int totalStations;
        public int totalEdges;

        public UpgradePlanResult() {
            this.topologicalOrder = new ArrayList<>();
            this.stationsInCycle = new HashSet<>();
            this.linksInCycle = new ArrayList<>();
        }
    }

    private Map<Integer, Station> stations;
    private Map<Integer, List<TrackSegment>> adjacencyList;
    private int totalEdges;

    public DirectedLineUpgradePlan() {
        this.stations = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.totalEdges = 0;
    }

    /**
     * Load stations from CSV file with complete attributes
     * Format: Station id,Station,Lat,Lon,CoordX,CoordY
     */
    public void loadStations(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine(); // Skip header

        int loadedCount = 0;
        int lineNumber = 1;

        while ((line = br.readLine()) != null) {
            lineNumber++;
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length >= 6) {
                try {
                    int stationId = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double lat = Double.parseDouble(parts[2].trim());
                    double lon = Double.parseDouble(parts[3].trim());
                    double coordX = Double.parseDouble(parts[4].trim());
                    double coordY = Double.parseDouble(parts[5].trim());

                    Station station = new Station(stationId, name, lat, lon, coordX, coordY);
                    stations.put(stationId, station);
                    adjacencyList.put(stationId, new ArrayList<>());
                    loadedCount++;

                } catch (NumberFormatException e) {
                    System.err.println("Warning: Invalid data at line " + lineNumber + ": " + line);
                }
            }
        }
        br.close();
        System.out.println("✓ Loaded " + loadedCount + " stations from Belgian rail network");
    }

    /**
     * Load directed track segments from CSV file
     * Format: departure_stid,arrival_stid,dist,capacity,cost
     */
    public void loadTrackSegments(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine(); // Skip header

        int loadedCount = 0;
        int skippedCount = 0;
        int lineNumber = 1;

        while ((line = br.readLine()) != null) {
            lineNumber++;
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length >= 5) {
                try {
                    int from = Integer.parseInt(parts[0].trim());
                    int to = Integer.parseInt(parts[1].trim());
                    double distance = Double.parseDouble(parts[2].trim());
                    int capacity = Integer.parseInt(parts[3].trim());
                    double cost = Double.parseDouble(parts[4].trim());

                    // Only add edge if both stations exist
                    if (stations.containsKey(from) && stations.containsKey(to)) {
                        TrackSegment segment = new TrackSegment(from, to, distance, capacity, cost);
                        adjacencyList.get(from).add(segment);
                        loadedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Invalid data at line " + lineNumber + ": " + line);
                }
            }
        }
        br.close();

        totalEdges = loadedCount;
        System.out.println("✓ Loaded " + loadedCount + " directed track segments");
        if (skippedCount > 0) {
            System.out.println("  ⚠ Skipped " + skippedCount + " segments (stations not found)");
        }
    }

    /**
     * Compute the upgrade plan using Kahn's Algorithm (BFS-based topological sort)
     * Time Complexity: O(V + E)
     */
    public UpgradePlanResult computeUpgradePlan() {
        long startTime = System.nanoTime();

        UpgradePlanResult result = new UpgradePlanResult();
        result.totalStations = stations.size();
        result.totalEdges = totalEdges;

        // Step 1: Calculate in-degree for each station - O(V + E)
        Map<Integer, Integer> inDegree = new HashMap<>();
        for (Integer stationId : stations.keySet()) {
            inDegree.put(stationId, 0);
        }

        for (Integer from : adjacencyList.keySet()) {
            for (TrackSegment segment : adjacencyList.get(from)) {
                if (inDegree.containsKey(segment.toStationId)) {
                    inDegree.put(segment.toStationId, inDegree.get(segment.toStationId) + 1);
                }
            }
        }

        // Step 2: Initialize queue with stations having in-degree 0 - O(V)
        Queue<Integer> queue = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        List<Integer> topologicalOrder = new ArrayList<>();

        // Step 3: Process stations with in-degree 0 - O(V + E)
        while (!queue.isEmpty()) {
            int current = queue.poll();
            topologicalOrder.add(current);

            // Reduce in-degree for all neighbors (stations that depend on current)
            if (adjacencyList.containsKey(current)) {
                for (TrackSegment segment : adjacencyList.get(current)) {
                    int neighbor = segment.toStationId;
                    if (inDegree.containsKey(neighbor)) {
                        inDegree.put(neighbor, inDegree.get(neighbor) - 1);

                        if (inDegree.get(neighbor) == 0) {
                            queue.offer(neighbor);
                        }
                    }
                }
            }
        }

        // Step 4: Check if all stations were processed
        if (topologicalOrder.size() == stations.size()) {
            result.hasCycle = false;
            result.topologicalOrder = topologicalOrder;
        } else {
            result.hasCycle = true;
            detectCycles(result, inDegree);
        }

        long endTime = System.nanoTime();
        result.computationTimeNanos = endTime - startTime;

        return result;
    }

    /**
     * Detect and extract stations and links involved in cycles - O(V + E)
     */
    private void detectCycles(UpgradePlanResult result, Map<Integer, Integer> inDegree) {
        // Step 1: Find all stations involved in cycles - O(V)
        Set<Integer> stationsInCycle = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            // Stations with in-degree > 0 after the process must be in a cycle
            if (entry.getValue() > 0 && stations.containsKey(entry.getKey())) {
                stationsInCycle.add(entry.getKey());
            }
        }

        result.stationsInCycle = stationsInCycle;

        // Step 2: Find all links (track segments) involved in cycles - O(E)
        List<TrackSegment> linksInCycle = new ArrayList<>();
        for (Integer stationId : stationsInCycle) {
            if (adjacencyList.containsKey(stationId)) {
                for (TrackSegment segment : adjacencyList.get(stationId)) {
                    if (stationsInCycle.contains(segment.toStationId)) {
                        linksInCycle.add(segment);
                    }
                }
            }
        }

        result.linksInCycle = linksInCycle;
    }

    /**
     * Print comprehensive results for USEI11
     */
    public void printResult(UpgradePlanResult result) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("USEI11 - DIRECTED LINE UPGRADE PLAN");
        System.out.println("Belgian Railway Network Infrastructure Planning");
        System.out.println("=".repeat(80));

        System.out.println("\nNetwork Statistics:");
        System.out.println("  • Total Stations: " + result.totalStations);
        System.out.println("  • Total Directed Track Segments: " + result.totalEdges);
        System.out.println("  • Average Connections per Station: " +
                String.format("%.2f", (double) result.totalEdges / result.totalStations));

        if (!result.hasCycle) {
            printNoCycleResult(result);
        } else {
            printCycleDetectedResult(result);
        }

        printTemporalComplexityAnalysis(result);
    }

    /**
     * Print results when no cycle is detected
     */
    private void printNoCycleResult(UpgradePlanResult result) {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("✓ GRAPH WITHOUT CYCLES - Valid Upgrade Order Found!");
        System.out.println("─".repeat(80));
        System.out.println("\nUpgrade Schedule (Top 20 + Last 10):");
        System.out.println("Stations must be upgraded in this order to respect all dependencies:");
        System.out.println();
        System.out.printf("%-8s | %-12s | %-30s | %-15s%n",
                "Position", "Station ID", "Station Name", "Coordinates");
        System.out.println("-".repeat(80));

        int totalSize = result.topologicalOrder.size();
        for (int i = 0; i < totalSize; i++) {
            int stationId = result.topologicalOrder.get(i);
            Station station = stations.get(stationId);

            // Print the first 20 entries
            if (i < 20) {
                System.out.printf("%-8d | %-12d | %-30s | (%.2f, %.2f)%n",
                        i + 1, stationId, station.name, station.coordX, station.coordY);
            }
            // Skip the middle section for large graphs
            else if (totalSize > 35 && i == 20) {
                int remaining = totalSize - 30;
                System.out.println("   ...   |     ...      | ... (" + remaining +
                        " stations omitted for brevity) ...");
                i = totalSize - 11; // Skip to last 10
            }
            // Print the last 10 entries
            else if (i >= totalSize - 10) {
                System.out.printf("%-8d | %-12d | %-30s | (%.2f, %.2f)%n",
                        i + 1, stationId, station.name, station.coordX, station.coordY);
            }
        }
    }

    /**
     * Print results when cycles are detected
     */
    private void printCycleDetectedResult(UpgradePlanResult result) {
        System.out.println("\n" + "─".repeat(80));
        System.out.println("✗ GRAPH WITH CYCLES - Cannot Establish Valid Upgrade Order!");
        System.out.println("─".repeat(80));

        System.out.println("\n⚠ Problem:");
        System.out.println("The network contains circular dependencies. Some stations require");
        System.out.println("each other to be upgraded first, creating an impossible ordering.");

        System.out.println("\n" + "─".repeat(80));
        System.out.println("STATIONS INVOLVED IN CYCLES (" + result.stationsInCycle.size() + " stations):");
        System.out.println("─".repeat(80));
        System.out.printf("%-12s | %-30s | %-20s%n",
                "Station ID", "Station Name", "Geographic Coords");
        System.out.println("-".repeat(80));

        List<Integer> sortedStationIds = new ArrayList<>(result.stationsInCycle);
        Collections.sort(sortedStationIds);

        for (Integer stationId : sortedStationIds) {
            Station station = stations.get(stationId);
            System.out.printf("%-12d | %-30s | (%.4f, %.4f)%n",
                    stationId, station.name, station.lat, station.lon);
        }

        System.out.println("\n" + "─".repeat(80));
        System.out.println("LINKS (TRACK SEGMENTS) INVOLVED IN CYCLES (" +
                result.linksInCycle.size() + " segments):");
        System.out.println("─".repeat(80));
        System.out.printf("%-30s → %-30s | %-10s | %-8s%n",
                "From Station", "To Station", "Distance", "Capacity");
        System.out.println("-".repeat(80));

        for (TrackSegment segment : result.linksInCycle) {
            Station from = stations.get(segment.fromStationId);
            Station to = stations.get(segment.toStationId);
            System.out.printf("%-30s → %-30s | %7.2f km | %8d%n",
                    truncate(from.name, 30),
                    truncate(to.name, 30),
                    segment.distance,
                    segment.capacity);
        }

        System.out.println("\n" + "─".repeat(80));
        System.out.println("RECOMMENDED ACTIONS:");
        System.out.println("─".repeat(80));
        System.out.println("1. Review the circular dependencies identified above");
        System.out.println("2. Break cycles by:");
        System.out.println("   • Temporarily reversing some track segment directions");
        System.out.println("   • Implementing phased upgrades for stations in cycles");
        System.out.println("   • Using backward-compatible intermediate solutions");
        System.out.println("3. Re-run the analysis after resolving circular dependencies");
    }

    /**
     * Print temporal complexity analysis
     */
    private void printTemporalComplexityAnalysis(UpgradePlanResult result) {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("TEMPORAL COMPLEXITY ANALYSIS");
        System.out.println("═".repeat(80));

        System.out.println("\nAlgorithm: Kahn's Algorithm (BFS-based Topological Sort)");
        System.out.println("\nTime Complexity: O(V + E)");
        System.out.println("  where:");
        System.out.println("    V = number of vertices (stations) = " + result.totalStations);
        System.out.println("    E = number of edges (track segments) = " + result.totalEdges);

        System.out.println("\nDetailed Breakdown:");
        System.out.println("  1. Computing in-degrees:        O(V + E)");
        System.out.println("     • Initialize all stations:    O(V)");
        System.out.println("     • Count incoming edges:       O(E)");
        System.out.println();
        System.out.println("  2. Initial queue population:    O(V)");
        System.out.println("     • Check each station once");
        System.out.println();
        System.out.println("  3. Processing queue:            O(V + E)");
        System.out.println("     • Each station dequeued once: O(V)");
        System.out.println("     • Each edge examined once:    O(E)");
        System.out.println();
        System.out.println("  4. Cycle detection (if needed): O(V + E)");
        System.out.println("     • Check remaining stations:   O(V)");
        System.out.println("     • Find cycle edges:           O(E)");
        System.out.println();
        System.out.println("  Total Time Complexity: O(V + E) = O(" + result.totalStations +
                " + " + result.totalEdges + ") = O(" +
                (result.totalStations + result.totalEdges) + ")");

        System.out.println("\nSpace Complexity: O(V) = O(" + result.totalStations + ")");
        System.out.println("  • In-degree map:        O(V)");
        System.out.println("  • Queue:                O(V) worst case");
        System.out.println("  • Result structures:    O(V)");

        System.out.println("\nActual Execution Time: " +
                String.format("%.3f ms", result.computationTimeNanos / 1_000_000.0));

        System.out.println("\nWhy This is Optimal:");
        System.out.println("  • Linear time O(V + E) is the best possible for this problem");
        System.out.println("  • Every vertex and edge must be examined at least once");
        System.out.println("  • Kahn's algorithm achieves this theoretical lower bound");
        System.out.println("  • Alternative DFS-based approach has same complexity but");
        System.out.println("    Kahn's algorithm is more intuitive for dependency scheduling");

        System.out.println("\n" + "═".repeat(80));
    }

    /**
     * Helper method to truncate strings for display
     */
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Export topological order to a simple text file for further processing
     */
    public void exportUpgradeOrder(UpgradePlanResult result, String filename) throws IOException {
        if (result.hasCycle) {
            System.out.println("Cannot export upgrade order: cycles detected in graph");
            return;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("# USEI11 - Directed Line Upgrade Plan\n");
        writer.write("# Belgian Railway Network Infrastructure Planning\n");
        writer.write("# Generated: " + new java.util.Date() + "\n");
        writer.write("#\n");
        writer.write("# Format: Position,StationID,StationName,Lat,Lon,CoordX,CoordY\n");
        writer.write("#\n");

        for (int i = 0; i < result.topologicalOrder.size(); i++) {
            int stationId = result.topologicalOrder.get(i);
            Station station = stations.get(stationId);
            writer.write(String.format("%d,%d,%s,%.6f,%.6f,%.2f,%.2f%n",
                    i + 1, stationId, station.name, station.lat, station.lon,
                    station.coordX, station.coordY));
        }

        writer.close();
        System.out.println("✓ Upgrade order exported to: " + filename);
    }
}