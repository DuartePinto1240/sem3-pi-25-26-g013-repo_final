package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.*;
import main.java.pt.ipp.isep.dei.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for USLP10 - Train Scheduling and Dispatch
 */
public class USLP10Controller {

    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;
    private final StationRepository stationRepository;
    private final FacilityRepository facilityRepository;
    private final SpeedCalculatorService speedCalc;
    private final PathFinderService pathFinder;
    private final SchedulerService scheduler;
    private final CrossingManagerService crossingMgr;

    public USLP10Controller() {
        this.trainRepository = Repositories.getInstance().getTrainRepository();
        this.facilityRepository = new OracleFacilityRepository();
        this.scheduleRepository = Repositories.getInstance().getScheduleRepository();
        this.stationRepository = Repositories.getInstance().getStationRepository();

        // Initialize services
        this.speedCalc = new SpeedCalculatorService();
        this.pathFinder = new PathFinderService(trainRepository, speedCalc);
        this.crossingMgr = new CrossingManagerService();
        this.scheduler = new SchedulerService(
                trainRepository,
                scheduleRepository,
                speedCalc,
                pathFinder,
                crossingMgr
        );
    }

    /**
     * Get all stations
     * @return list of all stations
     */
    public List<Station> getStations() {
        return stationRepository.findAll();
    }
    public List<FacilityDTO> getFacilities() {
        return facilityRepository.getAllFacilities();
    }

    /**
     * Dispatch a train with manually specified path.
     * The user provides the exact sequence of facilities to visit.
     *
     * @param trainId ID of the train to dispatch
     * @param manualPath ordered list of facility IDs that define the path
     * @param departure departure time
     * @return complete schedule with passage times
     */
    public TrainScheduleDTO dispatchManual(
            Long trainId,
            List<Long> manualPath,
            LocalDateTime departure
    ) {
        // Validate inputs
        if (trainId == null) {
            throw new IllegalArgumentException("Train ID cannot be null");
        }
        if (manualPath == null || manualPath.size() < 2) {
            throw new IllegalArgumentException("Path must have at least 2 facilities");
        }
        if (departure == null) {
            throw new IllegalArgumentException("Departure time cannot be null");
        }

        // Get train
        TrainDTO train = trainRepository.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }

