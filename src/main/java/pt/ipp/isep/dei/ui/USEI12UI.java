package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.RailwayController;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.util.RailwayLoader;


import java.io.IOException;
import java.util.Scanner;

public class USEI12UI implements Runnable {

    // CAMINHOS DOS FICHEIROS CSV (Ajusta se necessário)
    // Se estiverem na raiz do projeto, basta o nome. Se estiverem numa pasta, mete "pasta/ficheiro.csv"
    private static final String STATIONS_FILE = "train_station_dataset_v2/stations.csv";
    private static final String TRACKS_FILE = "train_station_dataset_v2/lines.csv";

    private final RailwayController controller;
    private Graph<Station, Track> railwayNetwork; // O grafo carregado
    private final Scanner scanner;

    public USEI12UI() {
        this.controller = new RailwayController();
        this.scanner = new Scanner(System.in);
        this.railwayNetwork = null;
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- RAILWAY MANAGER SYSTEM (USEI12) ---");
            System.out.println("1. Carregar Rede (Ler CSVs)");
            System.out.println("2. Gerar Minimal Backbone Network (MST)");
            System.out.println("0. Sair");
            System.out.print("Selecione uma opção: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    loadData();
                    break;
                case "2":
                    generateMinimalBackbone();
                    break;
                case "0":
                    running = false;
                    System.out.println("A encerrar...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // Carregamento real usando a classe RailwayLoader
    private void loadData() {
        System.out.println("\n--- A carregar dados da rede ferroviária... ---");

        // Chama o método estático da tua classe utils
        this.railwayNetwork = RailwayLoader.loadGraph(STATIONS_FILE, TRACKS_FILE);

        if (this.railwayNetwork != null && this.railwayNetwork.numVertices() > 0) {
            System.out.println("✅ Sucesso!");
            System.out.println("   Estações carregadas: " + railwayNetwork.numVertices());
            System.out.println("   Linhas carregadas: " + railwayNetwork.numEdges());
        } else {
            System.out.println("❌ Erro: Grafo vazio ou ficheiros não encontrados.");
            System.out.println("   Verifique se os ficheiros '" + STATIONS_FILE + "' e '" + TRACKS_FILE + "' estão no sítio certo.");
        }
    }

    // Executa a USEI12
    private void generateMinimalBackbone() {
        if (railwayNetwork == null || railwayNetwork.numVertices() == 0) {
            System.out.println("⚠️ Erro: Nenhuma rede carregada. Por favor execute a Opção 1 primeiro.");
            return;
        }

        System.out.println("\n--- USEI12: Calculating Minimal Backbone Network (Prim) ---");
        long startTime = System.nanoTime();

        // 1. Executar Algoritmo (Chama o Controller)
        Graph<Station, Track> backbone = controller.getMinimalBackboneNetwork(railwayNetwork);

        long endTime = System.nanoTime();

        // 2. Mostrar Resultados na Consola
        System.out.println("✅ Backbone calculado com sucesso!");
        System.out.println("   Número de Estações (Vértices): " + backbone.numVertices());
        System.out.println("   Número de Linhas (Arestas): " + backbone.numEdges());
        System.out.printf("   Tempo de Execução: %.3f ms%n", (endTime - startTime) / 1_000_000.0);

        // Output da Complexidade Teórica (Obrigatório segundo o enunciado)
        System.out.println("   Complexidade Teórica: O(E log V) - Usando Prim com Priority Queue");

        // 3. Exportar para Ficheiro DOT
        String filename = "minimal_backbone.dot";
        // Chama o Controller para exportar
        boolean success = controller.exportGraphToDOT(backbone, filename);

        if (success) {
            System.out.println("📄 Ficheiro DOT gerado: " + filename);

            // 4. Gerar SVG automaticamente
            generateSVG(filename);
        } else {
            System.out.println("❌ Erro ao criar ficheiro DOT.");
        }
    }

    // Método auxiliar para chamar o Graphviz via linha de comandos
    private void generateSVG(String dotFilename) {
        String svgFilename = dotFilename.replace(".dot", ".svg");
        try {
            System.out.println("🎨 A tentar gerar imagem SVG...");
            // Comando: neato -Tsvg input.dot -o output.svg
            // O enunciado especifica o uso de "neato" para respeitar as coordenadas geográficas
            ProcessBuilder pb = new ProcessBuilder("neato", "-Tsvg", dotFilename, "-o", svgFilename);
            Process p = pb.start();
            int exitCode = p.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ Imagem SVG gerada com sucesso: " + svgFilename);
            } else {
                System.out.println("⚠️ Aviso: Não foi possível gerar o SVG automaticamente (Exit Code: " + exitCode + ")");
                System.out.println("   Certifique-se que o Graphviz está instalado.");
                System.out.println("   Comando manual: neato -Tsvg " + dotFilename + " -o " + svgFilename);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("⚠️ Erro ao tentar executar o Graphviz: " + e.getMessage());
        }
    }
}