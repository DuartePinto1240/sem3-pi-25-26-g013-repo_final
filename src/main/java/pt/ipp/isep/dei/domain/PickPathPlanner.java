package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Planner for picking paths within the warehouse.
 * <p>
 * Provides two strategies for generating a sequence of PickStops:
 * </p>
 * <ul>
 * <li><b>Deterministic Sweep:</b> orders by aisle and then by bay.</li>
 * <li><b>Nearest Neighbour:</b> always chooses the stop closest to the current point, starting at the entrance (0,0).</li>
 * </ul>
 */
public class PickPathPlanner {

    /**
     * Generates a deterministic path (Strategy A) by ordering first by the aisle
     * and then by the bay of each PickStop.
     *
     * @param stops list of PickStops to be visited
     * @return PickPath containing the ordered sequence and the total distance
     */
    public PickPath planDeterministicSweep(List<PickStop> stops) {
        List<PickStop> ordered = new ArrayList<>(stops);
        Comparator<PickStop> comparator = Comparator
                .comparingInt((PickStop s) -> s.getCoordinate().getAisle())
                .thenComparingInt(s -> s.getCoordinate().getBay());
        ordered.sort(comparator);
        double total = calculateTotalDistance(ordered);
        return new PickPath(ordered, total);
    }

    /**
     * Generates a path using the Nearest Neighbour strategy.
     * It starts at the entrance coordinate (0,0) and at each step chooses the closest PickStop.
     *
     * @param stops list of PickStops to be visited
     * @return PickPath containing the ordered sequence and the total distance
     */
    public PickPath planNearestNeighbour(List<PickStop> stops) {
        List<PickStop> remaining = new ArrayList<>(stops);
        List<PickStop> ordered = new ArrayList<>();
        Coordinate current = new Coordinate(0,0);
        double total = 0.0;

        while (!remaining.isEmpty()) {
            PickStop nearest = null;
            double minDist = Double.MAX_VALUE;

            for (PickStop stop : remaining) {
                double dist = current.distanceTo(stop.getCoordinate());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = stop;
                }
            }

            ordered.add(nearest);
            total += minDist;
            current = nearest.getCoordinate();
            remaining.remove(nearest);
        }

        return new PickPath(ordered, total);
    }

    /**
     * Calculates the total distance of the path, starting from the entrance coordinate (0,0).
     *
     * @param sequence sequence of PickStops
     * @return total distance of the path
     */
    private int calculateTotalDistance(List<PickStop> sequence) {
        int total = 0;
        Coordinate current = new Coordinate(0,0);
        for (PickStop stop : sequence) {
            total += current.distanceTo(stop.getCoordinate());
            current = stop.getCoordinate();
        }
        return total;
    }
}

