package main.java.pt.ipp.isep.dei.domain;

public class PickStop {
    private Coordinate coordinate;
    private Allocation allocation;

    public PickStop(Coordinate coordinate, Allocation allocation) {
        this.coordinate = coordinate;
        this.allocation = allocation;
    }

    public Coordinate getCoordinate() { return coordinate; }

    public Allocation getAllocation() { return allocation; }

    @Override
    public String toString() {
        return allocation.getSku() + " @ " + coordinate.toString();
    }
}