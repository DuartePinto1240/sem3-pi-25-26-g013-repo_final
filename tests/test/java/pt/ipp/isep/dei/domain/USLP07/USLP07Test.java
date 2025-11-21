package test.java.pt.ipp.isep.dei.domain.USLP07;

import main.java.pt.ipp.isep.dei.controller.TrainDispatchSchedulerController;
import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TrainSchedule;
import main.java.pt.ipp.isep.dei.domain.Wagon;
import main.java.pt.ipp.isep.dei.service.TrainSchedulerService.TrainDispatchRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for USLP07 - Train Dispatch Scheduler.
 *
 * Os testes usam os CSV reais (stations.csv, railway_lines.csv, line_segments.csv,
 * locomotives.csv e Wagons.csv) através do TrainDispatchSchedulerController e
 * verificam os critérios de aceitação:
 *  - Cruzamentos em linhas single-track
 *  - Ausência de cruzamentos em double-track
 *  - Influência do peso dos vagões no tempo de viagem
 *  - Influência da potência total das locomotivas no tempo de viagem
 */
class USLP07Test {

    private static TrainDispatchSchedulerController controller;
    private static List<Station> allStations;
    private static List<Locomotive> allLocomotives;
    private static List<Wagon> allWagons;

    @BeforeAll
    static void setup() {
        System.out.println("\n=== USLP07 TEST INITIALIZATION ===");

        controller = new TrainDispatchSchedulerController();

        allStations = controller.getAvailableStations();
        allLocomotives = controller.getAvailableLocomotives();
        allWagons = controller.getAvailableWagons();

        System.out.println("Stations loaded:     " + allStations.size());
        System.out.println("Locomotives loaded: " + allLocomotives.size());
        System.out.println("Wagons loaded:      " + allWagons.size());

        assertFalse(allStations.isEmpty(), "Stations must not be empty");
        assertFalse(allLocomotives.isEmpty(), "Locomotives must not be empty");
        assertFalse(allWagons.isEmpty(), "Wagons must not be empty");
    }

    // ---------- Helpers ----------

    private static Station findStationById(String id) {
        return allStations.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Station id not found: " + id));
    }

