package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

/**
 * Represents a picking path within the warehouse.
 * <p>
 * Contains the sequence of stops (PickStops) and the total distance of the path.
 * </p>
 */
public class PickPath {

    /** Sequence of stops in the picking path. */
    private List<PickStop> sequence;

    /** Total distance of the path (in units compatible with the distance metric used). */
    private double totalDistance;

    /**
     * Constructor.
     *
     * @param sequence      list of PickStops representing the path sequence
     * @param totalDistance total distance of the path
     */
    public PickPath(List<PickStop> sequence, double totalDistance) {
        this.sequence = sequence;
        this.totalDistance = totalDistance;
    }

    /**
     * Returns the sequence of PickStops.
     *
     * @return list of PickStops
     */
    public List<PickStop> getSequence() {
        return sequence;
    }

    /**
     * Returns the total distance of the path.
     *
     * @return total distance
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Returns a textual representation of the path, including each PickStop and the total distance.
     *
     * @return a formatted string describing the path
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PickStop s : sequence)
            sb.append(" -> ").append(s);
        sb.append("\nTotal distance: ").append(totalDistance);
        return sb.toString();
    }
}