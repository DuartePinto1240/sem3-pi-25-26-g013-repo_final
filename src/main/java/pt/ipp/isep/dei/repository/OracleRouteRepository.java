package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.RouteDTO;
import main.java.pt.ipp.isep.dei.dto.RouteStopDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
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

                    List<RouteStopDTO> stops = getRouteStops(routeId);
                    List<Long> freightIds = getFreightIdsForRoute(routeId);

                    Long startFacilityId = stops.isEmpty() ? null : stops.get(0).facilityId();
                    String startFacilityName = stops.isEmpty() ? null : stops.get(0).facilityName();
                    Long endFacilityId = stops.isEmpty() ? null : stops.get(stops.size() - 1).facilityId();
                    String endFacilityName = stops.isEmpty() ? null : stops.get(stops.size() - 1).facilityName();

                    routes.add(new RouteDTO(
                            routeId,
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
        String sql = "SELECT route_id, route_type, created_date FROM Route WHERE route_id = ?";

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, routeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                List<RouteStopDTO> stops = getRouteStops(routeId);
                List<Long> freightIds = getFreightIdsForRoute(routeId);

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
                }

                return new RouteDTO(
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
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting route " + routeId, e);
        }
    }

    @Override
    public List<RouteStopDTO> getRouteStops(Long routeId) {
        // Try function first; fallback to direct query
        List<RouteStopDTO> stops;
        try {
            stops = getRouteStopsViaFunction(routeId);
        } catch (Exception e) {
            stops = getRouteStopsViaQuery(routeId);
        }

        // ✅ IMPORTANT FIX:
        // remove repeated facilities (keeps first occurrence order)
        return normalizeStops(stops);
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
                            0,
                            0
                    ));
                }
            }

            return stops;

        } catch (SQLException e) {
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

            return cs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Error creating route", e);
        }
    }

    // ============================================================
    // NORMALIZATION (fixes your 7 vs 9 issue in USLP10)
    // ============================================================

    /**
     * RouteStop in DB is sometimes saved with repeated facilities (loops),
     * causing USLP10 to display more stops than USLP08 planned.
     *
     * We normalize by keeping the first occurrence of each facility (stable order),
     * and then re-number stop_order sequentially.
     */
    private List<RouteStopDTO> normalizeStops(List<RouteStopDTO> rawStops) {
        if (rawStops == null || rawStops.isEmpty()) return List.of();

        Map<Long, RouteStopDTO> firstByFacility = new LinkedHashMap<>();
        for (RouteStopDTO s : rawStops) {
            if (s == null) continue;
            firstByFacility.putIfAbsent(s.facilityId(), s);
        }

        List<RouteStopDTO> out = new ArrayList<>();
        int order = 1;
        for (RouteStopDTO s : firstByFacility.values()) {
            out.add(new RouteStopDTO(
                    s.facilityId(),
                    s.facilityName(),
                    order++,
                    s.freightsToLoad(),
                    s.freightsToUnload()
            ));
        }
        return out;
    }

    // ============================================================
    // HELPERS
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

            return freightIds;

        } catch (SQLException e) {
            return List.of();
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}
