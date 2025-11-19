package main.java.pt.ipp.isep.dei.domain;

public class StationDistance {
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


}
