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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * UI for USLP07 — Train Dispatch Scheduler.
 */
public class USLP07UI implements Runnable {

    private final TrainDispatchSchedulerController controller;
    private final Scanner scanner = new Scanner(System.in);

    public USLP07UI() {
        this.controller = new TrainDispatchSchedulerController();
    }

    @Override
    public void run() {
        printHeader("USLP07: Train Dispatch Scheduler");
        System.out.println("Bem-vindo ao sistema de agendamento de comboios.\n");

        try {
            // 1. Obter lista de estações, locomotivas e wagons através do controller
            System.out.println("A carregar dados do sistema...");
            List<Station> stations = controller.getAvailableStations();
            if (stations.isEmpty()) {
                printError("Não existem estações configuradas.");
                return;
            }

            List<Locomotive> availableLocomotives = new ArrayList<>(controller.getAvailableLocomotives());
            if (availableLocomotives.isEmpty()) {
                printError("Não existem locomotivas configuradas.");
                return;
            }

            List<Wagon> availableWagons = new ArrayList<>(controller.getAvailableWagons());
            if (availableWagons.isEmpty()) {
                printError("Não existem wagons configurados.");
                return;
            }

            System.out.printf("✓ %d estações disponíveis%n", stations.size());
            System.out.printf("✓ %d locomotivas disponíveis%n", availableLocomotives.size());
            System.out.printf("✓ %d wagons disponíveis%n%n", availableWagons.size());

            // 2. Perguntar quantos comboios o Freight Manager quer despachar
            printSeparator();
            int numTrains = readIntInRange("Quantos comboios pretende agendar? (1 a 6): ", 1, 6);

            List<TrainDispatchRequest> requests = new ArrayList<>();
            Set<String> usedTrainIds = new LinkedHashSet<>();

            for (int t = 0; t < numTrains; t++) {
                printSeparator();
                System.out.printf("╔════════════════════════════════════════════════════════╗%n");
                System.out.printf("║         CONFIGURAR COMBOIO %d de %d                      ║%n", t + 1, numTrains);
                System.out.printf("╚════════════════════════════════════════════════════════╝%n%n");

                // ID do comboio (único)
                String trainId;
                while (true) {
                    System.out.print("▶ ID do comboio: ");
                    trainId = scanner.nextLine().trim();
                    if (trainId.isEmpty()) {
                        printError("O ID do comboio não pode ser vazio.");
                        continue;
                    }
                    if (usedTrainIds.contains(trainId)) {
                        printError("Já existe um comboio com esse ID nesta sessão. Escolha outro.");
                        continue;
                    }
                    usedTrainIds.add(trainId);
                    break;
                }

                // -------------------------
                // LOCOMOTIVAS (sem duplicar entre comboios)
                // -------------------------
                System.out.println("\n┌─ LOCOMOTIVAS ─────────────────────────────────────────────┐");
                if (availableLocomotives.isEmpty()) {
                    printError("Não há mais locomotivas disponíveis para atribuir.");
                    return;
                }

                int maxLocosForThisTrain = Math.min(6, availableLocomotives.size());
                System.out.printf("Disponíveis: %d locomotiva(s)%n", availableLocomotives.size());
                int numLocos = readIntInRange(
                        String.format("▶ Número de locomotivas neste comboio (mínimo 1, máximo %d): ", maxLocosForThisTrain),
                        1,
                        maxLocosForThisTrain
                );

                List<Locomotive> chosenLocos = new ArrayList<>();
                List<Locomotive> tempAvailableLocos = new ArrayList<>(availableLocomotives);
                
                // Mostrar lista apenas no início
                System.out.printf("%n  ┌─ Locomotivas Disponíveis ────────────────────────────────┐%n");
                for (int j = 0; j < tempAvailableLocos.size(); j++) {
                    Locomotive loco = tempAvailableLocos.get(j);
                    System.out.printf("  │ %2d. %-20s |%5.0f kW | %3.0f km/h | %3.0f tons │%n",
                            j + 1,
                            loco.getModel(),
                            loco.getPower(),
                            loco.getMaxSpeedKmH(),
                            loco.getWeightTons());
                }
                System.out.println("  └──────────────────────────────────────────────────────────┘");
                
                for (int i = 0; i < numLocos; i++) {
                    if (i > 0) {
                        System.out.printf("%n  Locomotivas já escolhidas: ");
                        for (int k = 0; k < chosenLocos.size(); k++) {
                            if (k > 0) System.out.print(", ");
                            System.out.print(chosenLocos.get(k).getModel());
                        }
                        System.out.println();
                    }
                    
                    while (true) {
                        int locoIdx = readIntInRange(
                                String.format("▶ Escolha a locomotiva #%d: ", i + 1),
                                1,
                                tempAvailableLocos.size()
                        );
                        
                        Locomotive chosen = tempAvailableLocos.get(locoIdx - 1);
                        
                        // Verificar se já está no comboio
                        if (chosenLocos.contains(chosen)) {
                            printError(String.format("A locomotiva '%s' (número %d) já está neste comboio. Escolha outra.", chosen.getModel(), locoIdx));
                            continue;
                        }
                        
                        chosenLocos.add(chosen);
                        // NÃO remover da lista temporária para manter os números originais
                        System.out.printf("  ✓ Locomotiva '%s' (número %d) adicionada ao comboio%n", chosen.getModel(), locoIdx);
                        break;
                    }
                }
                
                // Remover as locomotivas escolhidas da lista global
                availableLocomotives.removeAll(chosenLocos);
                System.out.println("└───────────────────────────────────────────────────────────┘");

                // -------------------------
                // WAGONS (sem duplicar entre comboios)
                // -------------------------
                System.out.println("\n┌─ WAGONS ───────────────────────────────────────────────┐");
                if (availableWagons.isEmpty()) {
                    printError("Não há mais wagons disponíveis para atribuir.");
                    return;
                }

                int maxWagonsForThisTrain = availableWagons.size();
                System.out.printf("Disponíveis: %d wagon(s)%n", availableWagons.size());
                int numWagons = readIntInRange(
                        String.format("▶ Quantos wagons pretende associar a este comboio? (mínimo 1, máximo %d): ", maxWagonsForThisTrain),
                        1,
                        maxWagonsForThisTrain
                );

                List<Wagon> chosenWagons = new ArrayList<>();
                List<Wagon> tempAvailableWagons = new ArrayList<>(availableWagons);
                
                // Mostrar lista completa apenas no início
                System.out.printf("%n  ┌─ Wagons Disponíveis ───────────────────────────────────────────────────┐%n");
                for (int j = 0; j < tempAvailableWagons.size(); j++) {
                    Wagon wagon = tempAvailableWagons.get(j);
                    String modelCode = wagon.getModel() != null ? wagon.getModel().getModelCode() : "N/A";
                    double maxSpeed = wagon.getMaxSpeedKmH();
                    double tareWeight = wagon.getTareWeightTons();
                    double loadedWeight = wagon.getFullyLoadedWeightTons();
                    System.out.printf("  │ %2d. %-15s | %-20s | %3.0f km/h | %.1f/%.1f tons │%n",
                            j + 1,
                            wagon.getNumber(),
                            modelCode.length() > 20 ? modelCode.substring(0, 17) + "..." : modelCode,
                            maxSpeed,
                            tareWeight,
                            loadedWeight);
                }
                System.out.println("  └────────────────────────────────────────────────────────────────────────┘");
                
                for (int i = 0; i < numWagons; i++) {
                    if (i > 0) {
                        System.out.printf("%n  Wagons já escolhidos: ");
                        for (int k = 0; k < chosenWagons.size(); k++) {
                            if (k > 0) System.out.print(", ");
                            System.out.print(chosenWagons.get(k).getNumber());
                        }
                        System.out.println();
                    }
                    
                    while (true) {
                        int wagonIdx = readIntInRange(
                                String.format("▶ Escolha o wagon #%d: ", i + 1),
                                1,
                                tempAvailableWagons.size()
                        );
                        
                        Wagon chosen = tempAvailableWagons.get(wagonIdx - 1);
                        
                        // Verificar se já está no comboio
                        if (chosenWagons.contains(chosen)) {
                            printError(String.format("O wagon '%s' (número %d) já está neste comboio. Escolha outro.", chosen.getNumber(), wagonIdx));
                            continue;
                        }
                        
                        chosenWagons.add(chosen);
                        // NÃO remover da lista temporária para manter os números originais
                        System.out.printf("  ✓ Wagon '%s' (número %d) adicionado ao comboio%n", chosen.getNumber(), wagonIdx);
                        break;
                    }
                }
                
                // Remover os wagons escolhidos da lista global
                availableWagons.removeAll(chosenWagons);
                System.out.println("└───────────────────────────────────────────────────────────┘");

                // -------------------------
                // ROTA
                // -------------------------
                System.out.println("\n┌─ ROTA ─────────────────────────────────────────────────┐");
                int numStations = readIntInRange(
                        "▶ Número de estações na rota (mínimo 2): ",
                        2,
                        stations.size()
                );

                List<Station> route = new ArrayList<>();

                // Primeira estação (qualquer)
                System.out.printf("%n  ┌─ Estações Disponíveis (início da rota) ───────────┐%n");
                for (int j = 0; j < stations.size(); j++) {
                    Station s = stations.get(j);
                    System.out.printf("  │ %2d. %-45s │%n", j + 1, s.getName() + " (ID: " + s.getId() + ")");
                }
                System.out.println("  └───────────────────────────────────────────────────┘");
                int firstIdx = readIntInRange("▶ Escolha a estação #1: ", 1, stations.size());
                Station currentStation = stations.get(firstIdx - 1);
                route.add(currentStation);
                System.out.printf("  ✓ Estação de partida: %s%n", currentStation.getName());

                // Estações seguintes: apenas com ligação direta
                for (int i = 1; i < numStations; i++) {
                    List<Station> directDests = controller.getDirectDestinationsFrom(currentStation);

                    if (directDests.isEmpty()) {
                        printError(String.format("A estação %s não tem ligações diretas adicionais. Rota interrompida.",
                                currentStation.getName()));
                        break;
                    }

                    String stationName = currentStation.getName();
                    int maxNameLength = Math.min(35, stationName.length());
                    String truncatedName = stationName.length() > 35 ? stationName.substring(0, 32) + "..." : stationName;
                    System.out.printf("%n  ┌─ Estações com ligação direta a %s ─────────────────┐%n", truncatedName);
                    for (int j = 0; j < directDests.size(); j++) {
                        Station dest = directDests.get(j);
                        String destName = dest.getName();
                        if (destName.length() > 30) {
                            destName = destName.substring(0, 27) + "...";
                        }
                        System.out.printf("  │ %2d. %-30s (ID: %s)                    │%n",
                                j + 1,
                                destName,
                                dest.getId());
                    }
                    System.out.println("  └────────────────────────────────────────────────────────────────┘");
                    System.out.printf("  Rota atual: %s%n", formatRoute(route));

                    int destChoice = readIntInRange(
                            String.format("▶ Escolha a estação #%d: ", i + 1),
                            1,
                            directDests.size()
                    );
                    Station nextStation = directDests.get(destChoice - 1);
                    route.add(nextStation);
                    System.out.printf("  ✓ Próxima estação: %s%n", nextStation.getName());
                    currentStation = nextStation;
                }
                System.out.println("└───────────────────────────────────────────────────────────┘");

                // -------------------------
                // DATA/HORA PARTIDA
                // -------------------------
                System.out.println("\n┌─ DATA/HORA DE PARTIDA ────────────────────────────────────┐");
                System.out.println("  Formato: YYYY-MM-DDTHH:MM (exemplo: 2025-11-16T08:00)");
                LocalDateTime departureTime = readDateTime(
                        "▶ Data/hora de partida: "
                );
                System.out.println("└───────────────────────────────────────────────────────────┘");

                TrainDispatchRequest request =
                        controller.createTrainDispatchRequest(
                                trainId,
                                route,
                                chosenLocos,
                                chosenWagons,
                                departureTime
                        );
                requests.add(request);

                // Mostrar resumo do comboio configurado
                printTrainSummary(request, t + 1);

                // Mostrar resumo de comboios já configurados
                if (t < numTrains - 1) {
                    printConfiguredTrainsSummary(requests);
                }
            }

            // 3. Chamar o controller para agendar todos os comboios
            printSeparator();
            System.out.println("A processar agendamento dos comboios...");
            List<TrainSchedule> schedules = controller.scheduleTrains(requests);
            System.out.println("✓ Agendamento concluído!\n");

            // 4. Apresentar o resultado
            printSeparator();
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║      RESULTADO DO PLANEAMENTO DE COMBOIOS              ║");
            System.out.println("╚════════════════════════════════════════════════════════╝\n");

            for (int i = 0; i < schedules.size(); i++) {
                TrainSchedule schedule = schedules.get(i);
                printScheduleDetails(schedule, i + 1, schedules.size());
                if (i < schedules.size() - 1) {
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.err.println("\nERRO inesperado: " + e.getMessage());
        }

        System.out.println("\nPressione Enter para voltar ao menu principal...");
        scanner.nextLine();
    }

    // -------------------------------------------------------------------------
    // Helpers de leitura/print
    // -------------------------------------------------------------------------

    private int readIntInRange(String prompt, int min, int max) {
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

    private LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return LocalDateTime.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("ERRO: Formato inválido. Use o formato ISO, por exemplo 2025-11-16T08:00");
            }
        }
    }


