package main.java.pt.ipp.isep.dei.domain;

import java.util.Collections;
import java.util.List;

public class RailwayLine {

    private final Station startStation;
    private final Station endStation;
    private final List<LineSegment> segments;

    public RailwayLine(List<LineSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("A railway line must have at least one segment.");
        }
        this.segments = segments;
        this.startStation = segments.get(0).getOrigin();
        this.endStation = segments.get(segments.size() - 1).getDestination();
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public List<LineSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }
}