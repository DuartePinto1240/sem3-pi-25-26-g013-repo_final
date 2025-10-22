package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa uma caixa (Box) armazenada num armazém, associada a um determinado produto (SKU).
 *
 * <p>Cada caixa contém uma quantidade específica do produto, podendo ou não ter uma
 * data de validade (dependendo de ser perecível ou não). O momento de receção
 * é registado através de {@code receivedAt}.</p>
 *
 * <p>Implementa a interface {@link Comparable}, permitindo ordenar caixas com base
 * nos princípios de FEFO (First Expired, First Out) e FIFO (First In, First Out):</p>
 * <ul>
 *   <li>Caixas com data de validade anterior são priorizadas (FEFO).</li>
 *   <li>Entre caixas sem validade (não perecíveis), as mais antigas são priorizadas (FIFO).</li>
 *   <li>Caixas perecíveis são sempre priorizadas em relação às não perecíveis.</li>
 * </ul>
 */
public class Box implements Comparable<Box> {

    /** Identificador único da caixa. */
    private String boxId;

    /** Código SKU (Stock Keeping Unit) do produto armazenado. */
    private String sku;

    /** Quantidade de unidades do produto dentro da caixa. */
    private int quantity;

    /** Data de validade do produto, ou {@code null} se não for perecível. */
    private LocalDate expiryDate;

    /** Data e hora em que a caixa foi recebida no armazém. */
    private LocalDateTime receivedAt;

    /**
     * Constrói uma nova instância de {@code Box}.
     *
     * @param boxId        identificador único da caixa
     * @param sku          código SKU do produto
     * @param quantity     quantidade de unidades do produto
     * @param expiryDate   data de validade (ou {@code null} se o produto não for perecível)
     * @param receivedAt   data e hora de receção da caixa
     */
    public Box(String boxId, String sku, int quantity, LocalDate expiryDate, LocalDateTime receivedAt) {
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    /**
     * Obtém o identificador da caixa.
     *
     * @return o ID da caixa
     */
    public String getBoxId() {
        return boxId;
    }

    /**
     * Obtém o código SKU do produto.
     *
     * @return o código SKU
     */
    public String getSku() {
        return sku;
    }

    /**
     * Obtém a quantidade de unidades do produto na caixa.
     *
     * @return a quantidade
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Obtém a data de validade da caixa.
     *
     * @return a data de validade, ou {@code null} se o produto não for perecível
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Obtém a data e hora em que a caixa foi recebida.
     *
     * @return a data e hora de receção
     */
    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    /**
     * Retorna uma representação textual da caixa, incluindo todos os seus atributos principais.
     *
     * @return uma string representando a caixa
     */
    @Override
    public String toString() {
        return "Box{" +
                "boxId='" + boxId + '\'' +
                ", sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", receivedAt=" + receivedAt +
                '}';
    }

    /**
     * Compara esta caixa com outra, seguindo a lógica combinada de FEFO (First Expired, First Out)
     * e FIFO (First In, First Out).
     *
     * <ul>
     *   <li>Caixas com data de validade mais próxima são consideradas menores.</li>
     *   <li>Caixas perecíveis são priorizadas em relação às não perecíveis.</li>
     *   <li>Se ambas forem não perecíveis ou tiverem a mesma validade, a mais antiga (recebida antes) é priorizada.</li>
     * </ul>
     *
     * @param other a outra caixa a comparar
     * @return um número negativo se esta caixa tiver prioridade,
     *         zero se forem equivalentes,
     *         ou positivo se a outra tiver prioridade
     */
    @Override
    public int compareTo(Box other) {
        // --- FEFO: produtos perecíveis com validade mais próxima são prioritários
        if (this.expiryDate != null && other.expiryDate != null) {
            int cmp = this.expiryDate.compareTo(other.expiryDate);
            if (cmp != 0) return cmp;
        }
        // --- Produtos perecíveis antes dos não perecíveis
        else if (this.expiryDate != null) {
            return -1; // esta caixa tem validade → prioridade
        } else if (other.expiryDate != null) {
            return 1;  // a outra tem validade → prioridade
        }

        // --- FIFO: fallback quando ambas têm mesma validade ou não são perecíveis
        return this.receivedAt.compareTo(other.receivedAt);
    }
}



