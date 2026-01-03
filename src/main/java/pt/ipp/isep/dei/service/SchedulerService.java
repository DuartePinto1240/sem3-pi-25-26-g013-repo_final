package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.ScheduleRepository;
import main.java.pt.ipp.isep.dei.repository.TrainRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main scheduler service - calculates complete train schedules
 */
public class SchedulerService {

    private final TrainRepository trainRepo;
    private final ScheduleRepository scheduleRepo;
    private final SpeedCalculatorService speedCalc;
    private final PathFinderService pathFinder;
    private final CrossingManagerService crossingMgr;

    public SchedulerService(
            TrainRepository trainRepo,
            ScheduleRepository scheduleRepo,
            SpeedCalculatorService speedCalc,
            PathFinderService pathFinder,
            CrossingManagerService crossingMgr
    ) {
        this.trainRepo = trainRepo;
        this.scheduleRepo = scheduleRepo;
        this.speedCalc = speedCalc;
        this.pathFinder = pathFinder;
        this.crossingMgr = crossingMgr;
    }

    /**
     * Calculate schedule for a single train with manual path
     */
    public TrainScheduleDTO calculateScheduleManual(
            TrainDTO train,
            List<Long> manualPath,
            LocalDateTime departureTime
    ) {
        // Get path segments
        PathDTO path = pathFinder.findPathWithWaypoints(manualPath, train);

        return calculateScheduleForPath(train, path, manualPath, departureTime);
    }

    /**
     * Calculate schedule for a single train with automatic pathfinding
     */
    public TrainScheduleDTO calculateScheduleAutomatic(
            TrainDTO train,
            Long fromFacilityId,
            Long toFacilityId,
            LocalDateTime departureTime
    ) {
        // Find optimal path
        PathDTO path = pathFinder.findPath(fromFacilityId, toFacilityId, train);

        // Extract facility IDs from path
        List<Long> facilityIds = new ArrayList<>();
        facilityIds.add(fromFacilityId);
        for (LineSegmentDTO seg : path.segments()) {
            if (!facilityIds.contains(seg.toFacilityId())) {
                facilityIds.add(seg.toFacilityId());
            }
        }

        return calculateScheduleForPath(train, path, facilityIds, departureTime);
    }

    /**
     * Dispatch multiple trains and calculate crossings
     */
    public List<TrainScheduleDTO> dispatchMultipleTrains(
            List<TrainDTO> trains,
            List<PathDTO> paths,
            List<LocalDateTime> departures
    ) {
        List<TrainScheduleDTO> schedules = new ArrayList<>();

        // Calculate schedule for each train
        for (int i = 0; i < trains.size(); i++) {
            TrainDTO train = trains.get(i);
            PathDTO path = paths.get(i);
            LocalDateTime departure = departures.get(i);

            List<Long> facilityIds = extractFacilityIds(path);
            TrainScheduleDTO schedule = calculateScheduleForPath(train, path, facilityIds, departure);
            schedules.add(schedule);
        }

        // Calculate crossings
        List<LineSegmentDTO> allSegments = paths.stream()
                .flatMap(p -> p.segments().stream())
                .toList();

        List<CrossingOperationDTO> crossings = crossingMgr.calculateCrossings(schedules, allSegments);

        // Add crossings to schedules
        for (TrainScheduleDTO schedule : schedules) {
            List<CrossingOperationDTO> trainCrossings = crossings.stream()
                    .filter(c -> c.trainId1().equals(schedule.trainId())
                            || c.trainId2().equals(schedule.trainId()))
                    .toList();

            // Create new schedule with crossings
            // (Since DTO is immutable, would need to recreate or use builder pattern)
        }

        return schedules;
    }

    // ============================================================
    // CORE CALCULATION
    // ============================================================

    private TrainScheduleDTO calculateScheduleForPath(
            TrainDTO train,
            PathDTO path,
            List<Long> facilityIds,
            LocalDateTime departureTime
    ) {
        // Get train components
        List<LocomotiveDTO> locomotives = getLocomotives(train);
        List<WagonDTO> wagons = getWagons(train);

        // VALIDAR SE TEM PATH
        if (path.segments().isEmpty()) {
            throw new RuntimeException("Cannot calculate schedule: path has no segments");
        }

        // Calculate passage times
        List<PassageTimeDTO> passageTimes = new ArrayList<>();
        LocalDateTime currentTime = departureTime;

        // First stop: departure
        passageTimes.add(new PassageTimeDTO(
                facilityIds.get(0),
                "Start", // would get from DB
                currentTime,
                currentTime,
                true
        ));

        // Calculate for each segment
        int facilityIndex = 1;
        for (LineSegmentDTO segment : path.segments()) {
            // CALCULAR TEMPO DE VIAGEM (COM VELOCIDADE!)
            double travelMinutes = speedCalc.calculateTravelTime(locomotives, wagons, segment);

            // Add travel time
            currentTime = currentTime.plusMinutes((long) Math.ceil(travelMinutes));

            // Check if train stops at this facility
            Long facilityId = facilityIds.get(facilityIndex);
            boolean stops = isScheduledStop(facilityId, facilityIds);

            LocalDateTime arrivalTime = currentTime;
            LocalDateTime departureTime2 = stops ? currentTime.plusMinutes(5) : null; // 5 min stop

            passageTimes.add(new PassageTimeDTO(
                    facilityId,
                    "Facility " + facilityId, // would get from DB
                    arrivalTime,
                    departureTime2,
                    stops
            ));

            if (stops) {
                currentTime = departureTime2;
            }

            facilityIndex++;
        }

        // Create and save schedule
        Long scheduleId = scheduleRepo.createSchedule(train.trainId(), null, departureTime);

        // Save all passage times
        for (PassageTimeDTO passage : passageTimes) {
            scheduleRepo.savePassageTime(scheduleId, passage);
        }

        return new TrainScheduleDTO(
                scheduleId,
                train.trainId(),
                null,
                departureTime,
                passageTimes,
                List.of() // crossings calculated separately
        );
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private List<LocomotiveDTO> getLocomotives(TrainDTO train) {
        List<LocomotiveDTO> locomotives = new ArrayList<>();
        for (Long locoId : train.locomotiveIds()) {
            // Would need to query DB for each locomotive
            // For now, simplified
        }
        return locomotives;
    }

    private List<WagonDTO> getWagons(TrainDTO train) {
        List<WagonDTO> wagons = new ArrayList<>();
        for (Long wagonId : train.wagonIds()) {
            // Would need to query DB for each wagon
        }
        return wagons;
    }

    private boolean isScheduledStop(Long facilityId, List<Long> allFacilities) {
        // First and last are always stops
        return facilityId.equals(allFacilities.get(0))
                || facilityId.equals(allFacilities.get(allFacilities.size() - 1));
    }

    private List<Long> extractFacilityIds(PathDTO path) {
        List<Long> ids = new ArrayList<>();
        if (!path.segments().isEmpty()) {
            ids.add(path.segments().get(0).fromFacilityId());
            for (LineSegmentDTO seg : path.segments()) {
                ids.add(seg.toFacilityId());
            }
        }
        return ids;
    }
}