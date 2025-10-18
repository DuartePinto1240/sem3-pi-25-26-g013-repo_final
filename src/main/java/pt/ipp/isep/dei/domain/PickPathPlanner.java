package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PickPathPlanner {

    //estratégia A
    public PickPath planDeterministicSweep (List<PickStop> stops) {
        List<PickStop> ordered = new ArrayList<>(stops);
        Comparator<PickStop> comparator = Comparator
                .comparingInt((PickStop s) -> s.getCoordinate().getAisle())
                .thenComparingInt(s -> s.getCoordinate().getBay());
        ordered.sort(comparator);
        double total = calculateTotalDistance(ordered);
        return new PickPath(ordered, total);
    }

    //estratégia B
    public PickPath planNearestNeighbour ( List<PickStop> stops){
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
