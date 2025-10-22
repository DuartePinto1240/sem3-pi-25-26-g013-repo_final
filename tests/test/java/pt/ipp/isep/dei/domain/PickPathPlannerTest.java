package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PickPathPlannerTest {

    // ---- Fakes mínimos ----

    static class FakeAllocation extends Allocation {
        public FakeAllocation(String sku) {
            // Usa valores dummy para os outros parâmetros
            super("ORD1", 1, sku, 1, 1.0, "BOX1", 0, 0);
        }
    }

    static class FakePickStop extends PickStop {
        FakePickStop(String sku, Coordinate coordinate) {
            super(coordinate, new FakeAllocation(sku));
        }
    }

    // ---- Testes ----

    @Test
    void testPlanDeterministicSweep_WithEmptyList() {
        PickPathPlanner planner = new PickPathPlanner();

        List<PickStop> empty = List.of();
        PickPath result = planner.planDeterministicSweep(empty);

        assertNotNull(result);
        assertTrue(result.getSequence().isEmpty());
        assertEquals(0.0, result.getTotalDistance());
    }

    @Test
    void testPlanNearestNeighbour_WithSingleStop() {
        PickPathPlanner planner = new PickPathPlanner();

        List<PickStop> stops = List.of(
                new FakePickStop("Only", new Coordinate(1, 1))
        );

        PickPath result = planner.planNearestNeighbour(stops);

        assertNotNull(result);
        assertEquals(1, result.getSequence().size());
        assertEquals(4.0, result.getTotalDistance(), 0.001,
                "Distância de (0,0) até (1,1) com fórmula da warehouse é 4.0");

    }

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