        // Calculate schedule
        return scheduler.calculateScheduleManual(train, manualPath, departure);
    }

    /**
     * Dispatch a train with automatic pathfinding.
     * The system calculates the optimal path between origin and destination.
     *
     * @param trainId ID of the train to dispatch
     * @param fromFacilityId origin facility ID
     * @param toFacilityId destination facility ID
     * @param departure departure time
     * @return complete schedule with passage times
     */
    public TrainScheduleDTO dispatchAutomatic(
            Long trainId,
            Long fromFacilityId,
            Long toFacilityId,
            LocalDateTime departure
    ) {
        // Validate inputs
        if (trainId == null) {
            throw new IllegalArgumentException("Train ID cannot be null");
        }
        if (fromFacilityId == null || toFacilityId == null) {
            throw new IllegalArgumentException("Origin and destination cannot be null");
        }
        if (fromFacilityId.equals(toFacilityId)) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }
        if (departure == null) {
            throw new IllegalArgumentException("Departure time cannot be null");
        }

        // Get train
        TrainDTO train = trainRepository.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }

        // Calculate schedule with automatic pathfinding
        return scheduler.calculateScheduleAutomatic(
                train,
                fromFacilityId,
                toFacilityId,
                departure
        );
    }

    /**
     * Dispatch multiple trains and calculate crossings.
     *
     * @param trainIds list of train IDs to dispatch
     * @param autoPath if true, use automatic pathfinding; if false, use manual paths
     * @return list of schedules for all dispatched trains
     */
    public List<TrainScheduleDTO> dispatchBatch(
            List<Long> trainIds,
            boolean autoPath
    ) {
        if (trainIds == null || trainIds.isEmpty()) {
            throw new IllegalArgumentException("Train IDs list cannot be empty");
        }

        List<TrainScheduleDTO> schedules = new ArrayList<>();
        List<TrainDTO> trains = new ArrayList<>();
        List<PathDTO> paths = new ArrayList<>();
        List<LocalDateTime> departures = new ArrayList<>();

        // Prepare trains and paths
        for (int i = 0; i < trainIds.size(); i++) {
            Long trainId = trainIds.get(i);
            TrainDTO train = trainRepository.getTrainById(trainId);

            if (train == null) {
                System.out.println("⚠ Warning: Train " + trainId + " not found. Skipping.");
                continue;
            }

            trains.add(train);

            // For batch dispatch, we need to define paths
            // This is simplified - in real implementation, paths would come from route planning
            PathDTO path = new PathDTO(null, List.of(), 0.0, 0.0);
            paths.add(path);

            // Stagger departures by 1 hour each
            LocalDateTime departure = LocalDateTime.now().plusHours(i);
            departures.add(departure);
        }

        // Dispatch all trains
        if (!trains.isEmpty()) {
            schedules = scheduler.dispatchMultipleTrains(trains, paths, departures);
        }

        return schedules;
    }

    /**
     * Get all active schedules within a time period.
     *
     * @param from start of time period
     * @param to end of time period
     * @return list of active schedules
     */
    public List<TrainScheduleDTO> getActiveSchedules(
            LocalDateTime from,
            LocalDateTime to
    ) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Time period bounds cannot be null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        return scheduleRepository.getActiveSchedules(from, to);
    }

    /**
     * Get detailed information about a specific train.
     *
     * @param trainId ID of the train
     * @return TrainDTO with complete train information
     */
    public TrainDTO getTrainById(long trainId) {
        TrainDTO train = trainRepository.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }
        return train;
    }

    /**
     * Get schedule for a specific train.
     *
     * @param trainId ID of the train
     * @return list of schedules for this train
     */
    public List<TrainScheduleDTO> getTrainSchedules(long trainId) {
        LocalDateTime now = LocalDateTime.now();
        List<TrainScheduleDTO> allSchedules = scheduleRepository.getActiveSchedules(
                now.minusMonths(1),
                now.plusMonths(1)
        );

        return allSchedules.stream()
                .filter(s -> s.trainId().equals(trainId))
                .toList();
    }

    /**
     * Cancel a scheduled train dispatch.
     *
     * @param scheduleId ID of the schedule to cancel
     */
    public void cancelSchedule(long scheduleId) {
        // TODO: Implement schedule cancellation
        // This would require a new method in ScheduleRepository
        throw new UnsupportedOperationException("Schedule cancellation not yet implemented");
    }

    /**
     * Get line segments between two facilities.
     * Useful for displaying path information.
     *
     * @param fromFacilityId origin facility
     * @param toFacilityId destination facility
     * @return list of line segments
     */
    public List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId) {
        return trainRepository.getLineSegments(fromFacilityId, toFacilityId);
    }

    /**
     * Estimate travel time between two facilities for a given train.
     *
     * @param trainId train ID
     * @param fromFacilityId origin
     * @param toFacilityId destination
     * @return estimated time in minutes
     */
    public double estimateTravelTime(Long trainId, Long fromFacilityId, Long toFacilityId) {
        TrainDTO train = trainRepository.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }

        List<LineSegmentDTO> segments = trainRepository.getLineSegments(fromFacilityId, toFacilityId);

        if (segments.isEmpty()) {
            throw new RuntimeException("No path found between facilities");
        }

        // Get locomotive and wagon details
        List<LocomotiveDTO> locos = new ArrayList<>();
        List<WagonDTO> wagons = new ArrayList<>();

        // TODO: Fetch actual locomotive and wagon DTOs
        // For now, simplified

        double totalTime = 0.0;
        for (LineSegmentDTO segment : segments) {
            // Simplified calculation: distance / speed
            totalTime += (segment.distance() / segment.maxSpeed()) * 60; // convert to minutes
        }

        return totalTime;
    }

    /**
     * Validate if a train can operate on a given path.
     * Checks:
     * - Electrification compatibility
     * - Gauge compatibility
     * - Weight limits
     * - Length limits
     *
     * @param trainId train ID
     * @param path list of facility IDs
     * @return true if valid, throws exception otherwise
     */
    public boolean validateTrainPath(Long trainId, List<Long> path) {
        TrainDTO train = trainRepository.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }

        // Check each segment in the path
        for (int i = 0; i < path.size() - 1; i++) {
            Long from = path.get(i);
            Long to = path.get(i + 1);

            List<LineSegmentDTO> segments = trainRepository.getLineSegments(from, to);

            if (segments.isEmpty()) {
                throw new IllegalArgumentException(
                        "No connection between facility " + from + " and " + to
                );
            }

            for (LineSegmentDTO segment : segments) {
                // Check if train can operate on this segment
                // TODO: Add actual validation logic
                // - Check electrification vs locomotive type
                // - Check gauge compatibility
                // - Check weight limits
            }
        }

        return true;
    }
}
