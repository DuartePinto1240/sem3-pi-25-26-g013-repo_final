package main.java.pt.ipp.isep.dei.dto;

public record WagonDTO(
        Long wagonId,
        String type,
        Double tareWeight,
        Double payload,
        Double length,
        Long currentFacilityId,
        String currentFacilityName,
        Long transitRouteId,
        String status
) {}
