package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.*;
import main.java.pt.ipp.isep.dei.service.RoutePlannerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for USLP09 - Train Assembly and Assignment to Routes
 *
 * This controller integrates with USLP08 to get route plans,
 * then allows assembling trains and assigning them to routes.
 *
 * Flow:
 * 1. Get route plans from USLP08 (RoutePlannerController)
 * 2. User selects a route plan
 * 3. User selects locomotives and wagons
 * 4. Create train
 * 5. Save route plan to database
 * 6. Assign train to saved route
 */
public class USLP09Controller {

    private final TrainRepository trainRepo;
    private final RouteRepository routeRepo;
    private final RoutePlannerController routePlanner; // ← USLP08!
    private final FreightRepository freightRepo;

    public USLP09Controller() {
        this.trainRepo = new OracleTrainRepository();
        this.routeRepo = new OracleRouteRepository();
        this.freightRepo = new OracleFreightRepository();

        // ✨ Instanciar USLP08
        RoutePlannerService plannerService = new RoutePlannerService();
        this.routePlanner = new RoutePlannerController(freightRepo, plannerService);
    }

    /**
     * Get available route plans from USLP08
     * These are route plans generated from pending freights
     *
     * @return List of route plans (not yet saved to database)
     */
    public List<RoutePlanDTO> getAvailableRoutePlans() {
        // ✨ Chama USLP08 para obter rotas planeadas
        return routePlanner.autoPlanAll();
    }

    /**
     * Get pending freights (for display purposes)
     */
    public List<PendingFreightDTO> getPendingFreights() {
        return freightRepo.getPendingFreights();
    }

    /**
     * Get locomotives sorted by distance to route start
     *
     * @param startFacilityId Starting facility of the route
     * @return List of locomotives sorted by distance
     */
    public List<LocomotiveDTO> getLocomotivesSortedByDistance(Long startFacilityId) {
        // Get all available locomotives
        List<LocomotiveDTO> allLocos = trainRepo.getAllLocomotives();

        // Filter available (not in trains)
        List<LocomotiveDTO> available = allLocos.stream()
                .filter(l -> "PARKED".equals(l.status()))
                .collect(Collectors.toList());

        // Sort by distance to start facility
        // Simplified: just check if at same facility first, then by ID
        return available.stream()
                .sorted((a, b) -> {
                    boolean aAtStart = a.currentFacilityId().equals(startFacilityId);
                    boolean bAtStart = b.currentFacilityId().equals(startFacilityId);

                    if (aAtStart && !bAtStart) return -1;
                    if (!aAtStart && bAtStart) return 1;

                    // Both at start or both not at start - sort by distance estimate
                    long distA = Math.abs(a.currentFacilityId() - startFacilityId);
                    long distB = Math.abs(b.currentFacilityId() - startFacilityId);

                    return Long.compare(distA, distB);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get wagons required for a route plan
     * These are the wagons carrying the freights in the route
     *
     * @param routePlan The route plan
     * @return List of wagons needed for the freights
     */
    public List<WagonDTO> getWagonsForRoutePlan(RoutePlanDTO routePlan) {
        List<WagonDTO> wagons = new ArrayList<>();

        for (Long freightId : routePlan.getFreightIds()) {
            List<CargoDTO> cargos = freightRepo.getCargosForFreight(freightId);

            for (CargoDTO cargo : cargos) {
                // Get wagon info
                WagonDTO wagon = trainRepo.getWagonById(cargo.wagonId());
                if (wagon != null && !wagons.contains(wagon)) {
                    wagons.add(wagon);
                }
            }
        }

        return wagons;
    }

    /**
     * Get all available wagons at a facility
     */
    public List<WagonDTO> getAvailableWagons(Long facilityId) {
        return trainRepo.getAvailableWagons(facilityId);
    }

    /**
     * Assemble train and assign to route plan
     *
     * This method:
     * 1. Creates the train with selected locomotives and wagons
     * 2. Saves the route plan to the database (converts RoutePlanDTO → Route)
     * 3. Assigns the train to the saved route
     * 4. Updates freights with the train_id
     *
     * @param routePlan Route plan from USLP08
     * @param locomotiveIds Selected locomotives
     * @param wagonIds Selected wagons
     * @return Created train with route assigned
     */
    public TrainDTO assembleAndAssignTrain(
            RoutePlanDTO routePlan,
            List<Long> locomotiveIds,
            List<Long> wagonIds
    ) {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            throw new IllegalArgumentException("A train must have at least one locomotive.");
        }

        if (routePlan == null || routePlan.getFreightIds().isEmpty()) {
            throw new IllegalArgumentException("Route plan must have at least one freight.");
        }

        try {
            // 1. Create train
            System.out.println("Creating train with " + locomotiveIds.size() +
                    " locomotive(s) and " + wagonIds.size() + " wagon(s)...");
            Long trainId = trainRepo.createTrain(locomotiveIds, wagonIds);
            System.out.println("✓ Train created: " + trainId);

            // 2. Save route plan to database
            System.out.println("Saving route plan to database...");
            Long routeId = saveRoutePlanToDatabase(routePlan);
            System.out.println("✓ Route saved: " + routeId);

            // 3. Assign train to route
            System.out.println("Assigning train to route...");
            trainRepo.assignTrainToRoute(trainId, routeId);
            System.out.println("✓ Train assigned to route");

            // 4. Return train details
            return trainRepo.getTrainById(trainId);

        } catch (Exception e) {
            System.err.println("Error assembling and assigning train: " + e.getMessage());
            throw new RuntimeException("Failed to assemble and assign train", e);
        }
    }

    /**
     * Save a route plan (from USLP08) to the database
     * Converts RoutePlanDTO to database Route structure
     *
     * @param routePlan Route plan from USLP08
     * @return Generated route_id
     */
    private Long saveRoutePlanToDatabase(RoutePlanDTO routePlan) {
        // Determine route type
        String routeType = routePlan.getFreightIds().size() == 1 ? "SIMPLE" : "COMPLEX";

        // Convert freight IDs to comma-separated string
        String freightIdsStr = routePlan.getFreightIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Call database function to create route
        return routeRepo.createRoute(routePlan.getFreightIds(), routeType);
    }

    /**
     * Get train details
     */
    public TrainDTO getTrainById(Long trainId) {
        return trainRepo.getTrainById(trainId);
    }

    /**
     * Get cargo details for display
     */
    public List<CargoDTO> getCargosForFreight(Long freightId) {
        return freightRepo.getCargosForFreight(freightId);
    }

    /**
     * Convert RoutePlanDTO to a display-friendly format
     */
    public String formatRoutePlan(RoutePlanDTO plan) {
        StringBuilder sb = new StringBuilder();

        sb.append("Freights: ").append(plan.getFreightIds().size()).append("\n");
        sb.append("Stops: ").append(plan.getStops().size()).append("\n");
        sb.append("Path: ");

        for (int i = 0; i < plan.getStops().size(); i++) {
            StopPlanDTO stop = plan.getStops().get(i);
            sb.append(stop.getFacilityName());
            if (i < plan.getStops().size() - 1) {
                sb.append(" → ");
            }
        }

        return sb.toString();
    }
}