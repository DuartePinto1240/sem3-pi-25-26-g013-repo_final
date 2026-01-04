package main.java.pt.ipp.isep.dei.dto;

public record AdjacentFacilityDTO(
        Long fromFacilityId,
        Long toFacilityId,
        Long lineId,
        String toFacilityName
) {}
