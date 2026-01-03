package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.FacilityDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Portuguese railway facilities (from BDDAD database)
 */
public class OracleFacilityRepository implements FacilityRepository {

    @Override
    public List<FacilityDTO> getAllFacilities() {
        String sql = "SELECT facility_id, facilitytype_id, name, location FROM Facility ORDER BY name";
        List<FacilityDTO> facilities = new ArrayList<>();

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                facilities.add(new FacilityDTO(
                        rs.getLong("facility_id"),
                        rs.getString("name"),
                        rs.getString("location")
                ));
            }
            return facilities;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching facilities", e);
        }
    }

    @Override
    public FacilityDTO getFacilityById(Long id) {
        String sql = "SELECT facility_id, name, location FROM Facility WHERE facility_id = ?";

        try (Connection conn = OracleDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FacilityDTO(
                            rs.getLong("facility_id"),
                            rs.getString("name"),
                            rs.getString("location")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching facility " + id, e);
        }
    }
}
