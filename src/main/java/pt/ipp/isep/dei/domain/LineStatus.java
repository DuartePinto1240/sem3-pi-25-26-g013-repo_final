package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents the allocation status of a specific line within an order.
 * <p>
 * Each {@code LineStatus} contains information about the requested and allocated
 * quantities of a SKU, as well as its current status defined by {@link LineStatusEnum}.
 * </p>
 */
public class LineStatus {

    /** Identifier of the order this line belongs to. */
    private String orderId;

    /** Line number within the order. */
    private int lineNo;

    /** SKU (Stock Keeping Unit) of the item in this line. */
    private String sku;

    /** Quantity requested in this order line. */
    private int requestedQty;

    /** Quantity actually allocated to this order line. */
    private int allocatedQty;

    /** Current allocation status of this line. */
    private LineStatusEnum status;

    /**
     * Constructs a new {@code LineStatus} with the given details.
     *
     * @param orderId       the ID of the order
     * @param lineNo        the line number within the order
     * @param sku           the SKU of the item
     * @param requestedQty  the quantity requested for this line
     * @param allocatedQty  the quantity actually allocated
     * @param status        the current status of this line
     */
    public LineStatus(String orderId, int lineNo, String sku, int requestedQty, int allocatedQty, LineStatusEnum status) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.requestedQty = requestedQty;
        this.allocatedQty = allocatedQty;
        this.status = status;
    }

    /** @return the ID of the order this line belongs to */
    public String getOrderId() { return orderId; }

    /** @return the line number within the order */
    public int getLineNo() { return lineNo; }

    /** @return the SKU of the item in this line */
    public String getSku() { return sku; }

    /** @return the quantity requested for this line */
    public int getRequestedQty() { return requestedQty; }

    /** @return the quantity actually allocated to this line */
    public int getAllocatedQty() { return allocatedQty; }

    /** @return the current allocation status of this line */
    public LineStatusEnum getStatus() { return status; }
}

