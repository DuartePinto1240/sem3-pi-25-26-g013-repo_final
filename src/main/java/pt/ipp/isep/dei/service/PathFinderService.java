package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.TrainRepository;

import java.util.*;

/**
 * Service for finding optimal paths between facilities
 * Uses Dijkstra's algorithm
 *
 * ✅ UPDATED:
 * - Graph is built using RailwayLine adjacency (not "direct segments only").
 * - Each edge (A->B) exists if there's a RailwayLine connecting A and B.
 * - Edge weight uses distance (sum of LineSegments between A and B) as cost.
 * - Result path is expanded into LineSegmentDTO list hop-by-hop.
 */
public class PathFinderService {

    private final TrainRepository trainRepo;
    private final SpeedCalculatorService speedCalc;

    public PathFinderService(TrainRepository trainRepo, SpeedCalculatorService speedCalc) {
        this.trainRepo = trainRepo;
        this.speedCalc = speedCalc;
    }

    /**
     * Find shortest path between two facilities (by distance)
     * @return PathDTO with ordered list of segments
     */
    public PathDTO findPath(
            Long fromFacilityId,
            Long toFacilityId,
            TrainDTO train
    ) {
        if (fromFacilityId == null || toFacilityId == null) {
            throw new RuntimeException("from/to cannot be null");
        }
        if (fromFacilityId.equals(toFacilityId)) {
            return new PathDTO(null, List.of(), 0.0, 0.0);
        }

        PathResult result = dijkstraByAdjacency(fromFacilityId, toFacilityId);

        if (result == null || result.path == null || result.path.size() < 2) {
            throw new RuntimeException("No path found from " + fromFacilityId + " to " + toFacilityId);
        }

        // Expand facility-path into LineSegmentDTO-path
        List<LineSegmentDTO> pathSegments = new ArrayList<>();
        for (int i = 0; i < result.path.size() - 1; i++) {
            Long a = result.path.get(i);
            Long b = result.path.get(i + 1);

            List<LineSegmentDTO> hopSegments = trainRepo.getLineSegments(a, b);
            if (hopSegments == null || hopSegments.isEmpty()) {
                // If adjacency exists but we cannot fetch segments, your DB function is inconsistent
                throw new RuntimeException("Adjacency exists but no segments returned for hop: " + a + " -> " + b);
            }
            pathSegments.addAll(hopSegments);
        }

        double totalDistance = pathSegments.stream().mapToDouble(LineSegmentDTO::distance).sum();

        // Duration here is just an estimate; real scheduling uses SpeedCalculatorService later.
        // Keep simple estimate using segment speed limit.
        double totalDuration = pathSegments.stream()
                .mapToDouble(seg -> {
                    double v = seg.maxSpeed();
                    if (v <= 0.0) return 0.0;
                    return (seg.distance() / v) * 60.0;
                })
                .sum();

        return new PathDTO(
                null,
                pathSegments,
                totalDistance,
                totalDuration
        );
    }

    /**
     * Find path with manual waypoints
     * ✅ UPDATED:
     * - Waypoints do NOT require direct segment between consecutive nodes.
     * - We call findPath(from,to) for each pair and concatenate.
     */
    public PathDTO findPathWithWaypoints(
            List<Long> facilityIds,
            TrainDTO train
    ) {
        if (facilityIds == null || facilityIds.size() < 2) {
            throw new RuntimeException("Manual path must contain at least 2 facilities.");
        }

        List<LineSegmentDTO> allSegments = new ArrayList<>();
        double totalDistance = 0.0;
        double totalDuration = 0.0;

        for (int i = 0; i < facilityIds.size() - 1; i++) {
            Long from = facilityIds.get(i);
            Long to = facilityIds.get(i + 1);

            PathDTO partial = findPath(from, to, train);

            if (partial.segments() == null || partial.segments().isEmpty()) {
                throw new RuntimeException("No path found for waypoint hop: " + from + " -> " + to);
            }

            allSegments.addAll(partial.segments());
            totalDistance += safe(partial.totalDistance(), 0.0);
            totalDuration += safe(partial.estimatedDuration(), 0.0);
        }

        return new PathDTO(
                null,
                allSegments,
                totalDistance,
                totalDuration
        );
    }

    // ============================================================
    // DIJKSTRA'S ALGORITHM (Adjacency-based)
    // ============================================================

    private static class Node implements Comparable<Node> {
        Long facilityId;
        double distance;
        Node(Long facilityId, double distance) { this.facilityId = facilityId; this.distance = distance; }
        @Override public int compareTo(Node other) { return Double.compare(this.distance, other.distance); }
    }

    private static class PathResult {
        List<Long> path;
        double distance;
        PathResult(List<Long> path, double distance) { this.path = path; this.distance = distance; }
    }

    private PathResult dijkstraByAdjacency(Long start, Long end) {
        Map<Long, Double> dist = new HashMap<>();
        Map<Long, Long> prev = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Set<Long> visited = new HashSet<>();

        dist.put(start, 0.0);
        pq.offer(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            if (visited.contains(cur.facilityId)) continue;
            visited.add(cur.facilityId);

            if (cur.facilityId.equals(end)) {
                List<Long> path = reconstruct(prev, end);
                return new PathResult(path, cur.distance);
            }

            // ✅ neighbors from RailwayLine adjacency
            List<AdjacentFacilityDTO> neighbors = trainRepo.getAdjacentFacilities(cur.facilityId);
            for (AdjacentFacilityDTO adj : neighbors) {
                Long next = adj.toFacilityId();
                if (next == null || visited.contains(next)) continue;

                // edge cost = distance between cur and next (sum of hop segments)
                double edgeCost = hopDistance(cur.facilityId, next);

                double nd = dist.get(cur.facilityId) + edgeCost;
                if (nd < dist.getOrDefault(next, Double.MAX_VALUE)) {
                    dist.put(next, nd);
                    prev.put(next, cur.facilityId);
                    pq.offer(new Node(next, nd));
                }
            }
        }

        return null;
    }

    private List<Long> reconstruct(Map<Long, Long> prev, Long end) {
        List<Long> path = new ArrayList<>();
        Long at = end;
        while (at != null) {
            path.add(0, at);
            at = prev.get(at);
        }
        return path;
    }

    private double hopDistance(Long from, Long to) {
        List<LineSegmentDTO> segs = trainRepo.getLineSegments(from, to);
        if (segs == null || segs.isEmpty()) {
            // If there's a RailwayLine adjacency but no segments returned, treat as "very expensive" or fail.
            // Better to FAIL fast because your data/function is inconsistent.
            throw new RuntimeException("No segments for hop: " + from + " -> " + to);
        }
        return segs.stream().mapToDouble(LineSegmentDTO::distance).sum();
    }

    private double safe(Double v, double def) {
        return (v == null || Double.isNaN(v) || Double.isInfinite(v)) ? def : v;
    }
}
