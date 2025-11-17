package main.java.pt.ipp.isep.dei.domain;

import java.time.LocalDateTime;

/**
 * Represents the occupation of a single line segment by a specific train
 * between an entry and an exit time.
 *
 * This is the core structure used in USLP07 to detect conflicts on
 * single-track lines and to plan crossings.
 */
public class ScheduledSegment {

    private final String trainId;
    private final LineSegment segment;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    /**
     * Direction of travel in the segment.
     * true  -> travelling from the segment origin to destination
     * false -> travelling from the segment destination to origin
     *
     * (Ainda não estamos a usar isto na lógica, mas será útil
     *  quando quiseres distinguir sentidos opostos.)
     */
    private final boolean directionAB;

    public ScheduledSegment(String trainId,
                            LineSegment segment,
                            LocalDateTime entryTime,
                            LocalDateTime exitTime,
                            boolean directionAB) {
        this.trainId = trainId;
        this.segment = segment;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.directionAB = directionAB;
    }

    public String getTrainId() {
        return trainId;
    }

    public LineSegment getSegment() {
        return segment;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public boolean isDirectionAB() {
        return directionAB;
    }

    public void applyDelay(java.time.Duration delay, LocalDateTime conflictStart) {
        // Só atrasamos segmentos a partir do momento do conflito
        if (!entryTime.isBefore(conflictStart)) {
            entryTime = entryTime.plus(delay);
            exitTime = exitTime.plus(delay);
        }
    }

    @Override
    public String toString() {
        return "ScheduledSegment{" +
                "trainId='" + trainId + '\'' +
                ", segment=" + segment +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", directionAB=" + directionAB +
                '}';
    }
}
