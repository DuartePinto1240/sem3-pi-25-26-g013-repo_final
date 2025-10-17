package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

public class TravelTimeResult {
    private final List<TravelSection> travelSections;
    private final double totalTimeMinutes;
    private final Locomotive locomotive;
    private final RailwayLine line;

    public TravelTimeResult(List<TravelSection> travelSections, double totalTimeMinutes, Locomotive locomotive, RailwayLine line) {
        this.travelSections = travelSections;
        this.totalTimeMinutes = totalTimeMinutes;
        this.locomotive = locomotive;
        this.line = line;
    }

    public void printResult() {
        long hours = (long) totalTimeMinutes / 60;
        long minutes = (long) totalTimeMinutes % 60;

        System.out.println("\n--- Resultado da Simulação de Viagem ---");
        System.out.printf("De: %s\n", line.getStartStation().getName());
        System.out.printf("Para: %s\n", line.getEndStation().getName());
        System.out.printf("Locomotiva: %s\n", locomotive.getModel());
        System.out.printf("Tempo Total Estimado: %d horas e %d minutos (%.2f min)\n", hours, minutes, totalTimeMinutes);
        System.out.println("----------------------------------------");
        System.out.println("Detalhes por Secção:");
        for (TravelSection section : travelSections) {
            System.out.println("- " + section);
        }
        System.out.println("----------------------------------------\n");
    }
}