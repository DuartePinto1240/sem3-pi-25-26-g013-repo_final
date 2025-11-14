package main.java.pt.ipp.isep.dei.ui;

// Importar as classes corretas
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TwoDTree;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader; // Usar o loader

import java.util.List;
import java.util.Set;

/**
 * UI (Consola) para executar e demonstrar a USEI07
 *.
 */
public class USEI07UI implements Runnable {

    // Defina o caminho para o seu ficheiro CSV aqui
    private static final String CSV_PATH = "train_stations_europe.csv";

    @Override
    public void run() {
        System.out.println("\n=== USEI07 — Build a balanced 2D-Tree on Latitude/Longitude ===");

        // 1. Carregar os dados (estações)
        System.out.printf("A carregar estações do ficheiro: %s...%n", CSV_PATH);
        TrainStationLoader loader = new TrainStationLoader();
        List<TrainStation> allStations = loader.load(CSV_PATH); // Agora usa TrainStation

        if (allStations == null || allStations.isEmpty()) {
            System.out.println("⚠️ Não foi possível carregar os dados das estações. A UI da USEI07 não pode continuar.");
            return;
        }

        System.out.println("A construir a 2D-Tree balanceada (estratégia 'bulk build')...");

        // 2. Executar a USEI07 (Construtor da TwoDTree)
        long startTime = System.nanoTime();
        TwoDTree stationTree = new TwoDTree(allStations); // Passa a List<TrainStation>
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.printf("Construção da árvore concluída em %d ms.%n", durationMs);

        // 3. Apresentar os "Returns" da USEI07
        System.out.println("\n--- Relatório da 2D-Tree (USEI07 Returns) ---");

        int size = stationTree.size();
        int height = stationTree.height();
        Set<Integer> bucketSizes = stationTree.getDistinctBucketSizes();

        System.out.printf("  > Tamanho da árvore (localizações únicas): %d%n", size);
        System.out.printf("  > Altura da árvore: %d%n", height);
        System.out.printf("  > Tamanhos distintos de 'buckets': %s%n", bucketSizes.toString());

        System.out.println("\n--- Análise de Complexidade Temporal ---");
        System.out.println("  (N = #estações, M = #localizações únicas)");
        System.out.println("  1. Pré-processamento (HashMap): O(N)");
        System.out.println("  2. Construção ('buildRecursive'): O(M log^2 M)");
        System.out.println("  > Complexidade Total: O(N + M log^2 M)");
    }
}