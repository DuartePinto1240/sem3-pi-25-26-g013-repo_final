package main.java.pt.ipp.isep.dei.domain;

import java.util.List;

/**
 * Representa o resultado completo da simulação de tempo de viagem de uma locomotiva
 * ao longo de uma determinada {@link RailwayLine}.
 * <p>
 * Esta classe agrega os resultados de todas as secções de viagem
 * ({@link TravelSection}), o tempo total estimado e a locomotiva utilizada.
 * </p>
 *
 * <p>
 * É imutável e destina-se a ser usada apenas para consulta e visualização
 * dos resultados da simulação.
 * </p>
 */
public class TravelTimeResult {

    /** Lista das secções de viagem (cada uma correspondente a um segmento da linha). */
    private final List<TravelSection> travelSections;

    /** Tempo total estimado para percorrer a linha completa, em minutos. */
    private final double totalTimeMinutes;

    /** Locomotiva usada na simulação de viagem. */
    private final Locomotive locomotive;

    /** Linha ferroviária percorrida pela locomotiva. */
    private final RailwayLine line;

    /**
     * Cria uma nova instância de {@code TravelTimeResult}.
     *
     * @param travelSections lista das secções de viagem (não deve ser nula)
     * @param totalTimeMinutes tempo total estimado, em minutos
     * @param locomotive locomotiva utilizada na viagem
     * @param line linha ferroviária percorrida
     */
    public TravelTimeResult(List<TravelSection> travelSections, double totalTimeMinutes,
                            Locomotive locomotive, RailwayLine line) {
        this.travelSections = travelSections;
        this.totalTimeMinutes = totalTimeMinutes;
        this.locomotive = locomotive;
        this.line = line;
    }

    /**
     * Imprime no terminal um resumo formatado do resultado da simulação de viagem.
     * <p>
     * Inclui:
     * <ul>
     *     <li>Estação de origem e destino;</li>
     *     <li>Modelo da locomotiva utilizada;</li>
     *     <li>Tempo total estimado (em horas e minutos);</li>
     *     <li>Detalhes individuais de cada secção percorrida.</li>
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
