package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.List;
import java.util.Map;

public class USEI02UI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== USEI02 — Order Eligibility & Allocation ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Bay> bays = (List<Bay>) data.get("bays");
        List<Wagon> wagons = (List<Wagon>) data.get("wagons");
        List<Order> orders = (List<Order>) data.get("orders");

        Warehouse warehouse = new Warehouse("W1");
        for (Bay b : bays) warehouse.addBay(b);
        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(wagons);

        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result strict = allocator.allocateOrders(orders, warehouse, false);
        OrderAllocator.Result partial = allocator.allocateOrders(orders, warehouse, true);

        printResult(strict, "Strict Mode");
        printResult(partial, "Partial Mode");
    }

    private void printResult(OrderAllocator.Result result, String label) {
        System.out.println("\n=== Resultados — " + label + " ===");
        for (LineStatus s : result.lineStatuses) {
            System.out.printf("Order %s | SKU %s | Req=%d | Alloc=%d | Status=%s%n",
                    s.getOrderId(), s.getSku(), s.getRequestedQty(), s.getAllocatedQty(), s.getStatus());
        }
    }
}
