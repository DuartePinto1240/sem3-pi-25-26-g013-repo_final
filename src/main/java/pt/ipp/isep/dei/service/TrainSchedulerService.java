package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.ScheduledSegment;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TrainSchedule;
import main.java.pt.ipp.isep.dei.domain.Wagon;
import main.java.pt.ipp.isep.dei.repository.LocomotiveRepository;
import main.java.pt.ipp.isep.dei.repository.RailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.StationRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for USLP07: scheduling multiple trains,
 * computing passage times, and handling crossings in single-track lines.
 */
public class TrainSchedulerService {

    private final StationRepository stationRepository;
    private final LocomotiveRepository locomotiveRepository;
    private final RailwayLineRepository railwayLineRepository;
    private final TrafficDispatcherService baseService;

    public TrainSchedulerService(StationRepository stationRepository,
                                 LocomotiveRepository locomotiveRepository,
                                 RailwayLineRepository railwayLineRepository,
                                 TrafficDispatcherService baseService) {
        this.stationRepository = stationRepository;
        this.locomotiveRepository = locomotiveRepository;
        this.railwayLineRepository = railwayLineRepository;
        this.baseService = baseService;
    }

    /**
     * Represents the input data needed to schedule a single train.
     */
    public static class TrainDispatchRequest {
        private final String trainId;
        private final List<Station> route;        // ordered stations: [A, B, C, ...]
        private final List<Locomotive> locomotives;
        private final LocalDateTime departureTime;
        private final List<Wagon> wagons;         // wagons that compose this train

        public TrainDispatchRequest(String trainId,
                                    List<Station> route,
                                    List<Locomotive> locomotives,
                                    LocalDateTime departureTime,
                                    List<Wagon> wagons) {
            this.trainId = trainId;
            this.route = route;
            this.locomotives = locomotives;
            this.departureTime = departureTime;
            this.wagons = wagons != null ? List.copyOf(wagons) : List.of();
        }

        public String getTrainId() {
            return trainId;
        }

        public List<Station> getRoute() {
            return route;
        }

        public List<Locomotive> getLocomotives() {
            return locomotives;
        }

        public LocalDateTime getDepartureTime() {
            return departureTime;
        }

        /**
         * Returns the wagons that compose this train.
         */
        public List<Wagon> getWagons() {
            return wagons;
        }
    }

    /**
     * Main entry point for USLP07.
     * Schedules a list of trains and returns their final schedules.
     */
    public List<TrainSchedule> scheduleTrains(List<TrainDispatchRequest> requests) {
        List<TrainSchedule> schedules = new ArrayList<>();
        List<ScheduledSegment> allSegments = new ArrayList<>();

        // 1. Schedule individual trains (sem conflitos)
        for (TrainDispatchRequest request : requests) {
            List<ScheduledSegment> segmentsForTrain = new ArrayList<>();
            TrainSchedule schedule = scheduleSingleTrain(request, segmentsForTrain);
            schedules.add(schedule);
            allSegments.addAll(segmentsForTrain);
        }

        // 2. Detetar conflitos em linhas simples e aplicar cruzamentos
        detectAndResolveConflicts(schedules, allSegments);

        return schedules;
    }

