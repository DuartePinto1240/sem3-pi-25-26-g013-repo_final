package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

/**
 * Representa um percurso de picking dentro do armazém.
 * <p>
 * Contém a sequência de paragens (PickStops) e a distância total do percurso.
 * </p>
 */
public class PickPath {

    /** Sequência de paragens do percurso de picking. */
    private List<PickStop> sequence;

    /** Distância total do percurso (em unidades compatíveis com a métrica de distância usada). */
    private double totalDistance;

    /**
     * Construtor.
     *
     * @param sequence      lista de PickStops representando a sequência do percurso
     * @param totalDistance distância total do percurso
     */
    public PickPath(List<PickStop> sequence, double totalDistance) {
        this.sequence = sequence;
        this.totalDistance = totalDistance;
    }

    /**
     * Retorna a sequência de PickStops.
     *
     * @return lista de PickStops
     */
    public List<PickStop> getSequence() {
        return sequence;
    }

    /**
     * Retorna a distância total do percurso.
     *
     * @return distância total
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Retorna uma representação textual do percurso, incluindo cada PickStop e a distância total.
     *
     * @return string descritiva do percurso
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PickStop s : sequence)
            sb.append(" -> ").append(s);
        sb.append("\nTotal distance: ").append(totalDistance);
        return sb.toString();
    }
}

