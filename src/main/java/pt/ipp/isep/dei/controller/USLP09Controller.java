package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.Repositories;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.repository.TrainRepository;

import java.util.List;

public class USLP09Controller {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    public USLP09Controller() {
        this.trainRepository = Repositories.getInstance().getTrainRepository();
        this.stationRepository = Repositories.getInstance().getStationRepository();
    }

    /**
     * Retrieves all stations to allow the user to select the assembly location.
     */
    public List<Station> getStations() {
        // Assuming findAll() or getStations() exists in your StationRepository interface
        return stationRepository.findAll();
    }

    /**
     * Gets available locomotives at a specific facility (station).
     */
    public List<LocomotiveDTO> getAvailableLocomotives(long facilityId) {
        return trainRepository.getAvailableLocomotives(facilityId);
    }

    /**
     * Gets available wagons at a specific facility (station).
     */
    public List<WagonDTO> getAvailableWagons(long facilityId) {
        return trainRepository.getAvailableWagons(facilityId);
    }

    /**
     * Assembles the train and persists it to the database.
     */
    public long createTrain(List<Long> locomotiveIds, List<Long> wagonIds) {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            throw new IllegalArgumentException("A train must have at least one locomotive.");
        }
        return trainRepository.createTrain(locomotiveIds, wagonIds);
    }
}