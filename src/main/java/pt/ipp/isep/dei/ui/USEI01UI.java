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

        Warehouse warehouse = new Warehouse("W1");
        for (Bay b : bays) warehouse.addBay(b);
        WagonUnloader unloader = new WagonUnloader(warehouse);

        unloader.unload(wagons);
        System.out.println("✅ Wagons descarregados: " + wagons.size());
    }
}
