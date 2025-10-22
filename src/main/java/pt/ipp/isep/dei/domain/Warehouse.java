package main.java.pt.ipp.isep.dei.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a warehouse that contains multiple {@link Bay}s.
 * <p>
 * Each {@code Warehouse} has a unique identifier and maintains a map of bays,
 * where each bay is identified by the combination of warehouse ID, aisle number, and bay number.
 * </p>
 */
public class Warehouse {

    /** Unique identifier of the warehouse. */
    private String id;

    /** Map of bays, with the key in the format "warehouseId-aisle-bay". */
    private Map<String, Bay> bays;

    /**
     * Constructs an empty warehouse with the provided ID.
     *
     * @param id unique identifier of the warehouse
     */
    public Warehouse(String id) {
        this.id = id;
        this.bays = new HashMap<>();
    }

    /**
     * Adds a bay to the warehouse.
     * <p>
     * The key used in the internal map is constructed by concatenating the warehouse ID, aisle, and bay number.
     * </p>
     *
     * @param bay the bay to be added to the warehouse
     */
    public void addBay(Bay bay) {
        String key = bay.getWarehouseId() + "-" + bay.getAisle() + "-" + bay.getBay();
        bays.put(key, bay);
    }

    /**
     * Gets a specific bay from the warehouse.
     *
     * @param warehouseId ID of the warehouse
     * @param aisle aisle number
     * @param bay bay number
     * @return the corresponding {@link Bay}, or {@code null} if it does not exist
     */
    public Bay getBay(String warehouseId, int aisle, int bay) {
        String key = warehouseId + "-" + aisle + "-" + bay;
        return bays.get(key);
    }

    /**
     * Returns all bays in the warehouse.
     *
     * @return map of bays
     */
    public Map<String, Bay> getBays() {
        return bays;
    }

    /**
     * Textual representation of the warehouse, including its ID and the bays it contains.
     *
     * @return string in the format {@code Warehouse{id='ID', bays={...}}}
     */
    @Override
    public String toString() {
        return "main.java.pt.ipp.isep.dei.domain.Warehouse{" +
                "id='" + id + '\'' +
                ", bays=" + bays +
                '}';
    }
}

