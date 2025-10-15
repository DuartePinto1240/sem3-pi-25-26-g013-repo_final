package main.java.pt.ipp.isep.dei.domain;

public class AllocationRow {
    private String orderId;
    private int lineNo;
    private String sku;
    private int qty; // quantidade retirada deste box
    private String boxId;
    private int aisle;
    private int bay;

    public AllocationRow(String orderId, int lineNo, String sku, int qty, String boxId, int aisle, int bay) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
        this.boxId = boxId;
        this.aisle = aisle;
        this.bay = bay;
    }

    // getters
    public String getOrderId() { return orderId; }
    public int getLineNo() { return lineNo; }
    public String getSku() { return sku; }
    public int getQty() { return qty; }
    public String getBoxId() { return boxId; }
    public int getAisle() { return aisle; }
    public int getBay() { return bay; }
}

