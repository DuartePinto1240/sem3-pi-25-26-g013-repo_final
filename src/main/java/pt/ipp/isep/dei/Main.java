package main.java.pt.ipp.isep.dei;

import main.java.pt.ipp.isep.dei.domain.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== USEI02 — Order Eligibility & Allocation ===");

        CsvLoader loader = new CsvLoader();

        // 🔹 1. Carregar Bays (estrutura do armazém)
        List<Bay> bays = loader.loadBays("C:/data/bays.csv");
        Warehouse warehouse = new Warehouse("W1");
        for (Bay b : bays) warehouse.addBay(b);
        System.out.println("Warehouse carregado com " + bays.size() + " bays.");

        // 🔹 2. Carregar e descarregar Wagons (USEI01)
        List<Wagon> wagons = loader.loadWagons("C:/data/wagons.csv");
        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(wagons);
        System.out.println("Wagons descarregados: " + wagons.size());

        // 🔹 3. Carregar ordens e respetivas linhas dos CSVs
        List<Order> orders = loader.loadOrders(
                "C:/data/orders.csv",
                "C:/data/order_lines.csv"
        );
        System.out.println("Ordens carregadas: " + orders.size());

        // 🔹 4. Executar USEI02 (modo STRICTo)
        OrderAllocator allocator = new OrderAllocator();
        OrderAllocator.Result strictResult = allocator.allocateOrders(orders, warehouse, false);

        System.out.println("\n=== Resultados - Strict Mode ===");
        printResult(strictResult);

        // 🔹 5. Executar USEI02 (modo PARTIAL)
        OrderAllocator.Result partialResult = allocator.allocateOrders(orders, warehouse, true);

        System.out.println("\n=== Resultados - Partial Mode ===");
        printResult(partialResult);

        System.out.println("\n=== Fim da execução USEI02 ===");

        System.out.println("\n=== USEI05 — Returns & Quarantine ===");

        // 1️⃣ Initialize core components
        Quarantine quarantine = new Quarantine();
        Inventory inventory = new Inventory();
        AuditLogger logger = new AuditLogger("returns_audit_log.txt");
        QualityOperator operator = new QualityOperator(quarantine, inventory, logger);

        // 2️⃣ Create some sample returns
        ReturnItem r1 = new ReturnItem("RET001", "SKU100", 10, "customer-remorse",
                java.time.LocalDateTime.parse("2025-09-22T17:00:00"), java.time.LocalDateTime.parse("2025-12-01T00:00:00"));
        ReturnItem r2 = new ReturnItem("RET002", "SKU200", 5, "damaged",
                java.time.LocalDateTime.parse("2025-09-20T10:00:00"), null);
        ReturnItem r3 = new ReturnItem("RET003", "SKU300", 7, "cycle-count",
                java.time.LocalDateTime.parse("2025-09-23T08:00:00"), null);

        // 3️⃣ Add them to quarantine (latest first)
        quarantine.add(r1);
        quarantine.add(r2);
        quarantine.add(r3);

        System.out.println("Returns loaded into quarantine: " + quarantine.size());

        // 4️⃣ Process all returns
        operator.processReturns();

        // 5️⃣ Display inventory after processing
        System.out.println("\n--- Inventory after processing ---");
        for (var entry : inventory.getStock().entrySet()) {
            System.out.println("SKU: " + entry.getKey());
            for (Box box : entry.getValue()) {
                System.out.println("   " + box);
            }
        }

        // 6️⃣ Display audit log contents
        System.out.println("\n--- Audit Log Contents ---");
        try {
            java.nio.file.Files.lines(java.nio.file.Path.of("returns_audit_log.txt"))
                    .forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error reading log: " + e.getMessage());
        }

        System.out.println("\n=== Fim da execução USEI05 ===");

    }

    // 🔸 Método auxiliar para imprimir resultados formatados
    private static void printResult(OrderAllocator.Result result) {
        System.out.println("\n--- Line Statuses ---");
        for (LineStatus status : result.lineStatuses) {
            System.out.printf("Order %s | Line %d | SKU %s | Req=%d | Alloc=%d | Status=%s%n",
                    status.getOrderId(), status.getLineNo(), status.getSku(),
                    status.getRequestedQty(), status.getAllocatedQty(), status.getStatus());
        }

        System.out.println("\n--- Allocation Rows ---");
        for (AllocationRow row : result.allocationRows) {
            System.out.printf("Order=%s | Line=%d | SKU=%s | Qty=%d | Box=%s | Aisle=%d | Bay=%d%n",
                    row.getOrderId(), row.getLineNo(), row.getSku(), row.getQty(),
                    row.getBoxId(), row.getAisle(), row.getBay());
        }
    }
}


