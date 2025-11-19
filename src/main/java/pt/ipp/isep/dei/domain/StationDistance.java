package main.java.pt.ipp.isep.dei.domain;

// Assuming TrainStation is in this package or imported
import main.java.pt.ipp.isep.dei.domain.TrainStation;

/**
 * Helper class to associate a TrainStation with its calculated distance
 * from a target point.
 *
 * Implements Comparable to fulfill AC4: sort by ascending distance,
 * then lexicographically by name as a tie-breaker.
 */
public class StationDistance implements Comparable<StationDistance> {

    private final TrainStation station;
    private final double distanceKm;

    public StationDistance(TrainStation station, double distanceKm) {
        this.station = station;
        this.distanceKm = distanceKm;
    }

    public TrainStation getStation() {
        return station;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    /**
     * Compares this StationDistance to another.
     *
     * @param other The other StationDistance object.
     * @return -1, 0, or 1 based on ascending distance, with
     * station name as a tie-breaker.
     */
    @Override
    public int compareTo(StationDistance other) {
        int distCmp = Double.compare(this.distanceKm, other.distanceKm);
        if (distCmp != 0) {
            return distCmp; // Sort by distance
        }
        // Tie-breaker: lexicographically by station name (AC4)
        // Assumes TrainStation has a method getStation() that returns the name.
        return this.station.getStation().compareTo(other.station.getStation());
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f km)", station.getStation(), distanceKm);
    }
}