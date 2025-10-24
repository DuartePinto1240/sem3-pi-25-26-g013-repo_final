package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

public class USEI03UI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== USEI03 — Picking Plans ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");
        List<Order> orders = (List<Order>) data.get("orders");
        List<Item> items = (List<Item>) data.get("items");

        if (bays.isEmpty()) {
            System.out.println("⚠️ Nenhuma bay encontrada.");
            return;
        }

        // ✅ Determinar ID do warehouse dinamicamente
        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        // ✅ Validar e adicionar apenas bays do mesmo warehouse
        for (Bay b : bays) {
            if (!b.getWarehouseId().equals(warehouseId)) {
                System.out.printf("⚠️ Ignorada bay de outro warehouse: %s%n", b.getWarehouseId());
                continue;
            }
            warehouse.addBay(b);
        }

        // 🚂 Descarregar os vagões
        new WagonUnloader(warehouse).unload(wagons);

        // 📦 Alocar encomendas (modo parcial)
        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result partial = allocator.allocateOrders(orders, warehouse, true);

        // ⚖️ Calcular pesos por item
        Map<String, Double> itemWeights = new HashMap<>();
        for (Item i : items) {
            itemWeights.put(i.getSku(), i.getUnitWeight());
        }

        List<Allocation> allocations = new ArrayList<>();
        for (AllocationRow row : partial.allocationRows) {
            double w = itemWeights.getOrDefault(row.getSku(), 0.0) * row.getQty();
            allocations.add(new Allocation(
                    row.getOrderId(),
                    row.getLineNo(),
                    row.getSku(),
                    row.getQty(),
                    w,
                    row.getBoxId(),
                    row.getAisle(),
                    row.getBay()
            ));
        }

        // 🧮 Capacidade do trolley
        Scanner sc = new Scanner(System.in);
        System.out.print("Indique capacidade máxima (peso) do trolley: ");
        double capacity = sc.nextDouble();

        // 🧠 Planeamento de picking
        PickingPlanner planner = new PickingPlanner(allocations, capacity);
        List<Trolley> trolleys = planner.planBestFitDecreasing();

        System.out.println("✅ Warehouse processado: " + warehouseId);
        System.out.println("Total de trolleys: " + trolleys.size());
    }
}
