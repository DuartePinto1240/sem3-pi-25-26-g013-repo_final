package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.dto.LineSegmentDTO;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.PathDTO;
import main.java.pt.ipp.isep.dei.dto.TrainDTO;
import main.java.pt.ipp.isep.dei.repository.TrainRepository;

import java.util.*;

/**
 * Service for finding optimal paths between facilities
 * Uses Dijkstra's algorithm
 */
public class PathFinderService {

    private final TrainRepository trainRepo;
    private final SpeedCalculatorService speedCalc;

    public PathFinderService(TrainRepository trainRepo, SpeedCalculatorService speedCalc) {
        this.trainRepo = trainRepo;
        this.speedCalc = speedCalc;
    }

    /**
     * Find shortest path between two facilities
     * @return PathDTO with ordered list of segments
     */
    public PathDTO findPath(
            Long fromFacilityId,
            Long toFacilityId,
            TrainDTO train
    ) {
        // Get all possible segments (simplified - in reality build complete graph)
        List<LineSegmentDTO> allSegments = getAllSegmentsInNetwork(fromFacilityId, toFacilityId);

        // Build adjacency graph
        Map<Long, List<Edge>> graph = buildGraph(allSegments);

        // Run Dijkstra
        PathResult result = dijkstra(graph, fromFacilityId, toFacilityId);

        if (result == null) {
            throw new RuntimeException("No path found from " + fromFacilityId + " to " + toFacilityId);
        }

        // Convert facility path to segment path
        List<LineSegmentDTO> pathSegments = convertToSegments(result.path, allSegments);

        double totalDistance = pathSegments.stream()
                .mapToDouble(LineSegmentDTO::distance)
                .sum();

        // Estimate duration (simplified)
        double totalDuration = pathSegments.stream()
                .mapToDouble(seg -> seg.distance() / seg.maxSpeed() * 60) // minutes
                .sum();

        return new PathDTO(
                null, // routeId not applicable here
                pathSegments,
                totalDistance,
                totalDuration
        );
    }

    /**
     * Find path with manual waypoints
     */
    public PathDTO findPathWithWaypoints(
            List<Long> facilityIds,
            TrainDTO train
    ) {
        List<LineSegmentDTO> allSegments = new ArrayList<>();
        double totalDistance = 0.0;
        double totalDuration = 0.0;

        // Find path between each consecutive pair
        for (int i = 0; i < facilityIds.size() - 1; i++) {
            Long from = facilityIds.get(i);
            Long to = facilityIds.get(i + 1);

            List<LineSegmentDTO> segments = trainRepo.getLineSegments(from, to);
            allSegments.addAll(segments);

            totalDistance += segments.stream().mapToDouble(LineSegmentDTO::distance).sum();
            totalDuration += segments.stream()
                    .mapToDouble(seg -> seg.distance() / seg.maxSpeed() * 60)
                    .sum();
        }

        return new PathDTO(
                null,
                allSegments,
                totalDistance,
                totalDuration
        );
    }

    // ============================================================
    // DIJKSTRA'S ALGORITHM
    // ============================================================

    private static class Edge {
        Long to;
        double weight;
        LineSegmentDTO segment;

        Edge(Long to, double weight, LineSegmentDTO segment) {
            this.to = to;
            this.weight = weight;
            this.segment = segment;
        }
    }

    private static class Node implements Comparable<Node> {
        Long facilityId;
        double distance;

        Node(Long facilityId, double distance) {
            this.facilityId = facilityId;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    private static class PathResult {
        List<Long> path;
        double distance;

        PathResult(List<Long> path, double distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    private PathResult dijkstra(Map<Long, List<Edge>> graph, Long start, Long end) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Set<Long> visited = new HashSet<>();

        distances.put(start, 0.0);
        pq.offer(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (visited.contains(current.facilityId)) {
                continue;
            }

            visited.add(current.facilityId);

            if (current.facilityId.equals(end)) {
                // Reconstruct path
                List<Long> path = new ArrayList<>();
                Long at = end;
                while (at != null) {
                    path.add(0, at);
                    at = previous.get(at);
                }
                return new PathResult(path, current.distance);
            }

            List<Edge> edges = graph.getOrDefault(current.facilityId, Collections.emptyList());
            for (Edge edge : edges) {
                if (visited.contains(edge.to)) {
                    continue;
                }

                double newDist = distances.get(current.facilityId) + edge.weight;
                if (newDist < distances.getOrDefault(edge.to, Double.MAX_VALUE)) {
                    distances.put(edge.to, newDist);
                    previous.put(edge.to, current.facilityId);
                    pq.offer(new Node(edge.to, newDist));
                }
            }
        }

        return null; // No path found
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private Map<Long, List<Edge>> buildGraph(List<LineSegmentDTO> segments) {
        Map<Long, List<Edge>> graph = new HashMap<>();

        for (LineSegmentDTO seg : segments) {
            // Forward direction
            graph.computeIfAbsent(seg.fromFacilityId(), k -> new ArrayList<>())
                    .add(new Edge(seg.toFacilityId(), seg.distance(), seg));

            // Backward direction (trains can go both ways)
            graph.computeIfAbsent(seg.toFacilityId(), k -> new ArrayList<>())
                    .add(new Edge(seg.fromFacilityId(), seg.distance(), seg));
        }

        return graph;
    }

    private List<LineSegmentDTO> convertToSegments(List<Long> facilityPath, List<LineSegmentDTO> allSegments) {
        List<LineSegmentDTO> result = new ArrayList<>();

        for (int i = 0; i < facilityPath.size() - 1; i++) {
            Long from = facilityPath.get(i);
            Long to = facilityPath.get(i + 1);

            // Find segment connecting these facilities
            LineSegmentDTO segment = allSegments.stream()
                    .filter(s -> (s.fromFacilityId().equals(from) && s.toFacilityId().equals(to))
                            || (s.fromFacilityId().equals(to) && s.toFacilityId().equals(from)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Segment not found: " + from + " -> " + to));

            result.add(segment);
        }

        return result;
    }

    private List<LineSegmentDTO> getAllSegmentsInNetwork(Long from, Long to) {
        // Simplified: get direct segments
        // In reality, you'd need to query all segments in the network
        // and build a complete graph
        return trainRepo.getLineSegments(from, to);
    }
}