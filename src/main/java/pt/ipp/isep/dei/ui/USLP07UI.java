package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.TrainDispatchSchedulerController;
import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TrainSchedule;
import main.java.pt.ipp.isep.dei.domain.Wagon;
import main.java.pt.ipp.isep.dei.service.TrainSchedulerService.TrainDispatchRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

            // 1. Obter lista de estações, locomotivas e wagons através do controller
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

            List<Wagon> allWagons = controller.getAvailableWagons();
            if (allWagons.isEmpty()) {
                System.out.println("\nERRO: Não existem wagons configurados. Não é possível formar comboios de mercadorias.");
                return;
            }

            // Listas GLOBAIS de recursos disponíveis (não se repetem entre comboios)
            List<Locomotive> availableLocosGlobal = new ArrayList<>(locomotives);
            List<Wagon> availableWagonsGlobal = new ArrayList<>(allWagons);

            // Máximo teórico de comboios (pelo menos 1 loco e 1 wagon por comboio)
            int maxTrainsPossible = Math.min(availableLocosGlobal.size(), availableWagonsGlobal.size());
            if (maxTrainsPossible == 0) {
                System.out.println("\nERRO: Recursos insuficientes para formar qualquer comboio (é necessário pelo menos 1 loco e 1 wagon).");
                return;
            }

            int numTrains = readIntRange(scanner,
                    String.format("\nQuantos comboios pretende agendar? (1 a %d): ", maxTrainsPossible),
                    1,
                    maxTrainsPossible);

            List<TrainDispatchRequest> requests = new ArrayList<>();
            Set<String> usedTrainIds = new HashSet<>();

            for (int t = 0; t < numTrains; t++) {

                // Mostrar comboios já configurados antes de pedir o próximo
                if (!requests.isEmpty()) {
                    System.out.println("\n--- Comboios já configurados ---");
                    for (int i = 0; i < requests.size(); i++) {
                        TrainDispatchRequest r = requests.get(i);
                        String origem = r.getRoute().isEmpty() ? "N/D" : r.getRoute().get(0).getName();
                        String destino = r.getRoute().size() < 2
                                ? "N/D"
                                : r.getRoute().get(r.getRoute().size() - 1).getName();
                        System.out.printf("%d. ID=%s | Partida=%s | Rota: %s -> %s%n",
                                i + 1,
                                r.getTrainId(),
                                r.getDepartureTime(),
                                origem,
                                destino);
                    }
                }

                System.out.printf("\n--- Configurar Comboio %d ---\n", t + 1);

                // Verificar recursos antes de começar este comboio
                if (availableLocosGlobal.isEmpty() || availableWagonsGlobal.isEmpty()) {
                    System.out.println("\nAVISO: Recursos esgotados (locomotivas ou wagons). Não é possível configurar mais comboios.");
                    break;
                }

                // ---------------- ID DO COMBOIO (único) ----------------
                String trainId;
                while (true) {
                    System.out.print("ID do comboio: ");
                    trainId = scanner.nextLine().trim();

                    if (trainId.isEmpty()) {
                        System.out.println("ERRO: O ID do comboio não pode ser vazio.");
                        continue;
                    }
                    if (usedTrainIds.contains(trainId)) {
                        System.out.println("ERRO: Já existe um comboio com esse ID nesta sessão. Escolha outro ID.");
                        continue;
                    }
                    usedTrainIds.add(trainId);
                    break;
                }

                // Para este comboio, usamos diretamente as listas globais
                List<Locomotive> availableLocosForTrain = availableLocosGlobal;
                List<Wagon> availableWagonsForTrain = availableWagonsGlobal;

                // ---------------- LOCOMOTIVAS (1º passo de montagem do comboio) ----------------
                int maxLocos = availableLocosForTrain.size();
                int numLocosTrain = readIntRange(scanner,
                        String.format("\nNúmero de locomotivas neste comboio (mínimo 1, máximo %d): ", maxLocos),
                        1,
                        maxLocos);

                List<Locomotive> locosForTrain = new ArrayList<>();
                for (int i = 0; i < numLocosTrain; i++) {
                    System.out.println("\n--- Locomotivas Disponíveis ---");
                    for (int idx = 0; idx < availableLocosForTrain.size(); idx++) {
                        System.out.printf("%d. %s%n", idx + 1, availableLocosForTrain.get(idx));
                    }

                    int locoChoice = readIntRange(scanner,
                            String.format("Escolha a locomotiva #%d (número): ", i + 1),
                            1,
                            availableLocosForTrain.size());

                    // remove da lista global para não ser escolhida noutro comboio
                    Locomotive chosenLoco = availableLocosForTrain.remove(locoChoice - 1);
                    locosForTrain.add(chosenLoco);
                }

                // ---------------- WAGONS (2º passo de montagem do comboio) ----------------
                if (availableWagonsForTrain.isEmpty()) {
                    System.out.println("\nAVISO: Não existem wagons disponíveis. Este comboio não pode ser criado.");
                    continue;
                }

                System.out.println("\n--- Wagons Disponíveis ---");
                for (int i = 0; i < availableWagonsForTrain.size(); i++) {
                    System.out.printf("%d. %s%n", i + 1, availableWagonsForTrain.get(i));
                }

                int maxWagons = availableWagonsForTrain.size();
                int numWagons = readIntRange(scanner,
                        String.format("Quantos wagons pretende associar a este comboio? (mínimo 1, máximo %d): ", maxWagons),
                        1,
                        maxWagons);

                List<Wagon> selectedWagons = new ArrayList<>();
                for (int i = 0; i < numWagons; i++) {
                    int wagIdx = readIntRange(scanner,
                            String.format("Escolha o wagon #%d (número na lista): ", i + 1),
                            1,
                            availableWagonsForTrain.size());

                    // remove da lista global para não ser escolhido noutro comboio
                    Wagon wagon = availableWagonsForTrain.remove(wagIdx - 1);
                    selectedWagons.add(wagon);
                }

                // ---------------- ROTA (DEPOIS DO COMBOIO ESTAR MONTADO) ----------------
                int maxStations = stations.size();
                int numStations = readIntRange(scanner,
                        "\nNúmero de estações na rota (mínimo 2): ",
                        2,
                        maxStations);

                List<Station> route = new ArrayList<>();
                for (int i = 0; i < numStations; i++) {
                    if (i == 0) {
                        // primeira estação: lista completa
                        System.out.println("\n--- Estações Disponíveis (início da rota) ---");
                        for (int j = 0; j < stations.size(); j++) {
                            System.out.printf("%d. %s%n", j + 1, stations.get(j));
                        }

                        int stationIdx = readIntRange(scanner,
                                "Escolha a estação #1 (número): ",
                                1,
                                stations.size());

                        route.add(stations.get(stationIdx - 1));
                    } else {
                        // próximas estações: apenas as que têm linha direta a partir da última
                        Station last = route.get(route.size() - 1);
                        List<Station> reachable = controller.getStationsDirectlyReachableFrom(last);

                        if (reachable.isEmpty()) {
                            System.out.println("\nERRO: Não existem linhas diretas a partir de " + last.getName() +
                                    ". Não é possível continuar esta rota. Vai ter de voltar a definir a rota deste comboio.");
                            // recomeçar a definição da rota deste comboio
                            route.clear();
                            i = -1; // o for vai fazer i++ e voltar a 0
                            continue;
                        }

                        System.out.println("\n--- Estações com ligação direta a " + last.getName() + " ---");
                        System.out.println("Rotas possíveis a partir desta estação:");
                        for (int j = 0; j < reachable.size(); j++) {
                            Station dest = reachable.get(j);
                            System.out.printf(
                                    "%d. %s (rota: %s -> %s)%n",
                                    j + 1,
                                    dest,
                                    last.getName(),
                                    dest.getName()
                            );
                        }

                        int stationIdx = readIntRange(scanner,
                                String.format("Escolha a estação #%d (número): ", i + 1),
                                1,
                                reachable.size());

                        route.add(reachable.get(stationIdx - 1));
                    }
                }

                // ---------------- PARTIDA ----------------
                LocalDateTime departureTime = readLocalDateTime(scanner,
                        "\nData/hora de partida (formato ISO, ex: 2025-11-16T08:00): ");

                // Criar o pedido para este comboio
                TrainDispatchRequest request =
                        controller.createTrainDispatchRequest(
                                trainId,
                                route,
                                locosForTrain,
                                departureTime,
                                selectedWagons
                        );
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
            System.err.println("\nERRO inesperado: " + e.getMessage());
        }

        System.out.println("\nPressione Enter para voltar ao menu principal...");
        new Scanner(System.in).nextLine();
    }

    // -------------------------------------------------------------------------
    // Helpers de leitura e validação de input
    // -------------------------------------------------------------------------

    /**
     * Lê um inteiro no intervalo [min, max], com validação.
     */
    private int readIntRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("ERRO: Introduza um número entre %d e %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Introduza um número inteiro válido.");
            }
        }
    }

    /**
     * Lê uma data/hora no formato ISO-8601 (ex: 2025-11-16T08:00),
     * repetindo até estar correta.
     */
    private LocalDateTime readLocalDateTime(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return LocalDateTime.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("ERRO: Formato de data/hora inválido. Use, por exemplo, 2025-11-16T08:00");
            }
        }
    }
}
