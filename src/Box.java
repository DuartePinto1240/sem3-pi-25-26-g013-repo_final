import java.time.LocalDate;
import java.time.LocalDateTime;

public class Box {
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
}


