package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

public class USEI05UI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== USEI05 — Returns & Quarantine ===");

        Map<String, Object> data = DataLoader.loadAndValidate();
        if (data == null) return;

        List<Item> items = (List<Item>) data.get("items");
        List<ReturnItem> returns = (List<ReturnItem>) data.get("returns");

        Quarantine quarantine = new Quarantine();
        Inventory inventory = new Inventory();
        AuditLogger logger = new AuditLogger("returns_audit_log.txt", true);
        QualityOperator operator = new QualityOperator(quarantine, inventory, logger);

        for (ReturnItem r : returns) {
            boolean validSku = items.stream().anyMatch(i -> i.getSku().equalsIgnoreCase(r.getSku()));
            if (!validSku) {
                System.err.println("⚠️ SKU inválido: " + r.getSku());
                continue;
            }
            quarantine.add(r);
        }

        System.out.println("Valid returns loaded into quarantine: " + quarantine.size());
        operator.processReturns();

        System.out.println("\n--- Inventory After Processing ---");
        inventory.getStock().forEach((sku, boxes) -> {
            System.out.println("SKU: " + sku);
            boxes.forEach(b -> System.out.println("   " + b));
        });

        System.out.println("\n--- Audit Log ---");
        try {
            java.nio.file.Files.lines(java.nio.file.Path.of("returns_audit_log.txt")).forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("❌ Error reading log file: " + e.getMessage());
        }
    }
}
