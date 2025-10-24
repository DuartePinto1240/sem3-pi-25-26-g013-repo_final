package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.util.DataLoader;

import java.util.*;

/**
 * Console User Interface (UI) for Use Case USEI05: Processing Returns and Quarantine Management.
 * <p>
 * This UI simulates the process of receiving returned items, loading them into a
 * {@link Quarantine}, and then using the {@link QualityOperator} to process them,
 * deciding whether to move them to the general {@link Inventory} or dispose of them,
 * while logging all actions with the {@link AuditLogger}.
 * </p>
 */
public class USEI05UI implements Runnable {

    /**
     * Executes the USEI05 use case sequence.
     * <p>
     * 1. Loads and validates items and returns data.
     * 2. Initializes {@link Quarantine}, {@link Inventory}, and {@link AuditLogger}.
     * 3. Loads valid {@link ReturnItem}s into the {@link Quarantine}.
     * 4. Executes the {@link QualityOperator} to process all quarantined items.
     * 5. Prints the final state of the {@link Inventory}.
     * 6. Prints the contents of the audit log file.
     * </p>
     */
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

        List<ReturnItem> validReturns = new ArrayList<>();
        for (ReturnItem r : returns) {
            boolean validSku = items.stream().anyMatch(i -> i.getSku().equalsIgnoreCase(r.getSku()));
            if (!validSku) {
                System.err.println("⚠️ SKU inválido: " + r.getSku());
                continue;
            }
            validReturns.add(r);
        }

        // Add all at once in correct (timestamp DESC) order
        quarantine.addAll(validReturns);

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