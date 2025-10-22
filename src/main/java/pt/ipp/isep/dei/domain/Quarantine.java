package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Representa a quarentena de itens devolvidos.
 * <p>
 * Itens devolvidos são armazenados temporariamente nesta estrutura antes de serem
 * processados pelo {@link QualityOperator}. Os itens são processados em ordem
 * de chegada (LIFO - last in, first out) por padrão.
 * </p>
 */
public class Quarantine {

    /** Pilha interna que mantém os itens devolvidos. */
    private final Deque<ReturnItem> stack = new ArrayDeque<>();

    /**
     * Adiciona um item à quarentena.
     *
     * @param item item devolvido a ser adicionado
     */
    public void add(ReturnItem item) {
        stack.push(item);
    }

    /**
     * Adiciona uma lista de itens à quarentena.
     * <p>
     * Os itens são ordenados por timestamp decrescente antes de serem adicionados,
     * garantindo que os mais recentes sejam processados primeiro.
     * </p>
     *
     * @param items lista de itens a adicionar
     */
    public void addAll(List<ReturnItem> items) {
        items.stream()
                .sorted() // by timestamp DESC
                .forEach(this::add);
    }

    /**
     * Remove e retorna o próximo item da quarentena.
     *
     * @return item devolvido mais recente, ou null se estiver vazia
     */
    public ReturnItem next() {
        return stack.poll();
    }

    /**
     * Verifica se a quarentena está vazia.
     *
     * @return true se não houver itens, false caso contrário
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Retorna o número de itens atualmente na quarentena.
     *
     * @return quantidade de itens na quarentena
     */
    public int size() {
        return stack.size();
    }
}

