package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.CrossingOperationDTO;
import main.java.pt.ipp.isep.dei.dto.PassageTimeDTO;
import main.java.pt.ipp.isep.dei.dto.TrainScheduleDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OracleScheduleRepository implements ScheduleRepository {

    @Override
    public Long createSchedule(Long trainId, Long routeId, LocalDateTime departureTime) {
        String call = "{ ? = call fn_create_schedule(?, ?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setLong(2, trainId);
            cs.setTimestamp(3, Timestamp.valueOf(departureTime));
            cs.execute();

            long scheduleId = cs.getLong(1);
            return scheduleId;

        } catch (SQLException e) {
            throw new RuntimeException("DB error creating schedule for train " + trainId, e);
        }
    }

    @Override
    public void savePassageTime(Long scheduleId, PassageTimeDTO passage) {
        String call = "{ call sp_save_passage_time(?, ?, ?, ?) }";

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setLong(1, scheduleId); // train_id (using scheduleId = trainId)
            cs.setLong(2, passage.facilityId());
            cs.setTimestamp(3, Timestamp.valueOf(passage.arrivalTime()));

            if (passage.departureTime() != null) {
                cs.setTimestamp(4, Timestamp.valueOf(passage.departureTime()));
            } else {
                cs.setNull(4, Types.TIMESTAMP);
            }

            cs.execute();

        } catch (SQLException e) {
            throw new RuntimeException("DB error saving passage time for schedule " + scheduleId, e);
        }
    }

    @Override
    public void saveCrossingOperation(Long scheduleId, CrossingOperationDTO crossing) {
        // For now, crossing operations are not stored in the database
        // You could create a new table CrossingOperation if needed

        // Future implementation:
        // CREATE TABLE CrossingOperation (
        //     crossing_id NUMBER PRIMARY KEY,
        //     schedule_id NUMBER,
        //     facility_id NUMBER,
        //     train_id_1 NUMBER,
        //     train_id_2 NUMBER,
        //     crossing_time DATE,
        //     crossing_type VARCHAR2(20)
        // );

        System.out.println("Crossing operation logged: " + crossing);
        // TODO: Implement if CrossingOperation table is created
    }

    @Override
    public List<TrainScheduleDTO> getActiveSchedules(LocalDateTime from, LocalDateTime to) {
        String call = "{ ? = call fn_get_train_schedule(?) }";
        List<TrainScheduleDTO> schedules = new ArrayList<>();

        // Get all distinct train_ids with movements in the time range
        List<Long> trainIds = getTrainIdsInPeriod(from, to);

        for (Long trainId : trainIds) {
            try (Connection conn = OracleDb.getConnection();
                 CallableStatement cs = conn.prepareCall(call)) {

                cs.registerOutParameter(1, Types.REF_CURSOR);
                cs.setLong(2, trainId);
                cs.execute();

                List<PassageTimeDTO> passageTimes = new ArrayList<>();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        Timestamp arrival = rs.getTimestamp("arrival_time");
                        Timestamp departure = rs.getTimestamp("departure_time");

                        passageTimes.add(new PassageTimeDTO(
                                rs.getLong("facility_id"),
                                rs.getString("facility_name"),
                                arrival != null ? arrival.toLocalDateTime() : null,
                                departure != null ? departure.toLocalDateTime() : null,
                                rs.getInt("stops") == 1
                        ));
                    }
                }

                if (!passageTimes.isEmpty()) {
                    schedules.add(new TrainScheduleDTO(
                            trainId, // scheduleId = trainId
                            trainId,
                            null, // routeId not available
                            passageTimes.get(0).arrivalTime(),
                            passageTimes,
                            List.of() // crossings - not implemented yet
                    ));
                }

            } catch (SQLException e) {
                throw new RuntimeException("DB error fetching schedule for train " + trainId, e);
            }
        }

        return schedules;
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private List<Long> getTrainIdsInPeriod(LocalDateTime from, LocalDateTime to) {
        String sql = "SELECT DISTINCT train_id FROM TrainMovement " +
                "WHERE arrival_time BETWEEN ? AND ?";
        List<Long> trainIds = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trainIds.add(rs.getLong("train_id"));
                }
            }
            return trainIds;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching train IDs in period", e);
        }
    }
}