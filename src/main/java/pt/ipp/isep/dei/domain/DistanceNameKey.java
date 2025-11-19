package main.java.pt.ipp.isep.dei.domain;

public class DistanceNameKey implements Comparable<DistanceNameKey>{
    private final double distance;
    private final String stationName;
    private final long seq;

    public DistanceNameKey(double distance, String stationName, long seq) {
        this.distance = distance;
        this.stationName = stationName;
        this.seq = seq;
    }

    public double getDistance() {
        return distance;
    }

    public long getSeq() {
        return seq;
    }

    public String getStationName() {
        return stationName;
    }

    @Override
    public int compareTo(DistanceNameKey o) {
        int cmp = Double.compare(this.distance, o.distance);
        if (cmp != 0) return cmp;
        cmp = o.stationName.compareTo(this.stationName);
        if (cmp != 0) return cmp;
        return Long.compare(this.seq, o.seq);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DistanceNameKey)) return false;
        DistanceNameKey o = (DistanceNameKey) obj;
        return Double.compare(this.distance, o.distance) == 0
                && this.stationName.equals(o.stationName)
                && this.seq == o.seq;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(distance);
        int res = (int) (temp ^ (temp >>> 32));
        res = 31 * res + stationName.hashCode();
        res = 31 * res + Long.hashCode(seq);
        return res;
    }
}
