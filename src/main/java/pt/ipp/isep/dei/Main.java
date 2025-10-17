package main.java.pt.ipp.isep.dei;

import main.java.pt.ipp.isep.dei.domain.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== USEI02 — Order Eligibility & Allocation ===");

        CsvLoader loader = new CsvLoader();

        // 🔹 1. Carregar Bays (estrutura do armazém)
        List<Bay> bays = loader.loadBays("train_station_dataset_v2/train_station_dataset/bays.csv");
        Warehouse warehouse = new Warehouse("W1");
        for (Bay b : bays) warehouse.addBay(b);
        System.out.println("Warehouse carregado com " + bays.size() + " bays.");

        // 🔹 2. Carregar e descarregar Wagons (USEI01)
        List<Wagon> wagons = loader.loadWagons("train_station_dataset_v2/train_station_dataset/wagons.csv");
        WagonUnloader unloader = new WagonUnloader(warehouse);
        unloader.unload(wagons);
        System.out.println("Wagons descarregados: " + wagons.size());

        // 🔹 3. Carregar ordens e respetivas linhas dos CSVs
        List<Order> orders = loader.loadOrders(
                "train_station_dataset_v2/train_station_dataset/orders.csv",
                "train_station_dataset_v2/train_station_dataset/order_lines.csv"
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

        // 1️⃣ Load items (needed to validate SKUs)
        List<Item> items = loader.loadItems("train_station_dataset_v2/train_station_dataset/items.csv");
        System.out.println("Items loaded: " + items.size());

        // 2️⃣ Load returns from CSV
        List<ReturnItem> returns = loader.loadReturns("train_station_dataset_v2/train_station_dataset/returns.csv");
        System.out.println("Returns loaded: " + returns.size());

        // 3️⃣ Initialize quarantine, inventory, and logger
        Quarantine quarantine = new Quarantine();
        Inventory inventory = new Inventory();
        AuditLogger logger = new AuditLogger("returns_audit_log.txt");
        QualityOperator operator = new QualityOperator(quarantine, inventory, logger);

        // 4️⃣ Validate and add returns to quarantine
        for (ReturnItem r : returns) {
            // Optional validation: ensure SKU exists in items list
            boolean validSku = items.stream()
                    .anyMatch(i -> i.getSku().equalsIgnoreCase(r.getSku()));
            if (!validSku) {
                System.err.println("⚠️ Unknown SKU in returns.csv: " + r.getSku() +
                        " (returnId=" + r.getReturnId() + ")");
                continue; // skip invalid
            }
            quarantine.add(r);
        }
        System.out.println("Valid returns loaded into quarantine: " + quarantine.size());

        // 5️⃣ Process all returns (restock/discard)
        operator.processReturns();

        // 6️⃣ Print resulting inventory contents
        System.out.println("\n--- Inventory After Processing ---");
        for (var entry : inventory.getStock().entrySet()) {
            System.out.println("SKU: " + entry.getKey());
            for (Box box : entry.getValue()) {
                System.out.println("   " + box);
            }
        }

        // 7️⃣ Print audit log file contents
        System.out.println("\n--- Audit Log (returns_audit_log.txt) ---");
        try {
            java.nio.file.Files.lines(java.nio.file.Path.of("returns_audit_log.txt"))
                    .forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("❌ Error reading log file: " + e.getMessage());
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


