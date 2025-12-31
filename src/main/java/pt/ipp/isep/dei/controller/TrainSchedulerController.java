package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.FreightRepository;
import main.java.pt.ipp.isep.dei.repository.ScheduleRepository;
import main.java.pt.ipp.isep.dei.repository.TrainRepository;
import main.java.pt.ipp.isep.dei.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Main controller for USLP09 (Train Assembly) and USLP10 (Train Scheduling)
 */
public class TrainSchedulerController {

    private final TrainRepository trainRepo;
    private final ScheduleRepository scheduleRepo;
    private final FreightRepository freightRepo;
    private final RoutePlannerService routePlanner;
    private final SpeedCalculatorService speedCalc;
    private final PathFinderService pathFinder;
    private final SchedulerService scheduler;
    private final CrossingManagerService crossingMgr;

    public TrainSchedulerController(
            TrainRepository trainRepo,
            ScheduleRepository scheduleRepo,
            FreightRepository freightRepo,
            RoutePlannerService routePlanner,
            SpeedCalculatorService speedCalc,
            PathFinderService pathFinder,
            SchedulerService scheduler,
            CrossingManagerService crossingMgr
    ) {
        this.trainRepo = trainRepo;
        this.scheduleRepo = scheduleRepo;
        this.freightRepo = freightRepo;
        this.routePlanner = routePlanner;
        this.speedCalc = speedCalc;
        this.pathFinder = pathFinder;
        this.scheduler = scheduler;
        this.crossingMgr = crossingMgr;
    }

    // ============================================================
    // USLP09 - TRAIN ASSEMBLY
    // ============================================================

    /**
     * Assemble a train for a given route
     * Interactive method that guides user through locomotive and wagon selection
     */
    public TrainDTO assembleTrain(RoutePlanDTO route, Scanner scanner) {
        System.out.println("\n=== TRAIN ASSEMBLY ===");
        System.out.println("Route: " + formatRoute(route));
        System.out.println();

        // Get starting facility
        Long startFacilityId = route.getStops().get(0).getFacilityId();
        String startFacilityName = route.getStops().get(0).getFacilityName();

        System.out.println("Starting facility: " + startFacilityName + " (ID: " + startFacilityId + ")");
        System.out.println();

        // Step 1: Select Locomotives
        List<Long> selectedLocoIds = selectLocomotives(startFacilityId, scanner);
        if (selectedLocoIds.isEmpty()) {
            throw new RuntimeException("No locomotives selected. Cannot create train.");
        }

        // Step 2: Select Wagons
        List<Long> selectedWagonIds = selectWagons(startFacilityId, route, scanner);
        if (selectedWagonIds.isEmpty()) {
            throw new RuntimeException("No wagons selected. Cannot create train.");
        }

        // Step 3: Validate train composition
        List<LocomotiveDTO> locomotives = selectedLocoIds.stream()
                .map(id -> trainRepo.getAvailableLocomotives(startFacilityId).stream()
                        .filter(l -> l.locomotiveId().equals(id))
                        .findFirst()
                        .orElse(null))
                .filter(l -> l != null)
                .collect(Collectors.toList());

        List<WagonDTO> wagons = selectedWagonIds.stream()
                .map(id -> trainRepo.getAvailableWagons(startFacilityId).stream()
                        .filter(w -> w.wagonId().equals(id))
                        .findFirst()
                        .orElse(null))
                .filter(w -> w != null)
                .collect(Collectors.toList());

        double totalLength = speedCalc.calculateTotalLength(locomotives, wagons);
        double totalWeight = speedCalc.calculateTotalWeight(locomotives, wagons);

        System.out.println("\n--- Train Summary ---");
        System.out.println("Locomotives: " + locomotives.size());
        System.out.println("Wagons: " + wagons.size());
        System.out.printf("Total Length: %.2f m%n", totalLength);
        System.out.printf("Total Weight: %.2f tonnes%n", totalWeight);
        System.out.println();

        // Step 4: Create train
        System.out.print("Confirm train creation? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("y")) {
            System.out.println("Train creation cancelled.");
            return null;
        }

        Long trainId = trainRepo.createTrain(selectedLocoIds, selectedWagonIds);
        System.out.println("✓ Train created with ID: " + trainId);

        // Step 5: Assign to route (if needed)
        trainRepo.assignTrainToRoute(trainId, null); // routeId not used in current schema

        return trainRepo.getTrainById(trainId);
    }

