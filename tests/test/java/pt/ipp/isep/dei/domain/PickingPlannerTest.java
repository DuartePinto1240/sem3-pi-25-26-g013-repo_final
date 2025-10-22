package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Allocation;
import main.java.pt.ipp.isep.dei.domain.PickingPlanner;
import main.java.pt.ipp.isep.dei.domain.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PickingPlanner}.
 * <p>
 * These tests validate the behavior of different picking planning strategies,
 * including First Fit, First Fit Decreasing, and Best Fit Decreasing algorithms.
 * They also test constructor behavior and edge cases such as empty allocation lists.
 * </p>
 */
public class PickingPlannerTest {

    private PickingPlanner planner;

    /**
     * Sets up a PickingPlanner instance with sample allocations and trolley capacity
     * before each test.
     */
    @BeforeEach
    void setUp() {
        Allocation a1 = new Allocation("O1", 1, "A1", 1, 40.0, "B1", 0, 0);
        Allocation a2 = new Allocation("O2", 1, "A2", 1, 30.0, "B2", 0, 0);
        Allocation a3 = new Allocation("O3", 1, "A3", 1, 50.0, "B3", 0, 0);
        Allocation a4 = new Allocation("O4", 1, "A4", 1, 20.0, "B4", 0, 0);

        List<Allocation> allocations = List.of(a1, a2, a3, a4);
        planner = new PickingPlanner(allocations, 70.0);
    }

    /**
     * Tests that the constructor correctly sets the trolley capacity and stores
     * the allocations list.
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals(70.0, planner.getTrolleyCapacity());
        assertNotNull(planner.getAllocations());
        assertEquals(4, planner.getAllocations().size());
    }

    /**
     * Tests that the Best Fit Decreasing algorithm produces an allocation plan
     * using equal or fewer trolleys than the First Fit algorithm and verifies
     * total weight usage.
     */
    @Test
    void testPlanBestFitDecreasing_OptimizesBetterThanFirstFit() {
        List<Trolley> firstFit = planner.planFirstFit();
        int trolleysFirstFit = firstFit.size();

        List<Allocation> allocations = List.of(
                new Allocation("O1", 1, "A1", 1, 40.0, "B1", 0, 0),
                new Allocation("O2", 1, "A2", 1, 30.0, "B2", 0, 0),
                new Allocation("O3", 1, "A3", 1, 50.0, "B3", 0, 0),
                new Allocation("O4", 1, "A4", 1, 20.0, "B4", 0, 0)
        );
        PickingPlanner bestFitPlanner = new PickingPlanner(allocations, 70.0);
        List<Trolley> bestFit = bestFitPlanner.planBestFitDecreasing();

        int trolleysBestFit = bestFit.size();

        assertTrue(trolleysBestFit <= trolleysFirstFit,
                "Best Fit Decreasing should use equal or fewer trolleys than First Fit");

        double totalUsed = bestFit.stream().mapToDouble(Trolley::getUsedWeight).sum();
        assertEquals(140.0, totalUsed, 0.001);
    }

    /**
     * Tests that when the allocations list is empty, all planning methods
     * return an empty list of trolleys.
     */
    @Test
    void testEmptyAllocations_ReturnsEmptyList() {
        PickingPlanner emptyPlanner = new PickingPlanner(new ArrayList<>(), 50.0);
        assertTrue(emptyPlanner.planFirstFit().isEmpty());
        assertTrue(emptyPlanner.planFirstFitDecreasing().isEmpty());
        assertTrue(emptyPlanner.planBestFitDecreasing().isEmpty());
    }

    /**
     * Tests that a single allocation fits correctly into one trolley.
     */
    @Test
    void testSingleAllocation_FitsInOneTrolley() {
        Allocation singleAlloc = new Allocation("O1", 1, "A1", 1, 20.0, "B1", 0, 0);
        PickingPlanner singlePlanner = new PickingPlanner(List.of(singleAlloc), 50.0);

        List<Trolley> result = singlePlanner.planFirstFit();
        assertEquals(1, result.size());
        assertEquals(20.0, result.get(0).getUsedWeight());
    }
}
