package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OracleTrainRepository implements TrainRepository {

    // ============================================================
    // LOCOMOTIVES
    // ============================================================

    @Override
    public List<LocomotiveDTO> getAvailableLocomotives(Long startFacilityId) {
        String call = "{ ? = call fn_get_available_locomotives(?, ?) }";
        List<LocomotiveDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, startFacilityId);
            cs.setInt(3, 0);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    out.add(new LocomotiveDTO(
                            rs.getLong("loco_id"),
                            rs.getString("model_name"),
                            rs.getString("locomotive_type"),
                            rs.getDouble("power_kw"),
                            rs.getDouble("weight"),
                            rs.getDouble("max_speed"),
                            rs.getLong("current_facility_id"),
                            rs.getString("current_facility_name"),
                            getLongOrNull(rs, "transit_train_id"),
                            rs.getString("status")
                    ));
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching available locomotives at facility " + startFacilityId, e);
        }
    }

    @Override
    public List<LocomotiveDTO> getAllLocomotives() {
        String sql = "SELECT l.loco_id, l.name AS loco_name, m.name AS model_name, " +
                "lm.power_kw, m.weight_t AS weight, lm.operational_speed_kmh AS max_speed, " +
                "l.start_facility_id AS current_facility_id, f.name AS current_facility_name, " +
                "CASE WHEN EXISTS (SELECT 1 FROM TrainLocomotive tl WHERE tl.loco_id = l.loco_id) " +
                "THEN 'IN_TRANSIT' ELSE 'PARKED' END AS status, " +
                "CASE WHEN EXISTS (SELECT 1 FROM Diesel_Locomotive dl WHERE dl.loco_id = l.loco_id) " +
                "THEN 'DIESEL' ELSE 'ELECTRIC' END AS locomotive_type " +
                "FROM Locomotive l " +
                "JOIN LocomotiveModel lm ON l.locomotive_model_id = lm.locomotive_model_id " +
                "JOIN Model m ON lm.model_id = m.model_id " +
                "JOIN Facility f ON l.start_facility_id = f.facility_id " +
                "ORDER BY l.loco_id";

        List<LocomotiveDTO> locos = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                locos.add(new LocomotiveDTO(
                        rs.getLong("loco_id"),
                        rs.getString("model_name"),
                        rs.getString("locomotive_type"),
                        rs.getDouble("power_kw"),
                        rs.getDouble("weight"),
                        rs.getDouble("max_speed"),
                        rs.getLong("current_facility_id"),
                        rs.getString("current_facility_name"),
                        null,
                        rs.getString("status")
                ));
            }

            return locos;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all locomotives", e);
        }
    }

    @Override
    public LocomotiveDTO getLocomotiveById(Long locomotiveId) {
        String sql =
                "SELECT l.loco_id, m.name AS model_name, " +
                        "lm.power_kw, m.weight_t AS weight, lm.operational_speed_kmh AS max_speed, " +
                        "l.start_facility_id AS current_facility_id, f.name AS current_facility_name, " +
                        "CASE WHEN EXISTS (SELECT 1 FROM TrainLocomotive tl WHERE tl.loco_id = l.loco_id) " +
                        "THEN 'IN_TRANSIT' ELSE 'PARKED' END AS status, " +
                        "CASE WHEN EXISTS (SELECT 1 FROM Diesel_Locomotive dl WHERE dl.loco_id = l.loco_id) " +
                        "THEN 'DIESEL' ELSE 'ELECTRIC' END AS locomotive_type " +
                        "FROM Locomotive l " +
                        "JOIN LocomotiveModel lm ON l.locomotive_model_id = lm.locomotive_model_id " +
                        "JOIN Model m ON lm.model_id = m.model_id " +
                        "JOIN Facility f ON l.start_facility_id = f.facility_id " +
                        "WHERE l.loco_id = ?";

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, locomotiveId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LocomotiveDTO(
                            rs.getLong("loco_id"),
                            rs.getString("model_name"),
                            rs.getString("locomotive_type"),
                            rs.getDouble("power_kw"),
                            rs.getDouble("weight"),
                            rs.getDouble("max_speed"),
                            rs.getLong("current_facility_id"),
                            rs.getString("current_facility_name"),
                            null,
                            rs.getString("status")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching locomotive " + locomotiveId, e);
        }
    }

    // ============================================================
    // WAGONS
    // ============================================================

    @Override
    public List<WagonDTO> getAvailableWagons(Long startFacilityId) {
        String call = "{ ? = call fn_get_available_wagons(?, ?) }";
        List<WagonDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, startFacilityId);
            cs.setInt(3, 0);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    out.add(new WagonDTO(
                            rs.getLong("wagon_id"),
                            rs.getString("wagon_type"),
                            rs.getDouble("tare_weight"),
                            rs.getDouble("payload_t"),
                            rs.getDouble("length"),
                            rs.getLong("current_facility_id"),
                            rs.getString("current_facility_name"),
                            getLongOrNull(rs, "transit_train_id"),
                            rs.getString("status")
                    ));
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching available wagons at facility " + startFacilityId, e);
        }
    }

    @Override
    public WagonDTO getWagonById(Long wagonId) {
        String sql = "SELECT w.wagon_id, w.start_facility_id AS current_facility_id, " +
                "f.name AS current_facility_name, wt.type AS wagon_type, " +
                "wm.payload_t, m.length_m, m.weight_t " +
                "FROM Wagon w " +
                "JOIN WagonModel wm ON w.wagonmodel_id = wm.wagonmodel_id " +
                "JOIN Model m ON wm.model_id = m.model_id " +
                "JOIN WagonType wt ON wm.wagontype_id = wt.wagontype_id " +
                "JOIN Facility f ON w.start_facility_id = f.facility_id " +
                "WHERE w.wagon_id = ?";

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, wagonId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new WagonDTO(
                            rs.getLong("wagon_id"),
                            rs.getString("wagon_type"),
                            rs.getDouble("weight_t"),
                            rs.getDouble("payload_t"),
                            rs.getDouble("length_m"),
                            rs.getLong("current_facility_id"),
                            rs.getString("current_facility_name"),
                            null,
                            "PARKED"
                    );
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching wagon " + wagonId, e);
        }
    }

    // ============================================================
    // LINE SEGMENTS (direct pair segments)
    // ============================================================

    @Override
    public List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId) {
        String call = "{ ? = call fn_get_line_segments(?, ?) }";
        List<LineSegmentDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, fromFacilityId);
            cs.setLong(3, toFacilityId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    out.add(new LineSegmentDTO(
                            rs.getLong("line_segment_id"),
                            rs.getLong("line_id"),
                            rs.getLong("start_facility_id"),
                            rs.getString("start_facility_name"),
                            rs.getLong("end_facility_id"),
                            rs.getString("end_facility_name"),
                            rs.getDouble("length_m") / 1000.0,
                            rs.getDouble("speed_limit"),
                            "Yes".equals(rs.getString("electrified")),
                            "double".equals(rs.getString("track_type")),
                            rs.getInt("gauge_id")
                    ));
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching line segments from " +
                    fromFacilityId + " to " + toFacilityId, e);
        }
    }

    // ============================================================
    // ADJACENCY (for pathfinding)
    // ============================================================

    @Override
    public List<AdjacentFacilityDTO> getAdjacentFacilities(Long facilityId) {
        String sql =
                "SELECT rl.start_facility_id, rl.end_facility_id, rl.line_id, " +
                        "       CASE WHEN rl.start_facility_id = ? THEN rl.end_facility_id ELSE rl.start_facility_id END AS other_facility_id, " +
                        "       f.name AS other_facility_name " +
                        "FROM RailwayLine rl " +
                        "JOIN Facility f ON f.facility_id = CASE WHEN rl.start_facility_id = ? THEN rl.end_facility_id ELSE rl.start_facility_id END " +
                        "WHERE rl.start_facility_id = ? OR rl.end_facility_id = ?";

        List<AdjacentFacilityDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, facilityId);
            ps.setLong(2, facilityId);
            ps.setLong(3, facilityId);
            ps.setLong(4, facilityId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long otherId = rs.getLong("other_facility_id");
                    String otherName = rs.getString("other_facility_name");
                    Long lineId = rs.getLong("line_id");

                    out.add(new AdjacentFacilityDTO(
                            facilityId,
                            otherId,
                            lineId,
                            otherName
                    ));
                }
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching adjacent facilities for facility " + facilityId, e);
        }
    }

    // ============================================================
    // TRAINS
    // ============================================================

    @Override
    public List<TrainDTO> getAllTrains() {
        String call = "{ ? = call fn_get_all_trains }";
        List<TrainDTO> trains = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Long trainId = rs.getLong("train_id");

                    TrainDTO fullTrain = getTrainById(trainId);
                    if (fullTrain != null) {
                        trains.add(fullTrain);
                    }
                }
            }
            return trains;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching all trains", e);
        }
    }

    @Override
    public TrainDTO getTrainById(Long trainId) {
        String call = "{ ? = call fn_get_train_details(?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, trainId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                if (rs.next()) {
                    List<Long> locoIds = getTrainLocomotives(trainId);
                    List<Long> wagonIds = getTrainWagons(trainId);

                    return new TrainDTO(
                            rs.getLong("train_id"),
                            rs.getString("train_name"),
                            locoIds,
                            wagonIds,
                            rs.getDouble("total_loco_weight") + rs.getDouble("total_wagon_weight"),
                            rs.getDouble("total_loco_length") + rs.getDouble("total_wagon_length"),
                            null,
                            rs.getDouble("max_length_m")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching train " + trainId, e);
        }
    }

    @Override
    public Long createTrain(List<Long> locomotiveIds, List<Long> wagonIds) {
        String call = "{ ? = call fn_create_train(?, ?, ?, ?, ?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setInt(2, 2);
            cs.setString(3, "AUTO" + System.currentTimeMillis());
            cs.setDouble(4, 500.0);
            cs.setString(5, locomotiveIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            cs.setString(6, wagonIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

            cs.execute();
            return cs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("DB error creating train", e);
        }
    }

    @Override
    public void assignTrainToRoute(Long trainId, Long routeId) {
        String call = "{ call sp_assign_train_to_route(?, ?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setLong(1, trainId);
            cs.setLong(2, routeId);
            cs.execute();

            System.out.println("✓ Train " + trainId + " assigned to route " + routeId);

        } catch (SQLException e) {
            throw new RuntimeException("Error assigning train " + trainId + " to route " + routeId, e);
        }
    }

    @Override
    public RouteDTO getRouteForTrain(Long trainId) {
        String sql = "SELECT route_id FROM Train WHERE train_id = ?";

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long routeId = rs.getLong("route_id");
                    if (rs.wasNull()) return null;

                    RouteRepository routeRepo = new OracleRouteRepository();
                    return routeRepo.getRouteById(routeId);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting route for train " + trainId, e);
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private List<Long> getTrainLocomotives(Long trainId) throws SQLException {
        String sql = "SELECT loco_id FROM TrainLocomotive WHERE train_id = ?";
        List<Long> locoIds = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    locoIds.add(rs.getLong("loco_id"));
                }
            }
        }
        return locoIds;
    }

    private List<Long> getTrainWagons(Long trainId) throws SQLException {
        String sql = "SELECT wagon_id FROM TrainWagon WHERE train_id = ?";
        List<Long> wagonIds = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    wagonIds.add(rs.getLong("wagon_id"));
                }
            }
        }
        return wagonIds;
    }

    private Long getLongOrNull(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    @Override
    public List<TrainDTO> getTrainsWithRoutes() {
        String call = "{ ? = call fn_get_trains_with_routes }";
        List<TrainDTO> trains = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Long trainId = rs.getLong("train_id");
                    System.out.println("Found train with route: " + trainId);

                    TrainDTO fullTrain = getTrainById(trainId);
                    if (fullTrain != null) {
                        trains.add(fullTrain);
                    }
                }
            }

            System.out.println("Total trains with routes: " + trains.size());
            return trains;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching trains with routes", e);
        }
    }

    @Override
    public String getFacilityName(Long facilityId) {
        String sql = "SELECT name FROM Facility WHERE facility_id = ?";

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, facilityId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
            return "Facility " + facilityId;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching facility name for id " + facilityId, e);
        }
    }
}
