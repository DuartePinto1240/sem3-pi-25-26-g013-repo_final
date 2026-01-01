package main.java.pt.ipp.isep.dei.repository;

/**
 * Singleton class that provides centralized access to all repository instances in the application.
 */
public class Repositories {

    /** Instância única do singleton */
    private static Repositories instance;

    // ================================================================
    // Repositórios
    // ================================================================

    private final AuthenticationRepository authenticationRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final RailwayLineRepository railwayLineRepository;
    private final ScheduleRepository scheduleRepository;

    // ================================================================
    // Construtor privado
    // ================================================================

    private Repositories() {
        authenticationRepository = new AuthenticationRepository();

        trainRepository = new OracleTrainRepository();
        stationRepository = new CsvStationRepository("train_station_dataset_v2/stations.csv");
        railwayLineRepository = new CsvRailwayLineRepository(
                "train_station_dataset_v2/lines.csv", stationRepository
        );

        // ScheduleRepository usando implementação Oracle
        scheduleRepository = new OracleScheduleRepository();
    }

    // ================================================================
    // Acesso ao singleton
    // ================================================================

    public static Repositories getInstance() {
        if (instance == null) {
            synchronized (Repositories.class) {
                if (instance == null) {
                    instance = new Repositories();
                }
            }
        }
        return instance;
    }

    // ================================================================
    // Getters
    // ================================================================

    public AuthenticationRepository getAuthenticationRepository() {
        return authenticationRepository;
    }

    public TrainRepository getTrainRepository() {
        return trainRepository;
    }

    public StationRepository getStationRepository() {
        return stationRepository;
    }

    public RailwayLineRepository getRailwayLineRepository() {
        return railwayLineRepository;
    }

    public ScheduleRepository getScheduleRepository() {
        return scheduleRepository;
    }
}
