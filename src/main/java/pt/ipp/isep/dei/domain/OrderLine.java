package main.java.pt.ipp.isep.dei.domain;

/**
 * Represents a single line in an order.
 * <p>
 * Each line specifies a SKU (product identifier) and the quantity requested for that SKU.
 * </p>
 */
public class OrderLine {

    /** Line number within the order. */
    private int lineNo;

    /** SKU of the product requested in this line. */
    private String sku;

    /** Quantity requested for this SKU in this line. */
    private int qtyRequested;

    /**
     * Constructs a new order line.
     *
     * @param lineNo       the line number within the order
     * @param sku          the SKU of the product
     * @param qtyRequested the quantity requested
     */
    public OrderLine(int lineNo, String sku, int qtyRequested) {
        this.lineNo = lineNo;
        this.sku = sku;
        this.qtyRequested = qtyRequested;
    }

    /**
     * Gets the line number of this order line.
     *
     * @return the line number
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * Gets the SKU of the product requested in this line.
     *
     * @return the SKU
     */
    public String getSku() {
        return sku;
    }

    /**
     * Gets the quantity requested for this line.
     *
     * @return the quantity requested
     */
    public int getQtyRequested() {
        return qtyRequested;
    }
}


