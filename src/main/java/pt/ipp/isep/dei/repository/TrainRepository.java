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

    // ✨ NEW - For USLP09
    List<LocomotiveDTO> getAllLocomotives(); // All locomotives (available + in use)
    WagonDTO getWagonById(Long wagonId); // Get specific wagon
    List<TrainDTO> getAllTrains();
    List<TrainDTO> getTrainsWithRoutes();// All trains
}
