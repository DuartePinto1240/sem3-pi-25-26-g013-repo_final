package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a storage box in a warehouse, associated with a specific product (SKU).
 *
 * <p>Each box contains a specific quantity of a product, and may or may not have
 * an expiration date (depending on whether it is perishable). The moment of reception
 * is recorded using {@code receivedAt}.</p>
 *
 * <p>This class implements {@link Comparable}, allowing boxes to be ordered
 * according to FEFO (First Expired, First Out) and FIFO (First In, First Out) principles:</p>
 * <ul>
 *   <li>Boxes with earlier expiration dates are prioritized (FEFO).</li>
 *   <li>Among non-perishable boxes, older boxes are prioritized (FIFO).</li>
 *   <li>Perishable boxes are always prioritized over non-perishable boxes.</li>
 * </ul>
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
     *
     * @param boxId      unique identifier of the box
     * @param sku        SKU code of the product
     * @param quantity   quantity of product units in the box
     * @param expiryDate expiration date (or {@code null} if the product is non-perishable)
     * @param receivedAt date and time the box was received
     */
    public Box(String boxId, String sku, int quantity, LocalDate expiryDate, LocalDateTime receivedAt) {
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    /** @return the unique box identifier. */
    public String getBoxId() {
        return boxId;
    }

    /** @return the SKU code of the product. */
    public String getSku() {
        return sku;
    }

    /** @return the quantity of units in the box. */
    public int getQuantity() {
        return quantity;
    }

    /** @return the expiration date, or {@code null} if the product is non-perishable. */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /** @return the date and time the box was received. */
    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    /**
     * Returns a string representation of the box, including all key attributes.
     *
     * @return string representation of the box
     */
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

    /**
     * Compares this box with another, using a combined FEFO (First Expired, First Out)
     * and FIFO (First In, First Out) strategy.
     *
     * <ul>
     *   <li>Boxes with earlier expiration dates are considered smaller.</li>
     *   <li>Perishable boxes are prioritized over non-perishable boxes.</li>
     *   <li>If both boxes are non-perishable or have the same expiration date,
     *       the older (received first) box is prioritized.</li>
     * </ul>
     *
     * @param other the other box to compare with
     * @return negative if this box has higher priority, zero if equal, positive if the other box has higher priority
     */
    @Override
    public int compareTo(Box other) {
        // FEFO: perishable products with earlier expiry dates have higher priority
        if (this.expiryDate != null && other.expiryDate != null) {
            int cmp = this.expiryDate.compareTo(other.expiryDate);
            if (cmp != 0) return cmp;
        }
        // Perishable boxes before non-perishable
        else if (this.expiryDate != null) {
            return -1; // this box is perishable → priority
        } else if (other.expiryDate != null) {
            return 1;  // other box is perishable → priority
        }

        // FIFO fallback when both have same expiry or are non-perishable
        return this.receivedAt.compareTo(other.receivedAt);
    }

}
