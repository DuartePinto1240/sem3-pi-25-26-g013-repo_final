package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a picking stop within the warehouse.
 * <p>
 * Each PickStop is associated with a specific Allocation and a Coordinate inside the warehouse.
 * </p>
 */
public class PickStop {

    /** Coordinate of the PickStop within the warehouse (aisle and bay). */
    private Coordinate coordinate;

    /** Allocation associated with this stop. */
    private Allocation allocation;

    /**
     * Constructor for the picking stop.
     *
     * @param coordinate coordinate of the PickStop in the warehouse
     * @param allocation allocation associated with this stop
     */
    public PickStop(Coordinate coordinate, Allocation allocation) {
        this.coordinate = coordinate;
        this.allocation = allocation;
    }

    /**
     * Returns the coordinate of this stop.
     *
     * @return Coordinate of the PickStop
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the allocation associated with this stop.
     *
     * @return Allocation of the PickStop
     */
    public Allocation getAllocation() {
        return allocation;
    }

    /**
     * Textual representation of the picking stop.
     *
     * @return string in the format "SKU @ (aisle,bay)"
     */
    @Override
    public String toString() {
        return allocation.getSku() + " @ " + coordinate.toString();
    }
}