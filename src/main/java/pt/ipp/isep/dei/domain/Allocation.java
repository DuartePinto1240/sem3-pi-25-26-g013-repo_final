package main.java.pt.ipp.isep.dei.domain;

public class Allocation {
    private String orderId;
    private int lineNo;
    private String sku;
    private double qty;
    private double weight;
    private String boxId;
    private int aisle;
    private int bay;

    // Construtor


    public Allocation(String orderId, int lineNo, String sku, double qty, double weight, String boxId, int aisle, int bay) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
        this.weight = weight;
        this.boxId = boxId;
        this.aisle = aisle;
        this.bay = bay;
    }

    // Métodos getters para acessar as propriedades privadas
    public String getOrderId() {
        return orderId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getSku() {
        return sku;
    }

    public double getQty() {
        return qty;
    }

    public double getWeight() {
        return weight;
    }

    public int getAisle() {
        return aisle;
    }

    public String getBoxId() {
        return boxId;
    }

    public int getBay() {
        return bay;
    }

    @java.lang.Override
    public java.lang.String toString() {
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



