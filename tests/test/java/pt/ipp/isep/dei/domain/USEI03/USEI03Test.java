package test.java.pt.ipp.isep.dei.domain.USEI03;

import main.java.pt.ipp.isep.dei.domain.*;
        import org.junit.jupiter.api.*;

        import java.time.LocalDate;
import java.util.*;

        import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for Use Case USEI03 — Picking Plans.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class USEI03Test {

    private List<Allocation> allocations;
    private PickingPlanner planner;

    @BeforeEach
    void setUp() {
        allocations = new ArrayList<>();

        // Exemplo: três SKUs com diferentes pesos e quantidades
        allocations.add(new Allocation("ORD001", 1, "SKU001", 5, 50.0, "BOX001", 1, 1));   // 50 kg total
        allocations.add(new Allocation("ORD001", 2, "SKU002", 2, 80.0, "BOX002", 1, 2));   // 80 kg total
        allocations.add(new Allocation("ORD002", 1, "SKU003", 10, 200.0, "BOX003", 2, 1)); // 200 kg total
    }

    // ========================================================
    // Teste: First Fit básico
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("First Fit: distribui allocations por trolleys corretamente")
    void testPlanFirstFit_Distribution() {
        planner = new PickingPlanner(allocations, 200.0);
        List<Trolley> trolleys = planner.planFirstFit();

        assertNotNull(trolleys);
        assertFalse(trolleys.isEmpty(), "Deve existir pelo menos um trolley");

        double totalAllocated = trolleys.stream()
                .flatMap(t -> t.getAllocations().stream())
                .mapToDouble(Allocation::getWeight)
                .sum();

        double totalExpected = allocations.stream()
                .mapToDouble(Allocation::getWeight)
                .sum();

        assertEquals(totalExpected, totalAllocated, 0.001,
                "O peso total alocado deve ser igual ao peso total original");

        // Primeiro trolley não deve ultrapassar a capacidade
        for (Trolley t : trolleys) {
            assertTrue(t.getUsedWeight() <= t.getCapacity() + 0.001,
                    "Trolley excedeu a capacidade: " + t.getUsedWeight());
        }
    }

    // ========================================================
    // Teste: First Fit Decreasing
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("First Fit Decreasing: allocations ordenadas por peso antes do encaixe")
    void testPlanFirstFitDecreasing_SortingAndPlacement() {
        planner = new PickingPlanner(allocations, 200.0);
        List<Trolley> trolleys = planner.planFirstFitDecreasing();

        assertNotNull(trolleys);
        assertTrue(trolleys.size() >= 1);

        // Verifica se as allocations foram ordenadas corretamente (decrescente)
        List<Double> weights = new ArrayList<>();
        for (Trolley t : trolleys) {
            for (Allocation a : t.getAllocations()) {
                weights.add(a.getWeight());
            }
        }

        List<Double> sortedDesc = new ArrayList<>(weights);
        sortedDesc.sort(Comparator.reverseOrder());
        assertEquals(sortedDesc, weights, "Allocations devem estar ordenadas por peso decrescente");
    }

    // ========================================================
    // Teste: Best Fit Decreasing
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Best Fit Decreasing: preenche trolleys de forma mais eficiente")
    void testPlanBestFitDecreasing_Optimization() {
        planner = new PickingPlanner(allocations, 200.0);
        List<Trolley> ff = planner.planFirstFit();
        planner = new PickingPlanner(allocations, 200.0);
        List<Trolley> bfd = planner.planBestFitDecreasing();

        // Best Fit Decreasing deve usar igual ou menos trolleys que o First Fit
        assertTrue(bfd.size() <= ff.size(),
                "Best Fit Decreasing deve ser igual ou mais eficiente que First Fit");
    }

    // ========================================================
    // Teste: Divisão automática de allocations grandes
    // ========================================================
    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Allocation Split: divide alocações que excedem a capacidade do trolley")
    void testSplitIfNeeded() {
        List<Allocation> heavyAlloc = List.of(
                new Allocation("ORD009", 1, "SKU_HEAVY", 50, 500.0, "BOX999", 3, 3)
        );

        planner = new PickingPlanner(heavyAlloc, 200.0);
        List<Trolley> trolleys = planner.planFirstFit();

        // Espera-se que a alocação original tenha sido dividida
        int totalQty = trolleys.stream()
                .flatMap(t -> t.getAllocations().stream())
                .filter(a -> a.getSku().equals("SKU_HEAVY"))
                .mapToInt(Allocation::getQty)
                .sum();

        assertEquals(50, totalQty, "A soma das quantidades das partes deve ser igual à original");

        for (Trolley t : trolleys) {
            assertTrue(t.getUsedWeight() <= t.getCapacity() + 0.001,
                    "Nenhum trolley deve exceder a capacidade");
        }

        assertTrue(trolleys.size() > 1, "Uma alocação dividida deve ocupar vários trolleys");
    }

    @AfterEach
    void tearDown() {
        allocations = null;
        planner = null;
    }
}

