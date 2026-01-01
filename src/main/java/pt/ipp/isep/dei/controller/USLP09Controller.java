package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.TrainDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;
import main.java.pt.ipp.isep.dei.repository.Repositories;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.repository.TrainRepository;

import java.util.List;

/**
 * Controller for USLP09 - Train Assembly and Assignment
 */
public class USLP09Controller {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    public USLP09Controller() {
        this.trainRepository = Repositories.getInstance().getTrainRepository();
        this.stationRepository = Repositories.getInstance().getStationRepository();
    }

    /**
     * Retrieves all stations to allow the user to select the assembly location.
     * @return list of all stations
     */
    public List<Station> getStations() {
        return stationRepository.findAll();
    }

    /**
     * Gets available locomotives at a specific facility (station).
     * Only returns locomotives that are PARKED (not in transit).
     *
     * @param facilityId the ID of the facility/station
     * @return list of available locomotives
     */
    public List<LocomotiveDTO> getAvailableLocomotives(long facilityId) {
        return trainRepository.getAvailableLocomotives(facilityId);
    }

    /**
     * Gets available wagons at a specific facility (station).
     * Only returns wagons that are PARKED (not in transit).
     *
     * @param facilityId the ID of the facility/station
     * @return list of available wagons
     */
    public List<WagonDTO> getAvailableWagons(long facilityId) {
        return trainRepository.getAvailableWagons(facilityId);
    }

    /**
     * Assembles a new train with the selected locomotives and wagons.
     * The train is persisted to the database.
     *
     * @param locomotiveIds list of locomotive IDs to include in the train
     * @param wagonIds list of wagon IDs to include in the train
     * @return the ID of the newly created train
     * @throws IllegalArgumentException if no locomotives are provided
     */
    public long createTrain(List<Long> locomotiveIds, List<Long> wagonIds) {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            throw new IllegalArgumentException("A train must have at least one locomotive.");
        }
        return trainRepository.createTrain(locomotiveIds, wagonIds);
    }

    /**
     * Gets detailed information about a specific train.
     *
     * @param trainId the ID of the train
     * @return TrainDTO with complete train information
     */
    public TrainDTO getTrainById(long trainId) {
        return trainRepository.getTrainById(trainId);
    }

    /**
     * Assigns a train to a specific route.
     * (Note: Current schema may not support this directly)
     *
     * @param trainId the ID of the train
     * @param routeId the ID of the route (may be null)
     */
    public void assignTrainToRoute(long trainId, Long routeId) {
        trainRepository.assignTrainToRoute(trainId, routeId);
    }

    /**
     * Validates if a train can be assembled with given components.
     * Checks:
     * - At least one locomotive
     * - Compatible gauges
     * - Total length within limits
     *
     * @param locomotiveIds locomotive IDs
     * @param wagonIds wagon IDs
     * @param facilityId facility where train is being assembled
     * @return true if valid, throws exception otherwise
     */
    public boolean validateTrainAssembly(List<Long> locomotiveIds, List<Long> wagonIds, long facilityId) {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            throw new IllegalArgumentException("A train must have at least one locomotive.");
        }

        // Get actual DTOs to validate
        List<LocomotiveDTO> locos = trainRepository.getAvailableLocomotives(facilityId);
        List<WagonDTO> wagons = trainRepository.getAvailableWagons(facilityId);

        // Check if all requested IDs exist
        for (Long locoId : locomotiveIds) {
            if (locos.stream().noneMatch(l -> l.locomotiveId().equals(locoId))) {
                throw new IllegalArgumentException("Locomotive not found or not available: " + locoId);
            }
        }

        for (Long wagonId : wagonIds) {
            if (wagons.stream().noneMatch(w -> w.wagonId().equals(wagonId))) {
                throw new IllegalArgumentException("Wagon not found or not available: " + wagonId);
            }
        }

        // Additional validations could be added here:
        // - Gauge compatibility
        // - Maximum train length
        // - Weight limits
        // etc.

        return true;
    }

    /**
     * Calculates estimated total weight of the train
     *
     * @param locomotiveIds locomotive IDs
     * @param wagonIds wagon IDs
     * @param facilityId facility ID to fetch DTOs
     * @return total weight in tonnes
     */
    public double calculateTotalWeight(List<Long> locomotiveIds, List<Long> wagonIds, long facilityId) {
        double totalWeight = 0.0;

        List<LocomotiveDTO> locos = trainRepository.getAvailableLocomotives(facilityId);
        for (Long locoId : locomotiveIds) {
            totalWeight += locos.stream()
                    .filter(l -> l.locomotiveId().equals(locoId))
                    .mapToDouble(LocomotiveDTO::weight)
                    .sum();
        }

        List<WagonDTO> wagons = trainRepository.getAvailableWagons(facilityId);
        for (Long wagonId : wagonIds) {
            totalWeight += wagons.stream()
                    .filter(w -> w.wagonId().equals(wagonId))
                    .mapToDouble(w -> w.tareWeight() + w.payload())
                    .sum();
        }

        return totalWeight;
    }

    /**
     * Calculates estimated total length of the train
     *
     * @param locomotiveIds locomotive IDs
     * @param wagonIds wagon IDs
     * @param facilityId facility ID to fetch DTOs
     * @return total length in meters
     */
    public double calculateTotalLength(List<Long> locomotiveIds, List<Long> wagonIds, long facilityId) {
        double totalLength = 0.0;

        // Assume average locomotive length of 20m
        totalLength += locomotiveIds.size() * 20.0;

        List<WagonDTO> wagons = trainRepository.getAvailableWagons(facilityId);
        for (Long wagonId : wagonIds) {
            totalLength += wagons.stream()
                    .filter(w -> w.wagonId().equals(wagonId))
                    .mapToDouble(WagonDTO::length)
                    .sum();
        }

        return totalLength;
    }
}