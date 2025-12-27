package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.CrossingOperationDTO;
import main.java.pt.ipp.isep.dei.dto.PassageTimeDTO;
import main.java.pt.ipp.isep.dei.dto.TrainScheduleDTO;

import java.time.LocalDateTime;
import java.util.List;

public class OracleScheduleRepository implements ScheduleRepository {
    @Override
    public Long createSchedule(Long trainId, Long routeId, LocalDateTime departureTime) {
        return 0L;
    }

    @Override
    public void savePassageTime(Long scheduleId, PassageTimeDTO passage) {

    }

    @Override
    public void saveCrossingOperation(Long scheduleId, CrossingOperationDTO crossing) {

    }

    @Override
    public List<TrainScheduleDTO> getActiveSchedules(LocalDateTime from, LocalDateTime to) {
        return List.of();
    }
    // Funções PL/SQL tipo:
    // fn_create_schedule(p_train_id, p_route_id, p_departure)
    // fn_save_passage_time(...)
}
