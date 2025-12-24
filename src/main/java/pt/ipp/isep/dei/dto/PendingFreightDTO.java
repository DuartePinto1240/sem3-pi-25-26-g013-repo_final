package main.java.pt.ipp.isep.dei.dto;

import java.time.LocalDateTime;

public record PendingFreightDTO(
        long freightId,
        long originFacilityId,
        String originName,
        long destinationFacilityId,
        String destinationName,
        LocalDateTime departTime,
        LocalDateTime arrivalTime
) {}
