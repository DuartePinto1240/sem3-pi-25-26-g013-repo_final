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
 * Testes unitários para a classe TrafficDispatcherService sem usar Mockito.
 * Utiliza implementações "stub" dos repositórios para simular os dados.
 */
class TrafficDispatcherServiceTest {

    private TrafficDispatcherService service;
    private StubStationRepository stationRepo;
    private StubLocomotiveRepository locoRepo;
    private StubRailwayLineRepository lineRepo;

    private Station s1, s2;
    private Locomotive loco;
    private RailwayLine line;

    @BeforeEach
    void setUp() {
        // Criar objetos de domínio
        s1 = new Station("S1", "Lisboa");
        s2 = new Station("S2", "Porto");
        loco = new Locomotive("L1", "CP2600", 120.0, 80.0, false);

        LineSegment seg1 = new LineSegment(s1, s2, 60.0, 100.0, false);
        LineSegment seg2 = new LineSegment(s2, s1, 40.0, 150.0, false);
        line = new RailwayLine(List.of(seg1, seg2));

        // Criar repositórios stub
        stationRepo = new StubStationRepository(Map.of(
                "S1", s1,
                "S2", s2
        ));
        locoRepo = new StubLocomotiveRepository(Map.of(
                "L1", loco
        ));
        lineRepo = new StubRailwayLineRepository(line);

        // Instanciar o serviço
        service = new TrafficDispatcherService(stationRepo, locoRepo, lineRepo);
    }

    // ---------------- TESTES PRINCIPAIS ----------------



    @Test
    void testCalculateTravelTime_InvalidStation_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                service.calculateTravelTime("INVALID", "S2", "L1"));
    }

    @Test
    void testCalculateTravelTime_InvalidLocomotive_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                service.calculateTravelTime("S1", "S2", "BADID"));
    }

    @Test
    void testCalculateTravelTime_NoDirectLine_ThrowsException() {
        lineRepo.setReturnNull(true);

        assertThrows(IllegalStateException.class, () ->
                service.calculateTravelTime("S1", "S2", "L1"));
    }

    @Test
    void testGetAvailableStations_ReturnsCorrectList() {
        List<Station> stations = service.getAvailableStations();
        assertEquals(2, stations.size());
        assertTrue(stations.contains(s1));
        assertTrue(stations.contains(s2));
    }

    @Test
    void testGetAvailableLocomotives_ReturnsCorrectList() {
        List<Locomotive> locos = service.getAvailableLocomotives();
        assertEquals(1, locos.size());
        assertEquals(loco, locos.get(0));
    }

    // ---------------- STUB CLASSES ----------------

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
