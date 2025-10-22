package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

public class Bay {
    private String warehouseId;
    private int aisle;
    private int bay;
    private int capacityBoxes;
    private List<Box> storedBoxes;

    // Construtor
    public Bay(String warehouseId, int aisle, int bay, int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bay = bay;
        this.capacityBoxes = capacityBoxes;
        this.storedBoxes = new ArrayList<>();
    }

    // Métodos getters para acessar as propriedades privadas
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

    public boolean canStoreBox(Box box) {
        return storedBoxes.size() < capacityBoxes; // Verifica se o bay tem capacidade
    }

    public void addBox(Box box) {
        if (canStoreBox(box)) {
            storedBoxes.add(box);
        }
    }

    public List<Box> getStoredBoxes() {
        return storedBoxes;
    }

    @Override
    public String toString() {
        return "main.java.pt.ipp.isep.dei.domain.Bay{" +
                "warehouseId='" + warehouseId + '\'' +
                ", aisle=" + aisle +
                ", bay=" + bay +
                ", capacityBoxes=" + capacityBoxes +
                '}';
    }
}



