package test.java.pt.ipp.isep.dei.domain.USEI07;

// Importar as classes do seu domínio
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TwoDNode;
import main.java.pt.ipp.isep.dei.domain.TwoDTree;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;

// Importar JUnit
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a USEI07 - Construção da 2D-Tree Balanceada
 *.
 */
class USEI07Test {

    private static TwoDTree stationTree;
    private static List<TrainStation> allStations;

    // Caminho para o dataset, baseado na UI
    private static final String CSV_PATH = "train_station_dataset_v2/train_stations_europe.csv";

    @BeforeAll
    static void setup() {
        System.out.println("A carregar dados e a construir 2D-Tree para testes...");

        // Arrange
        TrainStationLoader loader = new TrainStationLoader();
        allStations = loader.load(CSV_PATH);

        assertNotNull(allStations, "A lista de estações não pode ser nula.");
        assertFalse(allStations.isEmpty(), "O loader tem de carregar estações para os testes.");

        // Act (Ação principal da USEI07)
        long startTime = System.nanoTime();
        stationTree = new TwoDTree(allStations);
        long endTime = System.nanoTime();

        System.out.printf("Setup concluído. Árvore construída em %d ms.%n", (endTime - startTime) / 1_000_000);

        assertNotNull(stationTree, "A 2D-Tree não pode ser nula após a construção.");
    }

    @Test
    @DisplayName("USEI07 Returns: Testa as estatísticas da árvore (size, height, buckets)")
    void testTreeBuildStatistics() {
        // Act
        int size = stationTree.size(); // Número de localizações únicas (M)
        int height = stationTree.height();
        Set<Integer> bucketSizes = stationTree.getDistinctBucketSizes();

        // Log
        System.out.println("--- Estatísticas da Árvore (Returns USEI07) ---");
        System.out.println("  > Total de Estações (N): " + allStations.size());
        System.out.println("  > Localizações Únicas (M = size): " + size);
        System.out.println("  > Altura da Árvore: " + height);
        System.out.println("  > Tamanhos de Buckets: " + bucketSizes);

        // Assert
        assertTrue(size > 0, "A árvore deve conter localizações.");
        assertTrue(height >= 0, "A altura deve ser non-negativa.");
        assertTrue(size <= allStations.size(), "O tamanho (M) deve ser <= ao total de estações (N).");

        assertNotNull(bucketSizes, "O set de tamanhos de buckets não pode ser nulo.");
        assertFalse(bucketSizes.isEmpty(), "Deve haver pelo menos um tamanho de bucket.");
        assertTrue(bucketSizes.contains(1), "Deve existir pelo menos um bucket de tamanho 1.");
    }

    @Test
    @DisplayName("USEI07 Requirement: Testa se a Árvore é Balanceada")
    void testTreeIsBalanced() {
        // Arrange
        int m = stationTree.size(); // M = número de nós
        int h = stationTree.height();

        // A "balanced build strategy"
        // deve produzir uma árvore com altura O(log M).

        // Act
        // Limite superior teórico para uma árvore balanceada (ex: AVL)
        double maxAllowedHeight = 1.44 * (Math.log(m + 1) / Math.log(2)) + 2;

        // Log
        System.out.println("--- Teste de Balanceamento ---");
        System.out.println("  > Nós (M): " + m);
        System.out.println("  > Altura (h): " + h);
        System.out.printf("  > Limite Teórico (h_max): %.2f%n", maxAllowedHeight);

        // Assert
        assertTrue(h <= maxAllowedHeight,
                String.format("A árvore não está balanceada. Altura %d excede o limite de %.0f para %d nós.",
                        h, maxAllowedHeight, m));
    }

    @Test
    @DisplayName("USEI07 Requirement: Testa o manuseamento de Coordenadas Duplicadas ('Buckets')")
    void testDuplicateCoordinatesHandled() {
        // Arrange
        int n_stations = allStations.size();    // N = Total de estações
        int m_nodes = stationTree.size();       // M = Total de nós (localizações únicas)

        // Log
        System.out.println("--- Teste de 'Buckets' ---");
        System.out.println("  > Total Estações (N): " + n_stations);
        System.out.println("  > Nós da Árvore (M): " + m_nodes);

        // Assert
        // O tamanho M (nós) TEM de ser menor que N (estações)
        //.
        assertTrue(m_nodes < n_stations,
                "O tamanho da árvore (M) deve ser menor que o total de estações (N) se existirem duplicados.");
    }

    @Test
    @DisplayName("USEI07 Requirement: Testa se 'Buckets' estão Ordenados por Nome")
    void testBucketsAreSortedByName() {
        // Arrange
        // CORREÇÃO: O método nodesByLevel() retorna List<TwoDNode>
        //
        Map<Integer, List<TwoDNode>> levels = stationTree.nodesByLevel();

        int bucketsVerificados = 0;

        // Act
        for (List<TwoDNode> nodesAtLevel : levels.values()) {
            for (TwoDNode bucket : nodesAtLevel) {
                // Agora temos acesso direto ao bucket (TwoDNode)
                List<TrainStation> stationsInBucket = bucket.getStations();

                // Só precisamos de verificar buckets com mais de 1 estação
                if (stationsInBucket.size() > 1) {
                    bucketsVerificados++;
                    // Assert
                    for (int i = 0; i < stationsInBucket.size() - 1; i++) {
                        String name1 = stationsInBucket.get(i).getStation();
                        String name2 = stationsInBucket.get(i + 1).getStation();

                        // Verifica o requisito de ordenação por nome
                        //
                        assertTrue(name1.compareTo(name2) <= 0,
                                String.format("Erro de ordenação no bucket %s: '%s' vem antes de '%s'",
                                        bucket, name2, name1));
                    }
                }
            }
        }

        // Log
        System.out.println("--- Teste de Ordenação de Buckets ---");
        System.out.println("  > Buckets com >1 estação verificados: " + bucketsVerificados);
        assertTrue(bucketsVerificados > 0, "O dataset de teste deve conter buckets com duplicados para este teste ser válido.");
    }

    @Test
    @DisplayName("Teste de Robustez: Construtor com Lista Vazia")
    void testConstructorWithEmptyList() {
        // Arrange
        List<TrainStation> emptyList = new ArrayList<>();

        // Act
        TwoDTree emptyTree = new TwoDTree(emptyList);

        // Assert
        assertNotNull(emptyTree, "A árvore não pode ser nula.");
        assertEquals(0, emptyTree.size(), "O tamanho de uma árvore vazia deve ser 0.");
        // A altura de uma árvore vazia é -1, por convenção da BST
        //
        assertEquals(-1, emptyTree.height(), "A altura de uma árvore vazia deve ser -1.");
    }
}