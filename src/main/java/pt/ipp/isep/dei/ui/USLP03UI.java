package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TravelTimeResult;
import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.repository.CsvLocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.CsvRailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.CsvStationRepository;
import main.java.pt.ipp.isep.dei.service.TrafficDispatcherService;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Console User Interface (UI) for Use Case USLP03: Calculate Estimated Travel Time.
 * <p>
 * This UI handles the user interaction for selecting the starting station,
 * ending station, and a locomotive, and then calls the {@link TrafficDispatcherService}
 * to calculate the travel time across the railway network.
 * </p>
 */
public class USLP03UI implements Runnable {

    /**
     * Constructs the USLP03 UI.
     */
    public USLP03UI() {}

    /**
     * Executes the USLP03 use case sequence.
     * <p>
     * 1. Initializes all repositories by loading data from CSV files.
     * 2. Creates the {@link TrafficDispatcherService}.
     * 3. Prompts the user to select the start station, end station, and locomotive.
     * 4. Calls the service to calculate the travel time.
     * 5. Prints the detailed {@link TravelTimeResult}.
     * </p>
     */
    @Override
    public void run() {
        System.out.println("\n--- USLP03: Calcular Tempo de Viagem Estimado ---");

        try {
            // 1. Load data and create services HERE.
            final String CSV_FOLDER = "csv_data/";
            final String STATIONS_FILE = CSV_FOLDER + "stations.csv";
            final String LOCOMOTIVES_FILE = CSV_FOLDER + "locomotives.csv";
            final String LINES_FILE = CSV_FOLDER + "railway_lines.csv";
            final String SEGMENTS_FILE = CSV_FOLDER + "line_segments.csv";

            StationRepository stationRepo = new CsvStationRepository(STATIONS_FILE);
            LocomotiveRepository locomotiveRepo = new CsvLocomotiveRepository(LOCOMOTIVES_FILE);
            RailwayLineRepository lineRepo = new CsvRailwayLineRepository(LINES_FILE, SEGMENTS_FILE, stationRepo);

            TrafficDispatcherService service = new TrafficDispatcherService(stationRepo, locomotiveRepo, lineRepo);

            // 2. Execute the user interaction logic.
            Scanner scanner = new Scanner(System.in);
            List<Station> stations = service.getAvailableStations();
            if (stations.isEmpty()){
                System.out.println("\nERRO: Não existem estações configuradas no sistema.");
                return;
            }
            stations.sort(Comparator.comparing(station -> Integer.parseInt(station.getId())));

            System.out.println("\n--- Estações Disponíveis ---");
            for (int i = 0; i < stations.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, stations.get(i));
            }
            System.out.print("Escolha a estação de PARTIDA (número): ");
            int startIdx = Integer.parseInt(scanner.nextLine()) - 1;
            String startStationId = stations.get(startIdx).getId();

            System.out.print("Escolha a estação de CHEGADA (número): ");
            int endIdx = Integer.parseInt(scanner.nextLine()) - 1;
            String endStationId = stations.get(endIdx).getId();

            List<Locomotive> locomotives = service.getAvailableLocomotives();
            if (locomotives.isEmpty()){
                System.out.println("\nERRO: Não existem locomotivas configuradas no sistema.");
                return;
            }
            System.out.println("\n--- Locomotivas Disponíveis ---");
            for (int i = 0; i < locomotives.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, locomotives.get(i));
            }
            System.out.print("Escolha a LOCOMOTIVA (número): ");
            int locoIdx = Integer.parseInt(scanner.nextLine()) - 1;
            String locomotiveId = locomotives.get(locoIdx).getId();

            TravelTimeResult result = service.calculateTravelTime(startStationId, endStationId, locomotiveId);
            result.printResult();

        } catch (Exception e) {
            System.err.println("\nERRO: " + e.getMessage());
        }

        System.out.println("\nPressione Enter para voltar ao menu principal...");
        new Scanner(System.in).nextLine();
    }
}