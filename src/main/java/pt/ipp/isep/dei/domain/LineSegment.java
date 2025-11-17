package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a segment of a railway line between two stations.
 * Expected mapping from CSV (line_segments.csv):
 *  0: lineId
 *  1: sequenceNumber
 *  2: originStationId
 *  3: destinationStationId
 *  4: distanceKm
 *  5: maxSpeedKmH
 *  6: isElectric (0/1 or true/false)
 *  7: NumberTrack
 *  (other columns are currently ignored)
 */
public class LineSegment {

    private final int sequenceNumber;
    private final Station origin;
    private final Station destination;
    private final double distanceKm;
    private final double maxSpeedKmH;
    private final boolean electrified;

    /** Number of tracks on this segment (1 = single-track, >=2 = multiple). */
    private final int numberTracks;

    public LineSegment(int sequenceNumber,
                       Station origin,
                       Station destination,
                       double distanceKm,
                       double maxSpeedKmH,
                       boolean electrified,
                       int numberTracks) {
        this.sequenceNumber = sequenceNumber;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.maxSpeedKmH = maxSpeedKmH;
        this.electrified = electrified;
        this.numberTracks = numberTracks;
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

    public int getNumberTracks() {
        return numberTracks;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%.1f km, %.0f km/h, electrified=%s)",
                origin != null ? origin.getName() : "?",
                destination != null ? destination.getName() : "?",
                distanceKm,
                maxSpeedKmH,
                electrified);
    }
}
