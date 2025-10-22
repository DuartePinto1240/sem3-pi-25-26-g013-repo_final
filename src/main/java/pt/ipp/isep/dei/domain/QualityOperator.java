package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe responsável pelo processamento de itens devolvidos.
 * <p>
 * Um QualityOperator verifica os itens em quarentena e decide se devem ser
 * restocados no inventário ou descartados, registrando cada ação no AuditLogger.
 * </p>
 */
public class QualityOperator {

    /** Quarentena onde os itens devolvidos estão temporariamente armazenados. */
    private final Quarantine quarantine;

    /** Inventário principal do armazém onde caixas podem ser adicionadas. */
    private final Inventory inventory;

    /** Logger de auditoria para registrar ações de restock ou descarte. */
    private final AuditLogger logger;

    /**
     * Construtor do QualityOperator.
     *
     * @param quarantine quarentena contendo itens devolvidos
     * @param inventory inventário do armazém
     * @param logger logger para auditoria das ações
     */
    public QualityOperator(Quarantine quarantine, Inventory inventory, AuditLogger logger) {
        this.quarantine = quarantine;
        this.inventory = inventory;
        this.logger = logger;
    }

    /**
     * Processa todos os itens na quarentena.
     * <p>
     * Para cada item, decide se deve ser restocado ou descartado.
     * </p>
     */
    public void processReturns() {
        while (!quarantine.isEmpty()) {
            ReturnItem item = quarantine.next();
            if (shouldRestock(item)) restock(item);
            else discard(item);
        }
    }

    /**
     * Determina se um item deve ser restocado.
     *
     * @param item item devolvido a ser avaliado
     * @return true se o item deve ser restocado, false caso contrário
     */
    private boolean shouldRestock(ReturnItem item) {
        return item.getReason().equalsIgnoreCase("customer-remorse")
                || item.getReason().equalsIgnoreCase("cycle-count");
    }

    /**
     * Restoca um item devolvido no inventário.
     * <p>
     * Cria uma nova caixa para o item e adiciona no inventário, além de registrar a ação no logger.
     * </p>
     *
     * @param item item devolvido a ser restocado
     */
    private void restock(ReturnItem item) {
        LocalDate expiry = (item.getExpiryDate() != null)
                ? item.getExpiryDate().toLocalDate()
                : null;

        Box newBox = new Box("RET-" + item.getReturnId(),
                item.getSku(),
                item.getQuantity(),
                expiry,
                LocalDateTime.now());

        inventory.addBox(newBox);
        logger.log(String.format("returnId=%s | sku=%s | action=Restocked | qty=%d",
                item.getReturnId(), item.getSku(), item.getQuantity()));
    }

    /**
     * Descarta um item devolvido.
     * <p>
     * Apenas registra a ação de descarte no logger, sem alterar o inventário.
     * </p>
     *
     * @param item item devolvido a ser descartado
     */
    private void discard(ReturnItem item) {
        logger.log(String.format("returnId=%s | sku=%s | action=Discarded | qty=%d",
                item.getReturnId(), item.getSku(), item.getQuantity()));
    }
}

