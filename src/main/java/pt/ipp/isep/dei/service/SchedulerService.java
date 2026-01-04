package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.dto.*;
import main.java.pt.ipp.isep.dei.repository.ScheduleRepository;
import main.java.pt.ipp.isep.dei.repository.TrainRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main scheduler service - calculates complete train schedules
 */
public class SchedulerService {

    private final TrainRepository trainRepo;
    private final ScheduleRepository scheduleRepo;
    private final SpeedCalculatorService speedCalc;
    private final PathFinderService pathFinder;
    private final CrossingManagerService crossingMgr;

    // dwell time only when the train STOPS
    private static final long STOP_DWELL_MINUTES = 5;

    public SchedulerService(
            TrainRepository trainRepo,
            ScheduleRepository scheduleRepo,
            SpeedCalculatorService speedCalc,
            PathFinderService pathFinder,
            CrossingManagerService crossingMgr
    ) {
        this.trainRepo = trainRepo;
        this.scheduleRepo = scheduleRepo;
        this.speedCalc = speedCalc;
        this.pathFinder = pathFinder;
        this.crossingMgr = crossingMgr;
    }

    /**
     * OPTION B (simple): calculate schedule strictly following the Route stops list,
     * showing real facility names.
     *
     * IMPORTANT:
     * - If there's no DIRECT connection between consecutive stops, we fallback to PathFinder (multi-segment path)
     *   and sum the travel time of those segments.
     * - We still show ONLY the route stops in the schedule (no intermediate facilities printed).
     */
    public TrainScheduleDTO calculateScheduleForStopSequence(
            TrainDTO train,
            List<RouteStopDTO> stops,
            LocalDateTime departureTime
    ) {
        if (stops == null || stops.size() < 2) {
            throw new RuntimeException("Route must have at least 2 stops.");
        }

        // Map for names (so we print real names instead of "Facility X")
        Map<Long, String> nameByFacilityId = new HashMap<>();
        for (RouteStopDTO s : stops) {
            // if for some reason facilityName comes null, we still fallback later
            nameByFacilityId.put(s.facilityId(), s.facilityName());
        }

        // Stops are exactly these route facilities
        Set<Long> stopFacilities = new HashSet<>();
        for (RouteStopDTO s : stops) stopFacilities.add(s.facilityId());

        // Fetch REAL locomotives/wagons (so speed depends on power + weight)
        List<LocomotiveDTO> locomotives = getLocomotives(train);
        List<WagonDTO> wagons = getWagons(train);

        if (locomotives.isEmpty()) {
            throw new RuntimeException("Train " + train.trainId() + " has no locomotives loaded for speed calculation.");
        }

        List<PassageTimeDTO> passageTimes = new ArrayList<>();
        LocalDateTime currentTime = departureTime;

        Long startId = stops.get(0).facilityId();

        // Start always a stop: arrival=departure at start
        passageTimes.add(new PassageTimeDTO(
                startId,
                resolveFacilityName(startId, nameByFacilityId),
                currentTime,
                currentTime,
                true
        ));

        // For each consecutive pair of stops
        for (int i = 0; i < stops.size() - 1; i++) {
            Long fromId = stops.get(i).facilityId();
            Long toId = stops.get(i + 1).facilityId();

            // 1) Try direct segments (fast path)
            List<LineSegmentDTO> segments = trainRepo.getLineSegments(fromId, toId);

            // 2) Fallback: if not direct, ask PathFinder for a multi-segment path
            if (segments == null || segments.isEmpty()) {
                PathDTO path = pathFinder.findPath(fromId, toId, train);
                if (path == null || path.segments() == null || path.segments().isEmpty()) {
                    throw new RuntimeException("No railway path between " + fromId + " and " + toId);
                }
                segments = path.segments();
            }

            // Sum travel time across ALL segments from -> to
            for (LineSegmentDTO seg : segments) {
                double travelMinutes = speedCalc.calculateTravelTime(locomotives, wagons, seg);
                currentTime = currentTime.plusMinutes((long) Math.ceil(travelMinutes));
            }

            // Arrive at the next stop
            boolean isStop = stopFacilities.contains(toId);
            LocalDateTime arrival = currentTime;
            LocalDateTime depart = null;

            if (isStop) {
                depart = currentTime.plusMinutes(STOP_DWELL_MINUTES);
                currentTime = depart;
            }

            passageTimes.add(new PassageTimeDTO(
                    toId,
                    resolveFacilityName(toId, nameByFacilityId),
                    arrival,
                    depart,
                    isStop
            ));
        }

        // Persist schedule header
        Long scheduleId = scheduleRepo.createSchedule(train.trainId(), null, departureTime);

        // Persist passage times
        for (PassageTimeDTO p : passageTimes) {
            scheduleRepo.savePassageTime(scheduleId, p);
        }

        return new TrainScheduleDTO(
                scheduleId,
                train.trainId(),
                null,
                departureTime,
                passageTimes,
                List.of()
        );
    }

    /**
     * Calculate schedule for a single train with manual path (waypoints)
     * manualPath = facilities that MUST be visited in that order
     */
    public TrainScheduleDTO calculateScheduleManual(
            TrainDTO train,
            List<Long> manualPath,
            LocalDateTime departureTime
    ) {
        if (manualPath == null || manualPath.size() < 2) {
            throw new RuntimeException("Manual path must contain at least 2 facilities.");
        }

        PathDTO path = pathFinder.findPathWithWaypoints(manualPath, train);
        Set<Long> stopFacilities = new HashSet<>(manualPath);

        return calculateScheduleForPath(train, path, departureTime, stopFacilities);
    }

