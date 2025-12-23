package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USEI13Controller;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.dto.HubCentralityResultDTO;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.util.RailwayLoader;

import java.util.List;

public class USEI13UI implements Runnable {

    private static final String STATIONS_FILE = "train_station_dataset_v2/stations.csv";
    private static final String TRACKS_FILE   = "train_station_dataset_v2/lines.csv";

    private final USEI13Controller controller;

    public USEI13UI() {
        this.controller = new USEI13Controller();
    }

    @Override
    public void run() {
        try {
            System.out.println("\n--- USEI13: Rail Hub Centrality Analysis ---");

            // 1) Load graph
            Graph<Station, Track> railwayNetwork =
                    RailwayLoader.loadGraph(STATIONS_FILE, TRACKS_FILE);

            if (railwayNetwork == null || railwayNetwork.numVertices() == 0) {
                System.out.println("❌ Erro a carregar rede ferroviária.");
                return;
            }

            System.out.println("✅ Rede carregada: V="
                    + railwayNetwork.numVertices()
                    + " | E=" + railwayNetwork.numEdges());

            // 2) Compute ranking
            long t0 = System.nanoTime();
            List<HubCentralityResultDTO> ranking =
                    controller.computeHubRanking(railwayNetwork);
            long t1 = System.nanoTime();

            if (ranking == null || ranking.isEmpty()) {
                System.out.println("❌ Ranking vazio.");
                return;
            }

            System.out.printf("✅ Computado. Tempo: %.3f ms%n",
                    (t1 - t0) / 1_000_000.0);

            // 3) Show TOP 10
            int top = Math.min(10, ranking.size());
            System.out.println("\nTOP " + top + " HUBS:");
            for (int i = 0; i < top; i++) {
                System.out.printf("%2d) %s%n", i + 1, ranking.get(i));
            }

            // 4) Export
            controller.exportToCsv(ranking);
            System.out.println("\n✅ Resultados exportados para:");
            System.out.println("   " + controller.getExportPath());

        } catch (Exception e) {
            System.out.println("❌ Falha na execução da USEI13: " + e.getMessage());
        }
    }
}
