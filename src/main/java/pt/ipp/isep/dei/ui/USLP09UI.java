package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USLP09Controller;
import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for USLP09 - Train Assembly and Route Assignment
 *
 * Integrated with USLP08 to get route plans
 */
public class USLP09UI implements Runnable {
    private final USLP09Controller controller;

    public USLP09UI() {
        this.controller = new USLP09Controller();
    }

    @Override
    public void run() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║   USLP09: Train Assembly & Route Assignment    ║");
        System.out.println("╚════════════════════════════════════════════════╝");

        // Step 1: Get Route Plans from USLP08
        System.out.println("\n📋 Step 1: Getting route plans from USLP08...");
        List<RoutePlanDTO> routePlans = controller.getAvailableRoutePlans();

        if (routePlans.isEmpty()) {
            System.out.println("\n❌ No route plans available.");
            System.out.println("ℹ  Route plans are generated from pending freights.");
            System.out.println("   Please ensure there are pending freights in the system.");
            return;
        }

        System.out.println("✓ Found " + routePlans.size() + " route plan(s)");

        // Step 2: Select Route Plan
        RoutePlanDTO selectedPlan = selectRoutePlan(routePlans);
        if (selectedPlan == null) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 3: Display Route Plan Details
        displayRoutePlanDetails(selectedPlan);

        if (!Utils.confirm("\nProceed with train assembly for this route?")) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 4: Get starting facility
        Long startFacilityId = selectedPlan.getStops().get(0).getFacilityId();

        // Step 5: Select Locomotives
        System.out.println("\n🚂 Step 5: Select Locomotives");
        System.out.println("Locomotives sorted by distance to route start (" +
                selectedPlan.getStops().get(0).getFacilityName() + "):");

        List<Long> selectedLocoIds = selectLocomotives(startFacilityId);

        if (selectedLocoIds.isEmpty()) {
            System.out.println("❌ No locomotives selected. Cannot create train.");
            return;
        }
        System.out.println("✓ Selected " + selectedLocoIds.size() + " locomotive(s)");

        // Step 6: Select Wagons
        System.out.println("\n🚃 Step 6: Select Wagons");

        // Show wagons required for the route
        List<WagonDTO> requiredWagons = controller.getWagonsForRoutePlan(selectedPlan);
        if (!requiredWagons.isEmpty()) {
            System.out.println("\nWagons carrying route freights:");
            for (WagonDTO wagon : requiredWagons) {
                System.out.println("  • " + formatWagon(wagon));
            }
            System.out.println("\nThese wagons will be automatically included.");
        }

        List<Long> selectedWagonIds = selectWagons(startFacilityId, requiredWagons);

        System.out.println("✓ Selected " + selectedWagonIds.size() + " wagon(s) total");

        // Step 7: Display Summary
        displayAssemblySummary(selectedPlan, selectedLocoIds, selectedWagonIds);

