package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USLP10Controller;
import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * UI for USLP10 - Train Scheduling and Dispatching
 *
 * Flow:
 * 1. Select a train (that has a route assigned from USLP09)
 * 2. View train and route details
 * 3. Set departure time
 * 4. Choose: use route path OR define manual path
 * 5. Calculate and display schedule with arrival times
 */
public class USLP10UI implements Runnable {
    private final USLP10Controller controller;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public USLP10UI() {
        this.controller = new USLP10Controller();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║      USLP10: Train Scheduling & Dispatch       ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println("\n1. Dispatch Single Train");
            System.out.println("2. Batch Dispatch");
            System.out.println("3. View Active Schedules");
            System.out.println("4. View Train Details");
            System.out.println("0. Back to Main Menu");
            System.out.println();

            int option = Utils.readIntegerFromConsole("Select option: ");

            try {
                switch (option) {
                    case 1:
                        dispatchSingleTrain();
                        break;
                    case 2:
                        dispatchBatch();
                        break;
                    case 3:
                        viewActiveSchedules();
                        break;
                    case 4:
                        viewTrainDetails();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("⚠ Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                e.printStackTrace();
            }

            if (running) {
                Utils.readLineFromConsole("\nPress Enter to continue...");
            }
        }
    }

    // ================================================================
    // DISPATCH SINGLE TRAIN
    // ================================================================

    private void dispatchSingleTrain() {
        System.out.println("\n--- Dispatch Single Train ---");

        // Step 1: Select Train (that has route assigned)
        TrainWithRouteInfo trainInfo = selectTrainWithRoute();
        if (trainInfo == null) return;

        TrainDTO train = trainInfo.train;
        RouteDTO route = trainInfo.route;

        // Step 2: Display Train and Route Info
        displayTrainAndRouteInfo(train, route);

        if (!Utils.confirm("\nProceed with dispatching this train?")) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 3: Set Departure Time
        LocalDateTime departureTime = inputDepartureTime();
        if (departureTime == null) return;

        // Step 4: Choose path mode
        System.out.println("\n--- Path Selection ---");
        System.out.println("1. Use route path (automatic from USLP09)");
        System.out.println("2. Define manual path (override route)");
        int pathChoice = Utils.readIntegerFromConsole("Choose: ");

        List<Long> manualPath = null;
        String pathMode = "AUTOMATIC";

        if (pathChoice == 2) {
            manualPath = defineManualPath(route);
            if (manualPath == null || manualPath.isEmpty()) {
                System.out.println("❌ Invalid manual path.");
                return;
            }
            pathMode = "MANUAL";
        }

        // Step 5: Display Summary
        displayDispatchSummary(train, route, departureTime, pathMode, manualPath);

        if (!Utils.confirm("\nConfirm dispatch?")) {
            System.out.println("⚠ Dispatch cancelled.");
            return;
        }

        // Step 6: Dispatch
        try {
            TrainScheduleDTO schedule = controller.dispatchTrain(
                    train.trainId(),
                    departureTime,
                    pathMode,
                    manualPath
            );

            System.out.println("\n✓ Train dispatched successfully!");
            displaySchedule(schedule);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("No path found") || e.getMessage().contains("no route assigned")) {
                System.out.println("\n❌ DISPATCH FAILED");
                System.out.println(e.getMessage());
                System.out.println("\nPossible solutions:");
                System.out.println("  • Verify the train has a route assigned in USLP09");
                System.out.println("  • Check that railway connections exist between facilities");
                System.out.println("  • Try using manual path instead of automatic");
            } else {
                System.out.println("❌ Dispatch failed: " + e.getMessage());
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // BATCH DISPATCH
    // ================================================================

    private void dispatchBatch() {
        System.out.println("\n--- Batch Dispatch ---");
        System.out.println("Dispatch multiple trains with different departure times.");

        List<Long> trainIds = new ArrayList<>();
        List<LocalDateTime> departureTimes = new ArrayList<>();

        boolean selecting = true;
        while (selecting) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║  Train #" + (trainIds.size() + 1) + " Selection                              ║");
            System.out.println("╚════════════════════════════════════════════════╝");

            TrainWithRouteInfo trainInfo = selectTrainWithRoute();
            if (trainInfo == null) {
                if (trainIds.isEmpty()) {
                    System.out.println("⚠ No trains selected. Batch dispatch cancelled.");
                    return;
                }
                System.out.println("ℹ Moving to departure time selection...");
                break;
            }

            // Check if already added
            if (trainIds.contains(trainInfo.train.trainId())) {
                System.out.println("⚠ Train " + trainInfo.train.trainId() + " already added to batch.");
                continue;
            }

            LocalDateTime departure = inputDepartureTime();
            if (departure == null) {
                if (trainIds.isEmpty()) {
                    System.out.println("⚠ No trains selected. Batch dispatch cancelled.");
                    return;
                }
                break;
            }

            trainIds.add(trainInfo.train.trainId());
            departureTimes.add(departure);

            System.out.println("✓ Added: Train " + trainInfo.train.trainId() +
                    " (" + trainInfo.train.trainNumber() + ") @ " +
                    departure.format(DATE_FORMATTER));

            if (trainIds.size() >= 10) {
                System.out.println("ℹ Maximum of 10 trains per batch reached.");
                break;
            }

            if (!Utils.confirm("Add another train to batch?")) {
                selecting = false;
            }
        }

        if (trainIds.isEmpty()) {
            System.out.println("⚠ No trains to dispatch.");
            return;
        }

        // Summary
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              BATCH DISPATCH SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Total trains: " + trainIds.size());
        System.out.println();
        for (int i = 0; i < trainIds.size(); i++) {
            System.out.printf("%2d. Train %-6d  →  %s%n",
                    (i + 1),
                    trainIds.get(i),
                    departureTimes.get(i).format(DATE_FORMATTER));
        }
        System.out.println("=".repeat(60));

        if (!Utils.confirm("\nConfirm batch dispatch?")) {
            System.out.println("⚠ Batch dispatch cancelled.");
            return;
        }

        // Dispatch
        try {
            List<TrainScheduleDTO> schedules = controller.dispatchBatch(trainIds, departureTimes);

            System.out.println("\n✓ BATCH DISPATCH COMPLETED!");
            System.out.println("Successfully dispatched " + schedules.size() + " out of " + trainIds.size() + " train(s)");

            if (schedules.size() < trainIds.size()) {
                System.out.println("⚠ " + (trainIds.size() - schedules.size()) + " train(s) failed to dispatch.");
            }

            System.out.println("\n" + "=".repeat(80));
            for (int i = 0; i < schedules.size(); i++) {
                System.out.println("\n[" + (i + 1) + "] Train " + schedules.get(i).trainId());
                displayScheduleSummary(schedules.get(i));
            }
            System.out.println("\n" + "=".repeat(80));

        } catch (Exception e) {
            System.out.println("❌ Batch dispatch failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // VIEW SCHEDULES & DETAILS
    // ================================================================

    private void viewActiveSchedules() {
        System.out.println("\n--- Active Schedules ---");

        LocalDateTime from = LocalDateTime.now().minusDays(30);
        LocalDateTime to = LocalDateTime.now().plusDays(30);

        System.out.println("Searching from: " + from.format(DATE_FORMATTER));
        System.out.println("           to: " + to.format(DATE_FORMATTER));

        try {
            List<TrainScheduleDTO> schedules = controller.getActiveSchedules(from, to);

            if (schedules.isEmpty()) {
                System.out.println("\nℹ No active schedules found.");
                System.out.println("\nPossible reasons:");
                System.out.println("  • No trains have been dispatched yet (use option 1 or 2)");
                System.out.println("  • All schedules are outside the search time window");
                System.out.println("  • Schedule data was not saved to database");
                return;
            }

            System.out.println("\nFound " + schedules.size() + " active schedule(s):");
            System.out.println("=".repeat(80));

            for (int i = 0; i < schedules.size(); i++) {
                System.out.println("\n[" + (i + 1) + "] Schedule ID: " + schedules.get(i).scheduleId());
                displayScheduleSummary(schedules.get(i));
            }

            System.out.println("\n" + "=".repeat(80));

            if (Utils.confirm("\nView detailed schedule?")) {
                int idx = Utils.readIntegerFromConsole("Enter schedule number: ") - 1;
                if (idx >= 0 && idx < schedules.size()) {
                    displaySchedule(schedules.get(idx));
                } else {
                    System.out.println("⚠ Invalid schedule number.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching schedules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewTrainDetails() {
        System.out.println("\n--- View Train Details ---");

        TrainWithRouteInfo trainInfo = selectTrainWithRoute();
        if (trainInfo == null) return;

        displayTrainAndRouteInfo(trainInfo.train, trainInfo.route);
    }

    // ================================================================
    // HELPER METHODS - SELECTION
    // ================================================================

    private TrainWithRouteInfo selectTrainWithRoute() {
        List<TrainDTO> trains = controller.getTrainsWithRoutes();

        if (trains.isEmpty()) {
            System.out.println("\n❌ No trains with assigned routes available.");
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  TRAINS NEED ROUTES FIRST!");
            System.out.println("=".repeat(60));
            System.out.println("\nBefore dispatching trains (USLP10), you must:");
            System.out.println("  1. Create routes using USLP08 (Route Planning)");
            System.out.println("  2. Assign trains to routes using USLP09 (Train Assembly)");
            System.out.println("\nOnly then can trains be dispatched here in USLP10.");
            System.out.println("=".repeat(60));
            return null;
        }

        System.out.println("\nTrains with assigned routes:");
        List<String> trainStrings = new ArrayList<>();
        List<RouteDTO> routes = new ArrayList<>();

        for (TrainDTO train : trains) {
            RouteDTO route = controller.getRouteForTrain(train.trainId());
            routes.add(route);

            String routeInfo = route != null
                    ? String.format("%s → %s (%d stops)",
                    route.startFacilityName(),
                    route.endFacilityName(),
                    route.numStops())
                    : "No route";

            trainStrings.add(String.format(
                    "[%d] %s - %s",
                    train.trainId(),
                    train.trainNumber(),
                    routeInfo
            ));
        }

        int idx = Utils.showAndSelectIndex(trainStrings, "Select Train");
        if (idx == -1) return null;

        TrainDTO selectedTrain = trains.get(idx);
        RouteDTO selectedRoute = routes.get(idx);

        return new TrainWithRouteInfo(selectedTrain, selectedRoute);
    }

    private List<Long> defineManualPath(RouteDTO route) {
        System.out.println("\n--- Define Manual Path ---");
        System.out.println("You can override the route's automatic path.");
        System.out.println("\nDefault route stops:");

        for (RouteStopDTO stop : route.stops()) {
            System.out.printf("  %d. %-30s (ID: %d)%n",
                    stop.stopOrder(),
                    stop.facilityName(),
                    stop.facilityId());
        }

        System.out.println("\nEnter facility IDs in order (must form a connected path):");

        List<Long> path = new ArrayList<>();
        boolean defining = true;

        while (defining) {
            if (!path.isEmpty()) {
                System.out.println("\nCurrent manual path: " + formatPathWithNames(path));
            }

            long facilityId = Utils.readIntegerFromConsole(
                    "Enter Facility ID (-1 to finish" + (path.isEmpty() ? "" : ", 0 to restart") + "): "
            );

            if (facilityId == -1) {
                if (path.size() < 2) {
                    System.out.println("⚠ Path must have at least 2 facilities.");
                    if (!Utils.confirm("Continue defining?")) {
                        return null;
                    }
                    continue;
                }
                defining = false;
            } else if (facilityId == 0) {
                path.clear();
                System.out.println("✓ Path reset.");
            } else {
                path.add(facilityId);
                System.out.println("✓ Added Facility " + facilityId);
            }
        }

        return path;
    }

    private LocalDateTime inputDepartureTime() {
        System.out.println("\n--- Set Departure Time ---");
        System.out.println("Format: yyyy-MM-dd HH:mm");
        System.out.println("Example: " + LocalDateTime.now().plusHours(1).format(DATE_FORMATTER));
        System.out.println("(Leave empty to use current time + 1 hour)");
        System.out.print("> ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            LocalDateTime defaultTime = LocalDateTime.now().plusHours(1);
            System.out.println("✓ Using: " + defaultTime.format(DATE_FORMATTER));
            return defaultTime;
        }

        try {
            LocalDateTime parsedTime = LocalDateTime.parse(input, DATE_FORMATTER);

            // Validate not in the past
            if (parsedTime.isBefore(LocalDateTime.now())) {
                System.out.println("⚠ Warning: Departure time is in the past!");
                if (!Utils.confirm("Continue anyway?")) {
                    return inputDepartureTime(); // Try again
                }
            }

            System.out.println("✓ Departure time set: " + parsedTime.format(DATE_FORMATTER));
            return parsedTime;

        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid format. Please try again.");
            return inputDepartureTime(); // Recursive retry
        }
    }

    // ================================================================
    // HELPER METHODS - DISPLAY
    // ================================================================

    private void displayTrainAndRouteInfo(TrainDTO train, RouteDTO route) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           TRAIN & ROUTE INFORMATION");
        System.out.println("=".repeat(60));

        // Train info
        System.out.println("\n--- Train Details ---");
        System.out.println("Train ID: " + train.trainId());
        System.out.println("Train Number: " + train.trainNumber());
        System.out.println("Locomotives: " + train.locomotiveIds().size());
        System.out.println("Wagons: " + train.wagonIds().size());
        System.out.printf("Total Weight: %.2f tonnes%n", train.totalWeight());
        System.out.printf("Total Length: %.2f meters%n", train.totalLength());

        // Route info
        System.out.println("\n--- Assigned Route ---");
        System.out.println("Route ID: " + route.routeId());
        System.out.println("Type: " + route.routeType());
        System.out.println("Path: " + route.startFacilityName() + " → " + route.endFacilityName());
        System.out.println("Stops: " + route.numStops());
        System.out.println("Freights: " + route.numFreights());

        System.out.println("\n--- Route Stops ---");
        for (RouteStopDTO stop : route.stops()) {
            System.out.printf("%2d. %-30s  Load: %d  Unload: %d%n",
                    stop.stopOrder(),
                    stop.facilityName(),
                    stop.freightsToLoad(),
                    stop.freightsToUnload());
        }

        System.out.println("=".repeat(60));
    }

    private void displayDispatchSummary(
            TrainDTO train,
            RouteDTO route,
            LocalDateTime departureTime,
            String pathMode,
            List<Long> manualPath
    ) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              DISPATCH SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Train: " + train.trainNumber() + " (ID: " + train.trainId() + ")");
        System.out.println("Route: " + route.startFacilityName() + " → " + route.endFacilityName());
        System.out.println("Departure: " + departureTime.format(DATE_FORMATTER));
        System.out.println("Path Mode: " + pathMode);

        if ("MANUAL".equals(pathMode) && manualPath != null) {
            System.out.println("Manual Path: " + formatPath(manualPath));
        } else {
            System.out.println("Path: Following assigned route (" + route.numStops() + " stops)");
        }

        System.out.println("=".repeat(60));
    }

    private void displaySchedule(TrainScheduleDTO schedule) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                       TRAIN SCHEDULE");
        System.out.println("=".repeat(80));
        System.out.println("Schedule ID: " + schedule.scheduleId());
        System.out.println("Train ID: " + schedule.trainId());
        System.out.println("Departure: " + schedule.departureTime().format(DATE_FORMATTER));

        if (schedule.passageTimes().isEmpty()) {
            System.out.println("\n⚠ No passage times calculated.");
        } else {
            System.out.println("\n--- Passage Times ---");
            System.out.printf("%-4s %-30s %-12s %-12s %-10s%n",
                    "#", "Facility", "Arrival", "Departure", "Stops");
            System.out.println("-".repeat(80));

            int stopNum = 1;
            for (PassageTimeDTO passage : schedule.passageTimes()) {
                String arrival = passage.arrivalTime() != null
                        ? passage.arrivalTime().format(TIME_FORMATTER)
                        : "---";

                String departure = passage.departureTime() != null
                        ? passage.departureTime().format(TIME_FORMATTER)
                        : "---";

                String stops = passage.stops() ? "YES" : "NO";

                System.out.printf("%-4d %-30s %-12s %-12s %-10s%n",
                        stopNum++,
                        passage.facilityName(),
                        arrival,
                        departure,
                        stops);
            }
        }

        if (!schedule.crossings().isEmpty()) {
            System.out.println("\n--- Crossing Operations ---");
            for (CrossingOperationDTO crossing : schedule.crossings()) {
                System.out.printf("  • %s (%s): Train %d ↔ Train %d @ %s%n",
                        crossing.facilityName(),
                        crossing.crossingType(),
                        crossing.trainId1(),
                        crossing.trainId2(),
                        crossing.crossingTime().format(TIME_FORMATTER));
            }
        } else {
            System.out.println("\nℹ No crossing operations required.");
        }

        System.out.println("=".repeat(80));
    }

    private void displayScheduleSummary(TrainScheduleDTO schedule) {
        System.out.println("  Departure: " + schedule.departureTime().format(DATE_FORMATTER));
        System.out.println("  Stops: " + schedule.passageTimes().size());

        if (!schedule.passageTimes().isEmpty()) {
            PassageTimeDTO first = schedule.passageTimes().get(0);
            PassageTimeDTO last = schedule.passageTimes().get(schedule.passageTimes().size() - 1);
            System.out.println("  Route: " + first.facilityName() + " → " + last.facilityName());

            if (last.arrivalTime() != null && first.departureTime() != null) {
                long minutes = java.time.Duration.between(
                        first.departureTime(),
                        last.arrivalTime()
                ).toMinutes();
                System.out.println("  Duration: " + minutes + " minutes");
            }
        }
    }

    // ================================================================
    // HELPER METHODS - FORMATTING
    // ================================================================

    private String formatPath(List<Long> path) {
        if (path == null || path.isEmpty()) return "(empty)";
        return path.stream()
                .map(id -> "F" + id)
                .reduce((a, b) -> a + " → " + b)
                .orElse("");
    }

    private String formatPathWithNames(List<Long> path) {
        // In a full implementation, would fetch facility names
        return formatPath(path);
    }

    // ================================================================
    // INNER CLASS - HELPER
    // ================================================================

    private static class TrainWithRouteInfo {
        final TrainDTO train;
        final RouteDTO route;

        TrainWithRouteInfo(TrainDTO train, RouteDTO route) {
            this.train = train;
            this.route = route;
        }
    }
}