package main.java.pt.ipp.isep.dei.domain;


/**
 * Represents the connection (edge) between two stations.
 * Contains properties required for optimization algorithms.
 * Ref: Section 3.3.3
 */
public class Track {

    /** Distance in kilometers (used for USEI12 - Minimal Backbone). */
    private final double distance;

    /** Maximum flow capacity (used for USEI14). */
    private final double capacity;

    /** Cost metric combining distance/congestion (used for USEI15). */
    private final double cost;

    public Track(double distance, double capacity, double cost) {
        if (distance < 0) throw new IllegalArgumentException("Distance cannot be negative");
        this.distance = distance;
        this.capacity = capacity;
        this.cost = cost;
    }

    public double getDistance() {
        return distance;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return String.format("Track{dist=%.2f, cap=%.2f, cost=%.2f}", distance, capacity, cost);
    }
}