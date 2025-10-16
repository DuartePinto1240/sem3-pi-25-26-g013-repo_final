package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

public class PickPath {
    private List<PickStop> sequence;
    private double totalDistance;

    public PickPath(List<PickStop> sequence, double totalDistance) {
        this.sequence = sequence;
        this.totalDistance = totalDistance;
    }

    public List<PickStop> getSequence() {return sequence; }
    public double getTotalDistance() { return totalDistance; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PickStop s : sequence)
            sb.append(" -> ").append(s);
        sb.append("\nTotal distance: ").append(totalDistance);
        return sb.toString();
    }
}
