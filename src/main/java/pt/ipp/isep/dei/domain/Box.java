package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Box implements Comparable<Box> {
    private String boxId;
    private String sku;
    private int quantity;
    private LocalDate expiryDate; // null se não perecível
    private LocalDateTime receivedAt;

    // Construtor
    public Box(String boxId, String sku, int quantity, LocalDate expiryDate, LocalDateTime receivedAt) {
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    // Getters e setters
    public String getBoxId() {
        return boxId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    @Override
    public String toString() {
        return "Box{" +
                "boxId='" + boxId + '\'' +
                ", sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", receivedAt=" + receivedAt +
                '}';
    }

    @Override
    public int compareTo(Box other) {
        // --- FEFO: perishable products ordered by expiry date (earliest first)
        if (this.expiryDate != null && other.expiryDate != null) {
            int cmp = this.expiryDate.compareTo(other.expiryDate);
            if (cmp != 0) return cmp;
        }
        // --- Mixed: perishable before non-perishable
        else if (this.expiryDate != null) {
            return -1; // this one has expiry → goes first
        } else if (other.expiryDate != null) {
            return 1;  // other has expiry → goes first
        }

        // --- FIFO: fallback for equal expiry or both non-perishable
        return this.receivedAt.compareTo(other.receivedAt);
    }

}


