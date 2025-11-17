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
 *
 * Now also considers:
 * - Multiple locomotives per train (total power, total locomotive mass)
 * - Wagon mass (using WagonModel weights)
 * - Effective speed based on power-per-ton, limited by track and loco max speeds.
 */
public class TrainSchedulerService {

    /**
     * Reference power per ton (kW/ton) used to scale effective speed.
     * Around this value the train can use (approx.) the full allowed speed.
     */
    private static final double REFERENCE_POWER_PER_TON = 20.0;

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
        private final List<Station> route;           // ordered stations: [A, B, C, ...]
        private final List<Locomotive> locomotives;  // one or more locomotives
        private final List<Wagon> wagons;            // one or more wagons
        private final LocalDateTime departureTime;

        public TrainDispatchRequest(String trainId,
                                    List<Station> route,
                                    List<Locomotive> locomotives,
                                    List<Wagon> wagons,
                                    LocalDateTime departureTime) {
            this.trainId = trainId;
            this.route = route;
            this.locomotives = locomotives;
            this.wagons = wagons;
            this.departureTime = departureTime;
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

        public List<Wagon> getWagons() {
            return wagons;
        }

        public LocalDateTime getDepartureTime() {
            return departureTime;
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
        List<Wagon> wagons = request.getWagons() != null ? request.getWagons() : new ArrayList<>();

        // Segurança: um comboio tem de ter pelo menos 1 locomotiva
        if (locomotives == null || locomotives.isEmpty()) {
            schedule.setStatus(TrainSchedule.Status.INVALID);
            return schedule;
        }

        // ------------------------------------------------------------
        // 1) Agregar características do comboio (locos + wagons)
        // ------------------------------------------------------------
        double totalLocoPowerKw = 0.0;
        double totalLocoWeightTons = 0.0;
        double minLocoMaxSpeedKmH = Double.MAX_VALUE;

        for (Locomotive loco : locomotives) {
            if (loco == null) continue;
            totalLocoPowerKw += Math.max(0.0, loco.getPower());
            totalLocoWeightTons += Math.max(0.0, loco.getWeightTons());
            minLocoMaxSpeedKmH = Math.min(minLocoMaxSpeedKmH, loco.getMaxSpeedKmH());
        }

        if (minLocoMaxSpeedKmH == Double.MAX_VALUE) {
            // fallback defensivo
            minLocoMaxSpeedKmH = 120.0;
        }

        // Calcular peso atual dos vagões (considerando se estão carregados ou não)
        double totalWagonWeightTons = 0.0;
        double minWagonMaxSpeedKmH = Double.MAX_VALUE;
        
        for (Wagon w : wagons) {
            if (w == null) continue;
            // Usar peso atual (carregado ou vazio conforme tem boxes ou não)
            double currentWeight = w.getCurrentWeightTons();
            totalWagonWeightTons += Math.max(0.0, currentWeight);
            
            // Considerar também o limite de velocidade dos vagões
            double wagonMaxSpeed = w.getMaxSpeedKmH();
            if (wagonMaxSpeed > 0.0) {
                minWagonMaxSpeedKmH = Math.min(minWagonMaxSpeedKmH, wagonMaxSpeed);
            }
        }

        double totalTrainWeightTons = totalLocoWeightTons + totalWagonWeightTons;
        
        // Velocidade máxima do comboio = mínimo entre locomotivas e vagões
        double minTrainMaxSpeedKmH = Math.min(minLocoMaxSpeedKmH, 
                minWagonMaxSpeedKmH != Double.MAX_VALUE ? minWagonMaxSpeedKmH : Double.MAX_VALUE);

        // Passagem na estação inicial
        if (!route.isEmpty()) {
            schedule.addPassageTime(route.get(0), currentTime);
        }

        // ------------------------------------------------------------
        // 2) Para cada perna da rota (de estação i para i+1)
        // ------------------------------------------------------------
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

                double effectiveSpeedKmH = computeEffectiveSpeedKmH(
                        segment,
                        minTrainMaxSpeedKmH,
                        totalLocoPowerKw,
                        totalTrainWeightTons
                );

                // Segurança: nunca permitir 0 (evita divisão por zero)
                if (effectiveSpeedKmH <= 0.0) {
                    // se cair aqui, algo está muito errado -> marcar inválido
                    schedule.setStatus(TrainSchedule.Status.INVALID);
                    System.err.printf(
                            "Velocidade efetiva inválida (%.2f km/h) no segmento %s para o comboio %s%n",
                            effectiveSpeedKmH, segment, schedule.getTrainId()
                    );
                    break;
                }

                double timeHours = segment.getDistanceKm() / effectiveSpeedKmH;
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

    /**
     * Calcula a velocidade efetiva do comboio neste segmento, tendo em conta:
     * - Limite do segmento (track speed limit)
     * - Limite do comboio (mínimo entre locomotivas e vagões)
     * - Relação potência/tonelada (quanto maior, mais perto do limite anda)
     * 
     * A velocidade efetiva é calculada como o mínimo entre:
     * 1. O limite do segmento
     * 2. O limite do comboio (locomotivas e vagões)
     * 3. A velocidade ajustada pela relação potência/peso
     */
    private double computeEffectiveSpeedKmH(LineSegment segment,
                                            double minTrainMaxSpeedKmH,
                                            double totalLocoPowerKw,
                                            double totalTrainWeightTons) {

        double trackLimitKmH = segment.getMaxSpeedKmH();
        
        // Velocidade máxima teórica = mínimo entre limite do segmento e limite do comboio
        double baseSpeedKmH = Math.min(trackLimitKmH, minTrainMaxSpeedKmH);

        // Se não tivermos potência ou peso, retornamos a velocidade base
        if (totalLocoPowerKw <= 0.0 || totalTrainWeightTons <= 0.0) {
            return baseSpeedKmH;
        }

        // Calcular relação potência/peso (kW por tonelada)
        double powerPerTon = totalLocoPowerKw / totalTrainWeightTons;

        // Fator de velocidade baseado na relação potência/peso:
        // - powerPerTon >= REFERENCE_POWER_PER_TON -> factor = 1.0 (usa o máximo)
        // - powerPerTon muito baixo -> factor = 0.4 (anda bem mais devagar)
        // A fórmula usa uma função logarítmica suave para transição
        double ratio = powerPerTon / REFERENCE_POWER_PER_TON;
        
        // Aplicar função de ajuste: ratio^0.5 para suavizar a transição
        // Isso significa que com metade da potência de referência, temos ~70% da velocidade
        double factor = clamp(Math.pow(Math.max(0.0, ratio), 0.5), 0.4, 1.0);

        return baseSpeedKmH * factor;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // -------------------------------------------------------------------------
    // Conflitos, cruzamentos e atrasos
    // -------------------------------------------------------------------------

    private void detectAndResolveConflicts(List<TrainSchedule> schedules,
                                           List<ScheduledSegment> allSegments) {

        // Neste primeiro passo vamos assumir que TODOS os segmentos são single-track.
        // Mais tarde podes substituir esta condição pela tua lógica real:
        // ex: segment.getTrackType().equals("SINGLE")
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
