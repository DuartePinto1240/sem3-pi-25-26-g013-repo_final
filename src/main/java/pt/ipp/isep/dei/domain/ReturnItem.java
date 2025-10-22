package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDateTime;

/**
 * Representa um item retornado ao armazém.
 * <p>
 * Cada {@code ReturnItem} possui um identificador único, SKU, quantidade,
 * motivo do retorno, timestamp do retorno e, opcionalmente, uma data de validade.
 * </p>
 * <p>
 * Esta classe implementa {@link Comparable} para ordenar itens por timestamp
 * decrescente (mais recentes primeiro) e, em caso de empate, pelo {@code returnId}.
 * </p>
 */
public class ReturnItem implements Comparable<ReturnItem> {

    /** Identificador único do retorno. */
    private final String returnId;

    /** SKU do item retornado. */
    private final String sku;

    /** Quantidade do item retornado. Deve ser positiva. */
    private final int quantity;

    /** Motivo do retorno (ex.: "customer-remorse"). */
    private final String reason;

    /** Timestamp do retorno. */
    private final LocalDateTime timestamp;

    /** Data de validade do item, se aplicável; pode ser null. */
    private final LocalDateTime expiryDate;

    /**
     * Constrói um ReturnItem.
     *
     * @param returnId identificador único do retorno, não pode ser null
     * @param sku SKU do item, não pode ser null
     * @param quantity quantidade do item, deve ser positiva
     * @param reason motivo do retorno, não pode ser null
     * @param timestamp data e hora do retorno, não pode ser null
     * @param expiryDate data de validade do item retornado, pode ser null
     * @throws IllegalArgumentException se quantity ≤ 0 ou algum campo obrigatório for null
     */
    public ReturnItem(String returnId, String sku, int quantity, String reason,
                      LocalDateTime timestamp, LocalDateTime expiryDate) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive.");
        if (returnId == null || sku == null || reason == null || timestamp == null)
            throw new IllegalArgumentException("Invalid null field.");

        this.returnId = returnId;
        this.sku = sku;
        this.quantity = quantity;
        this.reason = reason;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
    }

    /** @return identificador único do retorno */
    public String getReturnId() { return returnId; }

    /** @return SKU do item retornado */
    public String getSku() { return sku; }

    /** @return quantidade do item retornado */
    public int getQuantity() { return quantity; }

    /** @return motivo do retorno */
    public String getReason() { return reason; }

    /** @return timestamp do retorno */
    public LocalDateTime getTimestamp() { return timestamp; }

    /** @return data de validade do item, ou null se não aplicável */
    public LocalDateTime getExpiryDate() { return expiryDate; }

    /**
     * Compara dois ReturnItems para ordenação.
     * <p>
     * Ordem decrescente por timestamp (mais recentes primeiro). Se o timestamp for igual,
     * ordena por {@code returnId} ascendente.
     * </p>
     *
     * @param other outro ReturnItem a comparar
     * @return valor de comparação
     */
    @Override
    public int compareTo(ReturnItem other) {
        int cmp = other.timestamp.compareTo(this.timestamp); // DESC
        if (cmp == 0) cmp = this.returnId.compareTo(other.returnId);
        return cmp;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] qty=%d reason=%s", returnId, sku, quantity, reason);
    }
}
