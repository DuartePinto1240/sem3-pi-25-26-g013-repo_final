package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.List;
import java.util.Map;

/**
 * Console User Interface (UI) for Use Case USEI01: Wagon Unloading and Box Allocation.
 * <p>
 * This UI loads data, initializes the {@link Warehouse} with {@link Bay}s,
 * and executes the {@link WagonUnloader} logic to allocate boxes from {@link Wagon}s
 * based on FEFO/FIFO strategy.
 * </p>
 */
public class USEI01UI implements Runnable {

    /**
     * Executes the USEI01 use case sequence.
     * <p>
     * 1. Loads and validates all domain data.
     * 2. Initializes the {@link Warehouse} based on the first {@link Bay}'s ID.
     * 3. Adds all relevant {@link Bay}s to the {@link Warehouse}.
     * 4. Runs the {@link WagonUnloader} to process all {@link Wagon}s.
     * </p>
     */
    @Override
    public void run() {
        System.out.println("\n=== USEI01 — Wagon Unloading ===\n");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");

        // ✅ Automatically determine the Warehouse ID based on the first Bay
        if (bays.isEmpty()) {
            System.out.println("⚠️ Nenhuma bay encontrada no ficheiro CSV.");
            return;
        }

        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        // ✅ Validate and add the bays to the warehouse
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