    /**
     * Automated train assembly based on route requirements
     */
    public TrainDTO assembleTrainAutomatic(RoutePlanDTO route) {
        System.out.println("\n=== AUTOMATIC TRAIN ASSEMBLY ===");

        Long startFacilityId = route.getStops().get(0).getFacilityId();

        // Get all available resources
        List<LocomotiveDTO> availableLocos = trainRepo.getAvailableLocomotives(startFacilityId)
                .stream()
                .filter(l -> "PARKED".equals(l.status()))
                .collect(Collectors.toList());

        List<WagonDTO> availableWagons = trainRepo.getAvailableWagons(startFacilityId)
                .stream()
                .filter(w -> "PARKED".equals(w.status()))
                .collect(Collectors.toList());

        if (availableLocos.isEmpty()) {
            throw new RuntimeException("No locomotives available at " + startFacilityId);
        }

        // Simple heuristic: pick first available locomotive
        List<Long> locoIds = List.of(availableLocos.get(0).locomotiveId());

        // Pick wagons needed for route freights
        List<Long> wagonIds = route.getStops().stream()
                .flatMap(stop -> stop.getToLoad().stream())
                .map(CargoDTO::wagonId)
                .distinct()
                .collect(Collectors.toList());

        // If no specific wagons, pick first 2 available
        if (wagonIds.isEmpty() && !availableWagons.isEmpty()) {
            wagonIds = availableWagons.stream()
                    .limit(2)
                    .map(WagonDTO::wagonId)
                    .collect(Collectors.toList());
        }

        Long trainId = trainRepo.createTrain(locoIds, wagonIds);
        System.out.println("✓ Auto-assembled train ID: " + trainId);

        return trainRepo.getTrainById(trainId);
    }

    // ============================================================
    // USLP10 - TRAIN DISPATCH (MANUAL PATH)
    // ============================================================

    /**
     * Dispatch a train with manually specified path
     */
    public TrainScheduleDTO dispatchManual(
            Long trainId,
            List<Long> manualPath,
            LocalDateTime departure
    ) {
        System.out.println("\n=== MANUAL DISPATCH ===");
        System.out.println("Train ID: " + trainId);
        System.out.println("Path: " + manualPath);
        System.out.println("Departure: " + departure);
        System.out.println();

        // Get train details
        TrainDTO train = trainRepo.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }

        // Calculate schedule
        TrainScheduleDTO schedule = scheduler.calculateScheduleManual(train, manualPath, departure);

        // Display schedule
        displaySchedule(schedule);