        if (!Utils.confirm("\nConfirm train assembly and route assignment?")) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 8: Assemble Train and Assign to Route
        try {
            TrainDTO train = controller.assembleAndAssignTrain(
                    selectedPlan,
                    selectedLocoIds,
                    selectedWagonIds
            );

            System.out.println("\n✓ SUCCESS! Train assembled and assigned to route!");
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║  Train ID: " + String.format("%-37d", train.trainId()) + "║");
            System.out.println("║  Freights: " + String.format("%-37d", selectedPlan.getFreightIds().size()) + "║");
            System.out.println("╚════════════════════════════════════════════════╝");

            displayTrainDetails(train);

            System.out.println("\nℹ This train is now ready for scheduling in USLP10.");

        } catch (Exception e) {
            System.out.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // ROUTE PLAN SELECTION
    // ================================================================

    private RoutePlanDTO selectRoutePlan(List<RoutePlanDTO> plans) {
        System.out.println("\n📍 Step 2: Select Route Plan");

        List<String> planStrings = new ArrayList<>();
        for (int i = 0; i < plans.size(); i++) {
            RoutePlanDTO plan = plans.get(i);
            planStrings.add(formatRoutePlanSummary(i + 1, plan));
        }

        int idx = Utils.showAndSelectIndex(planStrings, "Available Route Plans");
        if (idx == -1) return null;

        return plans.get(idx);
    }

    private String formatRoutePlanSummary(int num, RoutePlanDTO plan) {
        String type = plan.getFreightIds().size() == 1 ? "SIMPLE" : "COMPLEX";
        String path = plan.getStops().isEmpty() ? "?" :
                plan.getStops().get(0).getFacilityName() + " → " +
                        plan.getStops().get(plan.getStops().size() - 1).getFacilityName();

        return String.format("[%d] %s - %d freight(s), %d stop(s) - %s",
                num, type, plan.getFreightIds().size(), plan.getStops().size(), path);
    }

    // ================================================================
    // LOCOMOTIVE SELECTION
    // ================================================================

    private List<Long> selectLocomotives(Long startFacilityId) {
        List<LocomotiveDTO> availableLocos = controller.getLocomotivesSortedByDistance(startFacilityId);

        if (availableLocos.isEmpty()) {
            System.out.println("❌ No locomotives available.");
            return List.of();
        }

        List<Long> selectedLocoIds = new ArrayList<>();
        boolean selectingLocos = true;

        while (selectingLocos) {
            List<LocomotiveDTO> choices = new ArrayList<>(availableLocos);
            choices.removeIf(l -> selectedLocoIds.contains(l.locomotiveId()));

            if (choices.isEmpty()) {
                System.out.println("✓ All available locomotives selected.");
                break;
            }

            System.out.println("\nCurrently selected: " + selectedLocoIds.size() + " locomotive(s)");

            List<String> locoStrings = new ArrayList<>();
            for (LocomotiveDTO loco : choices) {
                locoStrings.add(formatLocomotive(loco));
            }

            int idx = Utils.showAndSelectIndex(locoStrings, "Select Locomotive (-1 when done)");

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

    private List<Long> selectWagons(Long startFacilityId, List<WagonDTO> requiredWagons) {
        // Start with required wagons
        List<Long> selectedWagonIds = requiredWagons.stream()
                .map(WagonDTO::wagonId)
                .collect(java.util.stream.Collectors.toList());

        if (!Utils.confirm("\nAdd additional wagons beyond the required ones?")) {
            return selectedWagonIds;
        }

        List<WagonDTO> availableWagons = controller.getAvailableWagons(startFacilityId);
        availableWagons.removeIf(w -> selectedWagonIds.contains(w.wagonId()));

        if (availableWagons.isEmpty()) {
            System.out.println("ℹ No additional wagons available at this facility.");
            return selectedWagonIds;
        }

        boolean selectingWagons = true;

        while (selectingWagons) {
            List<WagonDTO> choices = new ArrayList<>(availableWagons);
            choices.removeIf(w -> selectedWagonIds.contains(w.wagonId()));

            if (choices.isEmpty()) {
                System.out.println("✓ All available wagons selected.");
                break;
            }

            System.out.println("\nCurrently selected: " + selectedWagonIds.size() + " wagon(s)");

            List<String> wagonStrings = new ArrayList<>();
            for (WagonDTO wagon : choices) {
                wagonStrings.add(formatWagon(wagon));
            }

            int idx = Utils.showAndSelectIndex(wagonStrings, "Select Wagon (-1 when done)");

            if (idx == -1) {
                selectingWagons = false;
            } else {
                WagonDTO selected = choices.get(idx);
                selectedWagonIds.add(selected.wagonId());
                System.out.println("✓ Added: " + formatWagon(selected));

                if (!Utils.confirm("Add another wagon?")) {
                    selectingWagons = false;
                }
            }
        }

        return selectedWagonIds;
    }

    // ================================================================
    // DISPLAY METHODS
    // ================================================================

    private void displayRoutePlanDetails(RoutePlanDTO plan) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              ROUTE PLAN DETAILS");
        System.out.println("=".repeat(60));

        String type = plan.getFreightIds().size() == 1 ? "SIMPLE" : "COMPLEX";
        System.out.println("Type: " + type);
        System.out.println("Freights: " + plan.getFreightIds().size());
        System.out.println("Stops: " + plan.getStops().size());

        System.out.println("\n--- Route Path ---");
        for (int i = 0; i < plan.getStops().size(); i++) {
            StopPlanDTO stop = plan.getStops().get(i);
            System.out.printf("%2d. %-30s  Load: %-2d  Unload: %-2d%n",
                    (i + 1),
                    stop.getFacilityName(),
                    stop.getToLoad().size(),
                    stop.getToUnload().size());
        }

        System.out.println("\n--- Freights in Route ---");
        for (Long freightId : plan.getFreightIds()) {
            System.out.print("Freight " + freightId + ": ");
            List<CargoDTO> cargos = controller.getCargosForFreight(freightId);
            if (cargos.isEmpty()) {
                System.out.println("(no cargo details)");
            } else {
                System.out.println(cargos.size() + " cargo item(s)");
                for (CargoDTO cargo : cargos) {
                    System.out.printf("    • Wagon %d: %s (%.1f kg)%n",
                            cargo.wagonId(), cargo.description(), cargo.weight());
                }
            }
        }

        System.out.println("=".repeat(60));
    }

    private void displayAssemblySummary(RoutePlanDTO plan, List<Long> locoIds, List<Long> wagonIds) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              TRAIN ASSEMBLY SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Route: " + plan.getStops().get(0).getFacilityName() + " → " +
                plan.getStops().get(plan.getStops().size() - 1).getFacilityName());
        System.out.println("Stops: " + plan.getStops().size());
        System.out.println("Freights: " + plan.getFreightIds().size());
        System.out.println("\nLocomotives: " + locoIds.size());
        System.out.println("Wagons: " + wagonIds.size());
        System.out.println("=".repeat(60));
    }

    private void displayTrainDetails(TrainDTO train) {
        System.out.println("\n--- Train Details ---");
        System.out.println("Train Number: " + train.trainNumber());
        System.out.println("Locomotives: " + train.locomotiveIds().size());
        System.out.println("Wagons: " + train.wagonIds().size());
        System.out.printf("Total Weight: %.2f tonnes%n", train.totalWeight());
        System.out.printf("Total Length: %.2f meters%n", train.totalLength());
    }

    // ================================================================
    // FORMATTING HELPERS
    // ================================================================

    private String formatLocomotive(LocomotiveDTO loco) {
        return String.format("[%d] %s - %s - %.0f kW - at %s [%s]",
                loco.locomotiveId(),
                loco.model(),
                loco.type(),
                loco.power(),
                loco.currentFacilityName(),
                loco.status());
    }

    private String formatWagon(WagonDTO wagon) {
        return String.format("[%d] %s - %.1f t payload - at %s",
                wagon.wagonId(),
                wagon.type(),
                wagon.payload(),
                wagon.currentFacilityName());
    }
}