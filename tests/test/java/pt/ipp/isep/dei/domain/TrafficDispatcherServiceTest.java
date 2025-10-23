package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.service.TrafficDispatcherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TrafficDispatcherService} without using Mockito.
 * <p>
 * This test class uses simple stub implementations of the repositories to simulate data.
 * It covers scenarios such as travel time calculation, retrieval of available stations
 * and locomotives, and exception handling when invalid input is provided.
 * </p>
 */
class TrafficDispatcherServiceTest {

    private TrafficDispatcherService service;
    private StubStationRepository stationRepo;
    private StubLocomotiveRepository locoRepo;
    private StubRailwayLineRepository lineRepo;

    private Station s1, s2;
    private Locomotive loco;
    private RailwayLine line;

    /**
     * Initializes the service and stub repositories with test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Create domain objects
        s1 = new Station("S1", "Lisboa");
        s2 = new Station("S2", "Porto");
        loco = new Locomotive("L1", "CP2600", 120.0, 80.0, false);

        LineSegment seg1 = new LineSegment(1, s1, s2, 60.0, 100.0, false);
        LineSegment seg2 = new LineSegment(2, s2, s1, 40.0, 150.0, false);
        line = new RailwayLine(List.of(seg1, seg2));


        // Create stub repositories
        stationRepo = new StubStationRepository(Map.of(
                "S1", s1,
                "S2", s2
        ));
        locoRepo = new StubLocomotiveRepository(Map.of(
                "L1", loco
        ));
        lineRepo = new StubRailwayLineRepository(line);

        // Instantiate the service
        service = new TrafficDispatcherService(stationRepo, locoRepo, lineRepo);
    }

    // ---------------- TEST METHODS ----------------

    /**
     * Tests that calculating travel time with an invalid station ID throws an exception.
     */
    @Test
    void testCalculateTravelTime_InvalidStation_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                service.calculateTravelTime("INVALID", "S2", "L1"));
    }

    /**
     * Tests that calculating travel time with an invalid locomotive ID throws an exception.
     */
    @Test
    void testCalculateTravelTime_InvalidLocomotive_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                service.calculateTravelTime("S1", "S2", "BADID"));
    }

    /**
     * Tests that calculating travel time when no direct line exists throws an exception.
     */
    @Test
    void testCalculateTravelTime_NoDirectLine_ThrowsException() {
        lineRepo.setReturnNull(true);

        assertThrows(IllegalStateException.class, () ->
                service.calculateTravelTime("S1", "S2", "L1"));
    }

    /**
     * Tests that {@link TrafficDispatcherService#getAvailableStations()} returns the correct list.
     */
    @Test
    void testGetAvailableStations_ReturnsCorrectList() {
        List<Station> stations = service.getAvailableStations();
        assertEquals(2, stations.size());
        assertTrue(stations.contains(s1));
        assertTrue(stations.contains(s2));
    }

    /**
     * Tests that {@link TrafficDispatcherService#getAvailableLocomotives()} returns the correct list.
     */
    @Test
    void testGetAvailableLocomotives_ReturnsCorrectList() {
        List<Locomotive> locos = service.getAvailableLocomotives();
        assertEquals(1, locos.size());
        assertEquals(loco, locos.get(0));
    }

    // ---------------- STUB REPOSITORY CLASSES ----------------

    /**
     * Simple stub implementation of {@link StationRepository}.
     */
    private static class StubStationRepository implements StationRepository {
        private final Map<String, Station> data;

        StubStationRepository(Map<String, Station> data) {
            this.data = data;
        }

        @Override
        public List<Station> findAll() {
            return new ArrayList<>(data.values());
        }

        @Override
        public Station findById(String id) {
            return data.get(id);
        }
    }

    /**
     * Simple stub implementation of {@link LocomotiveRepository}.
     */
    private static class StubLocomotiveRepository implements LocomotiveRepository {
        private final Map<String, Locomotive> data;

        StubLocomotiveRepository(Map<String, Locomotive> data) {
            this.data = data;
        }

        @Override
        public List<Locomotive> findAll() {
            return new ArrayList<>(data.values());
        }

        @Override
        public Locomotive findById(String id) {
            return data.get(id);
        }
    }

    /**
     * Simple stub implementation of {@link RailwayLineRepository}.
     * Can optionally simulate the absence of a direct line.
     */
    private static class StubRailwayLineRepository implements RailwayLineRepository {
        private final RailwayLine line;
        private boolean returnNull = false;

        StubRailwayLineRepository(RailwayLine line) {
            this.line = line;
        }

        void setReturnNull(boolean returnNull) {
            this.returnNull = returnNull;
        }

        @Override
        public RailwayLine findDirectLine(Station start, Station end) {
            return returnNull ? null : line;
        }
    }
}
