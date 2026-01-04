package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.*;
import java.util.List;

public interface TrainRepository {

    // Existing methods
    List<LocomotiveDTO> getAvailableLocomotives(Long startFacilityId);
    List<WagonDTO> getAvailableWagons(Long startFacilityId);
    List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId);
    TrainDTO getTrainById(Long trainId);
    Long createTrain(List<Long> locomotiveIds, List<Long> wagonIds);
    void assignTrainToRoute(Long trainId, Long routeId);
    RouteDTO getRouteForTrain(Long trainId);
    String getFacilityName(Long facilityId);

    // required for USLP10 speed/time calculation
    LocomotiveDTO getLocomotiveById(Long locomotiveId);

    // For USLP09
    List<LocomotiveDTO> getAllLocomotives();
    WagonDTO getWagonById(Long wagonId);
    List<TrainDTO> getAllTrains();
    List<TrainDTO> getTrainsWithRoutes();

    List<AdjacentFacilityDTO> getAdjacentFacilities(Long facilityId);
}
