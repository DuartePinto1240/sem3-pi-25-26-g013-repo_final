package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TravelTimeResult;
import main.java.pt.ipp.isep.dei.service.TrafficDispatcherService;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class MainUI {
    private final TrafficDispatcherService service;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * O construtor correto, que recebe o serviço como dependência.
     * @param service A instância do TrafficDispatcherService.
     */
    public MainUI(TrafficDispatcherService service) {
        this.service = service;
    }

    /**
     * O método que inicia o ciclo principal da interface do utilizador.
     */
    public void run() {
        while (true) {

            System.out.println("1. Calcular tempo de viagem estimado");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    calculateTravelTimeUI();
                    break;
                case "0":
                    System.out.println("A sair...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void calculateTravelTimeUI() {
        try {
            List<Station> stations = service.getAvailableStations();
            if (stations.isEmpty()){
                System.out.println("\nERRO: Não existem estações configuradas no sistema.\n");
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
                System.out.println("\nERRO: Não existem locomotivas configuradas no sistema.\n");
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
            System.err.println("\nERRO: " + e.getMessage() + "\n");
        }
    }
}