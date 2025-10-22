package main.java.pt.ipp.isep.dei.domain;

/**
 * Representa uma linha de alocação de um produto (SKU) dentro de uma encomenda.
 * Cada linha indica a quantidade de um SKU retirada de uma caixa específica,
 * bem como a sua localização física no armazém (corredor e baía).
 */
public class AllocationRow {

    /** Identificador da encomenda associada. */
    private String orderId;

    /** Número da linha da encomenda. */
    private int lineNo;

    /** Código do produto (SKU). */
    private String sku;

    /** Quantidade retirada desta caixa. */
    private int qty;

    /** Identificador da caixa de onde o produto foi retirado. */
    private String boxId;

    /** Número do corredor (aisle) onde a caixa está localizada. */
    private int aisle;

    /** Número da baía (bay) dentro do corredor. */
    private int bay;

    /**
     * Constrói uma nova instância de {@code AllocationRow}.
     *
     * @param orderId identificador da encomenda
     * @param lineNo  número da linha dentro da encomenda
     * @param sku     código do produto (SKU)
     * @param qty     quantidade retirada desta caixa
     * @param boxId   identificador da caixa
     * @param aisle   número do corredor onde a caixa se encontra
     * @param bay     número da baía dentro do corredor
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

    /** @return o identificador da encomenda associada. */
    public String getOrderId() { return orderId; }

    /** @return o número da linha da encomenda. */
    public int getLineNo() { return lineNo; }

    /** @return o código do produto (SKU). */
    public String getSku() { return sku; }

    /** @return a quantidade retirada desta caixa. */
    public int getQty() { return qty; }

    /** @return o identificador da caixa. */
    public String getBoxId() { return boxId; }

    /** @return o número do corredor onde a caixa se encontra. */
    public int getAisle() { return aisle; }

    /** @return o número da baía dentro do corredor. */
    public int getBay() { return bay; }
}


