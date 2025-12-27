package main.java.pt.ipp.isep.dei.dto;

public record LocomotiveDTO(
        Long locomotiveId,
        String model,
        String type, // "ELECTRIC" ou "DIESEL"
        Double power, // kW
        Double weight,
        Double maxSpeed,
        Long currentFacilityId,
        String currentFacilityName,
        Long transitRouteId, // null se estiver parada
        String status // "PARKED", "IN_TRANSIT"
) {}
