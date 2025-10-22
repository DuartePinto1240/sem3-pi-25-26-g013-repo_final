package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um vagão que transporta várias caixas ({@link Box}).
 * <p>
 * Cada {@code Wagon} possui um identificador único e mantém uma lista de caixas.
 * </p>
 */
public class Wagon {

    /** Identificador único do vagão. */
    private String wagonId;

    /** Lista de caixas contidas neste vagão. */
    private List<Box> boxes;

    /**
     * Constrói um {@code Wagon} vazio com um identificador específico.
     *
     * @param wagonId identificador único do vagão
     */
    public Wagon(String wagonId) {
        this.wagonId = wagonId;
        this.boxes = new ArrayList<>();
    }

    /**
     * Constrói um {@code Wagon} com um identificador e uma lista de caixas inicial.
     *
     * @param wagonId identificador único do vagão
     * @param boxes lista de caixas a serem inicialmente adicionadas ao vagão
     */
    public Wagon(String wagonId, List<Box> boxes) {
        this.wagonId = wagonId;
        this.boxes = new ArrayList<>(boxes);
    }

    /**
     * Adiciona uma nova caixa ao vagão.
     *
     * @param box a caixa a ser adicionada
     */
    public void addBox(Box box) {
        boxes.add(box);
    }

    /**
     * Obtém o identificador do vagão.
     *
     * @return o ID do vagão
     */
    public String getWagonId() {
        return wagonId;
    }

    /**
     * Retorna a lista de caixas atualmente contidas no vagão.
     *
     * @return lista de objetos {@link Box}
     */
    public List<Box> getBoxes() {
        return boxes;
    }

    /**
     * Representação textual do vagão, incluindo seu ID e a quantidade de caixas.
     *
     * @return string no formato {@code Wagon{wagonId='ID', boxes=N}}
     */
    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId='" + wagonId + '\'' +
                ", boxes=" + boxes.size() +
                '}';
    }
}




