package main.java.pt.ipp.isep.dei.dto;

import java.time.LocalDateTime;

public record CrossingOperationDTO(
        Long facilityId,
        String facilityName,
        Long trainId1,
        Long trainId2,
        LocalDateTime crossingTime,
        String crossingType // "STATION", "SIDING"
) {}
