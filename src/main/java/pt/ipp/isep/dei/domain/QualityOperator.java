package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QualityOperator {

    private final Quarantine quarantine;
    private final Inventory inventory;
    private final AuditLogger logger;

    public QualityOperator(Quarantine quarantine, Inventory inventory, AuditLogger logger) {
        this.quarantine = quarantine;
        this.inventory = inventory;
        this.logger = logger;
    }

    public void processReturns() {
        while (!quarantine.isEmpty()) {
            ReturnItem item = quarantine.next();
            if (shouldRestock(item)) restock(item);
            else discard(item);
        }
    }

    private boolean shouldRestock(ReturnItem item) {
        return item.getReason().equalsIgnoreCase("customer-remorse")
                || item.getReason().equalsIgnoreCase("cycle-count");
    }

    private void restock(ReturnItem item) {
        LocalDate expiry = (item.getExpiryDate() != null)
                ? item.getExpiryDate().toLocalDate()
                : null;

        Box newBox = new Box("RET-" + item.getReturnId(),
                item.getSku(),
                item.getQuantity(),
                expiry,
                java.time.LocalDateTime.now());

        inventory.addBox(newBox);
        logger.log(String.format("returnId=%s | sku=%s | action=Restocked | qty=%d",
                item.getReturnId(), item.getSku(), item.getQuantity()));
    }


    private void discard(ReturnItem item) {
        logger.log(String.format("returnId=%s | sku=%s | action=Discarded | qty=%d",
                item.getReturnId(), item.getSku(), item.getQuantity()));
    }
}

