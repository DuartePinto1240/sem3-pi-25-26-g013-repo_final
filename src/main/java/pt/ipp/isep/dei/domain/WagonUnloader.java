package main.java.pt.ipp.isep.dei.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Class responsible for unloading wagons and allocating the boxes
 * into the appropriate bays in the warehouse, following
 * FEFO (First-Expired-First-Out) or FIFO (First-In-First-Out) principles.
 */
public class WagonUnloader {
    private Warehouse warehouse;

    /**
     * Constructor.
     *
     * @param warehouse the warehouse where the boxes will be stored.
     */
    public WagonUnloader(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Unloads the wagons and allocates the boxes into the bays.
     * <p>
     * Decides the storage strategy (FEFO/FIFO) based on whether the product is perishable.
     * </p>
     *
     * @param wagons list of {@link Wagon}s to unload
     */
    public void unload(List<Wagon> wagons) {
        for (Wagon wagon : wagons) {
            for (Box box : wagon.getBoxes()) {
                // Checks if the product is perishable (has an expiration date)
                if (box.getExpiryDate() != null) {
                    // Sort the box using FEFO (First-Expired-First-Out) - perishable goods
                    placeBoxInBay(box, true);  // FEFO
                } else {
                    // Sort the box using FIFO (First-In-First-Out) - non-perishable goods
                    placeBoxInBay(box, false); // FIFO
                }
            }
        }
    }

    /**
     * Performs the allocation of the box into the warehouse bay, respecting FEFO or FIFO.
     * <p>
     * The box is added to the first bay that can store it. After adding, the bay's
     * stored boxes are sorted according to the required strategy.
     * </p>
     *
     * @param box the box to be placed
     * @param isPerishable {@code true} if the box is perishable (FEFO required), {@code false} otherwise (FIFO required)
     */
    private void placeBoxInBay(Box box, boolean isPerishable) {
        Map<String, Bay> bays = warehouse.getBays();

        Comparator<Box> comparator = null;
        if (isPerishable) {
            comparator = new Comparator<Box>() {
                @Override
                public int compare(Box b1, Box b2) {
                    if (b1.getExpiryDate() == null && b2.getExpiryDate() == null) return 0;
                    if (b1.getExpiryDate() == null) return 1; // Non-perishable goes after
                    if (b2.getExpiryDate() == null) return -1; // Non-perishable goes after
                    return b1.getExpiryDate().compareTo(b2.getExpiryDate());
                }
            };
        } else {
            comparator = new Comparator<Box>() {
                @Override
                public int compare(Box b1, Box b2) {
                    return b1.getReceivedAt().compareTo(b2.getReceivedAt());
                }
            };
        }

        for (Bay bay : bays.values()) {
            if (bay.canStoreBox(box)) {
                bay.addBox(box);

                // Sort AFTER adding, only in this bay
                Collections.sort(bay.getStoredBoxes(), comparator);

                System.out.println("Box " + box.getBoxId() + " alocada no Bay " + bay.getBay() + " do Warehouse " + bay.getWarehouseId());
                return;
            }
        }

        // If no bay has sufficient capacity
        System.out.println("Não há capacidade suficiente para armazenar a Box " + box.getBoxId());
    }
}