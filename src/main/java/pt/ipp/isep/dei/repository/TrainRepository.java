package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.*;

import java.util.List;

public interface TrainRepository {
    List<LocomotiveDTO> getAvailableLocomotives(Long startFacilityId);
    List<WagonDTO> getAvailableWagons(Long startFacilityId);
    List<LineSegmentDTO> getLineSegments(Long fromFacilityId, Long toFacilityId);
    TrainDTO getTrainById(Long trainId);
    Long createTrain(List<Long> locomotiveIds, List<Long> wagonIds);
    void assignTrainToRoute(Long trainId, Long routeId);
    List<TrainDTO> getAllTrains(); // ← ADICIONAR ESTE
    RouteDTO getRouteForTrain(Long trainId);
    List<LocomotiveDTO> getLocomotivesSortedByDistance(Long routeId);
}
