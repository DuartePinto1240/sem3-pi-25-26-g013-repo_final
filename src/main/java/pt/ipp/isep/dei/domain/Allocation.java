package main.java.pt.ipp.isep.dei.domain;

/**
 * Representa uma alocação de um artigo (SKU) a um local específico do armazém,
 * associada a uma determinada linha de encomenda.
 * Contém informação sobre a localização física (corredor e baía),
 * o peso, a quantidade e o identificador da caixa onde o artigo está armazenado.
 */
public class Allocation {

    /** Identificador da encomenda associada. */
    private String orderId;

    /** Número da linha dentro da encomenda. */
    private int lineNo;

    /** Identificador único do produto (SKU). */
    private String sku;

    /** Quantidade de unidades deste SKU alocadas. */
    private int qty;

    /** Peso total (em quilogramas) desta alocação. */
    private double weight;

    /** Identificador da caixa onde o SKU está armazenado. */
    private String boxId;

    /** Número do corredor (aisle) onde o SKU está localizado. */
    private int aisle;

    /** Número da baía (bay) dentro do corredor. */
    private int bay;

    /**
     * Constrói uma nova instância de {@code Allocation}.
     *
     * @param orderId Identificador da encomenda
     * @param lineNo  Número da linha dentro da encomenda
     * @param sku     Código do produto (SKU)
     * @param qty     Quantidade de unidades
     * @param weight  Peso total da alocação (em kg)
     * @param boxId   Identificador da caixa
     * @param aisle   Corredor onde o produto está localizado
     * @param bay     Baía dentro do corredor
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

    /** @return o identificador da encomenda associada. */
    public String getOrderId() {
        return orderId;
    }

    /** @return o número da linha da encomenda. */
    public int getLineNo() {
        return lineNo;
    }

    /** @return o código do produto (SKU). */
    public String getSku() {
        return sku;
    }

    /** @return a quantidade de unidades deste produto. */
    public int getQty() {
        return qty;
    }

    /** @return o peso total da alocação (em kg). */
    public double getWeight() {
        return weight;
    }

    /** @return o número do corredor (aisle). */
    public int getAisle() {
        return aisle;
    }

    /** @return o identificador da caixa onde o produto está armazenado. */
    public String getBoxId() {
        return boxId;
    }

    /** @return o número da baía (bay) dentro do corredor. */
    public int getBay() {
        return bay;
    }

    /**
     * Define uma nova quantidade para esta alocação.
     *
     * @param qty nova quantidade a atribuir.
     */
    public void setQty(int qty) {
        this.qty = qty;
    }

    /**
     * Retorna uma representação textual da alocação,
     * contendo os principais atributos.
     *
     * @return String legível com os detalhes da alocação.
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




