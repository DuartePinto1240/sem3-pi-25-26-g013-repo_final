package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a coordinate within a warehouse, consisting of an aisle ({@code aisle})
 * and a position ({@code bay}) within that aisle.
 *
 * <p>This class is used to calculate distances between storage locations in the
 * warehouse, based on a specific logistics and picking metric.</p>
 */
public class Coordinate {

    /** The aisle number where the position is located. */
    private int aisle;

    /** The bay number within the aisle. */
    private int bay;

    /**
     * Creates a new warehouse coordinate.
     *
     * @param aisle the aisle number
     * @param bay   the position (bay) number within the aisle
     */
    public Coordinate(int aisle, int bay) {
        this.aisle = aisle;
        this.bay = bay;
    }

    /**
     * Returns the aisle number.
     *
     * @return the aisle number
     */
    public int getAisle() {
        return aisle;
    }

    /**
     * Returns the bay number within the aisle.
     *
     * @return the bay number
     */
    public int getBay() {
        return bay;
    }

    /**
     * Calculates the distance between this coordinate and another,
     * based on a warehouse-specific logistics cost model.
     *
     * <p>The distance is calculated as follows:</p>
     * <ul>
     *   <li>If both coordinates are in the same aisle, the distance is the absolute difference
     *       between their bay numbers.</li>
     *   <li>If they are in different aisles, the distance is calculated as:
     *       {@code this.bay + |this.aisle - other.aisle| * 3 + other.bay}</li>
     * </ul>
     *
     * <p>This model accounts for an additional cost of changing aisles,
     * assuming a fixed cost of 3 units per aisle transition.</p>
     *
     * @param other the other coordinate to compare with
     * @return the distance between the two positions according to the defined metric
     */
    public int distanceTo(Coordinate other) {
        if (this.aisle == other.aisle)
            return Math.abs(this.bay - other.bay);
        else
            return this.bay + Math.abs(this.aisle - other.aisle) * 3 + other.bay;
    }

    /**
     * Returns a string representation of the coordinate in the format {@code (aisle,bay)}.
     *
     * @return a string representing the coordinate
     */
    @Override
    public String toString() {
        return "(" + aisle + "," + bay + ")";
    }
}
