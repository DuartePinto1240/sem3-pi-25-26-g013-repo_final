package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import main.java.pt.ipp.isep.dei.domain.PickingPlanner;
import main.java.pt.ipp.isep.dei.domain.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PickingPlannerTest {

    private PickingPlanner planner;

    @BeforeEach
    void setUp() {
        // Criar allocations reais
        Allocation a1 = new Allocation("O1", 1, "A1", 1, 40.0, "B1", 0, 0);
        Allocation a2 = new Allocation("O2", 1, "A2", 1, 30.0, "B2", 0, 0);
        Allocation a3 = new Allocation("O3", 1, "A3", 1, 50.0, "B3", 0, 0);
        Allocation a4 = new Allocation("O4", 1, "A4", 1, 20.0, "B4", 0, 0);

        List<Allocation> allocations = List.of(a1, a2, a3, a4);
        planner = new PickingPlanner(allocations, 70.0);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(70.0, planner.getTrolleyCapacity());
        assertNotNull(planner.getAllocations());
        assertEquals(4, planner.getAllocations().size());
    }

    @Test
    void testPlanBestFitDecreasing_OptimizesBetterThanFirstFit() {
        List<Trolley> firstFit = planner.planFirstFit();
        int trolleysFirstFit = firstFit.size();

        // Criar novo planner para comparação justa
        Allocation a1 = new Allocation("O1", 1, "A1", 1, 40.0, "B1", 0, 0);
        Allocation a2 = new Allocation("O2", 1, "A2", 1, 30.0, "B2", 0, 0);
        Allocation a3 = new Allocation("O3", 1, "A3", 1, 50.0, "B3", 0, 0);
        Allocation a4 = new Allocation("O4", 1, "A4", 1, 20.0, "B4", 0, 0);

        List<Allocation> allocations = List.of(a1, a2, a3, a4);
        PickingPlanner bestFitPlanner = new PickingPlanner(allocations, 70.0);
        List<Trolley> bestFit = bestFitPlanner.planBestFitDecreasing();

        int trolleysBestFit = bestFit.size();

        assertTrue(trolleysBestFit <= trolleysFirstFit,
                "Best Fit Decreasing deve usar igual ou menos trolleys que First Fit");

        double totalUsed = bestFit.stream().mapToDouble(Trolley::getUsedWeight).sum();
        assertEquals(140.0, totalUsed, 0.001);
    }

    @Test
    void testEmptyAllocations_ReturnsEmptyList() {
        PickingPlanner emptyPlanner = new PickingPlanner(new ArrayList<>(), 50.0);
        assertTrue(emptyPlanner.planFirstFit().isEmpty());
        assertTrue(emptyPlanner.planFirstFitDecreasing().isEmpty());
        assertTrue(emptyPlanner.planBestFitDecreasing().isEmpty());
    }

    @Test
    void testSingleAllocation_FitsInOneTrolley() {
        Allocation singleAlloc = new Allocation("O1", 1, "A1", 1, 20.0, "B1", 0, 0);
        PickingPlanner singlePlanner = new PickingPlanner(List.of(singleAlloc), 50.0);

        List<Trolley> result = singlePlanner.planFirstFit();
        assertEquals(1, result.size());
        assertEquals(20.0, result.get(0).getUsedWeight());
    }
}

