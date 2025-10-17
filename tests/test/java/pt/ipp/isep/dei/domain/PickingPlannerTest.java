package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import main.java.pt.ipp.isep.dei.domain.PickingPlanner;
import main.java.pt.ipp.isep.dei.domain.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PickingPlannerTest {
    // Classe auxiliar simples para simular Allocation
    static class FakeAllocation {
        private final double weight;
        private final String name;

        FakeAllocation(double weight, String name) {
            this.weight = weight;
            this.name = name;
        }

        public double getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return name + "(" + weight + ")";
        }
    }

    private List<Allocation> toAllocations(List<FakeAllocation> fakeList) {
        // Adapta para List<Allocation> via cast forçado (só para os testes)
        @SuppressWarnings("unchecked")
        List<Allocation> list = (List<Allocation>)(List<?>)fakeList;
        return list;
    }

    private PickingPlanner planner;

    @BeforeEach
    void setUp() {
        List<FakeAllocation> allocs = List.of(
                new FakeAllocation(40, "A1"),
                new FakeAllocation(30, "A2"),
                new FakeAllocation(50, "A3"),
                new FakeAllocation(20, "A4")
        );
        planner = new PickingPlanner(toAllocations(allocs), 70.0);
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

        // Novo planner para comparação justa
        List<FakeAllocation> allocs = List.of(
                new FakeAllocation(40, "A1"),
                new FakeAllocation(30, "A2"),
                new FakeAllocation(50, "A3"),
                new FakeAllocation(20, "A4")
        );
        PickingPlanner bestFitPlanner = new PickingPlanner(toAllocations(allocs), 70.0);
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
        List<FakeAllocation> allocs = List.of(new FakeAllocation(20, "A1"));
        PickingPlanner singlePlanner = new PickingPlanner(toAllocations(allocs), 50.0);

        List<Trolley> result = singlePlanner.planFirstFit();
        assertEquals(1, result.size());
        assertEquals(20.0, result.get(0).getUsedWeight());
    }
}
