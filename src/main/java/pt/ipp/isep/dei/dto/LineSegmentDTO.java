package main.java.pt.ipp.isep.dei.dto;

public record LineSegmentDTO(
        Long segmentId,
        Long lineId,
        Long fromFacilityId,
        String fromFacilityName,
        Long toFacilityId,
        String toFacilityName,
        Double distance, // km
        Double maxSpeed, // km/h
        Boolean isElectrified,
        Boolean isDoubleTrack,
        Integer gauge
) {}
