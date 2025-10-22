package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a directed connection between two {@link Station} objects
 * within the railway network.
 * <p>
 * Each {@code LineSegment} defines a track section characterized by:
 * its length (in kilometers), maximum allowed speed (in km/h), and
 * whether the line is electrified. This class is immutable and
 * ensures integrity by validating all constructor arguments.
 * </p>
 */
public class LineSegment {

    /** The origin station of the segment. */
    private final Station origin;

    /** The destination station of the segment. */
    private final Station destination;

    /** Distance of the segment, in kilometers. */
    private final double distanceKm;

    /** Maximum allowed train speed on this segment, in km/h. */
    private final double maxSpeedKmH;

    /** Whether this segment supports electric trains. */
    private final boolean electrified;

    /**
     * Constructs a new {@link LineSegment} connecting two stations.
     *
     * @param origin       the starting {@link Station} of the segment (cannot be {@code null})
     * @param destination  the ending {@link Station} of the segment (cannot be {@code null})
     * @param distanceKm   the distance between stations, in kilometers (must be > 0)
     * @param maxSpeedKmH  the maximum permitted speed on this segment, in km/h (must be > 0)
     * @param electrified  whether this track section is electrified
     * @throws IllegalArgumentException if any parameter is invalid (e.g., null stations or non-positive distance/speed)
     */
    public LineSegment(Station origin, Station destination, double distanceKm, double maxSpeedKmH, boolean electrified) {
        // Integrity validation
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Origin and Destination stations must be valid (not null).");
        }
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be a positive value for a Line Segment.");
        }
        if (maxSpeedKmH <= 0) {
            throw new IllegalArgumentException("Max Speed must be a positive value for a Line Segment.");
        }

        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.maxSpeedKmH = maxSpeedKmH;
        this.electrified = electrified;
    }

    /**
     * @return the origin {@link Station} of this segment
     */
    public Station getOrigin() {
        return origin;
    }

    /**
     * @return the destination {@link Station} of this segment
     */
    public Station getDestination() {
        return destination;
    }

    /**
     * @return the total distance of this segment in kilometers
     */
    public double getDistanceKm() {
        return distanceKm;
    }

    /**
     * @return the maximum speed allowed for trains on this segment (km/h)
     */
    public double getMaxSpeedKmH() {
        return maxSpeedKmH;
    }

    /**
     * @return {@code true} if this line segment is electrified, {@code false} otherwise
     */
    public boolean isElectrified() {
        return electrified;
    }

    /**
     * Returns a concise textual description of this line segment,
     * including its endpoints, distance, speed limit, and electrification status.
     *
     * @return formatted string representation of the line segment
     */
    @Override
    public String toString() {
        return String.format("%s -> %s (%.1f km, %.0f km/h, electrified=%s)",
                origin.getName(), destination.getName(), distanceKm, maxSpeedKmH, electrified);
    }
}