    private void printConfiguredTrainsSummary(List<TrainDispatchRequest> requests) {
        if (requests.isEmpty()) {
            return;
        }
        System.out.println("\n┌─ COMBOIOS JÁ CONFIGURADOS ──────────────────────────────────────────────────────┐");
        int index = 1;
        for (TrainDispatchRequest r : requests) {
            String routeStr = formatRoute(r.getRoute());
            System.out.printf("│ %d. ID: %-5s | Partida: %-16s | Rota: %-30s │%n",
                    index++,
                    r.getTrainId(),
                    formatDateTime(r.getDepartureTime()),
                    routeStr.length() > 30 ? routeStr.substring(0, 27) + "..." : routeStr
            );
        }
        System.out.println("└─────────────────────────────────────────────────────────────────────────────────┘");
    }

    private void printTrainSummary(TrainDispatchRequest request, int trainNumber) {
        System.out.println("\n┌─ RESUMO DO COMBOIO CONFIGURADO ───────────────────────────┐");
        System.out.printf("│ ID: %-50s    │%n", request.getTrainId());
        System.out.printf("│ Locomotivas: %-43d  │%n", request.getLocomotives().size());
        System.out.printf("│ Wagons: %-47d   │%n", request.getWagons().size());
        System.out.printf("│ Rota: %-48s    │%n", formatRoute(request.getRoute()));
        System.out.printf("│ Partida: %-45s    │%n", formatDateTime(request.getDepartureTime()));
        System.out.println("└───────────────────────────────────────────────────────────┘");
    }

