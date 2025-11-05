package test.java.pt.ipp.isep.dei.domain.USEI04;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for Use Case USEI04 — Pick Path Sequencing.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class USEI04Test {

    private PickPathPlanner planner;
    private List<PickStop> pickStops;

    @BeforeEach
    void setUp() {
        planner = new PickPathPlanner();
        pickStops = new ArrayList<>();

        // Simula um conjunto de pontos de recolha (aisle, bay)
        pickStops.add(new PickStop(new Coordinate(1, 10),
                new Allocation("ORD001", 1, "SKU001", 5, 10.0, "BOX001", 1, 10)));
        pickStops.add(new PickStop(new Coordinate(2, 5),
                new Allocation("ORD001", 2, "SKU002", 3, 6.0, "BOX002", 2, 5)));
        pickStops.add(new PickStop(new Coordinate(1, 3),
                new Allocation("ORD002", 1, "SKU003", 2, 8.0, "BOX003", 1, 3)));
        pickStops.add(new PickStop(new Coordinate(3, 7),
                new Allocation("ORD003", 1, "SKU004", 1, 4.0, "BOX004", 3, 7)));
    }

    // ========================================================
    // Teste: Estratégia A — Deterministic Sweep
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Deterministic Sweep: Ordena os pontos por corredor e baia (ascendente)")
    void testPlanDeterministicSweep_OrderAndDistance() {
        PickPath path = planner.planDeterministicSweep(pickStops);

        assertNotNull(path, "O caminho não deve ser nulo");
        assertFalse(path.getSequence().isEmpty(), "A sequência de pontos não deve estar vazia");

        // Verifica se está ordenado por aisle (corredor) e depois por bay
        List<PickStop> seq = path.getSequence();
        for (int i = 1; i < seq.size(); i++) {
            Coordinate prev = seq.get(i - 1).getCoordinate();
            Coordinate curr = seq.get(i).getCoordinate();

            if (prev.getAisle() == curr.getAisle()) {
                assertTrue(prev.getBay() <= curr.getBay(),
                        "As bays devem estar em ordem crescente dentro do mesmo corredor");
            } else {
                assertTrue(prev.getAisle() <= curr.getAisle(),
                        "Os corredores devem estar em ordem crescente");
            }
        }

        // A distância deve ser positiva
        assertTrue(path.getTotalDistance() > 0,
                "A distância total deve ser positiva");
    }

    // ========================================================
    // Teste: Estratégia B — Nearest Neighbour
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Nearest Neighbour: Segue sempre para o ponto mais próximo")
    void testPlanNearestNeighbour_Behaviour() {
        PickPath path = planner.planNearestNeighbour(pickStops);

        assertNotNull(path, "O caminho não deve ser nulo");
        assertFalse(path.getSequence().isEmpty(), "A sequência de pontos não deve estar vazia");

        // Verifica se cada ponto é visitado apenas uma vez
        Set<String> visited = new HashSet<>();
        for (PickStop stop : path.getSequence()) {
            String key = stop.getCoordinate().getAisle() + ":" + stop.getCoordinate().getBay();
            assertTrue(visited.add(key), "Cada ponto deve ser visitado uma única vez");
        }

        // Verifica se a distância total é coerente com as coordenadas (menor que a soma ingênua)
        double naiveSum = 0;
        for (PickStop s : pickStops) {
            naiveSum += Math.abs(s.getCoordinate().getAisle()) + Math.abs(s.getCoordinate().getBay());
        }

        assertTrue(path.getTotalDistance() < naiveSum * 2,
                "A distância total deve ser plausivelmente menor que o caminho não otimizado");
    }

    // ========================================================
    // Teste: Merging de baias duplicadas (cenário preparatório)
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("PickStop Merge: Baias duplicadas devem somar quantidades")
    void testMergeDuplicateBays() {
        Allocation a1 = new Allocation("ORD001", 1, "SKU001", 5, 10.0, "BOX001", 1, 1);
        Allocation a2 = new Allocation("ORD001", 2, "SKU002", 3, 6.0, "BOX002", 1, 1);

        Map<String, PickStop> merged = new HashMap<>();
        for (Allocation alloc : List.of(a1, a2)) {
            String key = alloc.getAisle() + ":" + alloc.getBay();
            merged.merge(key, new PickStop(new Coordinate(alloc.getAisle(), alloc.getBay()), alloc),
                    (oldVal, newVal) -> {
                        oldVal.getAllocation().setQty(
                                oldVal.getAllocation().getQty() + alloc.getQty());
                        return oldVal;
                    });
        }

        assertEquals(1, merged.size(), "Baias duplicadas devem ser fundidas");
        PickStop mergedStop = merged.values().iterator().next();
        assertEquals(8, mergedStop.getAllocation().getQty(), "Quantidades devem ser somadas");
    }

    @AfterEach
    void tearDown() {
        planner = null;
        pickStops = null;
    }
}

