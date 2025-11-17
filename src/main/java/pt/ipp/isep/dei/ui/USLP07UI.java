package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.TrainDispatchSchedulerController;
import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TrainSchedule;
import main.java.pt.ipp.isep.dei.service.TrainSchedulerService.TrainDispatchRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * UI for USLP07 — Train Dispatch Scheduler.
 */
public class USLP07UI implements Runnable {

    private final TrainDispatchSchedulerController controller;

    public USLP07UI() {
        this.controller = new TrainDispatchSchedulerController();
    }

    @Override
    public void run() {
        System.out.println("\n--- USLP07: Train Dispatch Scheduler ---");

        try {
            Scanner scanner = new Scanner(System.in);

            // 1. Obter lista de estações e locomotivas através do controller
            List<Station> stations = controller.getAvailableStations();
            if (stations.isEmpty()) {
                System.out.println("\nERRO: Não existem estações configuradas.");
                return;
            }

            List<Locomotive> locomotives = controller.getAvailableLocomotives();
            if (locomotives.isEmpty()) {
                System.out.println("\nERRO: Não existem locomotivas configuradas.");
                return;
            }

            // 2. Perguntar quantos comboios o Freight Manager quer despachar
            System.out.print("\nQuantos comboios pretende agendar? ");
            int numTrains = Integer.parseInt(scanner.nextLine());

            List<TrainDispatchRequest> requests = new ArrayList<>();

            for (int t = 0; t < numTrains; t++) {
                System.out.printf("\n--- Configurar Comboio %d ---\n", t + 1);

                System.out.print("ID do comboio: ");
                String trainId = scanner.nextLine().trim();

                // Selecionar locomotiva (para já, uma por comboio)
                System.out.println("\n--- Locomotivas Disponíveis ---");
                for (int i = 0; i < locomotives.size(); i++) {
                    System.out.printf("%d. %s\n", i + 1, locomotives.get(i));
                }
                System.out.print("Escolha a LOCOMOTIVA (número): ");
                int locoIdx = Integer.parseInt(scanner.nextLine()) - 1;
                Locomotive chosenLoco = locomotives.get(locoIdx);

                // Definir rota: simples (2 estações) ou complexa (várias)
                System.out.print("\nNúmero de estações na rota (>=2): ");
                int numStations = Integer.parseInt(scanner.nextLine());

                List<Station> route = new ArrayList<>();
                for (int i = 0; i < numStations; i++) {
                    System.out.println("\n--- Estações Disponíveis ---");
                    for (int j = 0; j < stations.size(); j++) {
                        System.out.printf("%d. %s\n", j + 1, stations.get(j));
                    }
                    System.out.printf("Escolha a estação #%d (número): ", i + 1);
                    int idx = Integer.parseInt(scanner.nextLine()) - 1;
                    route.add(stations.get(idx));
                }

                // Definir data/hora de partida (mantemos ISO-8601 com 'T')
                System.out.print("\nData/hora de partida (formato ISO, ex: 2025-11-16T08:00): ");
                String departureStr = scanner.nextLine().trim();
                LocalDateTime departureTime = LocalDateTime.parse(departureStr);

                List<Locomotive> locosForTrain = new ArrayList<>();
                locosForTrain.add(chosenLoco);

                TrainDispatchRequest request =
                        controller.createTrainDispatchRequest(trainId, route, locosForTrain, departureTime);
                requests.add(request);
            }

            // 3. Chamar o controller para agendar todos os comboios
            List<TrainSchedule> schedules = controller.scheduleTrains(requests);

            // 4. Apresentar o resultado
            System.out.println("\n--- Resultado do Planeamento de Comboios ---");
            for (TrainSchedule schedule : schedules) {
                System.out.printf("\nComboio %s (Status: %s)\n",
                        schedule.getTrainId(), schedule.getStatus());
                System.out.printf("Partida: %s\n", schedule.getDepartureTime());
                System.out.printf("Chegada Estimada: %s\n", schedule.getEstimatedArrivalTime());

                System.out.println("Passagens:");
                for (TrainSchedule.PassageTime pt : schedule.getPassageTimes()) {
                    System.out.printf(" - %s às %s\n",
                            pt.getPoint().getName(), pt.getTime());
                }

                System.out.println("Cruzamentos:");
                for (TrainSchedule.CrossingOperation co : schedule.getCrossingOperations()) {
                    System.out.printf(
                            " - Local: %s | Outro comboio: %s | Comboio em espera: %s | Espera: %s\n",
                            co.getLocationName(),
                            co.getOtherTrainId(),
                            co.getWaitingTrainId(),
                            co.getWaitingDuration()
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("\nERRO: " + e.getMessage());
        }

        System.out.println("\nPressione Enter para voltar ao menu principal...");
        new Scanner(System.in).nextLine();
    }
}
