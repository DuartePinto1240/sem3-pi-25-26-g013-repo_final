package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a storage bay within a warehouse.
 *
 * <p>Each bay belongs to a warehouse, has an aisle number, a bay number,
 * a maximum capacity of boxes, and maintains a list of stored boxes.</p>
 */
public class Bay {

    private String warehouseId;
    private int aisle;
    private int bay;
    private int capacityBoxes;
    private List<Box> storedBoxes;

    /**
     * Constructs a new Bay.
     *
     * @param warehouseId   ID of the warehouse this bay belongs to
     * @param aisle         aisle number
     * @param bay           bay number within the aisle
     * @param capacityBoxes maximum number of boxes this bay can store
     */
    public Bay(String warehouseId, int aisle, int bay, int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bay = bay;
        this.capacityBoxes = capacityBoxes;
        this.storedBoxes = new ArrayList<>();
    }

    /**
     * Returns the ID of the warehouse this bay belongs to.
     *
     * @return warehouse ID
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Returns the aisle number of the bay.
     *
     * @return aisle number
     */
    public int getAisle() {
        return aisle;
    }

    /**
     * Returns the bay number within the aisle.
     *
     * @return bay number
     */
    public int getBay() {
        return bay;
    }

    /**
     * Returns the maximum box capacity of this bay.
     *
     * @return maximum number of boxes
     */
    public int getCapacityBoxes() {
        return capacityBoxes;
    }

    /**
     * Checks if a new box can be stored in this bay.
     *
     * @param box box to be stored
     * @return {@code true} if space is available, {@code false} otherwise
     */
    public boolean canStoreBox(Box box) {
        return storedBoxes.size() < capacityBoxes;
    }

    /**
     * Adds a box to this bay if space is available.
     *
     * @param box box to add
     */
    public void addBox(Box box) {
        if (canStoreBox(box)) {
            storedBoxes.add(box);
        }
    }

    /**
     * Returns the list of boxes currently stored in this bay.
     *
     * @return list of {@link Box} objects
     */
    public List<Box> getStoredBoxes() {
        return storedBoxes;
    }

    /**
     * Returns a string representation of this bay, including warehouse ID, aisle,
     * bay number, and maximum capacity.
     *
     * @return string representation of the bay
     */
    @Override
    public String toString() {
        return "Bay{" +
                "warehouseId='" + warehouseId + '\'' +
                ", aisle=" + aisle +
                ", bay=" + bay +
                ", capacityBoxes=" + capacityBoxes +
                '}';
    }

}
