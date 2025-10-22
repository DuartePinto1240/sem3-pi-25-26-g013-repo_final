package main.java.pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Planeador de percurso de picking dentro do armazém.
 * <p>
 * Fornece duas estratégias para gerar uma sequência de PickStops:
 * </p>
 * <ul>
 *     <li><b>Deterministic Sweep:</b> ordena por corredor (aisle) e depois por bay.</li>
 *     <li><b>Nearest Neighbour:</b> escolhe sempre a paragem mais próxima do ponto atual, iniciando na entrada (0,0).</li>
 * </ul>
 */
public class PickPathPlanner {

    /**
     * Gera um percurso determinístico (estratégia A) ordenando primeiro pelo corredor (aisle)
     * e depois pelo bay de cada PickStop.
     *
     * @param stops lista de PickStops a serem visitados
     * @return PickPath contendo a sequência ordenada e a distância total
     */
    public PickPath planDeterministicSweep(List<PickStop> stops) {
        List<PickStop> ordered = new ArrayList<>(stops);
        Comparator<PickStop> comparator = Comparator
                .comparingInt((PickStop s) -> s.getCoordinate().getAisle())
                .thenComparingInt(s -> s.getCoordinate().getBay());
        ordered.sort(comparator);
        double total = calculateTotalDistance(ordered);
        return new PickPath(ordered, total);
    }

    /**
     * Gera um percurso usando a estratégia do vizinho mais próximo (Nearest Neighbour).
     * Começa na coordenada de entrada (0,0) e a cada passo escolhe o PickStop mais próximo.
     *
     * @param stops lista de PickStops a serem visitados
     * @return PickPath contendo a sequência ordenada e a distância total
     */
    public PickPath planNearestNeighbour(List<PickStop> stops) {
        List<PickStop> remaining = new ArrayList<>(stops);
        List<PickStop> ordered = new ArrayList<>();
        Coordinate current = new Coordinate(0,0);
        double total = 0.0;

        while (!remaining.isEmpty()) {
            PickStop nearest = null;
            double minDist = Double.MAX_VALUE;

            for (PickStop stop : remaining) {
                double dist = current.distanceTo(stop.getCoordinate());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = stop;
                }
            }

            ordered.add(nearest);
            total += minDist;
            current = nearest.getCoordinate();
            remaining.remove(nearest);
        }

        return new PickPath(ordered, total);
    }

    /**
     * Calcula a distância total do percurso, iniciando da coordenada de entrada (0,0).
     *
     * @param sequence sequência de PickStops
     * @return distância total do percurso
     */
    private int calculateTotalDistance(List<PickStop> sequence) {
        int total = 0;
        Coordinate current = new Coordinate(0,0);
        for (PickStop stop : sequence) {
            total += current.distanceTo(stop.getCoordinate());
            current = stop.getCoordinate();
        }
        return total;
    }
}

