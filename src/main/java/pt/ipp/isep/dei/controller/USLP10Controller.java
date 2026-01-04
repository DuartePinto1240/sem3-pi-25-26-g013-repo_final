package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.*;
import main.java.pt.ipp.isep.dei.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<TrainDTO> getTrainsWithRoutes() {
        return trainRepo.getTrainsWithRoutes();
    }

    public List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId) {
        return trainRepo.getLineSegments(fromFacilityId, toFacilityId);
    }

    public RouteDTO getRouteForTrain(Long trainId) {
        return trainRepo.getRouteForTrain(trainId);
    }

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
        if ("MANUAL".equalsIgnoreCase(pathMode) && manualPath != null && !manualPath.isEmpty()) {
            System.out.println("ℹ Using manual path (overriding route)");
            return scheduler.calculateScheduleManual(train, manualPath, departureTime);
        }

        // AUTOMATIC: follow RouteStop sequence
        System.out.println("ℹ Using automatic path from assigned route (RouteStop sequence)");
        return scheduler.calculateScheduleForStopSequence(train, route.stops(), departureTime);
    }

    public TrainScheduleDTO dispatchTrainAutomatic(
            Long trainId,
            LocalDateTime departureTime
    ) {
        return dispatchTrain(trainId, departureTime, "AUTOMATIC", null);
    }

    public TrainScheduleDTO dispatchTrainManual(
            Long trainId,
            LocalDateTime departureTime,
            List<Long> manualPath
    ) {
        return dispatchTrain(trainId, departureTime, "MANUAL", manualPath);
    }

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

        if (schedules.size() > 1) {
            detectAndManageCrossings(schedules);
        }

        return schedules;
    }

    public List<TrainScheduleDTO> getActiveSchedules(LocalDateTime from, LocalDateTime to) {
        return scheduleRepo.getActiveSchedules(from, to);
    }

    public TrainDTO getTrainById(Long trainId) {
        return trainRepo.getTrainById(trainId);
    }

    private void detectAndManageCrossings(List<TrainScheduleDTO> schedules) {
        System.out.println("ℹ Crossing detection not fully implemented");
    }
}
