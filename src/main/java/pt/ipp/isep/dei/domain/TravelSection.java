package main.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineSegment;

/**
 * Representa uma secção de viagem (trajeto) ao longo de um {@link LineSegment}.
 * <p>
 * Esta classe atua como um Data Transfer Object (DTO), armazenando os resultados
 * do cálculo de velocidade e tempo de viagem para um segmento ferroviário específico.
 * </p>
 *
 * <p>
 * Cada {@code TravelSection} contém:
 * <ul>
 *     <li>O segmento de linha ferroviária percorrido;</li>
 *     <li>A velocidade efetiva usada (em km/h);</li>
 *     <li>O tempo de viagem correspondente (em minutos).</li>
 * </ul>
 * </p>
 *
 * <p>
 * Esta classe é imutável.
 * </p>
 */
public class TravelSection {

    /** Segmento de linha ferroviária associado a esta secção. */
    private final LineSegment segment;

    /** Velocidade efetiva usada na viagem, em km/h. */
    private final double effectiveSpeedKmH;

    /** Tempo necessário para percorrer o segmento, em minutos. */
    private final double sectionTimeMinutes;

    /**
     * Cria uma nova instância de {@code TravelSection}.
     *
     * @param segment o segmento ferroviário correspondente (não deve ser nulo)
     * @param effectiveSpeedKmH a velocidade efetiva de viagem, em km/h
     * @param sectionTimeMinutes o tempo total de viagem, em minutos
     */
    public TravelSection(LineSegment segment, double effectiveSpeedKmH, double sectionTimeMinutes) {
        this.segment = segment;
        this.effectiveSpeedKmH = effectiveSpeedKmH;
        this.sectionTimeMinutes = sectionTimeMinutes;
    }

    /**
     * Obtém o segmento ferroviário associado a esta secção.
     *
     * @return o {@link LineSegment} correspondente
     */
    public LineSegment getSegment() {
        return segment;
    }

    /**
     * Obtém a velocidade efetiva usada durante o percurso.
     *
     * @return velocidade em km/h
     */
    public double getEffectiveSpeedKmH() {
        return effectiveSpeedKmH;
    }

    /**
     * Obtém o tempo total necessário para percorrer o segmento.
     *
     * @return tempo de viagem em minutos
     */
    public double getSectionTimeMinutes() {
        return sectionTimeMinutes;
    }

    /**
     * Gera uma representação textual detalhada da secção de viagem.
     * <p>
     * Inclui origem, destino, distância, velocidade e tempo total.
     * </p>
     *
     * @return string formatada com os detalhes da viagem
     */
    @Override
    public String toString() {
        return String.format("%s -> %s | Dist: %.1f km | Velocidade: %.1f km/h | Tempo: %.2f min",
                segment.getOrigin().getName(),
                segment.getDestination().getName(),
                segment.getDistanceKm(),
                effectiveSpeedKmH,
                sectionTimeMinutes);
    }
}
