package main.java.pt.ipp.isep.dei.repository;

/**
 * Singleton class that provides centralized access to all repository instances in the application.
 * <p>
 * This ensures that all components use the same, unique repository objects, promoting
 * data consistency and managing resource overhead.
 * </p>
 */
public class Repositories {

    /** The single instance of the Repositories class. */
    private static Repositories instance;

    /** The repository for managing authentication-related data and logic. */
    private final AuthenticationRepository authenticationRepository;

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final RailwayLineRepository railwayLineRepository;


    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes all application repositories.
     */
    private Repositories() {
        authenticationRepository = new AuthenticationRepository();

        trainRepository = new OracleTrainRepository();
        stationRepository = new CsvStationRepository("train_station_dataset_v2/stations.csv");
        railwayLineRepository = new CsvRailwayLineRepository(
                "train_station_dataset_v2/lines.csv", stationRepository);
    }


    /**
     * Provides the global access point to the single {@code Repositories} instance.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return the singleton instance of {@code Repositories}
     */
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


    /**
     * Retrieves the instance of the {@link AuthenticationRepository}.
     *
     * @return the authentication repository instance
     */
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
}