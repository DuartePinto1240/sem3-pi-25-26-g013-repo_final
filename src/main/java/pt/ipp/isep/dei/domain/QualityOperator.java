package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class responsible for processing returned items.
 * <p>
 * A QualityOperator checks items in quarantine and decides whether they should be
 * restocked into the inventory or discarded, logging each action in the AuditLogger.
 * </p>
 */
public class QualityOperator {

    /** Quarantine where returned items are temporarily stored. */
    private final Quarantine quarantine;

    /** Main warehouse inventory where boxes can be added. */
    private final Inventory inventory;

    /** Audit logger to record restock or discard actions. */
    private final AuditLogger logger;

    /**
     * Constructor for the QualityOperator.
     *
     * @param quarantine quarantine containing returned items
     * @param inventory warehouse inventory
     * @param logger logger for auditing actions
     */
    public QualityOperator(Quarantine quarantine, Inventory inventory, AuditLogger logger) {
        this.quarantine = quarantine;
        this.inventory = inventory;
        this.logger = logger;
    }

    /**
     * Processes all items in the quarantine.
     * <p>
     * For each item, it decides whether to restock or discard it.
     * </p>
     */
    public void processReturns() {
        while (!quarantine.isEmpty()) {
            ReturnItem item = quarantine.next();
            if (shouldRestock(item)) restock(item);
            else discard(item);
        }
    }

    /**
     * Determines if an item should be restocked.
     *
     * @param item returned item to be evaluated
     * @return true if the item should be restocked, false otherwise
     */
    private boolean shouldRestock(ReturnItem item) {
        return item.getReason().equalsIgnoreCase("customer-remorse")
                || item.getReason().equalsIgnoreCase("cycle-count");
    }

    /**
     * Restocks a returned item into the inventory.
     * <p>
     * Creates a new box for the item and adds it to the inventory, besides logging the action.
     * </p>
     *
     * @param item returned item to be restocked
     */
    private void restock(ReturnItem item) {
        LocalDate expiry = (item.getExpiryDate() != null)
                ? item.getExpiryDate().toLocalDate()
                : null;

        Box newBox = new Box("RET-" + item.getReturnId(),
                item.getSku(),
                item.getQuantity(),
                expiry,
                LocalDateTime.now());

        inventory.addBox(newBox);
        logger.log(String.format("returnId=%s | sku=%s | action=Restocked | qty=%d",
                item.getReturnId(), item.getSku(), item.getQuantity()));
    }

    /**
     * Discards a returned item.
     * <p>
     * Only logs the discard action without modifying the inventory.
     * </p>
     *
     * @param item returned item to be discarded
     */
    private void discard(ReturnItem item) {
        logger.log(String.format("returnId=%s | sku=%s | action=Discarded",
                item.getReturnId(), item.getSku()));
    }
}