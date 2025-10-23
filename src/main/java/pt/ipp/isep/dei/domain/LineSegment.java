package main.java.pt.ipp.isep.dei.domain;

public class LineSegment {

    private final int sequenceNumber;
    private final Station origin;
    private final Station destination;
    private final double distanceKm;
    private final double maxSpeedKmH;
    private final boolean electrified;

    public LineSegment(int sequenceNumber, Station origin, Station destination, double distanceKm, double maxSpeedKmH, boolean electrified) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin station cannot be null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination station cannot be null");
        }
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }
        if (maxSpeedKmH <= 0) {
            throw new IllegalArgumentException("Max Speed must be positive");
        }
        this.sequenceNumber = sequenceNumber;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.maxSpeedKmH = maxSpeedKmH;
        this.electrified = electrified;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public Station getOrigin() {
        return origin;
    }

    public Station getDestination() {
        return destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getMaxSpeedKmH() {
        return maxSpeedKmH;
    }

    public boolean isElectrified() {
        return electrified;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%.1f km, %.0f km/h, electrified=%s)",
                origin.getName(), destination.getName(), distanceKm, maxSpeedKmH, electrified);
    }
}
