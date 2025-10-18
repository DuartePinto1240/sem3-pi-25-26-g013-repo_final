package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;
import main.java.pt.ipp.isep.dei.repository.CsvLocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.CsvRailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.CsvStationRepository;
import main.java.pt.ipp.isep.dei.service.TrafficDispatcherService;
import main.java.pt.ipp.isep.dei.ui.MainUI;

public class Main {
    public static void main(String[] args) {
        try {
            // Caminho para a pasta que contém os ficheiros CSV
            final String CSV_FOLDER = "csv_data/";

            // Caminhos relativos para os ficheiros CSV, a partir da raiz do projeto
            final String STATIONS_FILE = CSV_FOLDER + "stations.csv";
            final String LOCOMOTIVES_FILE = CSV_FOLDER + "locomotives.csv";
            final String LINES_FILE = CSV_FOLDER + "railway_lines.csv";
            final String SEGMENTS_FILE = CSV_FOLDER + "line_segments.csv";

            // Injeção de Dependências com a Versão CSV
            StationRepository stationRepo = new CsvStationRepository(STATIONS_FILE);
            LocomotiveRepository locomotiveRepo = new CsvLocomotiveRepository(LOCOMOTIVES_FILE);
            RailwayLineRepository lineRepo = new CsvRailwayLineRepository(LINES_FILE, SEGMENTS_FILE, stationRepo);

            // Criar o serviço com as dependências
            TrafficDispatcherService service = new TrafficDispatcherService(stationRepo, locomotiveRepo, lineRepo);

            // Criar e correr a UI
            MainUI ui = new MainUI(service);
            ui.run();

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO AO INICIAR A APLICAÇÃO: Não foi possível carregar os dados.");
            System.err.println("Verifique se a pasta 'csv_data' e os ficheiros existem na raiz do projeto.");
            e.printStackTrace();
        }
    }
}