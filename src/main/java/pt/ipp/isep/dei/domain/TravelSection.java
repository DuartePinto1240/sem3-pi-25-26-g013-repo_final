package main.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineSegment;

/**
 * Data Transfer Object (DTO) que armazena o resultado do cálculo de tempo
 * para um segmento específico.
 */
public class TravelSection {

    private final LineSegment segment;
    private final double effectiveSpeedKmH;
    private final double sectionTimeMinutes;

    public TravelSection(LineSegment segment, double effectiveSpeedKmH, double sectionTimeMinutes) {
        this.segment = segment;
        this.effectiveSpeedKmH = effectiveSpeedKmH;
        this.sectionTimeMinutes = sectionTimeMinutes;
    }

    public LineSegment getSegment() {
        return segment;
    }

    public double getEffectiveSpeedKmH() {
        return effectiveSpeedKmH;
    }

    public double getSectionTimeMinutes() {
        return sectionTimeMinutes;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s | Dist: %.1f km | Vel: %.1f km/h | Tempo: %.2f min",
                segment.getOrigin().getName(),
                segment.getDestination().getName(),
                segment.getDistanceKm(),
                effectiveSpeedKmH,
                sectionTimeMinutes);
    }
}