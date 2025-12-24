package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.dto.PendingFreightDTO;
import main.java.pt.ipp.isep.dei.dto.RoutePlanDTO;
import main.java.pt.ipp.isep.dei.repository.FreightRepository;
import main.java.pt.ipp.isep.dei.service.RoutePlannerService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoutePlannerController {

    private final FreightRepository repo;
    private final RoutePlannerService service;

    public RoutePlannerController(FreightRepository repo, RoutePlannerService service) {
        this.repo = repo;
        this.service = service;
    }

    public List<PendingFreightDTO> getPendingFreights() {
        return repo.getPendingFreights();
    }

    // auto-plan para TODOS os pendentes
    public List<RoutePlanDTO> autoPlanAll() {
        List<PendingFreightDTO> pending = repo.getPendingFreights();
        return service.planRoutes(pending, repo::getCargosForFreight);
    }

    // planear só IDs escolhidos
    public List<RoutePlanDTO> planSelected(Set<Long> selectedIds) {
        List<PendingFreightDTO> pending = repo.getPendingFreights().stream()
                .filter(f -> selectedIds.contains(f.freightId()))
                .collect(Collectors.toList());

        return service.planRoutes(pending, repo::getCargosForFreight);
    }
}

