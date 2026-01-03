package main.java.pt.ipp.isep.dei.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RouteDTO(
        Long routeId,
        String routeType, // "SIMPLE" or "COMPLEX"
        LocalDateTime createdDate,
        List<RouteStopDTO> stops,
        List<Long> freightIds,
        Long startFacilityId,
        String startFacilityName,
        Long endFacilityId,
        String endFacilityName,
        Integer numFreights,
        Integer numStops
) {}
