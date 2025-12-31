package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.LineSegmentDTO;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.TrainDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OracleTrainRepository implements TrainRepository {

    @Override
    public List<LocomotiveDTO> getAvailableLocomotives(Long startFacilityId) {
        String call = "{ ? = call fn_get_available_locomotives(?, ?) }";
        List<LocomotiveDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, startFacilityId);
            cs.setInt(3, 0); // radius_km = 0 (only at facility)
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
    public List<WagonDTO> getAvailableWagons(Long startFacilityId) {
        String call = "{ ? = call fn_get_available_wagons(?, ?) }";
        List<WagonDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, startFacilityId);
            cs.setInt(3, 0); // radius_km = 0
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
                            rs.getDouble("length_m") / 1000.0, // convert to km
                            rs.getDouble("speed_limit"),
                            "Yes".equals(rs.getString("electrified")),
                            "double".equals(rs.getString("track_type")),
                            rs.getInt("gauge_id")
                    ));
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching line segments from " + fromFacilityId + " to " + toFacilityId, e);
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
                    // Get locomotive IDs
                    List<Long> locoIds = getTrainLocomotives(trainId);

                    // Get wagon IDs
                    List<Long> wagonIds = getTrainWagons(trainId);

                    return new TrainDTO(
                            rs.getLong("train_id"),
                            rs.getString("train_name"),
                            locoIds,
                            wagonIds,
                            rs.getDouble("total_loco_weight") + rs.getDouble("total_wagon_weight"),
                            rs.getDouble("total_loco_length") + rs.getDouble("total_wagon_length"),
                            null, // max_weight - not in query
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
            cs.setInt(2, 2); // operator_id (Medway)
            cs.setString(3, "AUTO" + System.currentTimeMillis()); // auto-generated name
            cs.setDouble(4, 500.0); // max_length_m (default)
            cs.setString(5, locomotiveIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","))); // comma-separated loco IDs
            cs.setString(6, wagonIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","))); // comma-separated wagon IDs

            cs.execute();

            long trainId = cs.getLong(1);
            return trainId;

        } catch (SQLException e) {
            throw new RuntimeException("DB error creating train", e);
        }
    }

    @Override
    public void assignTrainToRoute(Long trainId, Long routeId) {
        // For now, routeId is not used in our schema
        // In future, you might want to add a route table
        // For now, this is a no-op or could update a status field

        // If you want to implement it:
        // UPDATE Train SET route_id = ? WHERE train_id = ?
        // But since we don't have route_id in Train table, skip for now
    }

    // ============================================================
    // HELPER METHODS
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
}