package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.LineSegmentDTO;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.TrainDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;

import java.util.List;

public class OracleTrainRepository implements TrainRepository {
    @Override
    public List<LocomotiveDTO> getAvailableLocomotives(Long startFacilityId) {
        return List.of();
    }

    @Override
    public List<WagonDTO> getAvailableWagons(Long startFacilityId) {
        return List.of();
    }

    @Override
    public List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId) {
        return List.of();
    }

    @Override
    public TrainDTO getTrainById(Long trainId) {
        return null;
    }

    @Override
    public Long createTrain(List<Long> locomotiveIds, List<Long> wagonIds) {
        return 0L;
    }

    @Override
    public void assignTrainToRoute(Long trainId, Long routeId) {

    }
    // Chamar funções PL/SQL como:
    // fn_get_available_locomotives(p_facility_id)
    // fn_get_available_wagons(p_facility_id)
    // fn_create_train(p_locomotive_ids, p_wagon_ids)
    // etc.
}
