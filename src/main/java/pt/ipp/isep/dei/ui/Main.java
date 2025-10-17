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
            // Caminhos para os ficheiros CSV na pasta 'resources'
            final String STATIONS_FILE = "/stations.csv";
            final String LOCOMOTIVES_FILE = "/locomotives.csv";
            final String LINES_FILE = "/railway_lines.csv";
            final String SEGMENTS_FILE = "/line_segments.csv";

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
            System.err.println("Verifique se os ficheiros CSV existem na pasta 'resources' e se o formato está correto.");
            e.printStackTrace();
        }
    }
}