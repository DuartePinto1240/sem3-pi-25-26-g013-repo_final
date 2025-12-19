package test.java.pt.ipp.isep.dei.domain.USEI15;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.dto.RiskPathFailureDTO;
import main.java.pt.ipp.isep.dei.dto.RiskPathResult;
import main.java.pt.ipp.isep.dei.dto.RiskPathSuccessDTO;
import main.java.pt.ipp.isep.dei.repository.CsvRailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.CsvStationRepository;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.service.Edge;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.service.MapGraph;
import main.java.pt.ipp.isep.dei.service.RiskAwarePathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for USEI15 using DATA from CSV files.
 * Validates the Bellman-Ford algorithm against the provided station and line datasets.
 */
class USEI15Test {

    private RiskAwarePathService service;
    private Graph<Station, Double> graph;
    private StationRepository stationRepo;
    private RailwayLineRepository lineRepo;

    // Adjust these paths if your test resources are in a different folder
    private static final String STATIONS_FILE = "train_station_dataset_v2/stations.csv";
    private static final String LINES_FILE = "train_station_dataset_v2/lines.csv";

    @BeforeEach
    void setUp() {
        service = new RiskAwarePathService();

        // 1. Load Data from Files
        // Note: We use the repositories directly to mimic the application startup
        stationRepo = new CsvStationRepository(STATIONS_FILE);
        lineRepo = new CsvRailwayLineRepository(LINES_FILE, stationRepo);

        // 2. Build the Graph
        graph = buildGraph(stationRepo, lineRepo);
    }

    /**
     * Test AC5: Find a valid shortest path between two connected stations.
     */
    @Test
    void testShortestPath_ConnectedStations() {
        // Find two stations that are guaranteed to be connected
        StationPair pair = findConnectedPair(graph);
        assertNotNull(pair, "Could not find any connected stations in the provided CSV files to run the test.");

        System.out.printf("Testing Path: %s -> %s%n", pair.source.getName(), pair.target.getName());

        RiskPathResult result = service.computeShortestPath(graph, pair.source, pair.target);

        assertTrue(result.isSuccess(), "Should find a path between connected stations.");
        RiskPathSuccessDTO success = (RiskPathSuccessDTO) result;

        assertFalse(success.getPath().isEmpty(), "Path list should not be empty.");
        assertTrue(success.getTotalCost() > 0, "Cost should be positive for railway lines.");

        // Print result for manual verification
        System.out.println("Path Found: " + success.getMessage());
        System.out.println("Complexity: " + success.getComplexityAnalysis());
    }

    /**
     * Test AC3/AC4: Detect a negative cycle.
     * Since the railway data likely contains only positive distances,
     * we must artificially inject a negative cycle into the graph to test this requirement.
     */
    @Test
    void testDetectNegativeCycle_InjectedFault() {
        // 1. Get a valid station from the graph
        Station start = graph.vertices().get(0);

        // 2. Create two fake stations to form a cycle with 'start'
        Station fake1 = new Station("FAKE_1", "Fake Station 1", 0, 0, 0, 0);
        Station fake2 = new Station("FAKE_2", "Fake Station 2", 0, 0, 0, 0);

        graph.addVertex(fake1);
        graph.addVertex(fake2);

        // 3. Create a Negative Cycle: start -> fake1 -> fake2 -> start
        // Total Cycle Cost: 10 + (-50) + 10 = -30
        graph.addEdge(start, fake1, 10.0);
        graph.addEdge(fake1, fake2, -50.0); // Large negative weight (e.g., massive bonus)
        graph.addEdge(fake2, start, 10.0);

        // 4. Run Search targeting the fake node
        RiskPathResult result = service.computeShortestPath(graph, start, fake2);

        // 5. Assert Failure
        assertFalse(result.isSuccess(), "Should fail because a negative cycle is reachable.");
        assertTrue(result instanceof RiskPathFailureDTO);

        RiskPathFailureDTO failure = (RiskPathFailureDTO) result;
        System.out.println("Success: " + failure.getMessage());
        assertTrue(failure.getNegativeCycle().contains(fake1), "Cycle should contain the injected nodes.");
    }

    /**
     * Test AC2: Handle Negative Weights (Risk/Bonus) that do NOT form a cycle.
     * We modify a single edge in the graph to have a negative cost (bonus)
     * and verify the algorithm still solves it (unlike Dijkstra).
     */
    @Test
    void testNegativeWeight_NoCycle() {
        StationPair pair = findConnectedPair(graph);
        assertNotNull(pair);

        // Find the direct edge between them and make it negative
        Edge<Station, Double> edge = graph.edge(pair.source, pair.target);
        if (edge != null) {
            // Remove old edge and add negative weight edge (e.g., -1.0)
            graph.removeEdge(pair.source, pair.target);
            graph.addEdge(pair.source, pair.target, -1.0);

            RiskPathResult result = service.computeShortestPath(graph, pair.source, pair.target);

            assertTrue(result.isSuccess());
            RiskPathSuccessDTO success = (RiskPathSuccessDTO) result;
            // The cost should now be negative or lower
            assertEquals(-1.0, success.getTotalCost(), 0.1);
        }
    }

    /**
     * Test AC7: Performance Metrics.
     */
    @Test
    void testPerformanceMetrics_ArePopulated() {
        StationPair pair = findConnectedPair(graph);
        RiskPathResult result = service.computeShortestPath(graph, pair.source, pair.target);

        assertNotNull(result.getComplexityAnalysis(), "Complexity analysis must be present.");
        assertTrue(result.getExecutionTimeMs() >= 0, "Execution time must be recorded.");
    }

    // --------------------------------------------------------
    // HELPER METHODS
    // --------------------------------------------------------

    private Graph<Station, Double> buildGraph(StationRepository sRepo, RailwayLineRepository lRepo) {
        MapGraph<Station, Double> g = new MapGraph<>(true);

        for (Station s : sRepo.findAll()) {
            g.addVertex(s);
        }

        for (RailwayLine line : lRepo.findAll()) {
            for (LineSegment seg : line.getSegments()) {
                g.addEdge(seg.getOrigin(), seg.getDestination(), seg.getDistanceKm());
                // Assuming undirected for initial test, or if lines.csv implies direction, adapt
                g.addEdge(seg.getDestination(), seg.getOrigin(), seg.getDistanceKm());
            }
        }
        return g;
    }

    /**
     * Helper to find two stations (A and B) that are actually connected.
     * Used so we don't have to hardcode IDs that might change in the CSV file.
     */
    private StationPair findConnectedPair(Graph<Station, Double> g) {
        for (Station s1 : g.vertices()) {
            // Find a neighbor
            Collection<Edge<Station, Double>> edges = g.outgoingEdges(s1);
            if (!edges.isEmpty()) {
                // Return S1 and its neighbor
                Station s2 = edges.iterator().next().getVDest();
                return new StationPair(s1, s2);
            }
        }
        return null;
    }

    private static class StationPair {
        Station source;
        Station target;
        StationPair(Station s, Station t) { source = s; target = t; }
    }
}