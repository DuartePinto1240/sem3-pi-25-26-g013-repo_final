package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.DirectedLineUpgradePlan;
import main.java.pt.ipp.isep.dei.domain.DirectedLineUpgradePlan.UpgradePlanResult;

import java.io.IOException;

/**
 * Console User Interface (UI) for Use Case USEI11: Directed Line Upgrade Plan.
 * <p>
 * This UI loads stations and directed track segments, computes the topological
 * ordering (upgrade plan) using Kahn's algorithm, detects cycles, and prints
 * the comprehensive results to the console.
 * </p>
 */
public class USEI11UI implements Runnable {

    // 🛑 ATENÇÃO: SUBSTITUA ESTES CAMINHOS PELOS CAMINHOS REAIS DOS SEUS FICHEIROS CSV!
    private static final String STATIONS_FILE = "C:\\Users\\duart\\IdeaProjects\\sem3-pi-25-26-g013-repo\\train_station_dataset_v2\\stations.csv";
    private static final String TRACKS_FILE = "C:\\Users\\duart\\IdeaProjects\\sem3-pi-25-26-g013-repo\\train_station_dataset_v2\\lines.csv";
    private static final String OUTPUT_FILE = "upgrade_order.csv";

    /**
     * Executes the USEI11 use case sequence.
     */
    @Override
    public void run() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("=== USEI11 — Directed Line Upgrade Plan (Kahn's Algorithm) ===");
        System.out.println("=".repeat(80));

        DirectedLineUpgradePlan planner = new DirectedLineUpgradePlan();

        try {
            // 1. Carregar os Dados
            System.out.println("\n📂 Carregando dados da Rede Ferroviária...");
            planner.loadStations(STATIONS_FILE);
            planner.loadTrackSegments(TRACKS_FILE);

            System.out.println("\n" + "═".repeat(80));
            System.out.println("⚙️  Executando Algoritmo de Ordenação Topológica...");
            System.out.println("═".repeat(80));

            // 2. Computar o Plano de Upgrade
            UpgradePlanResult result = planner.computeUpgradePlan();

            // 3. Imprimir Resultados
            planner.printResult(result);

            // 4. Exportar o resultado (se válido)
            if (!result.hasCycle) {
                planner.exportUpgradeOrder(result, OUTPUT_FILE);
            }

        } catch (IOException e) {
            System.err.println("\n❌ ERRO FATAL de I/O: Falha ao aceder aos ficheiros CSV.");
            System.err.println("Por favor, verifique se os caminhos no USEI11UI estão corretos.");
            System.err.println("Mensagem de erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n❌ Ocorreu um erro inesperado durante a execução.");
            e.printStackTrace();
        } finally {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("=== FIM DA EXECUÇÃO USEI11 ===");
            System.out.println("=".repeat(80));
        }
    }
}