    private void printScheduleDetails(TrainSchedule schedule, int number, int total) {
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        String header = String.format("COMBOIO %s (%d de %d)", schedule.getTrainId(), number, total);
        System.out.printf("│ %-54s    │%n", header);
        System.out.println("├───────────────────────────────────────────────────────────┤");
        
        // Status
        String statusStr = formatStatus(schedule.getStatus());
        System.out.printf("│ Status: %-50s│%n", statusStr);
        
        // Partida e Chegada
        System.out.printf("│ Partida:   %-47s│%n", formatDateTime(schedule.getDepartureTime()));
        System.out.printf("│ Chegada:   %-47s│%n", formatDateTime(schedule.getEstimatedArrivalTime()));
        
        // Duração
        long minutes = java.time.Duration.between(
                schedule.getDepartureTime(),
                schedule.getEstimatedArrivalTime()
        ).toMinutes();
        System.out.printf("│ Duração:   %-47s│%n", formatDuration(minutes));
        
        // Passagens
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│ PASSAGENS:                                                │");
        List<TrainSchedule.PassageTime> passages = schedule.getPassageTimes();
        if (passages.isEmpty()) {
            System.out.println("│   (nenhuma passagem registada)                         │");
        } else {
            for (TrainSchedule.PassageTime pt : passages) {
                System.out.printf("│   • %-30s às %-20s│%n",
                        pt.getPoint().getName(),
                        formatDateTime(pt.getTime()));
            }
        }
        
        // Cruzamentos
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│ CRUZAMENTOS:                                              │");
        List<TrainSchedule.CrossingOperation> crossings = schedule.getCrossingOperations();
        if (crossings.isEmpty()) {
            System.out.println("│   (nenhum cruzamento necessário)                          │");
        } else {
            for (TrainSchedule.CrossingOperation co : crossings) {
                System.out.println("│   ┌─────────────────────────────────────────────────────┐ │");
                System.out.printf("│   │ Local: %-45s │ │%n", co.getLocationName());
                System.out.printf("│   │ Comboio em espera: %-35s │ │%n", co.getWaitingTrainId());
                System.out.printf("│   │ Outro comboio: %-38s │ │%n", co.getOtherTrainId());
                System.out.printf("│   │ Tempo de espera: %-36s │ │%n", formatDuration(co.getWaitingDuration().toMinutes()));
                System.out.println("│   └─────────────────────────────────────────────────────┘ │");
            }
        }
        System.out.println("└───────────────────────────────────────────────────────────┘");
    }

    private String formatRoute(List<Station> route) {
        if (route.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < route.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append(route.get(i).getName());
        }
        return sb.toString();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String formatDuration(long minutes) {
        if (minutes < 60) {
            return String.format("%d minuto(s)", minutes);
        }
        long hours = minutes / 60;
        long mins = minutes % 60;
        if (mins == 0) {
            return String.format("%d hora(s)", hours);
        }
        return String.format("%d hora(s) e %d minuto(s)", hours, mins);
    }

    private String formatStatus(TrainSchedule.Status status) {
        switch (status) {
            case VALIDATED:
                return "✓ VALIDADO";
            case CONFLICT_WARNING:
                return "⚠ CONFLITO (com cruzamentos)";
            case INVALID:
                return "✗ INVÁLIDO";
            default:
                return status.toString();
        }
    }

    private void printHeader(String title) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.printf("║%s  ║%n", centerText(title, 54));
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    private void printSeparator() {
        System.out.println("\n" + "─".repeat(56));
    }

    private void printError(String message) {
        System.out.printf("✗ ERRO: %s%n", message);
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - padding;
        return " ".repeat(padding) + text + " ".repeat(rightPadding);
    }
}
