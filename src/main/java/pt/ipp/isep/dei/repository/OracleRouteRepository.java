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
                            toLocalDateTime(rs.getTimestamp("created_date")),
                            getRouteStops(routeId),
                            List.of(), // Freight IDs
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
        System.out.println("🔍 DEBUG: Getting route " + routeId);

        // ✅ QUERY SIMPLIFICADA - SEM SUBQUERIES
        String sql = "SELECT route_id, route_type, created_date FROM Route WHERE route_id = ?";

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, routeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✓ Route found in DB: " + routeId);

                    // Get stops
                    List<RouteStopDTO> stops = getRouteStops(routeId);
                    System.out.println("  Loaded stops: " + stops.size());

                    // Get freight IDs
                    List<Long> freightIds = getFreightIdsForRoute(routeId);
                    System.out.println("  Loaded freights: " + freightIds.size());

                    // Get start/end from stops
                    Long startFacilityId = null;
                    String startFacilityName = null;
                    Long endFacilityId = null;
                    String endFacilityName = null;

                    if (!stops.isEmpty()) {
                        RouteStopDTO first = stops.get(0);
                        RouteStopDTO last = stops.get(stops.size() - 1);

                        startFacilityId = first.facilityId();
                        startFacilityName = first.facilityName();
                        endFacilityId = last.facilityId();
                        endFacilityName = last.facilityName();

                        System.out.println("  Start: " + startFacilityName + " (" + startFacilityId + ")");
                        System.out.println("  End: " + endFacilityName + " (" + endFacilityId + ")");
                    }

                    RouteDTO route = new RouteDTO(
                            rs.getLong("route_id"),
                            rs.getString("route_type"),
                            toLocalDateTime(rs.getTimestamp("created_date")),
                            stops,
                            freightIds,
                            startFacilityId,
                            startFacilityName,
                            endFacilityId,
                            endFacilityName,
                            freightIds.size(),
                            stops.size()
                    );

                    System.out.println("✓ Route DTO created successfully");
                    return route;

                } else {
                    System.out.println("❌ Route NOT found in DB: " + routeId);
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error getting route " + routeId + ": " + e.getMessage());
            e.printStackTrace();
            return null; // ← NÃO fazer throw, retornar null
        }
    }

    @Override
    public List<RouteStopDTO> getRouteStops(Long routeId) {
        System.out.println("🔍 DEBUG: Getting stops for route " + routeId);

        // ✅ TENTAR FUNÇÃO PRIMEIRO
        try {
            return getRouteStopsViaFunction(routeId);
        } catch (Exception e) {
            System.out.println("⚠ Function failed, trying direct query...");
            return getRouteStopsViaQuery(routeId);
        }
    }

    private List<RouteStopDTO> getRouteStopsViaFunction(Long routeId) {
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

            System.out.println("  Loaded " + stops.size() + " stops via function");
            return stops;

        } catch (SQLException e) {
            throw new RuntimeException("Error calling fn_get_route_details: " + e.getMessage(), e);
        }
    }

    private List<RouteStopDTO> getRouteStopsViaQuery(Long routeId) {
        String sql = "SELECT rs.facility_id, f.name AS facility_name, rs.stop_order " +
                "FROM RouteStop rs " +
                "JOIN Facility f ON rs.facility_id = f.facility_id " +
                "WHERE rs.route_id = ? " +
                "ORDER BY rs.stop_order";

        List<RouteStopDTO> stops = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, routeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stops.add(new RouteStopDTO(
                            rs.getLong("facility_id"),
                            rs.getString("facility_name"),
                            rs.getInt("stop_order"),
                            0, // freights_to_load - not in simple query
                            0  // freights_to_unload - not in simple query
                    ));
                }
            }

            System.out.println("  Loaded " + stops.size() + " stops via query");
            return stops;

        } catch (SQLException e) {
            System.err.println("Error querying route stops: " + e.getMessage());
            return List.of();
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

            Long routeId = cs.getLong(1);
            System.out.println("✓ Created route " + routeId);

            return routeId;

        } catch (SQLException e) {
            throw new RuntimeException("Error creating route", e);
        }
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private List<Long> getFreightIdsForRoute(Long routeId) {
        String sql = "SELECT freight_id FROM Freight WHERE route_id = ? ORDER BY freight_id";
        List<Long> freightIds = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, routeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    freightIds.add(rs.getLong("freight_id"));
                }
            }

            System.out.println("  Found " + freightIds.size() + " freight(s) for route " + routeId);
            return freightIds;

        } catch (SQLException e) {
            System.err.println("Error fetching freight IDs: " + e.getMessage());
            return List.of();
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}