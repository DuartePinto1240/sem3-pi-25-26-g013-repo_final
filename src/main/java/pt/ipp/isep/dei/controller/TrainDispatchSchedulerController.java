package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TrainSchedule;
import main.java.pt.ipp.isep.dei.repository.CsvLocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.CsvRailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.CsvStationRepository;
import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.service.TrafficDispatcherService;
import main.java.pt.ipp.isep.dei.service.TrainSchedulerService;
import main.java.pt.ipp.isep.dei.service.TrainSchedulerService.TrainDispatchRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for USLP07 - Train Dispatch Scheduler.
 * Bridges the UI and the service layer, hiding repository/service setup from the UI.
 */
public class TrainDispatchSchedulerController {

    private final StationRepository stationRepository;
    private final LocomotiveRepository locomotiveRepository;
    private final RailwayLineRepository railwayLineRepository;
    private final TrafficDispatcherService baseService;
    private final TrainSchedulerService schedulerService;

    public TrainDispatchSchedulerController() {
        // Paths iguais aos que usaste no USLP03
        final String CSV_FOLDER = "csv_data/";
        final String STATIONS_FILE = CSV_FOLDER + "stations.csv";
        final String LOCOMOTIVES_FILE = CSV_FOLDER + "locomotives.csv";
        final String LINES_FILE = CSV_FOLDER + "railway_lines.csv";
        final String SEGMENTS_FILE = CSV_FOLDER + "line_segments.csv";

        this.stationRepository = new CsvStationRepository(STATIONS_FILE);
        this.locomotiveRepository = new CsvLocomotiveRepository(LOCOMOTIVES_FILE);
        this.railwayLineRepository = new CsvRailwayLineRepository(LINES_FILE, SEGMENTS_FILE, stationRepository);

        this.baseService = new TrafficDispatcherService(
                stationRepository,
                locomotiveRepository,
                railwayLineRepository
        );

        this.schedulerService = new TrainSchedulerService(
                stationRepository,
                locomotiveRepository,
                railwayLineRepository,
                baseService
        );
    }

    /**
     * Devolve a lista de estações disponíveis, já ordenada por ID (como no USLP03).
     */
    public List<Station> getAvailableStations() {
        List<Station> stations = baseService.getAvailableStations();
        stations.sort(Comparator.comparing(station -> Integer.parseInt(station.getId())));
        return stations;
    }

    /**
     * Devolve a lista de locomotivas disponíveis.
     */
    public List<Locomotive> getAvailableLocomotives() {
        return baseService.getAvailableLocomotives();
    }

    /**
     * Constrói um pedido de dispatch para um comboio.
     */
    public TrainDispatchRequest createTrainDispatchRequest(
            String trainId,
            List<Station> route,
            List<Locomotive> locomotives,
            java.time.LocalDateTime departureTime
    ) {
        return new TrainDispatchRequest(trainId, route, locomotives, departureTime);
    }

    /**
     * Agenda todos os comboios e devolve os respetivos schedules.
     */
    public List<TrainSchedule> scheduleTrains(List<TrainDispatchRequest> requests) {
        return schedulerService.scheduleTrains(requests);
    }
}
