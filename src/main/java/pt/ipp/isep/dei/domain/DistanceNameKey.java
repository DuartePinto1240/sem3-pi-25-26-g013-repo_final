package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a composite key consisting of a distance, a station name,and a sequential identifier. This key is
 * typically used for sorting or ordering stations by distance, with station name and sequence numberacting
 * as tie-breakers.
 *
 * <p>This class is immutable and implements {@link Comparable} to providea consistent ordering.</p>
 */

public class DistanceNameKey implements Comparable<DistanceNameKey>{

    /** The distance value associated with the station. */
    private final double distance;

    /** The name of the station. */
    private final String stationName;

    /** The name of the station. */
    private final long seq;

    /**
     * Constructs a new {@code DistanceNameKey}.
     *
     * @param distance the distance to associate with the key
     * @param stationName the name of the station
     * @param seq the sequential identifier
     */
    public DistanceNameKey(double distance, String stationName, long seq) {
        this.distance = distance;
        this.stationName = stationName;
        this.seq = seq;
    }

    /**
     * Returns the distance value.
     *
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the sequential identifier.
     *
     * @return the sequence number
     */
    public long getSeq() {
        return seq;
    }

    /**
     * Returns the station name.
     *
     * @return the station name
     */
    public String getStationName() {
        return stationName;
    }

    /**
     * Compares this key with another {@code DistanceNameKey} instance.
     * <p>Ordering priority:</p>
     * <ol>
     *   <li>distance (ascending)</li>
     *   <li>station name (lexicographical)</li>
     *   <li>sequence number (ascending)</li>
     * </ol>
     *
     * @param o the object to compare against
     * @return a negative integer, zero, or a positive integer depending on
     *         whether this object is less than, equal to, or greater than the other
     */
    @Override
    public int compareTo(DistanceNameKey o) {
        int cmp = Double.compare(this.distance, o.distance);
        if (cmp != 0) return cmp;
        cmp = o.stationName.compareTo(this.stationName);
        if (cmp != 0) return cmp;
        return Long.compare(this.seq, o.seq);
    }

    /**
     * Determines whether this key is equal to another object.
     *
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DistanceNameKey)) return false;
        DistanceNameKey o = (DistanceNameKey) obj;
        return Double.compare(this.distance, o.distance) == 0
                && this.stationName.equals(o.stationName)
                && this.seq == o.seq;
    }

    /**
     * Computes a hash code for this key.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(distance);
        int res = (int) (temp ^ (temp >>> 32));
        res = 31 * res + stationName.hashCode();
        res = 31 * res + Long.hashCode(seq);
        return res;
    }
}
