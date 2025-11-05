package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDateTime;

/**
 * Represents an item returned to the warehouse.
 * <p>
 * Each {@code ReturnItem} has a unique identifier, SKU, quantity,
 * reason for return, return timestamp, and, optionally, an expiry date.
 * </p>
 * <p>
 * This class implements {@link Comparable} to sort items by descending timestamp
 * (most recent first) and, in case of a tie, by {@code returnId}.
 * </p>
 */
public class ReturnItem implements Comparable<ReturnItem> {

    /** Unique identifier of the return. */
    private final String returnId;

    /** SKU of the returned item. */
    private final String sku;

    /** Quantity of the returned item. Must be positive. */
    private final int quantity;

    /** Reason for the return (e.g., "customer-remorse"). */
    private final String reason;

    /** Timestamp of the return. */
    private final LocalDateTime timestamp;

    /** Expiry date of the item, if applicable; can be null. */
    private final LocalDateTime expiryDate;

    /**
     * Constructs a ReturnItem.
     *
     * @param returnId unique identifier of the return, cannot be null
     * @param sku SKU of the item, cannot be null
     * @param quantity quantity of the item, must be positive
     * @param reason reason for the return, cannot be null
     * @param timestamp date and time of the return, cannot be null
     * @param expiryDate expiry date of the returned item, can be null
     * @throws IllegalArgumentException if quantity ≤ 0 or any mandatory field is null
     */
    public ReturnItem(String returnId, String sku, int quantity, String reason,
                      LocalDateTime timestamp, LocalDateTime expiryDate) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive.");
        if (returnId == null || sku == null || reason == null || timestamp == null)
            throw new IllegalArgumentException("Invalid null field.");

        this.returnId = returnId;
        this.sku = sku;
        this.quantity = quantity;
        this.reason = reason;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
    }

    /** @return the unique identifier of the return */
    public String getReturnId() { return returnId; }

    /** @return the SKU of the returned item */
    public String getSku() { return sku; }

    /** @return the quantity of the returned item */
    public int getQuantity() { return quantity; }

    /** @return the reason for the return */
    public String getReason() { return reason; }

    /** @return the timestamp of the return */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** @return the item's expiry date, or null if not applicable */
    public LocalDateTime getExpiryDate() { return expiryDate; }

    /**
     * Compares two ReturnItems for sorting.
     * <p>
     * Descending order by timestamp (most recent first). If the timestamp is equal,
     * it sorts by ascending {@code returnId}.
     * </p>
     *
     * @param other the other ReturnItem to compare
     * @return comparison value
     */
    @Override
    public int compareTo(ReturnItem other) {
        int cmp = other.timestamp.compareTo(this.timestamp); // DESC
        if (cmp == 0) cmp = this.returnId.compareTo(other.returnId);
        return cmp;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] qty=%d reason=%s", returnId, sku, quantity, reason);
    }
}