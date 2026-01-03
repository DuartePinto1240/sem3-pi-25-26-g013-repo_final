package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USLP09Controller;
import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for USLP09 - Train Assembly and Assignment to Routes
 *
 * Flow:
 * 1. Select a route (from USLP08)
 * 2. View route details
 * 3. Select locomotives (sorted by distance)
 * 4. Select wagons (required for route freights)
 * 5. Create train and assign to route
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

        // Step 1: Select Route
        RouteDTO selectedRoute = selectRoute();
        if (selectedRoute == null) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 2: Display Route Details
        displayRouteDetails(selectedRoute);

        if (!Utils.confirm("\nProceed with train assembly for this route?")) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 3: Select Locomotives (sorted by distance)
        System.out.println("\n🚂 Step 3: Select Locomotives");
        System.out.println("Locomotives are sorted by distance to route start:");
        List<Long> selectedLocoIds = selectLocomotives(selectedRoute.routeId());

        if (selectedLocoIds.isEmpty()) {
            System.out.println("❌ No locomotives selected. Cannot create train.");
            return;
        }
        System.out.println("✓ Selected " + selectedLocoIds.size() + " locomotive(s)");

        // Step 4: Select Wagons (required for route)
        System.out.println("\n🚃 Step 4: Select Wagons");
        System.out.println("These wagons will carry the freights in the route:");
        List<Long> selectedWagonIds = selectWagons(selectedRoute.routeId());

        if (selectedWagonIds.isEmpty()) {
            System.out.println("⚠ Warning: No wagons selected.");
            if (!Utils.confirm("Continue without wagons?")) {
                return;
            }
        } else {
            System.out.println("✓ Selected " + selectedWagonIds.size() + " wagon(s)");
        }

        // Step 5: Display Summary
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              TRAIN ASSEMBLY SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Route ID: " + selectedRoute.routeId());
        System.out.println("Route Type: " + selectedRoute.routeType());
        System.out.println("Route: " + selectedRoute.startFacilityName() + " → " + selectedRoute.endFacilityName());
        System.out.println("Stops: " + selectedRoute.numStops());
        System.out.println("Freights: " + selectedRoute.numFreights());
        System.out.println("\nLocomotives: " + selectedLocoIds);
        System.out.println("Wagons: " + selectedWagonIds);
        System.out.println("=".repeat(60));

        if (!Utils.confirm("\nConfirm train assembly and assignment?")) {
            System.out.println("⚠ Operation cancelled.");
            return;
        }

        // Step 6: Create Train and Assign to Route
        try {
            TrainDTO train = controller.assembleAndAssignTrain(
                    selectedRoute.routeId(),
                    selectedLocoIds,
                    selectedWagonIds
            );

            System.out.println("\n✓ SUCCESS! Train assembled and assigned to route!");
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║  Train ID: " + String.format("%-37d", train.trainId()) + "║");
            System.out.println("║  Route ID: " + String.format("%-37d", selectedRoute.routeId()) + "║");
            System.out.println("╚════════════════════════════════════════════════╝");

            displayTrainDetails(train);

        } catch (Exception e) {
            System.out.println("\n❌ ERROR creating train: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // ROUTE SELECTION
    // ================================================================

    private RouteDTO selectRoute() {
        System.out.println("\n📍 Step 1: Select Route");

        List<RouteDTO> routes = controller.getAvailableRoutes();

        if (routes.isEmpty()) {
            System.out.println("❌ No routes available.");
            System.out.println("ℹ  Please create routes first using USLP08 (Route Planning).");
            return null;
        }

        // Format routes for display
        List<String> routeStrings = new ArrayList<>();
        for (RouteDTO route : routes) {
            routeStrings.add(String.format(
                    "[%d] %s → %s (%d stops, %d freights, %s)",
                    route.routeId(),
                    route.startFacilityName(),
                    route.endFacilityName(),
                    route.numStops(),
                    route.numFreights(),
                    route.routeType()
            ));
        }

        int idx = Utils.showAndSelectIndex(routeStrings, "Available Routes");
        if (idx == -1) return null;

        return routes.get(idx);
    }

    // ================================================================
    // LOCOMOTIVE SELECTION (SORTED BY DISTANCE)
    // ================================================================

    private List<Long> selectLocomotives(Long routeId) {
        List<LocomotiveDTO> availableLocos = controller.getLocomotivesSortedByDistance(routeId);

        if (availableLocos.isEmpty()) {
            System.out.println("❌ No locomotives available.");
            return List.of();
        }

        System.out.println("\nAvailable locomotives (sorted by distance to route start):");
        System.out.println("Legend: [PARKED] = at station, [IN_TRANSIT] = on route\n");

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

            // Format for display with distance info
            List<String> locoStrings = new ArrayList<>();
            for (LocomotiveDTO loco : choices) {
                locoStrings.add(String.format(
                        "[%d] %s - %s - %.0f kW - at %s [%s]",
                        loco.locomotiveId(),
                        loco.model(),
                        loco.type(),
                        loco.power(),
                        loco.currentFacilityName(),
                        loco.status()
                ));
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
    // WAGON SELECTION (FOR ROUTE FREIGHTS)
    // ================================================================

    private List<Long> selectWagons(Long routeId) {
        List<WagonDTO> requiredWagons = controller.getWagonsForRoute(routeId);

        if (requiredWagons.isEmpty()) {
            System.out.println("ℹ No specific wagons required for this route.");
            System.out.println("You can add wagons manually if needed.");
        } else {
            System.out.println("\nWagons carrying route freights:");
            for (WagonDTO wagon : requiredWagons) {
                System.out.println("  • " + formatWagon(wagon));
            }
        }

        // For now, simplified: get available wagons at route start
        RouteDTO route = controller.getRouteDetails(routeId);
        Long startFacilityId = route.startFacilityId();

        // Would call: controller.getAvailableWagons(startFacilityId)
        // But for now, simplified
        List<Long> selectedWagonIds = new ArrayList<>();

        if (Utils.confirm("\nAdd additional wagons?")) {
            boolean selectingWagons = true;
            while (selectingWagons) {
                long wagonId = Utils.readIntegerFromConsole("Enter Wagon ID (-1 to finish): ");
                if (wagonId == -1) {
                    selectingWagons = false;
                } else {
                    selectedWagonIds.add(wagonId);
                    System.out.println("✓ Added wagon " + wagonId);
                }
            }
        }

        return selectedWagonIds;
    }

    // ================================================================
    // DISPLAY METHODS
    // ================================================================

    private void displayRouteDetails(RouteDTO route) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                  ROUTE DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("Route ID: " + route.routeId());
        System.out.println("Type: " + route.routeType());
        System.out.println("Created: " + route.createdDate());
        System.out.println("\nRoute Path:");
        System.out.println("  " + route.startFacilityName() + " → " + route.endFacilityName());
        System.out.println("\nStops (" + route.numStops() + "):");

        for (RouteStopDTO stop : route.stops()) {
            System.out.printf("  %d. %-30s  Load: %d  Unload: %d%n",
                    stop.stopOrder(),
                    stop.facilityName(),
                    stop.freightsToLoad(),
                    stop.freightsToUnload());
        }

        System.out.println("\nTotal Freights: " + route.numFreights());
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
        return String.format("[%d] %s - %s - %.0f kW at %s",
                loco.locomotiveId(),
                loco.model(),
                loco.type(),
                loco.power(),
                loco.currentFacilityName());
    }

    private String formatWagon(WagonDTO wagon) {
        return String.format("[%d] %s - %.1f t capacity",
                wagon.wagonId(),
                wagon.type(),
                wagon.payload());
    }
}