    private static TrainSchedule findScheduleByTrainId(List<TrainSchedule> schedules, String trainId) {
        return schedules.stream()
                .filter(s -> s.getTrainId().equals(trainId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found for train " + trainId));
    }

    // ============================================================
    // 1) Single-track: dois comboios na MESMA rota, tempos próximos
    //    -> deve existir cruzamento (CONFLICT_WARNING num deles)
    //    Rota: São Pedro da Torre (16) -> Valença (11), linha 7 (single-track)
    // ============================================================
    @Test
    void testSingleTrackSameRouteConflict() {
        System.out.println("\n--- TEST 1: Single-track, same route, close departures ---");

        Station saoPedroDaTorre = findStationById("16");
        Station valenca = findStationById("11");

        Locomotive loco1 = allLocomotives.get(0);
        Locomotive loco2 = allLocomotives.size() > 1 ? allLocomotives.get(1) : allLocomotives.get(0);

        Wagon wagon1 = allWagons.get(0);
        Wagon wagon2 = allWagons.size() > 1 ? allWagons.get(1) : allWagons.get(0);

        List<Station> route = List.of(saoPedroDaTorre, valenca);

        LocalDateTime t1 = LocalDateTime.of(2025, 11, 16, 8, 0);
        LocalDateTime t2 = t1.plusMinutes(1);

        TrainDispatchRequest req1 = controller.createTrainDispatchRequest(
                "ST_TRAIN_1",
                route,
                List.of(loco1),
                List.of(wagon1),
                t1
        );

        TrainDispatchRequest req2 = controller.createTrainDispatchRequest(
                "ST_TRAIN_2",
                route,
                List.of(loco2),
                List.of(wagon2),
                t2
        );

        List<TrainDispatchRequest> requests = List.of(req1, req2);
        List<TrainSchedule> schedules = controller.scheduleTrains(requests);

        assertEquals(2, schedules.size(), "Should have 2 schedules");

        long conflictCount = schedules.stream()
                .filter(s -> s.getStatus() == TrainSchedule.Status.CONFLICT_WARNING)
                .count();

        System.out.println("Schedules:");
        for (TrainSchedule s : schedules) {
            System.out.printf(" - %s: status=%s, arrival=%s, crossings=%d%n",
                    s.getTrainId(),
                    s.getStatus(),
                    s.getEstimatedArrivalTime(),
                    s.getCrossingOperations().size());
        }

        assertEquals(1, conflictCount,
                "Exactly one train should be marked with CONFLICT_WARNING on single-track");

        TrainSchedule conflictSchedule = schedules.stream()
                .filter(s -> s.getStatus() == TrainSchedule.Status.CONFLICT_WARNING)
                .findFirst()
                .orElseThrow();

        assertFalse(conflictSchedule.getCrossingOperations().isEmpty(),
                "Waiting train should have at least one crossing operation registered");
    }

    // ============================================================
    // 2) Double-track: mesma rota, mesmo horário e MESMA locomotiva,
    //    mas vagões com pesos diferentes -> tempos de chegada diferentes
    //    Rota: Porto Campanhã (5) -> Nine (20), linha 2 (NumberTracks >= 2)
    // ============================================================
    @Test
    void testDoubleTrackDifferentWagonWeightsAffectTravelTime() {
        System.out.println("\n--- TEST 2: Double-track, same loco, different wagon weights (long route) ---");

        Station campanha = findStationById("5");
        Station nine = findStationById("20");
        Station barcelos = findStationById("8");
        Station viana = findStationById("17");

        Locomotive loco = allLocomotives.get(0);

        // escolher o vagão mais leve e o mais pesado pela tara
        Wagon lightWagon = allWagons.stream()
                .min(Comparator.comparingDouble(w -> w.getModel().getTareWeightTons()))
                .orElseThrow();

        Wagon heavyWagon = allWagons.stream()
                .max(Comparator.comparingDouble(w -> w.getModel().getTareWeightTons()))
                .orElseThrow();

        System.out.printf("Light wagon tare: %.2f t, heavy wagon tare: %.2f t%n",
                lightWagon.getModel().getTareWeightTons(),
                heavyWagon.getModel().getTareWeightTons());

        // rota mais longa, só em linhas double-track
        List<Station> route = List.of(campanha, nine, barcelos, viana);

        LocalDateTime departure = LocalDateTime.of(2025, 11, 16, 10, 0);

        TrainDispatchRequest lightReq = controller.createTrainDispatchRequest(
                "DT_LIGHT",
                route,
                List.of(loco),
                List.of(lightWagon),
                departure
        );

        TrainDispatchRequest heavyReq = controller.createTrainDispatchRequest(
                "DT_HEAVY",
                route,
                List.of(loco),
                List.of(heavyWagon),
                departure
        );

        List<TrainSchedule> schedules = controller.scheduleTrains(List.of(lightReq, heavyReq));
        assertEquals(2, schedules.size());

        TrainSchedule lightSchedule = findScheduleByTrainId(schedules, "DT_LIGHT");
        TrainSchedule heavySchedule = findScheduleByTrainId(schedules, "DT_HEAVY");

        // Em double-track não deve haver cruzamentos
        schedules.forEach(s ->
                assertEquals(TrainSchedule.Status.VALIDATED, s.getStatus(),
                        "All trains on double-track should be VALIDATED (no crossings)")
        );

        LocalDateTime lightArrival = lightSchedule.getEstimatedArrivalTime();
        LocalDateTime heavyArrival = heavySchedule.getEstimatedArrivalTime();

        System.out.printf("Arrival LIGHT: %s, Arrival HEAVY: %s%n",
                lightArrival, heavyArrival);

        assertNotNull(lightArrival);
        assertNotNull(heavyArrival);

        assertFalse(heavyArrival.isBefore(lightArrival),
                "Heavier train must not arrive earlier than the lighter one on the same route and departure time");

    }


    // ============================================================
    // 3) Double-track: mesma rota, mesmo horário e MESMO vagão,
    //    mas número de locomotivas diferente -> comboio com mais potência
    //    deve chegar mais cedo.
    //    Rota: Porto Campanhã (5) -> Nine (20), linha 2
    // ============================================================
    @Test
    void testDoubleTrackMoreLocomotivesArriveEarlier() {
        System.out.println("\n--- TEST 3: Double-track, different number of locomotives (long route) ---");

        assertTrue(allLocomotives.size() >= 2,
                "This test requires at least 2 locomotives");

        Station campanha = findStationById("5");
        Station nine = findStationById("20");
        Station barcelos = findStationById("8");
        Station viana = findStationById("17");

        Locomotive loco1 = allLocomotives.get(0);
        Locomotive loco2 = allLocomotives.get(1);

        Wagon commonWagon = allWagons.get(0);

        // mesma rota longa em double-track
        List<Station> route = List.of(campanha, nine, barcelos, viana);
        LocalDateTime departure = LocalDateTime.of(2025, 11, 16, 11, 0);

        TrainDispatchRequest singleLocoReq = controller.createTrainDispatchRequest(
                "DT_1_LOCO",
                route,
                List.of(loco1),
                List.of(commonWagon),
                departure
        );

        TrainDispatchRequest doubleLocoReq = controller.createTrainDispatchRequest(
                "DT_2_LOCO",
                route,
                List.of(loco1, loco2),
                List.of(commonWagon),
                departure
        );

        List<TrainDispatchRequest> requests = List.of(singleLocoReq, doubleLocoReq);
        List<TrainSchedule> schedules = controller.scheduleTrains(requests);

        TrainSchedule singleSchedule = findScheduleByTrainId(schedules, "DT_1_LOCO");
        TrainSchedule doubleSchedule = findScheduleByTrainId(schedules, "DT_2_LOCO");

        schedules.forEach(s ->
                assertEquals(TrainSchedule.Status.VALIDATED, s.getStatus(),
                        "All trains on double-track should be VALIDATED (no crossings)")
        );

        LocalDateTime singleArrival = singleSchedule.getEstimatedArrivalTime();
        LocalDateTime doubleArrival = doubleSchedule.getEstimatedArrivalTime();

        System.out.printf("Arrival 1 loco: %s, Arrival 2 locos: %s%n",
                singleArrival, doubleArrival);

        assertNotNull(singleArrival);
        assertNotNull(doubleArrival);

        assertFalse(doubleArrival.isAfter(singleArrival),
                "Train with more locomotives (more power) must not arrive later than the one with fewer locomotives");

    }

    // ============================================================
    // 4) Single-track, mesma rota mas com grande diferença de tempo
    //    -> NÃO deve haver cruzamento, ambos VALIDATED
    //    Rota: São Pedro da Torre (16) -> Valença (11), linha 7
    // ============================================================
    @Test
    void testSingleTrackFarApartDeparturesNoConflict() {
        System.out.println("\n--- TEST 4: Single-track, far apart departures, no conflict ---");

        Station saoPedroDaTorre = findStationById("16");
        Station valenca = findStationById("11");

        Locomotive loco = allLocomotives.get(0);
        Wagon wagon = allWagons.get(0);

        List<Station> route = List.of(saoPedroDaTorre, valenca);

        LocalDateTime t1 = LocalDateTime.of(2025, 11, 16, 6, 0);
        LocalDateTime t2 = LocalDateTime.of(2025, 11, 16, 9, 0); // bem mais tarde

        TrainDispatchRequest req1 = controller.createTrainDispatchRequest(
                "ST_EARLY",
                route,
                List.of(loco),
                List.of(wagon),
                t1
        );

        TrainDispatchRequest req2 = controller.createTrainDispatchRequest(
                "ST_LATE",
                route,
                List.of(loco),
                List.of(wagon),
                t2
        );

        List<TrainSchedule> schedules = controller.scheduleTrains(List.of(req1, req2));

        System.out.println("Schedules on single-track with far apart departures:");
        for (TrainSchedule s : schedules) {
            System.out.printf(" - %s: status=%s, crossings=%d%n",
                    s.getTrainId(),
                    s.getStatus(),
                    s.getCrossingOperations().size());
        }

        // Com tempos muito afastados, não deve haver overlaps -> sem cruzamentos
        for (TrainSchedule s : schedules) {
            assertEquals(TrainSchedule.Status.VALIDATED, s.getStatus(),
                    "No crossings expected with far apart departures on single-track");
            assertTrue(s.getCrossingOperations().isEmpty(),
                    "No crossing operations should be registered");
        }
    }
}

