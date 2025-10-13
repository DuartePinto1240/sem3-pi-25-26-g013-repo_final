package main.java.pt.ipp.isep.dei.domain;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    private String id;
    private Map<String, Bay> bays;

    // Construtor
    public Warehouse(String id) {
        this.id = id;
        this.bays = new HashMap<>();
    }

    // Adicionar um bay ao armazém
    public void addBay(Bay bay) {
        String key = bay.getWarehouseId() + "-" + bay.getAisle() + "-" + bay.getBay(); // Usando os getters
        bays.put(key, bay);
    }

    // Retornar bay específico
    public Bay getBay(String warehouseId, int aisle, int bay) {
        String key = warehouseId + "-" + aisle + "-" + bay;
        return bays.get(key);
    }

    // Getter para a lista de bays
    public Map<String, Bay> getBays() {
        return bays;
    }

    @Override
    public String toString() {
        return "main.java.pt.ipp.isep.dei.domain.Warehouse{" +
                "id='" + id + '\'' +
                ", bays=" + bays +
                '}';
    }
}




