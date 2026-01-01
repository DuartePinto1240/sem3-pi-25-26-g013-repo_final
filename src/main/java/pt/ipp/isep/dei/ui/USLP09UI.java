package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USLP09Controller;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.TrainDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class USLP09UI implements Runnable {
    private final USLP09Controller controller;

    public USLP09UI() {
        this.controller = new USLP09Controller();
    }

    @Override
    public void run() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║         USLP09: Train Assembly                 ║");
        System.out.println("╚════════════════════════════════════════════════╝");

        // Step 1: Select Station (Facility)
        List<Station> stations = controller.getStations();
        if (stations.isEmpty()) {
            System.out.println("❌ No stations available.");
            return;
        }

        System.out.println("\n📍 Step 1: Select assembly station");
        int stationIndex = Utils.showAndSelectIndex(stations, "Available Stations");
        if (stationIndex == -1) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        Station selectedStation = stations.get(stationIndex);
        long stationId = getStationId(selectedStation);
        System.out.println("✓ Station selected: " + selectedStation);

        // Step 2: Select Locomotives
        System.out.println("\n🚂 Step 2: Select locomotives");
        List<Long> selectedLocoIds = selectLocomotives(stationId);

        if (selectedLocoIds.isEmpty()) {
            System.out.println("❌ No locomotives selected. Cannot create train.");
            return;
        }
        System.out.println("✓ Selected " + selectedLocoIds.size() + " locomotive(s)");

        // Step 3: Select Wagons
        System.out.println("\n🚃 Step 3: Select wagons");
        List<Long> selectedWagonIds = selectWagons(stationId);

        if (selectedWagonIds.isEmpty()) {
            System.out.println("⚠ No wagons selected. Train will have locomotives only.");
        } else {
            System.out.println("✓ Selected " + selectedWagonIds.size() + " wagon(s)");
        }

        // Step 4: Display Summary and Confirm
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              TRAIN ASSEMBLY SUMMARY");
        System.out.println("=".repeat(50));
        displayAssemblySummary(stationId, selectedLocoIds, selectedWagonIds);
        System.out.println("=".repeat(50));

        if (!Utils.confirm("\nProceed with train creation?")) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 5: Create Train
        try {
            long trainId = controller.createTrain(selectedLocoIds, selectedWagonIds);

            System.out.println("\n✓ SUCCESS! Train assembled");
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║  Train ID: " + String.format("%-37d", trainId) + "║");
            System.out.println("╚════════════════════════════════════════════════╝");

            // Display full train details
            TrainDTO train = controller.getTrainById(trainId);
            if (train != null) {
                displayTrainDetails(train);
            }

        } catch (Exception e) {
            System.out.println("\n❌ ERROR creating train: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // LOCOMOTIVE SELECTION
    // ================================================================

    private List<Long> selectLocomotives(long stationId) {
        List<LocomotiveDTO> availableLocos = controller.getAvailableLocomotives(stationId);

        if (availableLocos.isEmpty()) {
            System.out.println("❌ No locomotives available at this station.");
            return List.of();
        }

        List<Long> selectedLocoIds = new ArrayList<>();
        boolean selectingLocos = true;

        while (selectingLocos) {
            // Filter out already selected
            List<LocomotiveDTO> choices = new ArrayList<>(availableLocos);
            choices.removeIf(l -> selectedLocoIds.contains(l.locomotiveId()));

            if (choices.isEmpty()) {
                System.out.println("✓ All available locomotives have been selected.");
                break;
            }

            System.out.println("\nCurrently selected: " + selectedLocoIds.size() + " locomotive(s)");
            int idx = Utils.showAndSelectIndex(
                    choices,
                    "Available Locomotives (select -1 when done)"
            );

            if (idx == -1) {
                if (selectedLocoIds.isEmpty()) {
                    System.out.println("⚠ You must select at least one locomotive!");
                    if (!Utils.confirm("Continue selecting?")) {
                        return List.of();
                    }
                    continue;
                }
                selectingLocos = false;
            } else {
                LocomotiveDTO selected = choices.get(idx);
                selectedLocoIds.add(selected.locomotiveId());
                System.out.println("✓ Added: " + formatLocomotive(selected));

                if (!Utils.confirm("Add another locomotive?")) {
                    selectingLocos = false;
                }
            }
        }

        return selectedLocoIds;
    }

    // ================================================================
    // WAGON SELECTION
    // ================================================================

    private List<Long> selectWagons(long stationId) {
        List<WagonDTO> availableWagons = controller.getAvailableWagons(stationId);

        if (availableWagons.isEmpty()) {
            System.out.println("ℹ No wagons available at this station.");
            return List.of();
        }

        List<Long> selectedWagonIds = new ArrayList<>();
        boolean selectingWagons = true;

        while (selectingWagons) {
            // Filter out already selected
            List<WagonDTO> choices = new ArrayList<>(availableWagons);
            choices.removeIf(w -> selectedWagonIds.contains(w.wagonId()));

            if (choices.isEmpty()) {
                System.out.println("✓ All available wagons have been selected.");
                break;
            }

            System.out.println("\nCurrently selected: " + selectedWagonIds.size() + " wagon(s)");
            int idx = Utils.showAndSelectIndex(
                    choices,
                    "Available Wagons (select -1 when done)"
            );

            if (idx == -1) {
                selectingWagons = false;
            } else {
                WagonDTO selected = choices.get(idx);
                selectedWagonIds.add(selected.wagonId());
                System.out.println("✓ Added: " + formatWagon(selected));

                if (!choices.isEmpty() && !Utils.confirm("Add another wagon?")) {
                    selectingWagons = false;
                }
            }
        }

        return selectedWagonIds;
    }

    // ================================================================
    // DISPLAY METHODS
    // ================================================================

    private void displayAssemblySummary(long stationId, List<Long> locoIds, List<Long> wagonIds) {
        System.out.println("Station ID: " + stationId);
        System.out.println("\nLocomotives (" + locoIds.size() + "):");

        List<LocomotiveDTO> locos = controller.getAvailableLocomotives(stationId);
        for (Long id : locoIds) {
            locos.stream()
                    .filter(l -> l.locomotiveId().equals(id))
                    .findFirst()
                    .ifPresent(l -> System.out.println("  • " + formatLocomotive(l)));
        }

        System.out.println("\nWagons (" + wagonIds.size() + "):");
        if (wagonIds.isEmpty()) {
            System.out.println("  (none)");
        } else {
            List<WagonDTO> wagons = controller.getAvailableWagons(stationId);
            for (Long id : wagonIds) {
                wagons.stream()
                        .filter(w -> w.wagonId().equals(id))
                        .findFirst()
                        .ifPresent(w -> System.out.println("  • " + formatWagon(w)));
            }
        }

        // Calculate totals
        double totalWeight = calculateTotalWeight(stationId, locoIds, wagonIds);
        double totalLength = calculateTotalLength(stationId, locoIds, wagonIds);

        System.out.println("\nEstimated totals:");
        System.out.printf("  Total Weight: %.2f tonnes%n", totalWeight);
        System.out.printf("  Total Length: %.2f meters%n", totalLength);
    }

    private void displayTrainDetails(TrainDTO train) {
        System.out.println("\n--- Train Details ---");
        System.out.println("Train Number: " + train.trainNumber());
        System.out.println("Locomotives: " + train.locomotiveIds().size());
        System.out.println("Wagons: " + train.wagonIds().size());
        System.out.printf("Total Weight: %.2f tonnes%n", train.totalWeight());
        System.out.printf("Total Length: %.2f meters%n", train.totalLength());
        System.out.printf("Max Length: %.2f meters%n", train.maxLength());
    }

    // ================================================================
    // FORMATTING HELPERS
    // ================================================================

    private String formatLocomotive(LocomotiveDTO loco) {
        return String.format("[%d] %s - %s - %.0f kW - %.0f km/h",
                loco.locomotiveId(),
                loco.model(),
                loco.type(),
                loco.power(),
                loco.maxSpeed());
    }

    private String formatWagon(WagonDTO wagon) {
        return String.format("[%d] %s - %.1f t payload - %.1f m",
                wagon.wagonId(),
                wagon.type(),
                wagon.payload(),
                wagon.length());
    }

    // ================================================================
    // CALCULATION HELPERS
    // ================================================================

    private double calculateTotalWeight(long stationId, List<Long> locoIds, List<Long> wagonIds) {
        double total = 0.0;

        List<LocomotiveDTO> locos = controller.getAvailableLocomotives(stationId);
        for (Long id : locoIds) {
            total += locos.stream()
                    .filter(l -> l.locomotiveId().equals(id))
                    .mapToDouble(LocomotiveDTO::weight)
                    .sum();
        }

        List<WagonDTO> wagons = controller.getAvailableWagons(stationId);
        for (Long id : wagonIds) {
            total += wagons.stream()
                    .filter(w -> w.wagonId().equals(id))
                    .mapToDouble(w -> w.tareWeight() + w.payload())
                    .sum();
        }

        return total;
    }

    private double calculateTotalLength(long stationId, List<Long> locoIds, List<Long> wagonIds) {
        double total = 0.0;

        // Assume average locomotive length ~20m
        total += locoIds.size() * 20.0;

        List<WagonDTO> wagons = controller.getAvailableWagons(stationId);
        for (Long id : wagonIds) {
            total += wagons.stream()
                    .filter(w -> w.wagonId().equals(id))
                    .mapToDouble(WagonDTO::length)
                    .sum();
        }

        return total;
    }

    // ================================================================
    // STATION ID HELPER
    // ================================================================

    private long getStationId(Station s) {
        // TODO: Replace with actual s.getId() if Station has it
        // For now, using hashCode as placeholder
        return Math.abs(s.hashCode());
    }
}