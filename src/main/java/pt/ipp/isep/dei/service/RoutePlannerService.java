package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.dto.CargoDTO;
import main.java.pt.ipp.isep.dei.dto.PendingFreightDTO;
import main.java.pt.ipp.isep.dei.dto.RoutePlanDTO;
import main.java.pt.ipp.isep.dei.dto.StopPlanDTO;

import java.util.*;
import java.util.stream.Collectors;

public class RoutePlannerService {

    public List<RoutePlanDTO> planRoutes(List<PendingFreightDTO> pending,
                                         java.util.function.LongFunction<List<CargoDTO>> cargosByFreight) {

        // index: origem -> lista de freights
        Map<Long, List<PendingFreightDTO>> byOrigin = pending.stream()
                .collect(Collectors.groupingBy(PendingFreightDTO::originFacilityId));

        Set<Long> used = new HashSet<>();
        List<RoutePlanDTO> routes = new ArrayList<>();

        // escolher "starts" (origens que não aparecem como destino de ninguém) para ficar mais “bonito”
        Set<Long> allDestinations = pending.stream().map(PendingFreightDTO::destinationFacilityId).collect(Collectors.toSet());
        List<PendingFreightDTO> starts = pending.stream()
                .filter(f -> !allDestinations.contains(f.originFacilityId()))
                .sorted(Comparator.comparing(PendingFreightDTO::departTime).thenComparing(PendingFreightDTO::freightId))
                .toList();

        // 1) construir cadeias a partir dos starts
        for (PendingFreightDTO start : starts) {
            if (used.contains(start.freightId())) continue;
            RoutePlanDTO route = buildChain(start, byOrigin, used, cargosByFreight);
            routes.add(route);
        }

        // 2) sobrou tudo o resto (ciclos ou freights isolados) → cada um vira route (ou tenta chain na mesma)
        List<PendingFreightDTO> leftovers = pending.stream()
                .filter(f -> !used.contains(f.freightId()))
                .sorted(Comparator.comparing(PendingFreightDTO::departTime).thenComparing(PendingFreightDTO::freightId))
                .toList();

        for (PendingFreightDTO f : leftovers) {
            if (used.contains(f.freightId())) continue;
            RoutePlanDTO route = buildChain(f, byOrigin, used, cargosByFreight);
            routes.add(route);
        }

        return routes;
    }

    private RoutePlanDTO buildChain(PendingFreightDTO first,
                                    Map<Long, List<PendingFreightDTO>> byOrigin,
                                    Set<Long> used,
                                    java.util.function.LongFunction<List<CargoDTO>> cargosByFreight) {

        RoutePlanDTO route = new RoutePlanDTO();

        PendingFreightDTO current = first;
        while (current != null && !used.contains(current.freightId())) {
            used.add(current.freightId());
            route.getFreightIds().add(current.freightId());

            // adicionar/atualizar stops:
            StopPlanDTO originStop = getOrCreateStop(route, current.originFacilityId(), current.originName());
            StopPlanDTO destStop   = getOrCreateStop(route, current.destinationFacilityId(), current.destinationName());

            List<CargoDTO> cargos = cargosByFreight.apply(current.freightId());
            originStop.getToLoad().addAll(cargos);
            destStop.getToUnload().addAll(cargos);

            // escolher próximo freight cujo origin == current.destination
            List<PendingFreightDTO> candidates = byOrigin.getOrDefault(current.destinationFacilityId(), List.of()).stream()
                    .filter(f -> !used.contains(f.freightId()))
                    .sorted(Comparator.comparing(PendingFreightDTO::departTime).thenComparing(PendingFreightDTO::freightId))
                    .toList();

            current = candidates.isEmpty() ? null : candidates.get(0);
        }

        return route;
    }

    private StopPlanDTO getOrCreateStop(RoutePlanDTO route, long facilityId, String name) {
        for (StopPlanDTO s : route.getStops()) {
            if (s.getFacilityId() == facilityId) return s;
        }
        StopPlanDTO created = new StopPlanDTO(facilityId, name);
        route.getStops().add(created);
        return created;
    }
}
