package main.java.pt.ipp.isep.dei.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the final schedule for a single train in USLP07.
 * <p>
 * It contains the planned route, departure time, estimated arrival time,
 * passage times at each relevant point, crossing operations and status.
 * </p>
 */
public class TrainSchedule {

    public enum Status {
        VALIDATED,
        CONFLICT_WARNING,
        INVALID
    }

    private final String trainId;
    private final List<Station> route; // ordered list of stations
    private final LocalDateTime departureTime;
    private LocalDateTime estimatedArrivalTime;
    private Status status;

    private final List<PassageTime> passageTimes = new ArrayList<>();
    private final List<CrossingOperation> crossingOperations = new ArrayList<>();

    public TrainSchedule(String trainId,
                         List<Station> route,
                         LocalDateTime departureTime) {
        this.trainId = trainId;
        this.route = new ArrayList<>(route);
        this.departureTime = departureTime;
        this.status = Status.VALIDATED;
    }

    public String getTrainId() {
        return trainId;
    }

    public List<Station> getRoute() {
        return Collections.unmodifiableList(route);
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<PassageTime> getPassageTimes() {
        return Collections.unmodifiableList(passageTimes);
    }

    public List<CrossingOperation> getCrossingOperations() {
        return Collections.unmodifiableList(crossingOperations);
    }

    public void addPassageTime(Station station, LocalDateTime time) {
        passageTimes.add(new PassageTime(station, time));
    }

    public void addCrossingOperation(String locationName,
                                     String otherTrainId,
                                     String waitingTrainId,
                                     Duration waitingDuration) {
        crossingOperations.add(
                new CrossingOperation(locationName, otherTrainId, waitingTrainId, waitingDuration)
        );
    }

    /**
     * Applies a time delay to all passage times from a given moment onwards
     * and updates the estimated arrival time.
     */
    public void applyDelay(LocalDateTime conflictStart, Duration delay) {
        for (PassageTime pt : passageTimes) {
            if (!pt.getTime().isBefore(conflictStart)) {
                pt.setTime(pt.getTime().plus(delay));
            }
        }
        if (estimatedArrivalTime != null && !estimatedArrivalTime.isBefore(conflictStart)) {
            estimatedArrivalTime = estimatedArrivalTime.plus(delay);
        }
    }

    /**
     * Simple DTO for a passage time at a station or siding.
     */
    public static class PassageTime {
        private final Station point;
        private LocalDateTime time;

        public PassageTime(Station point, LocalDateTime time) {
            this.point = point;
            this.time = time;
        }

        public Station getPoint() {
            return point;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }

    /**
     * Simple DTO for a crossing operation between trains.
     */
    public static class CrossingOperation {
        private final String locationName;
        private final String otherTrainId;
        private final String waitingTrainId;
        private final Duration waitingDuration;

        public CrossingOperation(String locationName,
                                 String otherTrainId,
                                 String waitingTrainId,
                                 Duration waitingDuration) {
            this.locationName = locationName;
            this.otherTrainId = otherTrainId;
            this.waitingTrainId = waitingTrainId;
            this.waitingDuration = waitingDuration;
        }

        public String getLocationName() {
            return locationName;
        }

        public String getOtherTrainId() {
            return otherTrainId;
        }

        public String getWaitingTrainId() {
            return waitingTrainId;
        }

        public Duration getWaitingDuration() {
            return waitingDuration;
        }
    }
}
