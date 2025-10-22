package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

/**
 * Represents the complete result of a locomotive travel time simulation
 * along a given {@link RailwayLine}.
 * <p>
 * This class aggregates the results of all travel sections
 * ({@link TravelSection}), the total estimated time, and the locomotive used.
 * </p>
 *
 * <p>
 * It is immutable and intended to be used only for querying and visualizing
 * the simulation results.
 * </p>
 */
public class TravelTimeResult {

    /** List of travel sections (each corresponding to a segment of the line). */
    private final List<TravelSection> travelSections;

    /** Total estimated time to traverse the complete line, in minutes. */
    private final double totalTimeMinutes;

    /** Locomotive used in the travel simulation. */
    private final Locomotive locomotive;

    /** Railway line traversed by the locomotive. */
    private final RailwayLine line;

    /**
     * Creates a new instance of {@code TravelTimeResult}.
     *
     * @param travelSections list of travel sections (must not be null)
     * @param totalTimeMinutes total estimated time, in minutes
     * @param locomotive the locomotive used in the journey
     * @param line the railway line traversed
     */
    public TravelTimeResult(List<TravelSection> travelSections, double totalTimeMinutes,
                            Locomotive locomotive, RailwayLine line) {
        this.travelSections = travelSections;
        this.totalTimeMinutes = totalTimeMinutes;
        this.locomotive = locomotive;
        this.line = line;
    }

    /**
     * Prints a formatted summary of the travel simulation result to the console.
     * <p>
     * Includes:
     * <ul>
     * <li>Origin and destination station;</li>
     * <li>Model of the locomotive used;</li>
     * <li>Total estimated time (in hours and minutes);</li>
     * <li>Individual details of each section traversed.</li>
     * </ul>
     * </p>
     */
    public void printResult() {
        long hours = (long) totalTimeMinutes / 60;
        long minutes = (long) totalTimeMinutes % 60;

        System.out.println("\n--- Resultado da Simulação de Viagem ---");
        System.out.printf("De: %s\n", line.getStartStation().getName());
        System.out.printf("Para: %s\n", line.getEndStation().getName());
        System.out.printf("Locomotiva: %s\n", locomotive.getModel());
        System.out.printf("Tempo Total Estimado: %d horas e %d minutos (%.2f min)\n",
                hours, minutes, totalTimeMinutes);
        System.out.println("----------------------------------------");
        System.out.println("Detalhes por Secção:");
        for (TravelSection section : travelSections) {
            System.out.println("- " + section);
        }
        System.out.println("----------------------------------------\n");
    }
}