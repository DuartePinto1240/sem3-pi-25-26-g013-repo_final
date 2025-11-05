package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents an allocation of a product (SKU) to a specific warehouse location,
 * associated with a specific order line.
 * <p>
 * Contains information about physical location (aisle and bay),
 * total weight, quantity, and the box identifier where the product is stored.
 * </p>
 */
public class Allocation {

    /** Identifier of the associated order. */
    private String orderId;

    /** Line number within the order. */
    private int lineNo;

    /** Unique product identifier (SKU). */
    private String sku;

    /** Quantity of units of this SKU allocated. */
    private int qty;

    /** Total weight (in kilograms) of this allocation. */
    private double weight;

    /** Identifier of the box where the SKU is stored. */
    private String boxId;

    /** Aisle number where the SKU is located. */
    private int aisle;

    /** Bay number within the aisle. */
    private int bay;

    /**
     * Constructs a new {@code Allocation} instance.
     *
     * @param orderId Identifier of the order
     * @param lineNo  Line number within the order
     * @param sku     Product SKU code
     * @param qty     Quantity of units
     * @param weight  Total weight of the allocation (in kg)
     * @param boxId   Box identifier
     * @param aisle   Aisle where the product is located
     * @param bay     Bay within the aisle
     */
    public Allocation(String orderId, int lineNo, String sku, int qty, double weight, String boxId, int aisle, int bay) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
        this.weight = weight;
        this.boxId = boxId;
        this.aisle = aisle;
        this.bay = bay;
    }

    /** @return the identifier of the associated order. */
    public String getOrderId() {
        return orderId;
    }

    /** @return the line number within the order. */
    public int getLineNo() {
        return lineNo;
    }

    /** @return the product SKU code. */
    public String getSku() {
        return sku;
    }

    /** @return the quantity of units allocated. */
    public int getQty() {
        return qty;
    }

    /** @return the total weight of the allocation (in kg). */
    public double getWeight() {
        return weight;
    }

    /** @return the aisle number where the product is located. */
    public int getAisle() {
        return aisle;
    }

    /** @return the identifier of the box where the product is stored. */
    public String getBoxId() {
        return boxId;
    }

    /** @return the bay number within the aisle. */
    public int getBay() {
        return bay;
    }

    /**
     * Sets a new quantity for this allocation.
     *
     * @param qty new quantity to assign
     */
    public void setQty(int qty) {
        this.qty = qty;
    }

    /**
     * Returns a textual representation of the allocation,
     * including its main attributes.
     *
     * @return human-readable string with allocation details
     */
    @Override
    public String toString() {
        return "Allocation{" +
                "orderId='" + orderId + '\'' +
                ", lineNo=" + lineNo +
                ", sku='" + sku + '\'' +
                ", qty=" + qty +
                ", weight=" + weight +
                ", boxId='" + boxId + '\'' +
                ", aisle=" + aisle +
                ", bay=" + bay +
                '}';
    }
}
