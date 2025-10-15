package main.java.pt.ipp.isep.dei.domain;

public class LineStatus {
    private String orderId;
    private int lineNo;
    private String sku;
    private int requestedQty;
    private int allocatedQty;
    private LineStatusEnum status;

    public LineStatus(String orderId, int lineNo, String sku, int requestedQty, int allocatedQty, LineStatusEnum status) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.requestedQty = requestedQty;
        this.allocatedQty = allocatedQty;
        this.status = status;
    }

    // getters
    public String getOrderId() { return orderId; }
    public int getLineNo() { return lineNo; }
    public String getSku() { return sku; }
    public int getRequestedQty() { return requestedQty; }
    public int getAllocatedQty() { return allocatedQty; }
    public LineStatusEnum getStatus() { return status; }
}