    /**
     * Calculates the basic schedule (route + passage times) for a single train,
     * and builds the list of scheduled segments for that train.
     *
     * This does NOT consider other trains yet; that is handled in detectAndResolveConflicts.
     */
    public TrainSchedule scheduleSingleTrain(TrainDispatchRequest request,
                                             List<ScheduledSegment> segmentsOut) {
        TrainSchedule schedule = new TrainSchedule(
                request.getTrainId(),
                request.getRoute(),
                request.getDepartureTime()
        );

        LocalDateTime currentTime = request.getDepartureTime();

        List<Station> route = request.getRoute();
        List<Locomotive> locomotives = request.getLocomotives();
        List<Wagon> wagons = request.getWagons();

        if (locomotives == null || locomotives.isEmpty()) {
            schedule.setStatus(TrainSchedule.Status.INVALID);
            System.err.printf("Comboio %s não tem locomotivas associadas.%n", schedule.getTrainId());
            return schedule;
        }

        // Locomotiva principal (para características base)
        Locomotive mainLoco = locomotives.get(0);

        // --- Peso total do comboio (locos + wagons) ---
        double totalLocoWeight = 0.0;
        for (Locomotive loco : locomotives) {
            totalLocoWeight += loco.getWeightTons();
        }

        double totalWagonWeight = 0.0;
        double wagonsSpeedLimit = Double.POSITIVE_INFINITY; // limite imposto pelos wagons

        for (Wagon w : wagons) {
            if (w.getModel() != null) {
                // assumimos que o modelo tem tara e velocidade máxima
                totalWagonWeight += w.getModel().getTareWeightTons();
                if (w.getModel().getMaxSpeedKmH() > 0) {
                    wagonsSpeedLimit = Math.min(wagonsSpeedLimit, w.getModel().getMaxSpeedKmH());
                }
            }
        }

        double totalTrainWeight = totalLocoWeight + totalWagonWeight;

        // Se nenhum wagon tiver definido maxSpeed, ignoramos esse limite (ficará +∞)
        if (Double.isInfinite(wagonsSpeedLimit)) {
            wagonsSpeedLimit = mainLoco.getMaxSpeedKmH();
        }

        // Passagem na estação inicial
        if (!route.isEmpty()) {
            schedule.addPassageTime(route.get(0), currentTime);
        }

        // Para cada perna da rota (de estação i para i+1)
        for (int i = 0; i < route.size() - 1; i++) {
            Station from = route.get(i);
            Station to = route.get(i + 1);

            RailwayLine line = railwayLineRepository.findDirectLine(from, to);
            if (line == null) {
                schedule.setStatus(TrainSchedule.Status.INVALID);
                System.err.printf(
                        "Não existe linha direta entre %s e %s para o comboio %s%n",
                        from.getName(), to.getName(), schedule.getTrainId()
                );
                break;
            }

            // Para cada segmento da linha
            for (LineSegment segment : line.getSegments()) {

                // --- Velocidade base (segmento + locomotiva + wagons) ---
                double baseSpeed = segment.getMaxSpeedKmH();
                baseSpeed = Math.min(baseSpeed, mainLoco.getMaxSpeedKmH());
                baseSpeed = Math.min(baseSpeed, wagonsSpeedLimit);

                // Segurança: evitar 0 ou negativo
                if (baseSpeed <= 0) {
                    baseSpeed = 10.0; // fallback mínimo
                }

                // --- Penalização por peso total ---
                double weightThreshold = 800.0; // tons
                double minFactor = 0.5;

                double factor;
                if (totalTrainWeight <= 0) {
                    factor = 1.0;
                } else if (totalTrainWeight <= weightThreshold) {
                    factor = 1.0;
                } else {
                    factor = weightThreshold / totalTrainWeight;
                    if (factor < minFactor) {
                        factor = minFactor;
                    }
                }

                double effectiveSpeed = baseSpeed * factor;

                // --- Cálculo do tempo ---
                double timeHours = segment.getDistanceKm() / effectiveSpeed;
                double timeMinutes = timeHours * 60.0;
                long minutesRounded = Math.round(timeMinutes);

                LocalDateTime entryTime = currentTime;
                LocalDateTime exitTime = currentTime.plusMinutes(minutesRounded);

                boolean directionAB = true; // simplificação; podes melhorar se tiveres info de direção

                ScheduledSegment scheduledSegment = new ScheduledSegment(
                        schedule.getTrainId(),
                        segment,
                        entryTime,
                        exitTime,
                        directionAB
                );
                segmentsOut.add(scheduledSegment);

                currentTime = exitTime;
            }

            schedule.addPassageTime(to, currentTime);
        }

        schedule.setEstimatedArrivalTime(currentTime);

        if (schedule.getStatus() == null) {
            schedule.setStatus(TrainSchedule.Status.VALIDATED);
        }

        return schedule;
    }

