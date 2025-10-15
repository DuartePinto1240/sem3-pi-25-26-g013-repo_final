package main.java.pt.ipp.isep.dei.domain;

public class OrderLine {
    private int lineNo;
    private String sku;
    private int qtyRequested;

    public OrderLine(int lineNo, String sku, int qtyRequested) {
        this.lineNo = lineNo;
        this.sku = sku;
        this.qtyRequested = qtyRequested;
    }

    public int getLineNo() { return lineNo; }
    public String getSku() { return sku; }
    public int getQtyRequested() { return qtyRequested; }
}

