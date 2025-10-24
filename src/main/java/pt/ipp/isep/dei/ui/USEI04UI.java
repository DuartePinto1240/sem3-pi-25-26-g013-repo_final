package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

/**
 * Console User Interface (UI) for Use Case USEI04: Pick Path Sequencing and Optimization.
 * <p>
 * This UI simulates the full flow: loading data, allocating orders, and then
 * generating two different optimized pick paths—Deterministic Sweep and Nearest Neighbour—
 * for the consolidated set of required pick stops, comparing their total distances.
 * </p>
 */
public class USEI04UI implements Runnable {

    /**
     * Executes the USEI04 use case sequence.
     * <p>
     * 1. Loads and validates all domain data.
     * 2. Initializes the {@link Warehouse} and stock via {@link WagonUnloader}.
     * 3. Allocates {@link Order}s (partial mode) using {@link OrderAllocator}.
     * 4. Converts allocation results into weighted {@link Allocation} objects.
     * 5. Consolidates allocations into unique {@link PickStop}s (grouping by aisle and bay).
     * 6. Generates the pick path using two different algorithms: Deterministic Sweep and Nearest Neighbour.
     * 7. Prints the total distance for each generated path.
     * </p>
     */
    @Override
    public void run() {
        System.out.println("\n=== USEI04 — Pick Path Sequencing ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");
        List<Order> orders = (List<Order>) data.get("orders");
        List<Item> items = (List<Item>) data.get("items");

        if (bays.isEmpty()) {
            System.out.println("⚠️ Nenhuma bay encontrada no CSV.");
            return;
        }

        // ✅ Create warehouse based on the actual ID of the bays
        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        // ✅ Add only consistent bays
        for (Bay b : bays) {
            if (!b.getWarehouseId().equals(warehouseId)) {
                System.out.printf("⚠️ Ignorada bay de outro warehouse: %s%n", b.getWarehouseId());
                continue;
            }
            warehouse.addBay(b);
        }

        // 🚂 Unload the wagons
        new WagonUnloader(warehouse).unload(wagons);

        // 📦 Allocate orders
        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, true);

        // 🔢 Convert to allocations
        Map<String, Double> weights = new HashMap<>();
        for (Item item : items)
            weights.put(item.getSku(), item.getUnitWeight());

        List<Allocation> allocations = new ArrayList<>();
        for (AllocationRow row : result.allocationRows) {
            double weight = weights.getOrDefault(row.getSku(), 0.0) * row.getQty();
            allocations.add(new Allocation(
                    row.getOrderId(),
                    row.getLineNo(),
                    row.getSku(),
                    row.getQty(),
                    weight,
                    row.getBoxId(),
                    row.getAisle(),
                    row.getBay()
            ));
        }

        // 🚶‍♂️ Group by pick location (aisle + bay)
        Map<String, PickStop> merged = new HashMap<>();
        for (Allocation a : allocations) {
            String key = a.getAisle() + ":" + a.getBay();
            merged.merge(key, new PickStop(new Coordinate(a.getAisle(), a.getBay()), a),
                    (oldVal, newVal) -> {
                        oldVal.getAllocation().setQty(
                                oldVal.getAllocation().getQty() + a.getQty());
                        return oldVal;
                    });
        }

        // 🧭 Path planning
        PickPathPlanner planner = new PickPathPlanner();
        PickPath deterministic = planner.planDeterministicSweep(new ArrayList<>(merged.values()));
        PickPath nearest = planner.planNearestNeighbour(new ArrayList<>(merged.values()));

        System.out.println("✅ Warehouse processado: " + warehouseId);
        System.out.printf("Deterministic Distance: %.2f%n", deterministic.getTotalDistance());
        System.out.printf("Nearest-Neighbour Distance: %.2f%n", nearest.getTotalDistance());
    }
}