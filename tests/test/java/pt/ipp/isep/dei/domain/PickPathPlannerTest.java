package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PickPathPlanner}.
 * <p>
 * These tests validate the behavior of pick path planning algorithms,
 * including deterministic sweep and nearest neighbour strategies.
 * They cover edge cases such as empty pick stop lists and verify distance calculations.
 * </p>
 */
public class PickPathPlannerTest {

    // ---- Minimal fakes for testing ----

    /**
     * A fake Allocation used to simplify PickStop creation for tests.
     */
    static class FakeAllocation extends Allocation {
        public FakeAllocation(String sku) {
            // Use dummy values for other parameters
            super("ORD1", 1, sku, 1, 1.0, "BOX1", 0, 0);
        }
    }

    /**
     * A fake PickStop using a FakeAllocation for testing purposes.
     */
    static class FakePickStop extends PickStop {
        FakePickStop(String sku, Coordinate coordinate) {
            super(coordinate, new FakeAllocation(sku));
        }
    }

    /**
     * Tests that the deterministic sweep planner returns an empty path
     * when given an empty list of pick stops.
     */
    @Test
    void testPlanDeterministicSweep_WithEmptyList() {
        PickPathPlanner planner = new PickPathPlanner();

        List<PickStop> empty = List.of();
        PickPath result = planner.planDeterministicSweep(empty);

        assertNotNull(result);
        assertTrue(result.getSequence().isEmpty());
        assertEquals(0.0, result.getTotalDistance());
    }

    /**
     * Tests that the nearest neighbour planner correctly handles a single pick stop.
     * Verifies that the path contains exactly one stop and that the total distance
     * is correctly calculated from the origin.
     */
    @Test
    void testPlanNearestNeighbour_WithSingleStop() {
        PickPathPlanner planner = new PickPathPlanner();

        List<PickStop> stops = List.of(
                new FakePickStop("Only", new Coordinate(1, 1))
        );

        PickPath result = planner.planNearestNeighbour(stops);

        assertNotNull(result);
        assertEquals(1, result.getSequence().size());

        double expectedDistance = new Coordinate(0, 0).distanceTo(new Coordinate(1, 1));
        assertEquals(expectedDistance, result.getTotalDistance(), 0.001,
                "Distance calculated by PickPathPlanner should match the internal formula");
    }

    /**
     * Tests that the nearest neighbour planner correctly handles multiple pick stops.
     * Verifies that the returned path includes all stops and that the total distance
     * is greater than zero.
     */
    @Test
    void testPlanNearestNeighbour_WithMultipleStops() {
        PickPathPlanner planner = new PickPathPlanner();

        List<PickStop> stops = List.of(
                new FakePickStop("A", new Coordinate(1, 1)),
                new FakePickStop("B", new Coordinate(2, 2)),
                new FakePickStop("C", new Coordinate(3, 1))
        );

        PickPath result = planner.planNearestNeighbour(stops);

        assertNotNull(result);
        assertEquals(3, result.getSequence().size());
        assertTrue(result.getTotalDistance() > 0.0);
    }
}
