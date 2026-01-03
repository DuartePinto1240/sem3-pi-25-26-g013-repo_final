package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.*;
import main.java.pt.ipp.isep.dei.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for USLP10 - Train Scheduling and Dispatching
 *
 * This controller handles:
 * 1. Getting trains that have routes assigned (from USLP09)
 * 2. Dispatching trains with departure time
 * 3. Calculating arrival times based on:
 *    - Track limitations
 *    - Locomotive power
 *    - Wagon weight
 * 4. Detecting and managing train crossings
 */
public class USLP10Controller {

    private final TrainRepository trainRepo;
    private final ScheduleRepository scheduleRepo;
    private final RouteRepository routeRepo;
    private final SpeedCalculatorService speedCalc;
    private final PathFinderService pathFinder;
    private final SchedulerService scheduler;
    private final CrossingManagerService crossingMgr;

    public USLP10Controller() {
        this.trainRepo = new OracleTrainRepository();
        this.scheduleRepo = new OracleScheduleRepository();
        this.routeRepo = new OracleRouteRepository();

        this.speedCalc = new SpeedCalculatorService();
        this.pathFinder = new PathFinderService(trainRepo, speedCalc);
        this.crossingMgr = new CrossingManagerService();
        this.scheduler = new SchedulerService(
                trainRepo, scheduleRepo, speedCalc, pathFinder, crossingMgr
        );
    }

    /**
     * Get all trains that have routes assigned (ready for dispatch)
     */
    /**
     * Get all trains that have routes assigned (ready for dispatch)
     */
    public List<TrainDTO> getTrainsWithRoutes() {
        return trainRepo.getTrainsWithRoutes(); // Chama método específico
    }
    public List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId) {
        return trainRepo.getLineSegments(fromFacilityId, toFacilityId);
    }

    /**
     * Get route assigned to a train
     */
    public RouteDTO getRouteForTrain(Long trainId) {
        return trainRepo.getRouteForTrain(trainId);
    }

    /**
     * Dispatch train - Main method for USLP10
     * The train MUST already have a route assigned (from USLP09)
     *
     * @param trainId Train to dispatch
     * @param departureTime Planned departure time
     * @param pathMode "MANUAL" or "AUTOMATIC"
     * @param manualPath If manual, specific facilities to visit (can override route)
     * @return Train schedule with calculated arrival times
     */
    public TrainScheduleDTO dispatchTrain(
            Long trainId,
            LocalDateTime departureTime,
            String pathMode,
            List<Long> manualPath
    ) {
        // 1. Get train
        TrainDTO train = trainRepo.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }

        // 2. Get route assigned to train
        RouteDTO route = trainRepo.getRouteForTrain(trainId);
        if (route == null) {
            throw new RuntimeException(
                    "Train " + trainId + " has no route assigned. " +
                            "Please use USLP09 to assign a route first!"
            );
        }

        // 3. Determine path
        List<Long> facilityPath;

        if ("MANUAL".equalsIgnoreCase(pathMode) && manualPath != null && !manualPath.isEmpty()) {
            // Manual path - user can override route
            facilityPath = manualPath;
            System.out.println("ℹ Using manual path (overriding route)");
        } else {
            // Automatic - use route stops
            facilityPath = route.stops().stream()
                    .map(RouteStopDTO::facilityId)
                    .toList();
            System.out.println("ℹ Using automatic path from assigned route");
        }

        // 4. Calculate schedule
        return scheduler.calculateScheduleManual(train, facilityPath, departureTime);
    }

    /**
     * Simplified dispatch - automatic path from route
     */
    public TrainScheduleDTO dispatchTrainAutomatic(
            Long trainId,
            LocalDateTime departureTime
    ) {
        return dispatchTrain(trainId, departureTime, "AUTOMATIC", null);
    }

    /**
     * Dispatch with manual path override
     */
    public TrainScheduleDTO dispatchTrainManual(
            Long trainId,
            LocalDateTime departureTime,
            List<Long> manualPath
    ) {
        return dispatchTrain(trainId, departureTime, "MANUAL", manualPath);
    }

    /**
     * Batch dispatch multiple trains
     */
    public List<TrainScheduleDTO> dispatchBatch(
            List<Long> trainIds,
            List<LocalDateTime> departureTimes
    ) {
        if (trainIds.size() != departureTimes.size()) {
            throw new IllegalArgumentException("Must provide departure time for each train");
        }

        List<TrainScheduleDTO> schedules = new ArrayList<>();

        for (int i = 0; i < trainIds.size(); i++) {
            try {
                TrainScheduleDTO schedule = dispatchTrainAutomatic(
                        trainIds.get(i),
                        departureTimes.get(i)
                );
                schedules.add(schedule);
            } catch (Exception e) {
                System.err.println("⚠ Warning: Failed to dispatch train " + trainIds.get(i) + ": " + e.getMessage());
            }
        }

        // Detect crossings between all schedules
        if (schedules.size() > 1) {
            detectAndManageCrossings(schedules);
        }

        return schedules;
    }

    /**
     * Get active schedules
     */
    public List<TrainScheduleDTO> getActiveSchedules(LocalDateTime from, LocalDateTime to) {
        return scheduleRepo.getActiveSchedules(from, to);
    }

    /**
     * Get train details
     */
    public TrainDTO getTrainById(Long trainId) {
        return trainRepo.getTrainById(trainId);
    }

    // ================================================================
    // PRIVATE HELPERS
    // ================================================================

    private void detectAndManageCrossings(List<TrainScheduleDTO> schedules) {
        // Implement crossing detection logic
        // For each pair of schedules, check if they share segments at overlapping times
        // This is complex and would require detailed implementation
        System.out.println("ℹ Crossing detection not fully implemented");
    }
}