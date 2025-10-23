package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

public class USEI04UI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== USEI04 — Pick Path Sequencing ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");
        List<Order> orders = (List<Order>) data.get("orders");
        List<Item> items = (List<Item>) data.get("items");

        Warehouse warehouse = new Warehouse("W1");
        for (Bay b : bays) warehouse.addBay(b);
        new WagonUnloader(warehouse).unload(wagons);

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result result = allocator.allocateOrders(orders, warehouse, true);

        // Convert to allocations
        Map<String, Double> weights = new HashMap<>();
        for (Item item : items) weights.put(item.getSku(), item.getUnitWeight());
        List<Allocation> allocations = new ArrayList<>();
        for (AllocationRow row : result.allocationRows) {
            double weight = weights.getOrDefault(row.getSku(), 0.0) * row.getQty();
            allocations.add(new Allocation(row.getOrderId(), row.getLineNo(), row.getSku(),
                    row.getQty(), weight, row.getBoxId(), row.getAisle(), row.getBay()));
        }

        // Merge stops
        Map<String, PickStop> merged = new HashMap<>();
        for (Allocation a : allocations) {
            String key = a.getAisle() + ":" + a.getBay();
            merged.merge(key, new PickStop(new Coordinate(a.getAisle(), a.getBay()), a),
                    (oldVal, newVal) -> { oldVal.getAllocation().setQty(
                            oldVal.getAllocation().getQty() + a.getQty());
                        return oldVal; });
        }

        PickPathPlanner planner = new PickPathPlanner();
        PickPath deterministic = planner.planDeterministicSweep(new ArrayList<>(merged.values()));
        PickPath nearest = planner.planNearestNeighbour(new ArrayList<>(merged.values()));

        System.out.printf("Deterministic Distance: %.2f%n", deterministic.getTotalDistance());
        System.out.printf("Nearest-Neighbour Distance: %.2f%n", nearest.getTotalDistance());
    }
}
