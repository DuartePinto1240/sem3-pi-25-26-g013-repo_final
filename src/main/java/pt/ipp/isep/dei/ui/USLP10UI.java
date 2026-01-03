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

public class USLP10UI implements Runnable {
    private final USLP10Controller controller;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public USLP10UI() {
        this.controller = new USLP10Controller();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║         USLP10: Train Scheduling              ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println("\n1. Dispatch train (Manual Path)");
            System.out.println("2. Dispatch train (Automatic Path)");
            System.out.println("3. Batch Dispatch");
            System.out.println("4. View Active Schedules");
            System.out.println("5. View Train Details");
            System.out.println("0. Back to Main Menu");
            System.out.println();

            int option = Utils.readIntegerFromConsole("Select option: ");

            try {
                switch (option) {
                    case 1:
                        dispatchManual();
                        break;
                    case 2:
                        dispatchAutomatic();
                        break;
                    case 3:
                        dispatchBatch();
                        break;
                    case 4:
                        viewActiveSchedules();
                        break;
                    case 5:
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
    // MANUAL DISPATCH
    // ================================================================

    private void dispatchManual() {
        System.out.println("\n--- Manual Dispatch ---");

        // Step 1: Select Train (COM VALIDAÇÃO!)
        long trainId = selectTrainFromList();
        if (trainId == -1) return;

        // VALIDAR SE O TRAIN EXISTE
        try {
            TrainDTO train = controller.getTrainById(trainId);
            if (train == null) {
                System.out.println("❌ Train not found: " + trainId);
                return;
            }
            System.out.println("✓ Train found: " + train.trainNumber());
        } catch (Exception e) {
            System.out.println("❌ Error: Train " + trainId + " does not exist in database.");
            return;
        }

        // Step 2: Define Manual Path
        List<Long> path = defineManualPath();
        if (path.isEmpty() || path.size() < 2) {
            System.out.println("❌ Path must have at least 2 facilities.");
            return;
        }

        // Step 2.5: VALIDAR SE O PATH É POSSÍVEL
        if (!validatePath(path)) {
            System.out.println("❌ Invalid path: No railway connection between selected facilities.");
            if (!Utils.confirm("Continue anyway?")) {
                return;
            }
        }

        // Step 3: Set Departure Time
        LocalDateTime departure = inputDepartureTime();
        if (departure == null) return;

        // Step 4: Display Summary
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           DISPATCH SUMMARY (MANUAL)");
        System.out.println("=".repeat(50));
        System.out.println("Train ID: " + trainId);
        System.out.println("Path: " + formatPath(path));
        System.out.println("Departure: " + departure.format(DATE_FORMATTER));
        System.out.println("=".repeat(50));

        if (!Utils.confirm("\nConfirm dispatch?")) {
            System.out.println("⚠ Dispatch cancelled.");
            return;
        }

        // Step 5: Dispatch
        try {
            TrainScheduleDTO schedule = controller.dispatchManual(trainId, path, departure);
            System.out.println("\n✓ Train dispatched successfully!");
            displaySchedule(schedule);
        } catch (Exception e) {
            System.out.println("❌ Dispatch failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // AUTOMATIC DISPATCH
    // ================================================================

    private void dispatchAutomatic() {
        System.out.println("\n--- Automatic Dispatch ---");

        // Step 1: Select Train (COM VALIDAÇÃO!)
        long trainId = selectTrainFromList();
        if (trainId == -1) return;

        // VALIDAR SE O TRAIN EXISTE
        try {
            TrainDTO train = controller.getTrainById(trainId);
            if (train == null) {
                System.out.println("❌ Train not found: " + trainId);
                return;
            }
            System.out.println("✓ Train found: " + train.trainNumber());
        } catch (Exception e) {
            System.out.println("❌ Error: Train " + trainId + " does not exist in database.");
            return;
        }

        // Step 2: Select Origin and Destination
        System.out.println("\nSelect ORIGIN facility:");
        long fromFacilityId = selectFacility();
        if (fromFacilityId == -1) return;

        System.out.println("\nSelect DESTINATION facility:");
        long toFacilityId = selectFacility();
        if (toFacilityId == -1) return;

        if (fromFacilityId == toFacilityId) {
            System.out.println("❌ Origin and destination cannot be the same.");
            return;
        }

        // Step 3: Set Departure Time
        LocalDateTime departure = inputDepartureTime();
        if (departure == null) return;

        // Step 4: Display Summary
        System.out.println("\n" + "=".repeat(50));
        System.out.println("        DISPATCH SUMMARY (AUTOMATIC)");
        System.out.println("=".repeat(50));
        System.out.println("Train ID: " + trainId);
        System.out.println("From: Facility " + fromFacilityId);
        System.out.println("To: Facility " + toFacilityId);
        System.out.println("Departure: " + departure.format(DATE_FORMATTER));
        System.out.println("Path: (will be calculated automatically)");
        System.out.println("=".repeat(50));

        if (!Utils.confirm("\nConfirm dispatch?")) {
            System.out.println("⚠ Dispatch cancelled.");
            return;
        }

        // Step 5: Dispatch (COM TRATAMENTO DE ERRO!)
        try {
            TrainScheduleDTO schedule = controller.dispatchAutomatic(
                    trainId, fromFacilityId, toFacilityId, departure
            );
            System.out.println("\n✓ Train dispatched successfully!");
            displaySchedule(schedule);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("No path found")) {
                System.out.println("\n❌ PATHFINDING FAILED");
                System.out.println("No railway connection exists between the selected facilities.");
                System.out.println("From: Facility " + fromFacilityId);
                System.out.println("To: Facility " + toFacilityId);
                System.out.println("\nPlease verify:");
                System.out.println("  • Both facilities are part of the railway network");
                System.out.println("  • There is a connected path via railway lines");
                System.out.println("  • Try using Manual Dispatch to define the exact path");
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

        // Step 1: Select Multiple Trains
        List<Long> trainIds = selectMultipleTrains();
        if (trainIds.isEmpty()) {
            System.out.println("⚠ No trains selected.");
            return;
        }

        // Step 2: Choose path mode
        boolean autoPath = Utils.confirm("Use automatic pathfinding for all trains?");

        // Step 3: Confirm
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           BATCH DISPATCH SUMMARY");
        System.out.println("=".repeat(50));
        System.out.println("Trains: " + trainIds);
        System.out.println("Path Mode: " + (autoPath ? "Automatic" : "Manual"));
        System.out.println("=".repeat(50));

        if (!Utils.confirm("\nConfirm batch dispatch?")) {
            System.out.println("⚠ Batch dispatch cancelled.");
            return;
        }

        // Step 4: Dispatch
        try {
            List<TrainScheduleDTO> schedules = controller.dispatchBatch(trainIds, autoPath);
            System.out.println("\n✓ Batch dispatch completed!");
            System.out.println("Successfully dispatched " + schedules.size() + " train(s)");

            for (TrainScheduleDTO schedule : schedules) {
                displaySchedule(schedule);
            }
        } catch (Exception e) {
            System.out.println("❌ Batch dispatch failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // VIEW SCHEDULES
    // ================================================================

    private void viewActiveSchedules() {
        System.out.println("\n--- Active Schedules ---");

        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now().plusDays(7);

        try {
            List<TrainScheduleDTO> schedules = controller.getActiveSchedules(from, to);

            if (schedules.isEmpty()) {
                System.out.println("ℹ No active schedules found.");
                return;
            }

            System.out.println("\nFound " + schedules.size() + " active schedule(s):");
            System.out.println("=".repeat(80));

            for (int i = 0; i < schedules.size(); i++) {
                System.out.println("\n[" + (i + 1) + "] Schedule ID: " + schedules.get(i).scheduleId());
                displayScheduleSummary(schedules.get(i));
            }

            System.out.println("\n" + "=".repeat(80));

            if (Utils.confirm("View detailed schedule?")) {
                int idx = Utils.readIntegerFromConsole("Enter schedule number: ") - 1;
                if (idx >= 0 && idx < schedules.size()) {
                    displaySchedule(schedules.get(idx));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching schedules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // VIEW TRAIN DETAILS
    // ================================================================

    private void viewTrainDetails() {
        System.out.println("\n--- View Train Details ---");

        // Opção de escolher da lista ou digitar ID
        System.out.println("1. Select from list");
        System.out.println("2. Enter Train ID manually");
        int choice = Utils.readIntegerFromConsole("Choose: ");

        long trainId;
        if (choice == 1) {
            trainId = selectTrainFromList();
            if (trainId == -1) return;
        } else {
            trainId = Utils.readIntegerFromConsole("Enter Train ID: ");
        }

        try {
            TrainDTO train = controller.getTrainById(trainId);

            if (train == null) {
                System.out.println("❌ Train not found: " + trainId);
                System.out.println("ℹ  Please verify the Train ID and try again.");
                return;
            }

            System.out.println("\n" + "=".repeat(50));
            System.out.println("              TRAIN DETAILS");
            System.out.println("=".repeat(50));
            System.out.println("Train ID: " + train.trainId());
            System.out.println("Train Number: " + train.trainNumber());
            System.out.println("Locomotives: " + train.locomotiveIds().size());
            System.out.println("Wagons: " + train.wagonIds().size());
            System.out.printf("Total Weight: %.2f tonnes%n", train.totalWeight());
            System.out.printf("Total Length: %.2f meters%n", train.totalLength());
            System.out.printf("Max Length: %.2f meters%n", train.maxLength());
            System.out.println("=".repeat(50));

        } catch (RuntimeException e) {
            System.out.println("❌ Error: " + e.getMessage());
            System.out.println("ℹ  The train may not exist or there was a database error.");
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // HELPER METHODS - INPUT (MUDANÇAS AQUI!)
    // ================================================================

    private long selectTrain() {
        return Utils.readIntegerFromConsole("Enter Train ID: ");
    }
    private long selectTrainFromList() {
        List<TrainDTO> trains = controller.getAllTrains();

        if (trains.isEmpty()) {
            System.out.println("❌ No trains available.");
            System.out.println("ℹ  You can still enter a Train ID manually.");

            if (Utils.confirm("Enter Train ID manually?")) {
                return Utils.readIntegerFromConsole("Enter Train ID: ");
            }
            return -1;
        }

        // Format trains for display
        List<String> trainStrings = trains.stream()
                .map(t -> String.format("[%d] %s - %d locos, %d wagons",
                        t.trainId(), t.trainNumber(),
                        t.locomotiveIds().size(), t.wagonIds().size()))
                .toList();

        int idx = Utils.showAndSelectIndex(trainStrings, "Available Trains");
        if (idx == -1) {
            if (Utils.confirm("Enter Train ID manually instead?")) {
                return Utils.readIntegerFromConsole("Enter Train ID: ");
            }
            return -1;
        }

        return trains.get(idx).trainId();
    }

    /**
     * Validate if path is possible (has railway connections)
     */
    private boolean validatePath(List<Long> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Long from = path.get(i);
            Long to = path.get(i + 1);

            try {
                List<LineSegmentDTO> segments = controller.getLineSegments(from, to);
                if (segments.isEmpty()) {
                    System.out.println("⚠ Warning: No direct connection between Facility " + from + " and " + to);
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private long selectFacility() {
        List<FacilityDTO> facilities = controller.getFacilities();

        if (facilities.isEmpty()) {
            System.out.println("❌ No facilities available.");
            return -1;
        }

        int idx = Utils.showAndSelectIndex(facilities, "Available Facilities");
        if (idx == -1) return -1;

        FacilityDTO selected = facilities.get(idx);
        return selected.facilityId();
    }

    private List<Long> selectMultipleTrains() {
        List<Long> trainIds = new ArrayList<>();
        boolean selecting = true;

        while (selecting) {
            System.out.print("\nEnter Train ID (or -1 to finish): ");
            long trainId = Utils.readIntegerFromConsole("");

            if (trainId == -1) {
                if (trainIds.isEmpty()) {
                    System.out.println("⚠ You must select at least one train.");
                    continue;
                }
                selecting = false;
            } else {
                trainIds.add(trainId);
                System.out.println("✓ Added train " + trainId);
            }
        }

        return trainIds;
    }

    private List<Long> defineManualPath() {
        List<Long> path = new ArrayList<>();
        List<FacilityDTO> facilities = controller.getFacilities();

        if (facilities.isEmpty()) {
            System.out.println("❌ No facilities available.");
            return path;
        }

        System.out.println("\nDefine path (select facilities in order):");
        boolean defining = true;

        while (defining) {
            System.out.println("\nCurrent path: " + formatPath(path));
            int idx = Utils.showAndSelectIndex(facilities, "Select next facility (-1 to finish)");

            if (idx == -1) {
                if (path.size() < 2) {
                    System.out.println("⚠ Path must have at least 2 facilities.");
                    if (!Utils.confirm("Continue defining path?")) {
                        return List.of();
                    }
                    continue;
                }
                defining = false;
            } else {
                FacilityDTO selected = facilities.get(idx);
                long facilityId = selected.facilityId();
                path.add(facilityId);
                System.out.println("✓ Added: " + selected.name());
            }
        }

        return path;
    }

    private LocalDateTime inputDepartureTime() {
        System.out.println("\nEnter departure time:");
        System.out.println("Format: yyyy-MM-dd HH:mm");
        System.out.println("Example: 2026-01-15 08:30");
        System.out.print("> ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("⚠ Using current time + 1 hour");
            return LocalDateTime.now().plusHours(1);
        }

        try {
            return LocalDateTime.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("⚠ Invalid format. Using current time + 1 hour");
            return LocalDateTime.now().plusHours(1);
        }
    }

    // ================================================================
    // HELPER METHODS - DISPLAY
    // ================================================================

    private void displaySchedule(TrainScheduleDTO schedule) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    TRAIN SCHEDULE");
        System.out.println("=".repeat(80));
        System.out.println("Schedule ID: " + schedule.scheduleId());
        System.out.println("Train ID: " + schedule.trainId());
        System.out.println("Departure: " + schedule.departureTime().format(DATE_FORMATTER));
        System.out.println("\n--- Passage Times ---");
        System.out.println(String.format("%-30s %-20s %-20s %-6s",
                "Facility", "Arrival", "Departure", "Stops"));
        System.out.println("-".repeat(80));

        for (PassageTimeDTO passage : schedule.passageTimes()) {
            String arrival = passage.arrivalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String departureStr = passage.departureTime() != null
                    ? passage.departureTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    : "-";
            String stops = passage.stops() ? "YES" : "NO";

            System.out.printf("%-30s %-20s %-20s %-6s%n",
                    passage.facilityName(), arrival, departureStr, stops);
        }

        if (!schedule.crossings().isEmpty()) {
            System.out.println("\n--- Crossing Operations ---");
            for (CrossingOperationDTO crossing : schedule.crossings()) {
                System.out.printf("  • %s (%s): Train %d ↔ Train %d @ %s%n",
                        crossing.facilityName(),
                        crossing.crossingType(),
                        crossing.trainId1(),
                        crossing.trainId2(),
                        crossing.crossingTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }

        System.out.println("=".repeat(80));
    }

    private void displayScheduleSummary(TrainScheduleDTO schedule) {
        System.out.println("  Train: " + schedule.trainId());
        System.out.println("  Departure: " + schedule.departureTime().format(DATE_FORMATTER));
        System.out.println("  Stops: " + schedule.passageTimes().size());
        if (!schedule.passageTimes().isEmpty()) {
            PassageTimeDTO first = schedule.passageTimes().get(0);
            PassageTimeDTO last = schedule.passageTimes().get(schedule.passageTimes().size() - 1);
            System.out.println("  Route: " + first.facilityName() + " → " + last.facilityName());
        }
    }

    // ================================================================
    // HELPER METHODS - FORMATTING
    // ================================================================

    private String formatPath(List<Long> path) {
        if (path.isEmpty()) return "(empty)";
        return path.stream()
                .map(id -> "F" + id)
                .reduce((a, b) -> a + " → " + b)
                .orElse("");
    }
}