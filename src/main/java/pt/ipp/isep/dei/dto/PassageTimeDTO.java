package main.java.pt.ipp.isep.dei.dto;

import java.time.LocalDateTime;

public record PassageTimeDTO(
        Long facilityId,
        String facilityName,
        LocalDateTime arrivalTime,
        LocalDateTime departureTime, // null se não parar
        Boolean stops
) {}
