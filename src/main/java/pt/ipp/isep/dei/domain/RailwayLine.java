package main.java.pt.ipp.isep.dei.domain;

import java.util.Collections;
import java.util.List;

/**
 * Represents a railway line composed of several consecutive segments.
 * <p>
 * A {@code RailwayLine} has a starting station, an ending station,
 * and a list of {@link LineSegment}s that describe the route.
 * </p>
 */
public class RailwayLine {

    /** The starting station of the railway line. */
    private final Station startStation;

    /** The ending station of the railway line. */
    private final Station endStation;

    /** List of segments that compose the railway line. */
    private final List<LineSegment> segments;

    /**
     * Creates a RailwayLine from a list of consecutive segments.
     *
     * @param segments list of {@link LineSegment}s, cannot be null or empty
     * @throws IllegalArgumentException if the list is null or empty
     */
    public RailwayLine(List<LineSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("A railway line must have at least one segment.");
        }
        this.segments = segments;
        this.startStation = segments.get(0).getOrigin();
        this.endStation = segments.get(segments.size() - 1).getDestination();
    }

    /**
     * Returns the starting station of the railway line.
     *
     * @return the starting station
     */
    public Station getStartStation() {
        return startStation;
    }

    /**
     * Returns the ending station of the railway line.
     *
     * @return the ending station
     */
    public Station getEndStation() {
        return endStation;
    }

    /**
     * Returns the list of segments that compose the railway line.
     * <p>
     * The returned list is immutable.
     * </p>
     *
     * @return an immutable list of {@link LineSegment}s
     */
    public List<LineSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }
}