package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.dto.RiskPathFailureDTO;
import main.java.pt.ipp.isep.dei.dto.RiskPathResult;
import main.java.pt.ipp.isep.dei.dto.RiskPathSuccessDTO;

import java.util.*;

public class RiskAwarePathService {

    private static final String BELLMAN_FORD_COMPLEXITY = "O(V * E)";

    public RiskPathResult computeShortestPath(Graph<Station, Double> graph, Station source, Station target) {
        long startTime = System.currentTimeMillis();

        if (!graph.validVertex(source) || !graph.validVertex(target)) {
            throw new IllegalArgumentException("Source or Target station not found in the graph.");
        }

        Map<Station, Double> dist = new HashMap<>();
        Map<Station, Station> pred = new HashMap<>();

        for (Station v : graph.vertices()) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }
        dist.put(source, 0.0);

        int V = graph.numVertices();
        Collection<Edge<Station, Double>> edges = graph.edges();

        // Relaxation
        for (int i = 1; i < V; i++) {
            boolean changed = false;
            for (Edge<Station, Double> edge : edges) {
                if (relax(edge, dist, pred)) {
                    changed = true;
                }
            }
            if (!changed) break;
        }

        // Negative Cycle Detection
        for (Edge<Station, Double> edge : edges) {
            Station u = edge.getVOrig();
            Station v = edge.getVDest();
            double weight = edge.getWeight();

            if (dist.get(u) != Double.POSITIVE_INFINITY &&
                    dist.get(u) + weight < dist.get(v)) {

                List<Station> cycle = findCycle(v, pred);
                long endTime = System.currentTimeMillis();
                return new RiskPathFailureDTO(cycle, endTime - startTime, BELLMAN_FORD_COMPLEXITY);
            }
        }

        long endTime = System.currentTimeMillis();

        if (dist.get(target) == Double.POSITIVE_INFINITY) {
            return new RiskPathSuccessDTO(new ArrayList<>(), Double.POSITIVE_INFINITY, endTime - startTime, BELLMAN_FORD_COMPLEXITY);
        }

        List<Station> path = reconstructPath(pred, source, target);
        return new RiskPathSuccessDTO(path, dist.get(target), endTime - startTime, BELLMAN_FORD_COMPLEXITY);
    }

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

    private List<Station> findCycle(Station startNode, Map<Station, Station> pred) {
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