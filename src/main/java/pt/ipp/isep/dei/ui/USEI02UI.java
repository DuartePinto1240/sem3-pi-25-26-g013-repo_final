package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.List;
import java.util.Map;

/**
 * Console User Interface (UI) for Use Case USEI02: Order Eligibility and Allocation.
 * <p>
 * This UI simulates the process of unloading wagons (USEI01 dependency) and then
 * attempting to allocate items for pending {@link Order}s from the warehouse stock,
 * running the allocation logic in both strict and partial modes.
 * </p>
 */
public class USEI02UI implements Runnable {

    /**
     * Executes the USEI02 use case sequence.
     * <p>
     * 1. Loads and validates all domain data (Bays, Wagons, Orders).
     * 2. Initializes the {@link Warehouse} and adds relevant {@link Bay}s.
     * 3. Executes the {@link WagonUnloader} to populate the warehouse stock.
     * 4. Runs {@link OrderAllocator} in "Strict Mode" (all lines must be allocatable).
     * 5. Runs {@link OrderAllocator} in "Partial Mode" (partial allocation allowed).
     * 6. Prints the results of both allocation attempts.
     * </p>
     */
    @Override
    public void run() {
        System.out.println("\n=== USEI02 — Order Eligibility & Allocation ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");
        List<Order> orders = (List<Order>) data.get("orders");

        if (bays.isEmpty()) {
            System.out.println("⚠️ Nenhuma bay encontrada.");
            return;
        }

        // ✅ Create the warehouse based on the actual ID of the first Bay
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
        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(wagons);

        // 📦 Allocate orders
        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result strict = allocator.allocateOrders(orders, warehouse, false);
        OrderAllocator.Result partial = allocator.allocateOrders(orders, warehouse, true);

        printResult(strict, "Strict Mode");
        printResult(partial, "Partial Mode");
    }

    /**
     * Prints the results of an order allocation attempt.
     *
     * @param result the {@link OrderAllocator.Result} containing status information
     * @param label a string label to identify the result set (e.g., "Strict Mode")
     */
    private void printResult(OrderAllocator.Result result, String label) {
        System.out.println("\n=== Resultados — " + label + " ===");
        for (LineStatus s : result.lineStatuses) {
            System.out.printf("Order %s | SKU %s | Req=%d | Alloc=%d | Status=%s%n",
                    s.getOrderId(), s.getSku(), s.getRequestedQty(), s.getAllocatedQty(), s.getStatus());
        }
    }
}