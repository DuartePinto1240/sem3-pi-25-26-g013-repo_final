package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.RoutePlannerController;
import main.java.pt.ipp.isep.dei.dto.CargoDTO;
import main.java.pt.ipp.isep.dei.dto.PendingFreightDTO;
import main.java.pt.ipp.isep.dei.dto.RoutePlanDTO;
import main.java.pt.ipp.isep.dei.dto.StopPlanDTO;
import main.java.pt.ipp.isep.dei.repository.FreightRepository;
import main.java.pt.ipp.isep.dei.repository.OracleFreightRepository;
import main.java.pt.ipp.isep.dei.service.RoutePlannerService;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class USLP08UI implements Runnable {

    private final RoutePlannerController controller;

    public USLP08UI() {
        FreightRepository repo = new OracleFreightRepository();
        RoutePlannerService service = new RoutePlannerService(); // no-args no teu projeto
        this.controller = new RoutePlannerController(repo, service);
    }

    @Override
    public void run() {
        System.out.println("\n=== USLP08 - Route Planner (Pending Freights) ===\n");

        List<PendingFreightDTO> pending = controller.getPendingFreights();
        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending freights (all freights already assigned to a train).");
            return;
        }

        printPending(pending);

        int opt = Utils.showAndSelectIndex(
                Arrays.asList("Auto-plan ALL pending freights", "Select freight IDs to plan"),
                "\nOptions:"
        );

        if (opt == -1) return;

        List<RoutePlanDTO> plans;
        if (opt == 0) {
            plans = controller.autoPlanAll();
        } else {
            Set<Long> ids = readIds("Enter freight IDs separated by comma (e.g., 2007,9101): ");
            if (ids.isEmpty()) {
                System.out.println("No valid IDs provided.");
                return;
            }
            plans = controller.planSelected(ids);
        }

        printPlans(plans);
    }

    private void printPending(List<PendingFreightDTO> pending) {
        System.out.println("Pending freights:");
        for (PendingFreightDTO f : pending) {
            System.out.printf("  #%d | %s -> %s | depart=%s | arrive=%s%n",
                    f.freightId(), f.originName(), f.destinationName(),
                    f.departTime(), f.arrivalTime());
        }
    }

    private void printPlans(List<RoutePlanDTO> plans) {
        System.out.println("\n=== Planned Routes ===\n");

        if (plans == null || plans.isEmpty()) {
            System.out.println("(no routes generated)");
            return;
        }

        int r = 1;
        for (RoutePlanDTO route : plans) {
            System.out.printf("Route %d (freights: %s)%n", r++, route.getFreightIds());

            int s = 1;
            for (StopPlanDTO stop : route.getStops()) {
                System.out.printf("  Stop %d: %s%n", s++, stop.getFacilityName());

                if (!stop.getToLoad().isEmpty()) {
                    System.out.println("    Load:");
                    for (CargoDTO c : stop.getToLoad()) {
                        System.out.printf("      cargo#%d wagon#%d [%s] qty=%d%n",
                                c.cargoId(), c.wagonId(), c.type(), c.quantity());
                    }
                }

                if (!stop.getToUnload().isEmpty()) {
                    System.out.println("    Unload:");
                    for (CargoDTO c : stop.getToUnload()) {
                        System.out.printf("      cargo#%d wagon#%d [%s] qty=%d%n",
                                c.cargoId(), c.wagonId(), c.type(), c.quantity());
                    }
                }
            }
            System.out.println();
        }
    }

    private Set<Long> readIds(String prompt) {
        String line = Utils.readLineFromConsole(prompt);
        if (line == null || line.isBlank()) return Set.of();

        return Arrays.stream(line.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try { return Long.parseLong(s); }
                    catch (NumberFormatException e) { return null; }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
