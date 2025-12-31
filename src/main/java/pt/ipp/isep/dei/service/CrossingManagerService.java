package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.dto.CrossingOperationDTO;
import main.java.pt.ipp.isep.dei.dto.LineSegmentDTO;
import main.java.pt.ipp.isep.dei.dto.PassageTimeDTO;
import main.java.pt.ipp.isep.dei.dto.TrainScheduleDTO;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for managing train crossings on single-track segments
 */
public class CrossingManagerService {

    /**
     * Calculate required crossings for multiple trains
     */
    public List<CrossingOperationDTO> calculateCrossings(
            List<TrainScheduleDTO> schedules,
            List<LineSegmentDTO> singleTrackSegments
    ) {
        List<CrossingOperationDTO> crossings = new ArrayList<>();

        // Group schedules by segment
        for (LineSegmentDTO segment : singleTrackSegments) {
            if (segment.isDoubleTrack()) {
                continue; // No crossing needed
            }

            // Find trains using this segment
            List<TrainScheduleDTO> trainsOnSegment = findTrainsOnSegment(schedules, segment);

            // Check for conflicts
            for (int i = 0; i < trainsOnSegment.size(); i++) {
                for (int j = i + 1; j < trainsOnSegment.size(); j++) {
                    TrainScheduleDTO train1 = trainsOnSegment.get(i);
                    TrainScheduleDTO train2 = trainsOnSegment.get(j);

                    CrossingOperationDTO crossing = detectCrossing(train1, train2, segment);
                    if (crossing != null) {
                        crossings.add(crossing);
                    }
                }
            }
        }

        return crossings;
    }

    /**
     * Detect if two trains need to cross on a segment
     */
    private CrossingOperationDTO detectCrossing(
            TrainScheduleDTO train1,
            TrainScheduleDTO train2,
            LineSegmentDTO segment
    ) {
        // Get passage times for segment endpoints
        PassageTimeDTO train1Start = findPassageTime(train1, segment.fromFacilityId());
        PassageTimeDTO train1End = findPassageTime(train1, segment.toFacilityId());
        PassageTimeDTO train2Start = findPassageTime(train2, segment.fromFacilityId());
        PassageTimeDTO train2End = findPassageTime(train2, segment.toFacilityId());

        if (train1Start == null || train1End == null || train2Start == null || train2End == null) {
            return null; // Trains don't use this segment
        }

        // Check if trains are going opposite directions
        boolean train1Forward = train1Start.arrivalTime().isBefore(train1End.arrivalTime());
        boolean train2Forward = train2Start.arrivalTime().isBefore(train2End.arrivalTime());

        if (train1Forward == train2Forward) {
            return null; // Same direction, no crossing needed
        }

        // Check time overlap
        LocalDateTime train1Enter = train1Start.arrivalTime();
        LocalDateTime train1Exit = train1End.arrivalTime();
        LocalDateTime train2Enter = train2Start.arrivalTime();
        LocalDateTime train2Exit = train2End.arrivalTime();

        boolean overlap = !(train1Exit.isBefore(train2Enter) || train2Exit.isBefore(train1Enter));

        if (!overlap) {
            return null; // No time conflict
        }

        // Need crossing - determine location
        // Prefer station crossing over siding
        Long crossingFacility = segment.fromFacilityId(); // Simplified
        String crossingType = "STATION"; // or "SIDING" if segment has siding

        LocalDateTime crossingTime = train1Enter.isAfter(train2Enter) ? train1Enter : train2Enter;

        return new CrossingOperationDTO(
                crossingFacility,
                "Crossing Point", // facility name would come from DB
                train1.trainId(),
                train2.trainId(),
                crossingTime,
                crossingType
        );
    }

    private List<TrainScheduleDTO> findTrainsOnSegment(
            List<TrainScheduleDTO> schedules,
            LineSegmentDTO segment
    ) {
        List<TrainScheduleDTO> result = new ArrayList<>();

        for (TrainScheduleDTO schedule : schedules) {
            boolean usesSegment = schedule.passageTimes().stream()
                    .anyMatch(p -> p.facilityId().equals(segment.fromFacilityId())
                            || p.facilityId().equals(segment.toFacilityId()));

            if (usesSegment) {
                result.add(schedule);
            }
        }

        return result;
    }

    private PassageTimeDTO findPassageTime(TrainScheduleDTO schedule, Long facilityId) {
        return schedule.passageTimes().stream()
                .filter(p -> p.facilityId().equals(facilityId))
                .findFirst()
                .orElse(null);
    }
}