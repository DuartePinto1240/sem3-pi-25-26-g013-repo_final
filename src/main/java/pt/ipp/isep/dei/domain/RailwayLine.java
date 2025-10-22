package main.java.pt.ipp.isep.dei.domain;

import java.util.Collections;
import java.util.List;

/**
 * Representa uma linha ferroviária composta por vários segmentos consecutivos.
 * <p>
 * Uma {@code RailwayLine} possui uma estação de início, uma estação final
 * e uma lista de {@link LineSegment} que descrevem o percurso.
 * </p>
 */
public class RailwayLine {

    /** Estação de início da linha ferroviária. */
    private final Station startStation;

    /** Estação final da linha ferroviária. */
    private final Station endStation;

    /** Lista de segmentos que compõem a linha ferroviária. */
    private final List<LineSegment> segments;

    /**
     * Cria uma RailwayLine a partir de uma lista de segmentos consecutivos.
     *
     * @param segments lista de {@link LineSegment}, não pode ser null ou vazia
     * @throws IllegalArgumentException se a lista for null ou vazia
     */
    public RailwayLine(List<LineSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("A railway line must have at least one segment.");
        }
        this.segments = segments;
        this.startStation = segments.get(0).getOrigin();
        this.endStation = segments.get(segments.size() - 1).getDestination();
    }

    /**
     * Retorna a estação de início da linha ferroviária.
     *
     * @return estação inicial
     */
    public Station getStartStation() {
        return startStation;
    }

    /**
     * Retorna a estação final da linha ferroviária.
     *
     * @return estação final
     */
    public Station getEndStation() {
        return endStation;
    }

    /**
     * Retorna a lista de segmentos que compõem a linha ferroviária.
     * <p>
     * A lista retornada é imutável.
     * </p>
     *
     * @return lista imutável de {@link LineSegment}
     */
    public List<LineSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }
}