    // -------------------------------------------------------------------------
    // Conflitos, cruzamentos e atrasos
    // -------------------------------------------------------------------------

    private void detectAndResolveConflicts(List<TrainSchedule> schedules,
                                           List<ScheduledSegment> allSegments) {

        // Filtrar só segmentos de via única
        List<ScheduledSegment> singleTrackSegments = new ArrayList<>();
        for (ScheduledSegment seg : allSegments) {
            if (isSingleTrack(seg.getSegment())) {
                singleTrackSegments.add(seg);
            }
        }

        // Comparar segmentos 2 a 2 para encontrar overlaps
        for (int i = 0; i < singleTrackSegments.size(); i++) {
            ScheduledSegment segA = singleTrackSegments.get(i);

            for (int j = i + 1; j < singleTrackSegments.size(); j++) {
                ScheduledSegment segB = singleTrackSegments.get(j);

                // Segmento físico diferente -> ignora
                if (!segA.getSegment().equals(segB.getSegment())) {
                    continue;
                }

                // Mesmo comboio -> ignora
                if (segA.getTrainId().equals(segB.getTrainId())) {
                    continue;
                }

                // Overlap temporal?
                if (!overlaps(segA.getEntryTime(), segA.getExitTime(),
                        segB.getEntryTime(), segB.getExitTime())) {
                    continue;
                }

                // (No futuro podes verificar directionAB para garantir sentidos opostos)

                // Temos conflito -> decidir quem espera
                ScheduledSegment prioritySegment;
                ScheduledSegment waitingSegment;

                if (segA.getEntryTime().isBefore(segB.getEntryTime())) {
                    prioritySegment = segA;
                    waitingSegment = segB;
                } else {
                    prioritySegment = segB;
                    waitingSegment = segA;
                }

                LocalDateTime oldEntry = waitingSegment.getEntryTime();
                LocalDateTime newEntry = prioritySegment.getExitTime();

                if (!newEntry.isAfter(oldEntry)) {
                    // já não precisa esperar (caso limite) -> ignora
                    continue;
                }

                Duration delay = Duration.between(oldEntry, newEntry);

                // Encontrar os schedules correspondentes
                TrainSchedule waitingSchedule =
                        findScheduleByTrainId(schedules, waitingSegment.getTrainId());
                TrainSchedule prioritySchedule =
                        findScheduleByTrainId(schedules, prioritySegment.getTrainId());

                if (waitingSchedule == null || prioritySchedule == null) {
                    continue;
                }

                // Aplicar delay ao schedule (passagens + chegada)
                waitingSchedule.applyDelay(oldEntry, delay);

                // Aplicar delay aos segmentos desse comboio a partir do momento do conflito
                applyDelayToSegments(allSegments, waitingSegment.getTrainId(), oldEntry, delay);

                // Registar operação de cruzamento
                String locationName = waitingSegment.getSegment().toString();
                waitingSchedule.addCrossingOperation(
                        locationName,
                        prioritySchedule.getTrainId(),
                        waitingSchedule.getTrainId(),
                        delay
                );

                waitingSchedule.setStatus(TrainSchedule.Status.CONFLICT_WARNING);
            }
        }
    }

    private boolean overlaps(LocalDateTime aStart, LocalDateTime aEnd,
                             LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private TrainSchedule findScheduleByTrainId(List<TrainSchedule> schedules, String trainId) {
        for (TrainSchedule s : schedules) {
            if (s.getTrainId().equals(trainId)) {
                return s;
            }
        }
        return null;
    }

    private void applyDelayToSegments(List<ScheduledSegment> allSegments,
                                      String trainId,
                                      LocalDateTime conflictStart,
                                      Duration delay) {
        for (ScheduledSegment seg : allSegments) {
            if (!seg.getTrainId().equals(trainId)) continue;
            seg.applyDelay(delay, conflictStart);
        }
    }

    private boolean isSingleTrack(LineSegment segment) {
        // 1 via -> via única; 2 ou mais -> via dupla/múltipla
        return segment.getNumberTracks() == 1;
    }

}
