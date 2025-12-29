package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.LineSegmentDTO;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.TrainDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OracleTrainRepository implements TrainRepository {

    @Override
    public List<LocomotiveDTO> getAvailableLocomotives(Long startFacilityId) {
        List<LocomotiveDTO> locos = new ArrayList<>();
        String sql = "{ ? = call fn_get_available_locomotives(?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.REF_CURSOR); // Oracle Types.CURSOR
            cs.setLong(2, startFacilityId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    // Adjust column names to match your SQL output
                    locos.add(new LocomotiveDTO(
                            rs.getLong("id"),
                            rs.getString("model"),
                            rs.getString("type"),
                            rs.getDouble("power"),
                            rs.getDouble("weight"),
                            rs.getDouble("max_speed"),
                            startFacilityId,
                            null, // Facility Name not strictly needed for selection logic or can be fetched
                            null, // Route ID
                            "PARKED"
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching locomotives: " + e.getMessage());
        }
        return locos;
    }

    @Override
    public List<WagonDTO> getAvailableWagons(Long startFacilityId) {
        List<WagonDTO> wagons = new ArrayList<>();
        String sql = "{ ? = call fn_get_available_wagons(?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, startFacilityId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    wagons.add(new WagonDTO(
                            rs.getLong("id"),
                            rs.getString("type"),
                            rs.getDouble("tare_weight"),
                            rs.getDouble("payload"),
                            rs.getDouble("length"),
                            startFacilityId,
                            null,
                            null,
                            "PARKED"
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching wagons: " + e.getMessage());
        }
        return wagons;
    }

    @Override
    public Long createTrain(List<Long> locomotiveIds, List<Long> wagonIds) {
        long newTrainId = -1;
        // Function signature: fn_create_train(loco_ids_string, wagon_ids_string) return NUMBER
        String sql = "{ ? = call fn_create_train(?, ?) }";

        // Helper to convert List<Long> to "1,2,3"
        String locosStr = locomotiveIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String wagonsStr = wagonIds == null ? "" : wagonIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, locosStr);
            cs.setString(3, wagonsStr);

            cs.execute();

            newTrainId = cs.getLong(1);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating train: " + e.getMessage());
        }
        return newTrainId;
    }

    // Implementing other interface methods as stubs if not required for USLP09 but required by compilation
    @Override
    public List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId) { return null; }
    @Override
    public TrainDTO getTrainById(Long trainId) { return null; }
    @Override
    public void assignTrainToRoute(Long trainId, Long routeId) {
        // Logic to update train with route ID
    }
}