    /**
     * Calculate schedule for a single train with automatic pathfinding
     */
    public TrainScheduleDTO calculateScheduleAutomatic(
            TrainDTO train,
            Long fromFacilityId,
            Long toFacilityId,
            LocalDateTime departureTime
    ) {
        if (fromFacilityId == null || toFacilityId == null) {
            throw new RuntimeException("from/to facility cannot be null.");
        }
        if (fromFacilityId.equals(toFacilityId)) {
            throw new RuntimeException("from/to facilities must be different.");
        }

        PathDTO path = pathFinder.findPath(fromFacilityId, toFacilityId, train);

        Set<Long> stopFacilities = new HashSet<>();
        stopFacilities.add(fromFacilityId);
        stopFacilities.add(toFacilityId);

        return calculateScheduleForPath(train, path, departureTime, stopFacilities);
    }

    /**
     * Dispatch multiple trains and calculate crossings
     */
    public List<TrainScheduleDTO> dispatchMultipleTrains(
            List<TrainDTO> trains,
            List<PathDTO> paths,
            List<LocalDateTime> departures
    ) {
        if (trains == null || paths == null || departures == null) {
            throw new RuntimeException("Inputs cannot be null.");
        }
        if (trains.size() != paths.size() || trains.size() != departures.size()) {
            throw new RuntimeException("Trains, paths, and departures must have the same size.");
        }

        List<TrainScheduleDTO> schedules = new ArrayList<>();

        for (int i = 0; i < trains.size(); i++) {
            TrainDTO train = trains.get(i);
            PathDTO path = paths.get(i);
            LocalDateTime departure = departures.get(i);

            Set<Long> stopFacilities = new HashSet<>();
            if (!path.segments().isEmpty()) {
                stopFacilities.add(path.segments().get(0).fromFacilityId());
                stopFacilities.add(path.segments().get(path.segments().size() - 1).toFacilityId());
            }

            TrainScheduleDTO schedule = calculateScheduleForPath(train, path, departure, stopFacilities);
            schedules.add(schedule);
        }

        try {
            List<LineSegmentDTO> allSegments = paths.stream()
                    .flatMap(p -> p.segments().stream())
                    .toList();

            crossingMgr.calculateCrossings(schedules, allSegments);
        } catch (Exception ignored) {
            // crossing persistence not implemented
        }

        return schedules;
    }

    // ============================================================
    // CORE CALCULATION (kept)
    // ============================================================

    private TrainScheduleDTO calculateScheduleForPath(
            TrainDTO train,
            PathDTO path,
            LocalDateTime departureTime,
            Set<Long> stopFacilities
    ) {
        if (path == null || path.segments() == null || path.segments().isEmpty()) {
            throw new RuntimeException("Cannot calculate schedule: path has no segments");
        }

        List<LocomotiveDTO> locomotives = getLocomotives(train);
        List<WagonDTO> wagons = getWagons(train);

        List<PassageTimeDTO> passageTimes = new ArrayList<>();
        LocalDateTime currentTime = departureTime;

        Long startFacility = path.segments().get(0).fromFacilityId();

        passageTimes.add(new PassageTimeDTO(
                startFacility,
                trainRepo.getFacilityName(startFacility),
                currentTime,
                currentTime,
                true
        ));

        for (LineSegmentDTO segment : path.segments()) {
            double travelMinutes = speedCalc.calculateTravelTime(locomotives, wagons, segment);
            currentTime = currentTime.plusMinutes((long) Math.ceil(travelMinutes));

            Long nextFacility = segment.toFacilityId();

            boolean isStop = stopFacilities != null && stopFacilities.contains(nextFacility);

            LocalDateTime arrival = currentTime;
            LocalDateTime depart = null;

            if (isStop) {
                depart = currentTime.plusMinutes(STOP_DWELL_MINUTES);
                currentTime = depart;
            }

            passageTimes.add(new PassageTimeDTO(
                    nextFacility,
                    trainRepo.getFacilityName(nextFacility),
                    arrival,
                    depart,
                    isStop
            ));
        }

        Long scheduleId = scheduleRepo.createSchedule(train.trainId(), null, departureTime);

        for (PassageTimeDTO p : passageTimes) {
            scheduleRepo.savePassageTime(scheduleId, p);
        }

        return new TrainScheduleDTO(
                scheduleId,
                train.trainId(),
                null,
                departureTime,
                passageTimes,
                List.of()
        );
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String resolveFacilityName(Long facilityId, Map<Long, String> nameByFacilityId) {
        String n = nameByFacilityId.get(facilityId);
        if (n != null && !n.isBlank()) return n;
        return trainRepo.getFacilityName(facilityId);
    }

    private List<LocomotiveDTO> getLocomotives(TrainDTO train) {
        if (train == null || train.locomotiveIds() == null || train.locomotiveIds().isEmpty()) {
            return new ArrayList<>();
        }

        // We already have getAllLocomotives() in repo: filter by the train IDs
        List<LocomotiveDTO> all = trainRepo.getAllLocomotives();
        Set<Long> ids = new HashSet<>(train.locomotiveIds());

        return all.stream()
                .filter(l -> l != null && ids.contains(l.locomotiveId()))
                .collect(Collectors.toList());
    }

    private List<WagonDTO> getWagons(TrainDTO train) {
        if (train == null || train.wagonIds() == null || train.wagonIds().isEmpty()) {
            return new ArrayList<>();
        }

        List<WagonDTO> wagons = new ArrayList<>();
        for (Long id : train.wagonIds()) {
            if (id == null) continue;
            WagonDTO w = trainRepo.getWagonById(id);
            if (w != null) wagons.add(w);
        }
        return wagons;
    }
}
