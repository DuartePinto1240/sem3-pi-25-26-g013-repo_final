package main.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineSegment;

/**
 * Represents a travel section (journey) along a {@link LineSegment}.
 * <p>
 * This class acts as a Data Transfer Object (DTO), storing the results
 * of speed and travel time calculation for a specific railway segment.
 * </p>
 *
 * <p>
 * Each {@code TravelSection} contains:
 * <ul>
 * <li>The railway line segment traveled;</li>
 * <li>The effective speed used (in km/h);</li>
 * <li>The corresponding travel time (in minutes).</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is immutable.
 * </p>
 */
public class TravelSection {

    /** Railway line segment associated with this section. */
    private final LineSegment segment;

    /** Effective speed used in the journey, in km/h. */
    private final double effectiveSpeedKmH;

    /** Time required to travel the segment, in minutes. */
    private final double sectionTimeMinutes;

    /**
     * Creates a new instance of {@code TravelSection}.
     *
     * @param segment the corresponding railway segment (must not be null)
     * @param effectiveSpeedKmH the effective travel speed, in km/h
     * @param sectionTimeMinutes the total travel time, in minutes
     */
    public TravelSection(LineSegment segment, double effectiveSpeedKmH, double sectionTimeMinutes) {
        this.segment = segment;
        this.effectiveSpeedKmH = effectiveSpeedKmH;
        this.sectionTimeMinutes = sectionTimeMinutes;
    }

    /**
     * Gets the railway segment associated with this section.
     *
     * @return the corresponding {@link LineSegment}
     */
    public LineSegment getSegment() {
        return segment;
    }

    /**
     * Gets the effective speed used during the journey.
     *
     * @return speed in km/h
     */
    public double getEffectiveSpeedKmH() {
        return effectiveSpeedKmH;
    }

    /**
     * Gets the total time required to travel the segment.
     *
     * @return travel time in minutes
     */
    public double getSectionTimeMinutes() {
        return sectionTimeMinutes;
    }

    /**
     * Generates a detailed textual representation of the travel section.
     * <p>
     * Includes origin, destination, distance, speed, and total time.
     * </p>
     *
     * @return formatted string with travel details
     */
    @Override
    public String toString() {
        return String.format("%s -> %s | Dist: %.1f km | Velocidade: %.1f km/h | Tempo: %.2f min",
                segment.getOrigin().getName(),
                segment.getDestination().getName(),
                segment.getDistanceKm(),
                effectiveSpeedKmH,
                sectionTimeMinutes);
    }
}