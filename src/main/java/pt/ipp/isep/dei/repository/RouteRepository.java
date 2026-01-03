package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.RouteDTO;
import main.java.pt.ipp.isep.dei.dto.RouteStopDTO;

import java.util.List;

public interface RouteRepository {
    List<RouteDTO> getAvailableRoutes();
    RouteDTO getRouteById(Long routeId);
    List<RouteStopDTO> getRouteStops(Long routeId);
    Long createRoute(List<Long> freightIds, String routeType);
}
