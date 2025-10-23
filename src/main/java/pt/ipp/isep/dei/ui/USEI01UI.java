package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.List;
import java.util.Map;

public class USEI01UI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== USEI01 — Wagon Unloading ===\n");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");

        // ✅ Determinar automaticamente o ID do armazém com base na primeira Bay
        if (bays.isEmpty()) {
            System.out.println("⚠️ Nenhuma bay encontrada no ficheiro CSV.");
            return;
        }

        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        // ✅ Validar e adicionar as bays ao armazém
        for (Bay b : bays) {
            if (!b.getWarehouseId().equals(warehouseId)) {
                System.out.printf("⚠️ Ignorada Bay de warehouse diferente: %s (esperado %s)%n",
                        b.getWarehouseId(), warehouseId);
                continue;
            }
            warehouse.addBay(b);
        }

        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(wagons);

        System.out.println("✅ Wagons descarregados: " + wagons.size());
        System.out.println("📦 Armazém processado: " + warehouseId);
    }
}

