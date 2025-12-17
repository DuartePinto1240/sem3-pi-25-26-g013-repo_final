package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.dto.*;
import java.util.*;

public class RiskAwarePathService {

    /**
     * Computes the shortest path using Bellman-Ford to handle negative weights
     * and detect negative cycles.
     * * @param graph The existing graph structure (Station as Vertex, Double as Weight)
     */
    public RiskPathResult computeShortestPath(Graph<Station, Double> graph, Station source, Station target) {
        if (!graph.validVertex(source) || !graph.validVertex(target)) {
            throw new IllegalArgumentException("Source or Target station not in graph.");
        }

        // 1. Initialize distances
        Map<Station, Double> dist = new HashMap<>();
        Map<Station, Station> pred = new HashMap<>();

        for (Station s : graph.vertices()) {
            dist.put(s, Double.POSITIVE_INFINITY);
        }
        dist.put(source, 0.0);

        int V = graph.numVertices();
        Collection<Edge<Station, Double>> edges = graph.edges();

        // 2. Relax edges |V| - 1 times
        // We iterate one less than V because a simple path can have at most V-1 edges
        for (int i = 1; i < V; i++) {
            boolean changed = false;
            for (Edge<Station, Double> edge : edges) {
                if (relax(edge, dist, pred)) {
                    changed = true;
                }
            }
            if (!changed) break; // Optimization
        }

        // 3. Detect Negative Cycles
        for (Edge<Station, Double> edge : edges) {
            Station u = edge.getVOrig();
            Station v = edge.getVDest();
            double weight = edge.getWeight();

            if (dist.get(u) != Double.POSITIVE_INFINITY &&
                    dist.get(u) + weight < dist.get(v)) {

                // Negative cycle found! Reconstruct it.
                List<Station> cycle = findCycle(v, pred);
                return new RiskPathFailureDTO(cycle);
            }
        }

        // 4. Construct Result
        if (dist.get(target) == Double.POSITIVE_INFINITY) {
            // Target not reachable
            return new RiskPathSuccessDTO(new ArrayList<>(), Double.POSITIVE_INFINITY);
        }

        List<Station> path = reconstructPath(pred, source, target);
        return new RiskPathSuccessDTO(path, dist.get(target));
    }

    // Helper: Relaxation step
    private boolean relax(Edge<Station, Double> edge, Map<Station, Double> dist, Map<Station, Station> pred) {
        Station u = edge.getVOrig();
        Station v = edge.getVDest();
        double weight = edge.getWeight();

        if (dist.get(u) != Double.POSITIVE_INFINITY &&
                dist.get(u) + weight < dist.get(v)) {

            dist.put(v, dist.get(u) + weight);
            pred.put(v, u);
            return true;
        }
        return false;
    }

    // Helper: Build path from predecessors
    private List<Station> reconstructPath(Map<Station, Station> pred, Station source, Station target) {
        LinkedList<Station> path = new LinkedList<>();
        Station curr = target;
        while (curr != null) {
            path.addFirst(curr);
            if (curr.equals(source)) break;
            curr = pred.get(curr);
        }
        return path;
    }

    // Helper: Reconstruct cycle
    private List<Station> findCycle(Station startNode, Map<Station, Station> pred) {
        // Go back V times to ensure we are inside the cycle
        Station curr = startNode;
        for (int i = 0; i < pred.size(); i++) {
            curr = pred.get(curr);
        }

        List<Station> cycle = new ArrayList<>();
        Station cycleStart = curr;
        do {
            cycle.add(curr);
            curr = pred.get(curr);
        } while (curr != null && !curr.equals(cycleStart));

        cycle.add(cycleStart);
        Collections.reverse(cycle);
        return cycle;
    }
}