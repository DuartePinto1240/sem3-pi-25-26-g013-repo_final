package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.CargoDTO;
import main.java.pt.ipp.isep.dei.dto.PendingFreightDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OracleFreightRepository implements FreightRepository {

    @Override
    public List<PendingFreightDTO> getPendingFreights() {
        String call = "{ ? = call fn_uslp08_get_pending_freights }";
        List<PendingFreightDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    out.add(new PendingFreightDTO(
                            rs.getLong("freight_id"),
                            rs.getLong("origin_facility_id"),
                            rs.getString("origin_name"),
                            rs.getLong("destination_facility_id"),
                            rs.getString("destination_name"),
                            toLdt(rs.getTimestamp("depart_time")),
                            toLdt(rs.getTimestamp("arrival_time"))
                    ));
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching pending freights", e);
        }
    }

    @Override
    public List<CargoDTO> getCargosForFreight(long freightId) {
        String call = "{ ? = call fn_uslp08_get_freight_cargos(?) }";
        List<CargoDTO> out = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setLong(2, freightId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    out.add(new CargoDTO(
                            rs.getLong("cargo_id"),
                            rs.getLong("wagon_id"),
                            rs.getString("description"),
                            rs.getString("type"),
                            rs.getInt("quantity"),
                            rs.getDouble("volume"),
                            rs.getDouble("weight"),
                            toLd(rs.getDate("expiry_date"))
                    ));
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB error fetching cargos for freight " + freightId, e);
        }
    }

    private static LocalDateTime toLdt(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    private static LocalDate toLd(Date d) {
        return d == null ? null : d.toLocalDate();
    }
}
