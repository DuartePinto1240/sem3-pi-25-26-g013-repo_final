package main.java.pt.ipp.isep.dei.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TrainScheduleDTO(
        Long scheduleId,
        Long trainId,
        Long routeId,
        LocalDateTime departureTime,
        List<PassageTimeDTO> passageTimes,
        List<CrossingOperationDTO> crossings
) {}
