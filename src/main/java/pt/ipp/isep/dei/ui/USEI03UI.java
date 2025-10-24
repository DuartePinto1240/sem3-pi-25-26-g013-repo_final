package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

/**
 * Console User Interface (UI) for Use Case USEI03: Picking Plans Generation.
 * <p>
 * This UI simulates the full flow: loading data, unloading wagons, allocating
 * orders (partially), and then generating a sequence of trolley-based picking plans
 * using a Best-Fit Decreasing algorithm, based on user-defined trolley capacity.
 * </p>
 */
public class USEI03UI implements Runnable {

    /**
     * Executes the USEI03 use case sequence.
     * <p>
     * 1. Loads and validates all domain data.
     * 2. Initializes the {@link Warehouse} and stock via {@link WagonUnloader}.
     * 3. Allocates {@link Order}s (partial mode) using {@link OrderAllocator}.
     * 4. Calculates weights for allocated items and converts them into {@link Allocation} objects.
     * 5. Prompts the user for the maximum trolley weight capacity.
     * 6. Generates the picking plan using {@link PickingPlanner} (Best-Fit Decreasing).
     * 7. Prints the summary result.
     * </p>
     */
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

        // ✅ Determine warehouse ID dynamically
        String warehouseId = bays.get(0).getWarehouseId();
        Warehouse warehouse = new Warehouse(warehouseId);

        // ✅ Validate and add only bays from the same warehouse
        for (Bay b : bays) {
            if (!b.getWarehouseId().equals(warehouseId)) {
                System.out.printf("⚠️ Ignorada bay de outro warehouse: %s%n", b.getWarehouseId());
                continue;
            }
            warehouse.addBay(b);
        }

        // 🚂 Unload the wagons
        new WagonUnloader(warehouse).unload(wagons);

        // 📦 Allocate orders (partial mode)
        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result partial = allocator.allocateOrders(orders, warehouse, true);

        // ⚖️ Calculate weights per item
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

        // 🧮 Trolley capacity
        Scanner sc = new Scanner(System.in);
        System.out.print("Indique capacidade máxima (peso) do trolley: ");
        double capacity = sc.nextDouble();

        // 🧠 Picking planning
        PickingPlanner planner = new PickingPlanner(allocations, capacity);
        List<Trolley> trolleys = planner.planBestFitDecreasing();

        System.out.println("✅ Warehouse processado: " + warehouseId);
        System.out.println("Total de trolleys: " + trolleys.size());
    }
}