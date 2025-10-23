package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a single allocation line of a product (SKU) within an order.
 * <p>
 * Each line indicates the quantity of a SKU taken from a specific box,
 * along with its physical location in the warehouse (aisle and bay).
 * </p>
 */
public class AllocationRow {

    /** Identifier of the associated order. */
    private String orderId;

    /** Line number within the order. */
    private int lineNo;

    /** Product SKU code. */
    private String sku;

    /** Quantity taken from this box. */
    private int qty;

    /** Identifier of the box from which the product was taken. */
    private String boxId;

    /** Aisle number where the box is located. */
    private int aisle;

    /** Bay number within the aisle. */
    private int bay;

    /**
     * Constructs a new {@code AllocationRow} instance.
     *
     * @param orderId identifier of the order
     * @param lineNo  line number within the order
     * @param sku     product SKU code
     * @param qty     quantity taken from this box
     * @param boxId   identifier of the box
     * @param aisle   aisle number where the box is located
     * @param bay     bay number within the aisle
     */
    public AllocationRow(String orderId, int lineNo, String sku, int qty, String boxId, int aisle, int bay) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
        this.boxId = boxId;
        this.aisle = aisle;
        this.bay = bay;
    }

    /** @return the identifier of the associated order. */
    public String getOrderId() { return orderId; }

    /** @return the line number within the order. */
    public int getLineNo() { return lineNo; }

    /** @return the product SKU code. */
    public String getSku() { return sku; }

    /** @return the quantity taken from this box. */
    public int getQty() { return qty; }

    /** @return the identifier of the box. */
    public String getBoxId() { return boxId; }

    /** @return the aisle number where the box is located. */
    public int getAisle() { return aisle; }

    /** @return the bay number within the aisle. */
    public int getBay() { return bay; }
}
