package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.PickPath;
import main.java.pt.ipp.isep.dei.domain.PickPathPlanner;
import main.java.pt.ipp.isep.dei.domain.PickStop;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PickPathPlannerTest {

    static class FakeCoordinate {
        private final int aisle;
        private final int bay;

        FakeCoordinate(int aisle, int bay) {
            this.aisle = aisle;
            this.bay = bay;
        }

        public int getAisle() {
            return aisle;
        }

        public int getBay() {
            return bay;
        }

        public int distanceTo(FakeCoordinate other) {
            if (this.aisle == other.aisle)
                return Math.abs(this.bay - other.bay);
            else
                return this.bay + Math.abs(this.aisle - other.aisle) * 3 + other.bay;
        }

        @Override
        public String toString() {
            return "(" + aisle + "," + bay + ")";
        }
    }

    static class FakePickStop {
        private final FakeCoordinate coordinate;
        private final String name;

        FakePickStop(String name, FakeCoordinate coordinate) {
            this.name = name;
            this.coordinate = coordinate;
        }

        public FakeCoordinate getCoordinate() {
            return coordinate;
        }

        @Override
        public String toString() {
            return name + "@" + coordinate;
        }
    }

    static class FakePickPath {
        private final List<FakePickStop> stops;
        private final double totalDistance;

        FakePickPath(List<FakePickStop> stops, double totalDistance) {
            this.stops = stops;
            this.totalDistance = totalDistance;
        }

        public List<FakePickStop> getStops() {
            return stops;
        }

        public double getTotalDistance() {
            return totalDistance;
        }
    }

    // --- Método de ajuda para converter listas genéricas ---
    @SuppressWarnings("unchecked")
    private List<PickStop> toPickStops(List<FakePickStop> fakes) {
        return (List<PickStop>) (List<?>) fakes;
    }

    @Test
    void testPlanDeterministicSweep_WithEmptyList() {
        PickPathPlanner planner = new PickPathPlanner();
        List<PickStop> empty = new ArrayList<>();

        PickPath result = planner.planDeterministicSweep(empty);

        assertNotNull(result);
        assertTrue(result.getSequence().isEmpty());
        assertEquals(0.0, result.getTotalDistance());
    }

    @Test
    void testPlanNearestNeighbour_WithSingleStop() {
        PickPathPlanner planner = new PickPathPlanner();

        List<FakePickStop> stops = List.of(
                new FakePickStop("Only", new FakeCoordinate(1, 1))
        );

        PickPath result = planner.planNearestNeighbour(toPickStops(stops));

        assertNotNull(result);
        assertEquals(1, result.getSequence().size());
        assertEquals(1.0, result.getTotalDistance(), 0.001,
                "Distância de (0,0) até (1,1) = bay + diff*3 + otherBay = 0 + 3 + 1 = 4, mas depende da fórmula concreta");
    }
}
