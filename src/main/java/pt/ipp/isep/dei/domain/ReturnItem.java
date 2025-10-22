package main.java.pt.ipp.isep.dei.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReturnItem implements Comparable<ReturnItem> {

    private final String returnId;
    private final String sku;
    private final int quantity;
    private final String reason;
    private final LocalDateTime timestamp;
    private final LocalDateTime expiryDate;

    public ReturnItem(String returnId, String sku, int quantity, String reason,
                      LocalDateTime timestamp, LocalDateTime expiryDate) {
        if (returnId == null || sku == null || reason == null || timestamp == null)
            throw new IllegalArgumentException("Invalid null field.");

        this.returnId = returnId;
        this.sku = sku;
        this.quantity = quantity;
        this.reason = reason;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
    }

    public String getReturnId() { return returnId; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public LocalDateTime getExpiryDate() { return expiryDate; }

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
