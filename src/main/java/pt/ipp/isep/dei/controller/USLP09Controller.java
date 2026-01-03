package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for USLP09 - Train Assembly and Assignment to Routes
 *
 * This controller handles:
 * 1. Listing available routes (from USLP08)
 * 2. Selecting locomotives and wagons for a route
 * 3. Creating a train
 * 4. Assigning the train to the route
 */
public class USLP09Controller {

    private final TrainRepository trainRepo;
    private final RouteRepository routeRepo;

    public USLP09Controller() {
        this.trainRepo = new OracleTrainRepository();
        this.routeRepo = new OracleRouteRepository();
    }

    /**
     * Get all available routes (routes without assigned trains)
     */
    public List<RouteDTO> getAvailableRoutes() {
        return routeRepo.getAvailableRoutes();
    }

    /**
     * Get detailed information about a specific route
     */
    public RouteDTO getRouteDetails(Long routeId) {
        return routeRepo.getRouteById(routeId);
    }

    /**
     * Get locomotives sorted by distance to route start
     * Implements acceptance criteria: locomotives ordered by distance
     */
    public List<LocomotiveDTO> getLocomotivesSortedByDistance(Long routeId) {
        return trainRepo.getLocomotivesSortedByDistance(routeId);
    }

    /**
     * Get wagons required for the route
     * These are the wagons that carry the freights in the route
     */
    public List<WagonDTO> getWagonsForRoute(Long routeId) {
        RouteDTO route = routeRepo.getRouteById(routeId);
        if (route == null) return List.of();

        // Get wagons from freights
        List<WagonDTO> wagons = new ArrayList<>();
        for (Long freightId : route.freightIds()) {
            wagons.addAll(getWagonsForFreight(freightId));
        }
        return wagons;
    }

    /**
     * Assemble train and assign to route
     *
     * @param routeId Route to assign train to
     * @param locomotiveIds Selected locomotives
     * @param wagonIds Selected wagons
     * @return Created train
     */
    public TrainDTO assembleAndAssignTrain(
            Long routeId,
            List<Long> locomotiveIds,
            List<Long> wagonIds
    ) {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            throw new IllegalArgumentException("A train must have at least one locomotive.");
        }

        // 1. Create train
        Long trainId = trainRepo.createTrain(locomotiveIds, wagonIds);

        // 2. Assign train to route
        trainRepo.assignTrainToRoute(trainId, routeId);

        // 3. Return train details
        return trainRepo.getTrainById(trainId);
    }

    /**
     * Get train details
     */
    public TrainDTO getTrainById(Long trainId) {
        return trainRepo.getTrainById(trainId);
    }

    // Helper method
    private List<WagonDTO> getWagonsForFreight(Long freightId) {
        // Would need to query FreightWagon table
        // Simplified for now
        return List.of();
    }
}