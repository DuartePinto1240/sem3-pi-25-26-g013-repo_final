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
     */
    public Bay(String warehouseId, int aisle, int bay, int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bay = bay;
        this.capacityBoxes = capacityBoxes;
        this.storedBoxes = new ArrayList<>();
    }

    // --- Getters ---

    public String getWarehouseId() {
        return warehouseId;
    }

    public int getAisle() {
        return aisle;
    }

    public int getBay() {
        return bay;
    }

    public int getCapacityBoxes() {
        return capacityBoxes;
    }

    public List<Box> getStoredBoxes() {
        return storedBoxes;
    }

    // --- Gestão de Capacidade ---

    /**
     * Checks if a new box can be stored in this bay.
     *
     * @param box box to be stored (not used in current simple capacity check)
     * @return {@code true} if space is available, {@code false} otherwise
     */
    public boolean canStoreBox(Box box) {
        // Verifica se o número de caixas armazenadas é menor que a capacidade máxima
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
        } else {
            // Se esta baia está sendo usada no WagonUnloader, esta condição
            // não deve ser atingida se o algoritmo de alocação estiver correto.
        }
    }

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
