package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um "Bay" (local de armazenamento) dentro de um armazém.
 * <p>
 * Cada bay pertence a um warehouse, tem um corredor (aisle), um número de bay,
 * uma capacidade máxima de caixas, e mantém uma lista das caixas armazenadas.
 * </p>
 */
public class Bay {

    private String warehouseId;
    private int aisle;
    private int bay;
    private int capacityBoxes;
    private List<Box> storedBoxes;

    /**
     * Construtor de um Bay.
     *
     * @param warehouseId ID do armazém ao qual este bay pertence
     * @param aisle número do corredor
     * @param bay número do bay dentro do corredor
     * @param capacityBoxes capacidade máxima de caixas que este bay pode armazenar
     */
    public Bay(String warehouseId, int aisle, int bay, int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bay = bay;
        this.capacityBoxes = capacityBoxes;
        this.storedBoxes = new ArrayList<>();
    }

    /**
     * Retorna o ID do armazém ao qual este bay pertence.
     *
     * @return ID do armazém
     */
    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Retorna o número do corredor (aisle) do bay.
     *
     * @return número do corredor
     */
    public int getAisle() {
        return aisle;
    }

    /**
     * Retorna o número do bay dentro do corredor.
     *
     * @return número do bay
     */
    public int getBay() {
        return bay;
    }

    /**
     * Retorna a capacidade máxima de caixas deste bay.
     *
     * @return capacidade máxima de caixas
     */
    public int getCapacityBoxes() {
        return capacityBoxes;
    }

    /**
     * Verifica se é possível armazenar uma nova caixa neste bay.
     *
     * @param box caixa a ser armazenada
     * @return {@code true} se houver espaço disponível, {@code false} caso contrário
     */
    public boolean canStoreBox(Box box) {
        return storedBoxes.size() < capacityBoxes;
    }

    /**
     * Adiciona uma caixa ao bay, caso haja espaço disponível.
     *
     * @param box caixa a adicionar
     */
    public void addBox(Box box) {
        if (canStoreBox(box)) {
            storedBoxes.add(box);
        }
    }

    /**
     * Retorna a lista de caixas armazenadas neste bay.
     *
     * @return lista de {@link Box} armazenadas
     */
    public List<Box> getStoredBoxes() {
        return storedBoxes;
    }

    @Override
    public String toString() {
        return "main.java.pt.ipp.isep.dei.domain.Bay{" +
                "warehouseId='" + warehouseId + '\'' +
                ", aisle=" + aisle +
                ", bay=" + bay +
                ", capacityBoxes=" + capacityBoxes +
                '}';
    }
}




