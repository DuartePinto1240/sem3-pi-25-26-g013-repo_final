package main.java.pt.ipp.isep.dei.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa um armazém que contém múltiplos {@link Bay}.
 * <p>
 * Cada {@code Warehouse} possui um identificador único e mantém um mapa de bays,
 * onde cada bay é identificado pela combinação de ID do armazém, corredor (aisle) e bay number.
 * </p>
 */
public class Warehouse {

    /** Identificador único do armazém. */
    private String id;

    /** Mapa de bays, com chave no formato "warehouseId-aisle-bay". */
    private Map<String, Bay> bays;

    /**
     * Constrói um armazém vazio com o ID fornecido.
     *
     * @param id identificador único do armazém
     */
    public Warehouse(String id) {
        this.id = id;
        this.bays = new HashMap<>();
    }

    /**
     * Adiciona um bay ao armazém.
     * <p>
     * A chave usada no mapa interno é construída concatenando o ID do armazém, corredor e número do bay.
     * </p>
     *
     * @param bay bay a ser adicionado ao armazém
     */
    public void addBay(Bay bay) {
        String key = bay.getWarehouseId() + "-" + bay.getAisle() + "-" + bay.getBay();
        bays.put(key, bay);
    }

    /**
     * Obtém um bay específico do armazém.
     *
     * @param warehouseId ID do armazém
     * @param aisle número do corredor
     * @param bay número do bay
     * @return o {@link Bay} correspondente, ou {@code null} se não existir
     */
    public Bay getBay(String warehouseId, int aisle, int bay) {
        String key = warehouseId + "-" + aisle + "-" + bay;
        return bays.get(key);
    }

    /**
     * Retorna todos os bays do armazém.
     *
     * @return mapa imutável de bays
     */
    public Map<String, Bay> getBays() {
        return bays;
    }

    /**
     * Representação textual do armazém, incluindo seu ID e os bays que contém.
     *
     * @return string no formato {@code Warehouse{id='ID', bays={...}}}
     */
    @Override
    public String toString() {
        return "main.java.pt.ipp.isep.dei.domain.Warehouse{" +
                "id='" + id + '\'' +
                ", bays=" + bays +
                '}';
    }
}




