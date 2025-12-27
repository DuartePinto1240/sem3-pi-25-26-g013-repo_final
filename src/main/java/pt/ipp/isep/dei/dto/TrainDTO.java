package main.java.pt.ipp.isep.dei.dto;

import java.util.List;

public record TrainDTO(
        Long trainId,
        String trainNumber,
        List<Long> locomotiveIds,
        List<Long> wagonIds,
        Double totalWeight,
        Double totalLength,
        Double maxWeight,
        Double maxLength
) {}
