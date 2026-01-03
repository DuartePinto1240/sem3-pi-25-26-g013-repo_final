package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.FacilityDTO;
import java.util.List;

public interface FacilityRepository {
    List<FacilityDTO> getAllFacilities();
    FacilityDTO getFacilityById(Long id);
}
