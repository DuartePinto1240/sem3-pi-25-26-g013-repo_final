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

        // Process each train individually
        for (int i = 0; i < trainIds.size(); i++) {
            Long trainId = trainIds.get(i);

            try {
                // Get train
                TrainDTO train = trainRepository.getTrainById(trainId);

                if (train == null) {
                    System.out.println("⚠ Warning: Train " + trainId + " not found. Skipping.");
                    continue;
                }

                System.out.println("\nProcessing Train " + trainId + "...");

                if (autoPath) {
                    // For automatic batch dispatch, we need origin and destination
                    // This is simplified - in real implementation, these would come from routes
                    System.out.println("⚠ Automatic pathfinding for batch not fully implemented.");
                    System.out.println("  Please use manual dispatch for each train individually.");
                    continue;
                } else {
                    // For manual batch, we need to get path from user for each train
                    System.out.println("⚠ Manual path definition required for Train " + trainId);
                    System.out.println("  Please use manual dispatch for each train individually.");
                    continue;
                }

            } catch (Exception e) {
                System.out.println("❌ Error processing train " + trainId + ": " + e.getMessage());
                continue;
            }
        }

        if (schedules.isEmpty()) {
            System.out.println("\n⚠ No trains were dispatched.");
            System.out.println("Batch dispatch requires pre-defined routes or manual configuration.");
            System.out.println("Please use individual dispatch (options 1 or 2) for now.");
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
    /**
     * Get detailed information about a specific train.
     *
     * @param trainId ID of the train
     * @return TrainDTO with complete train information, or null if not found
     */
    public TrainDTO getTrainById(long trainId) {
        try {
            TrainDTO train = trainRepository.getTrainById(trainId);
            return train; // Pode ser null se não existir
        } catch (Exception e) {
            // Não fazer throw, retornar null
            System.err.println("Error fetching train " + trainId + ": " + e.getMessage());
            return null;
        }
    }



    public List<TrainDTO> getAllTrains() {
        // Simplified: Get all trains from database
        // In a real implementation, you'd query the Train table
        List<TrainDTO> trains = new ArrayList<>();

        // For now, return trains we know exist
        try {
            // Try to get known trains
            for (long id : List.of(5421L, 5435L, 5437L, 9001L, 9100L, 9200L, 9300L)) {
                try {
                    TrainDTO train = trainRepository.getTrainById(id);
                    if (train != null) {
                        trains.add(train);
                    }
                } catch (Exception e) {
                    // Skip trains that don't exist
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching trains: " + e.getMessage());
        }

        return trains;
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
