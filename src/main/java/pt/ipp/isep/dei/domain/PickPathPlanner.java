package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

public class PickPathPlanner {

    //estratégia A
    public PickPath planDeterministicSweep (List<PickStop> stops) {
        // implementar
        return null;
    }

    //estratégia B
    public PickPath planNearestNeighbour ( List<PickStop> stops){
        // implementar;
        return null;
    }

    private double calculateTotalDistance(List<PickStop> sequence) {
        double total = 0.0;
        Coordinate current = new Coordinate(0,0);
        for (PickStop stop : sequence) {
            total += current.distanceTo(stop.getCoordinate());
            current = stop.getCoordinate();
        }
        return total;
    }
}
