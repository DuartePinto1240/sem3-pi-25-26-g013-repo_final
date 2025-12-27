package main.java.pt.ipp.isep.dei.dto;

import java.util.List;

public record PathDTO(
        Long routeId,
        List<LineSegmentDTO> segments,
        Double totalDistance,
        Double estimatedDuration // minutos
) {}
