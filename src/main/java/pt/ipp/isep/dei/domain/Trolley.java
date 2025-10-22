package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um carrinho (trolley) utilizado para transportar alocações ({@link Allocation})
 * durante o processo de picking no armazém.
 * <p>
 * Cada trolley possui uma capacidade máxima em peso e armazena um conjunto de alocações
 * cujo peso total não pode exceder essa capacidade.
 * </p>
 */
public class Trolley {

    /** Identificador único do trolley. */
    private final int id;

    /** Capacidade máxima do trolley, em quilogramas. */
    private final double capacity;

    /** Peso total atualmente utilizado no trolley. */
    private double usedWeight;

    /** Lista de alocações contidas neste trolley. */
    private final List<Allocation> allocations;

    /**
     * Constrói um novo {@code Trolley} com a capacidade especificada.
     *
     * @param id identificador único do trolley
     * @param capacity capacidade máxima do trolley (em quilogramas)
     */
    public Trolley(int id, double capacity) {
        this.id = id;
        this.capacity = capacity;
        this.usedWeight = 0;
        this.allocations = new ArrayList<>();
    }

    /**
     * Obtém o identificador do trolley.
     *
     * @return o ID do trolley
     */
    public int getId() {
        return id;
    }

    /**
     * Obtém a capacidade máxima do trolley.
     *
     * @return a capacidade total, em quilogramas
     */
    public double getCapacity() {
        return capacity;
    }

    /**
     * Obtém o peso atualmente utilizado no trolley.
     *
     * @return o peso total ocupado, em quilogramas
     */
    public double getUsedWeight() {
        return usedWeight;
    }

    /**
     * Retorna a lista de alocações atualmente armazenadas no trolley.
     *
     * @return lista de objetos {@link Allocation}
     */
    public List<Allocation> getAllocations() {
        return allocations;
    }

    /**
     * Verifica se o trolley tem capacidade suficiente para adicionar
     * uma nova alocação com o peso especificado.
     *
     * @param weight peso da nova alocação
     * @return {@code true} se o trolley puder suportar o peso adicional, {@code false} caso contrário
     */
    public boolean canFit(double weight) {
        return usedWeight + weight <= capacity;
    }

    /**
     * Adiciona uma nova alocação ao trolley e atualiza o peso total utilizado.
     *
     * @param alloc a alocação a ser adicionada
     */
    public void addAllocation(Allocation alloc) {
        allocations.add(alloc);
        usedWeight += alloc.getWeight();
    }

    /**
     * Calcula a taxa de utilização do trolley com base na capacidade total.
     *
     * @return a percentagem de utilização do trolley (0–100%)
     */
    public double getUtilization() {
        return (usedWeight / capacity) * 100;
    }
}
