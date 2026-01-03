package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.RouteDTO;
import main.java.pt.ipp.isep.dei.dto.RouteStopDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OracleRouteRepository implements RouteRepository {

    @Override
    public List<RouteDTO> getAvailableRoutes() {
        String call = "{ ? = call fn_get_available_routes }";
        List<RouteDTO> routes = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Long routeId = rs.getLong("route_id");

                    routes.add(new RouteDTO(
                            routeId,
                            rs.getString("route_type"),
                            rs.getTimestamp("created_date").toLocalDateTime(),
                            getRouteStops(routeId), // Get stops separately
                            List.of(), // Freight IDs would need separate query
                            rs.getLong("start_facility_id"),
                            rs.getString("start_facility_name"),
                            rs.getLong("end_facility_id"),
                            rs.getString("end_facility_name"),
                            rs.getInt("num_freights"),
                            rs.getInt("num_stops")
                    ));
                }
            }
            return routes;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching available routes", e);
        }
    }

    @Override
    public RouteDTO getRouteById(Long routeId) {
        List<RouteDTO> all = getAvailableRoutes();
        return all.stream()
                .filter(r -> r.routeId().equals(routeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<RouteStopDTO> getRouteStops(Long routeId) {
        String call = "{ ? = call fn_get_route_details(?) }";
        List<RouteStopDTO> stops = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, routeId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    stops.add(new RouteStopDTO(
                            rs.getLong("facility_id"),
                            rs.getString("facility_name"),
                            rs.getInt("stop_order"),
                            rs.getInt("freights_to_load"),
                            rs.getInt("freights_to_unload")
                    ));
                }
            }
            return stops;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching route stops for route " + routeId, e);
        }
    }

    @Override
    public Long createRoute(List<Long> freightIds, String routeType) {
        String call = "{ ? = call fn_create_route(?, ?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, freightIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
            cs.setString(3, routeType);
            cs.execute();

            return cs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Error creating route", e);
        }
    }
}