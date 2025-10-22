package main.java.pt.ipp.isep.dei.domain;

import java.util.*;

/**
 * Represents the warehouse inventory, managing all {@link Box} objects
 * grouped by their SKU codes.
 * <p>
 * The inventory ensures that boxes for each SKU are maintained in
 * FEFO/FIFO order (First-Expire-First-Out / First-In-First-Out),
 * so retrieval operations naturally respect product rotation.
 * </p>
 */
public class Inventory {

    /** Map associating each SKU to its list of stored boxes. */
    private final Map<String, List<Box>> stock = new HashMap<>();

    /**
     * Adds a {@link Box} to the inventory.
     * <p>
     * If the SKU does not yet exist in the inventory, it is created automatically.
     * The list of boxes for that SKU is then sorted according to FEFO/FIFO rules,
     * as defined by {@link Box#compareTo(Box)}.
     * </p>
     *
     * @param box the box to add (must not be {@code null})
     */
    public void addBox(Box box) {
        stock.putIfAbsent(box.getSku(), new ArrayList<>());
        List<Box> boxes = stock.get(box.getSku());
        boxes.add(box);
        Collections.sort(boxes); // FEFO/FIFO ordering
    }

    /**
     * Checks if the inventory contains at least one box for a given SKU.
     *
     * @param sku the SKU code to check
     * @return {@code true} if the SKU exists in the inventory; {@code false} otherwise
     */
    public boolean containsSku(String sku) {
        return stock.containsKey(sku);
    }

    /**
     * Returns all boxes associated with a given SKU.
     * <p>
     * If the SKU is not found, an empty (immutable) list is returned.
     * </p>
     *
     * @param sku the SKU code
     * @return list of boxes (possibly empty, never {@code null})
     */
    public List<Box> getBoxes(String sku) {
        return stock.getOrDefault(sku, Collections.emptyList());
    }

    /**
     * Returns an unmodifiable view of the entire stock map.
     * <p>
     * The returned map cannot be modified, ensuring data integrity.
     * </p>
     *
     * @return an unmodifiable map of SKU → list of boxes
     */
    public Map<String, List<Box>> getStock() {
        return Collections.unmodifiableMap(stock);
    }
}