        return schedule;
    }

    // ============================================================
    // USLP10 - TRAIN DISPATCH (AUTOMATIC PATH)
    // ============================================================

    /**
     * Dispatch a train with automatic pathfinding
     */
    public TrainScheduleDTO dispatchAutomatic(
            Long trainId,
            Long fromFacilityId,
            Long toFacilityId,
            LocalDateTime departure
    ) {
        System.out.println("\n=== AUTOMATIC DISPATCH ===");
        System.out.println("Train ID: " + trainId);
        System.out.println("From: " + fromFacilityId);
        System.out.println("To: " + toFacilityId);
        System.out.println("Departure: " + departure);
        System.out.println();

        // Get train details
        TrainDTO train = trainRepo.getTrainById(trainId);
        if (train == null) {
            throw new RuntimeException("Train not found: " + trainId);
        }

        // Calculate schedule with automatic pathfinding
        TrainScheduleDTO schedule = scheduler.calculateScheduleAutomatic(
                train, fromFacilityId, toFacilityId, departure
        );

        // Display schedule
        displaySchedule(schedule);

        return schedule;
    }

    // ============================================================
    // USLP10 - BATCH DISPATCH
    // ============================================================

    /**
     * Dispatch multiple trains and calculate crossings
     */
    public List<TrainScheduleDTO> dispatchBatch(
            List<Long> trainIds,
            boolean autoPath
    ) {
        System.out.println("\n=== BATCH DISPATCH ===");
        System.out.println("Trains: " + trainIds);
        System.out.println("Auto Path: " + autoPath);
        System.out.println();

        List<TrainDTO> trains = new ArrayList<>();
        List<PathDTO> paths = new ArrayList<>();
        List<LocalDateTime> departures = new ArrayList<>();

        for (Long trainId : trainIds) {
            TrainDTO train = trainRepo.getTrainById(trainId);
            if (train == null) {
                System.out.println("⚠ Warning: Train " + trainId + " not found. Skipping.");
                continue;
            }

            trains.add(train);

            // For simplicity, use dummy path and departure
            // In real implementation, these would come from route planning
            PathDTO path = new PathDTO(null, List.of(), 0.0, 0.0);
            paths.add(path);
            departures.add(LocalDateTime.now().plusHours(trainIds.indexOf(trainId)));
        }

        // Dispatch all trains
        List<TrainScheduleDTO> schedules = scheduler.dispatchMultipleTrains(trains, paths, departures);

        // Display all schedules
        for (TrainScheduleDTO schedule : schedules) {
            displaySchedule(schedule);
        }

        // Display crossings
        if (!schedules.isEmpty() && !schedules.get(0).crossings().isEmpty()) {
            System.out.println("\n=== CROSSING OPERATIONS ===");
            schedules.stream()
                    .flatMap(s -> s.crossings().stream())
                    .distinct()
                    .forEach(this::displayCrossing);
        }

        return schedules;
    }

    // ============================================================
    // HELPER METHODS - LOCOMOTIVE SELECTION
    // ============================================================

    private List<Long> selectLocomotives(Long facilityId, Scanner scanner) {
        List<LocomotiveDTO> available = trainRepo.getAvailableLocomotives(facilityId)
                .stream()
                .filter(l -> "PARKED".equals(l.status()))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("⚠ No locomotives available at this facility.");
            return List.of();
        }

        System.out.println("--- Available Locomotives ---");
        for (int i = 0; i < available.size(); i++) {
            LocomotiveDTO loco = available.get(i);
            System.out.printf("%d. [%d] %s - %s - %.0f kW - %.0f km/h - %.1f t%n",
                    i + 1,
                    loco.locomotiveId(),
                    loco.model(),
                    loco.type(),
                    loco.power(),
                    loco.maxSpeed(),
                    loco.weight());
        }
        System.out.println();

        System.out.print("Select locomotive(s) (comma-separated numbers, e.g. 1,2): ");
        String input = scanner.nextLine().trim();

        List<Long> selected = new ArrayList<>();
        for (String num : input.split(",")) {
            try {
                int index = Integer.parseInt(num.trim()) - 1;
                if (index >= 0 && index < available.size()) {
                    selected.add(available.get(index).locomotiveId());
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Invalid input: " + num);
            }
        }

        System.out.println("✓ Selected " + selected.size() + " locomotive(s)");
        return selected;
    }

    // ============================================================
    // HELPER METHODS - WAGON SELECTION
    // ============================================================

    private List<Long> selectWagons(Long facilityId, RoutePlanDTO route, Scanner scanner) {
        List<WagonDTO> available = trainRepo.getAvailableWagons(facilityId)
                .stream()
                .filter(w -> "PARKED".equals(w.status()))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("⚠ No wagons available at this facility.");
            return List.of();
        }

        // Show wagons needed for route
        List<Long> requiredWagonIds = route.getStops().stream()
                .flatMap(stop -> stop.getToLoad().stream())
                .map(CargoDTO::wagonId)
                .distinct()
                .collect(Collectors.toList());

        System.out.println("--- Available Wagons ---");
        System.out.println("(* = required for route freights)");

        for (int i = 0; i < available.size(); i++) {
            WagonDTO wagon = available.get(i);
            boolean required = requiredWagonIds.contains(wagon.wagonId());
            System.out.printf("%s%d. [%d] %s - %.1f t payload - %.1f m%n",
                    required ? "* " : "  ",
                    i + 1,
                    wagon.wagonId(),
                    wagon.type(),
                    wagon.payload(),
                    wagon.length());
        }
        System.out.println();

        System.out.print("Select wagon(s) (comma-separated, or 'auto' for required only): ");
        String input = scanner.nextLine().trim();

        List<Long> selected = new ArrayList<>();

        if ("auto".equalsIgnoreCase(input)) {
            selected.addAll(requiredWagonIds);
        } else {
            for (String num : input.split(",")) {
                try {
                    int index = Integer.parseInt(num.trim()) - 1;
                    if (index >= 0 && index < available.size()) {
                        selected.add(available.get(index).wagonId());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Invalid input: " + num);
                }
            }
        }

        System.out.println("✓ Selected " + selected.size() + " wagon(s)");
        return selected;
    }

    // ============================================================
    // HELPER METHODS - DISPLAY
    // ============================================================

    private void displaySchedule(TrainScheduleDTO schedule) {
        System.out.println("\n--- Schedule for Train " + schedule.trainId() + " ---");
        System.out.println("Departure: " + schedule.departureTime());
        System.out.println("\nPassage Times:");
        System.out.println(String.format("%-30s %-20s %-20s %-6s",
                "Facility", "Arrival", "Departure", "Stops"));
        System.out.println("-".repeat(80));

        for (PassageTimeDTO passage : schedule.passageTimes()) {
            System.out.printf("%-30s %-20s %-20s %-6s%n",
                    passage.facilityName(),
                    passage.arrivalTime(),
                    passage.departureTime() != null ? passage.departureTime() : "-",
                    passage.stops() ? "YES" : "NO");
        }

        if (!schedule.crossings().isEmpty()) {
            System.out.println("\nCrossing Operations: " + schedule.crossings().size());
        }

        System.out.println();
    }

    private void displayCrossing(CrossingOperationDTO crossing) {
        System.out.printf("  Crossing at %s (%s): Train %d ↔ Train %d @ %s%n",
                crossing.facilityName(),
                crossing.crossingType(),
                crossing.trainId1(),
                crossing.trainId2(),
                crossing.crossingTime());
    }

    private String formatRoute(RoutePlanDTO route) {
        return route.getStops().stream()
                .map(StopPlanDTO::getFacilityName)
                .collect(Collectors.joining(" → "));
    }

    // ============================================================
    // CONVENIENCE METHODS
    // ============================================================

    /**
     * Get available locomotives at a facility
     */
    public List<LocomotiveDTO> getAvailableLocomotives(Long facilityId) {
        return trainRepo.getAvailableLocomotives(facilityId);
    }

    /**
     * Get available wagons at a facility
     */
    public List<WagonDTO> getAvailableWagons(Long facilityId) {
        return trainRepo.getAvailableWagons(facilityId);
    }

    /**
     * Get train details
     */
    public TrainDTO getTrain(Long trainId) {
        return trainRepo.getTrainById(trainId);
    }

    /**
     * Get train schedule
     */
    public List<TrainScheduleDTO> getActiveSchedules(LocalDateTime from, LocalDateTime to) {
        return scheduleRepo.getActiveSchedules(from, to);
    }
}