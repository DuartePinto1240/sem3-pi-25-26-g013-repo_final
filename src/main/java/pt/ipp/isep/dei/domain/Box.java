package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a storage box in a warehouse, associated with a specific product (SKU).
 * Implements Comparable for ordering based on FEFO (Expiry Date), FIFO (Received At),
 * and finally Box ID.
 */
public class Box implements Comparable<Box> {

    /** Unique identifier of the box. */
    private String boxId;

    /** SKU (Stock Keeping Unit) code of the stored product. */
    private String sku;

    /** Quantity of product units inside the box. */
    private int quantity;

    /** Expiration date of the product, or {@code null} if not perishable. */
    private LocalDate expiryDate;

    /** Date and time when the box was received in the warehouse. */
    private LocalDateTime receivedAt;

    /**
     * Constructs a new {@code Box} instance.
     */
    public Box(String boxId, String sku, int quantity, LocalDate expiryDate, LocalDateTime receivedAt) {
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    // --- Getters ---

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

    // --- Ordenação FEFO/FIFO/Box ID ---

    /**
     * Compares this box with another, using a combined FEFO (First Expired, First Out)
     * and FIFO (First In, First Out) strategy, with boxId as the final tie-breaker.
     *
     * @param other the other box to compare with
     * @return negative if this box has higher priority, zero if equal, positive if the other box has higher priority
     */
    @Override
    public int compareTo(Box other) {
        // 1. FEFO: Comparar datas de validade (expiryDate)

        // A. Comparação entre dois perecíveis com datas diferentes. (Mais Antiga Primeiro)
        if (this.expiryDate != null && other.expiryDate != null) {
            int cmp = this.expiryDate.compareTo(other.expiryDate);
            if (cmp != 0) {
                return cmp;
            }
        }
        // B. Priorizar Perecíveis (não null) sobre Não-Perecíveis (null)
        else if (this.expiryDate != null) {
            return -1; // "This" (perecível) tem prioridade
        }
        else if (other.expiryDate != null) {
            return 1;  // "Other" (perecível) tem prioridade
        }
        // Se chegarmos aqui, ambas têm o mesmo status de validade (ambas null ou ambas com a mesma data)

        // 2. FIFO: Comparar datas de receção (receivedAt)
        int receivedCmp = this.receivedAt.compareTo(other.receivedAt);
        if (receivedCmp != 0) {
            return receivedCmp; // Mais Antiga (recebida primeiro) tem prioridade
        }

        // 3. Desempate Final: Comparar boxId (Garante ordem estável e determinística)
        return this.boxId.compareTo(other.boxId);
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
}