package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.CrossingOperationDTO;
import main.java.pt.ipp.isep.dei.dto.PassageTimeDTO;
import main.java.pt.ipp.isep.dei.dto.TrainScheduleDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository {
    Long createSchedule(Long trainId, Long routeId, LocalDateTime departureTime);
    void savePassageTime(Long scheduleId, PassageTimeDTO passage);
    void saveCrossingOperation(Long scheduleId, CrossingOperationDTO crossing);
    List<TrainScheduleDTO> getActiveSchedules(LocalDateTime from, LocalDateTime to);
}
