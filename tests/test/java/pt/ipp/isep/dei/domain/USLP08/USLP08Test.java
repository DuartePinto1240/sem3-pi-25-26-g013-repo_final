package test.java.pt.ipp.isep.dei.domain.USLP08;

import main.java.pt.ipp.isep.dei.controller.RoutePlannerController;
import main.java.pt.ipp.isep.dei.dto.PendingFreightDTO;
import main.java.pt.ipp.isep.dei.dto.RoutePlanDTO;
import main.java.pt.ipp.isep.dei.dto.StopPlanDTO;
import main.java.pt.ipp.isep.dei.repository.FreightRepository;
import main.java.pt.ipp.isep.dei.repository.OracleFreightRepository;
import main.java.pt.ipp.isep.dei.service.RoutePlannerService;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class USLP08Test {

    private RoutePlannerController controller;

    @BeforeEach
    void setup(TestInfo info) {
        System.out.println("\n============================================================");
        System.out.println("[TEST START] " + info.getDisplayName());
        System.out.println("============================================================");

        FreightRepository repo = new OracleFreightRepository();
        RoutePlannerService service = new RoutePlannerService(); // no-args
        controller = new RoutePlannerController(repo, service);
    }

    @AfterEach
    void tearDown(TestInfo info) {
        System.out.println("[TEST END] " + info.getDisplayName());
        System.out.println("============================================================\n");
    }

    @Test
    @Order(1)
    @DisplayName("USLP08 - Fetch pending freights (PL/SQL)")
    void shouldFetchPendingFreights_fromDatabase() {
        List<PendingFreightDTO> pending;

        try {
            pending = controller.getPendingFreights();
        } catch (Exception e) {
            System.out.println("[FAIL] Exception while fetching pending freights: " + e.getMessage());
            fail("DB connection or PL/SQL function failed: " + e.getMessage());
            return;
        }

        System.out.println("[INFO] Pending freights returned: " + pending.size());
        for (PendingFreightDTO f : pending) {
            System.out.printf("  - #%d | %s -> %s | depart=%s | arrive=%s%n",
                    f.freightId(), f.originName(), f.destinationName(),
                    f.departTime(), f.arrivalTime());
        }

        assertNotNull(pending, "Pending list must not be null");
        assertFalse(pending.isEmpty(), "Expected at least 1 pending freight in the test dataset");

        PendingFreightDTO f0 = pending.get(0);
        assertTrue(f0.freightId() > 0);
        assertNotNull(f0.originName());
        assertNotNull(f0.destinationName());
        assertNotNull(f0.departTime());
        assertNotNull(f0.arrivalTime());

        System.out.println("[OK] Pending freights fetched and validated.");
    }

    @Test
    @Order(2)
    @DisplayName("USLP08 - Plan selected pending freight (uses cargos PL/SQL)")
    void shouldPlanSinglePendingFreight_andShowStops() {
        List<PendingFreightDTO> pending = controller.getPendingFreights();
        assumeFalse(pending.isEmpty(), "No pending freights available to test planning");

        long freightId = pending.get(0).freightId();
        System.out.println("[INFO] Selected freightId for planning: " + freightId);

        List<RoutePlanDTO> plans = controller.planSelected(Set.of(freightId));

        System.out.println("[INFO] Routes returned: " + (plans == null ? 0 : plans.size()));
        printRoutesSummary(plans);

        assertNotNull(plans);
        assertFalse(plans.isEmpty(), "Planning a valid pending freight should produce at least 1 route");

        RoutePlanDTO route = plans.get(0);
        assertNotNull(route.getStops());
        assertFalse(route.getStops().isEmpty());

        for (StopPlanDTO stop : route.getStops()) {
            assertNotNull(stop.getFacilityName());
            assertNotNull(stop.getToLoad());
            assertNotNull(stop.getToUnload());
        }

        System.out.println("[OK] Single freight planning produced stops and load/unload lists.");
    }

    @Test
    @Order(3)
    @DisplayName("USLP08 - Auto plan ALL pending freights")
    void autoPlanAll_shouldReturnRoutesWithStopsInOrder() {
        List<RoutePlanDTO> routes = controller.autoPlanAll();

        System.out.println("[INFO] Auto-plan routes returned: " + (routes == null ? 0 : routes.size()));
        printRoutesSummary(routes);

        assertNotNull(routes);
        assertFalse(routes.isEmpty(), "Auto-plan should produce at least one route for the dataset");

        for (RoutePlanDTO r : routes) {
            assertNotNull(r.getFreightIds());
            assertFalse(r.getFreightIds().isEmpty());

            assertNotNull(r.getStops());
            assertTrue(r.getStops().size() >= 2);

            for (StopPlanDTO s : r.getStops()) {
                assertNotNull(s.getFacilityName());
                assertFalse(s.getFacilityName().isBlank());
            }
        }

        System.out.println("[OK] Auto-plan validated: routes + ordered stops exist.");
    }

    @Test
    @Order(4)
    @DisplayName("USLP08 - Plan selection ignores invalid IDs")
    void planSelected_shouldIgnoreInvalidIds() {
        List<PendingFreightDTO> pending = controller.getPendingFreights();
        assumeFalse(pending.isEmpty(), "No pending freights available");

        Set<Long> valid = pending.stream()
                .limit(2)
                .map(PendingFreightDTO::freightId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Long> selection = new LinkedHashSet<>(valid);
        selection.add(99999999L);

        System.out.println("[INFO] Selection requested: " + selection);
        System.out.println("[INFO] Valid pending used: " + valid);

        List<RoutePlanDTO> routes = controller.planSelected(selection);

        System.out.println("[INFO] Routes returned: " + (routes == null ? 0 : routes.size()));
        printRoutesSummary(routes);

        assertNotNull(routes);
        assertFalse(routes.isEmpty());

        Set<Long> plannedFreights = routes.stream()
                .flatMap(r -> r.getFreightIds().stream())
                .collect(Collectors.toSet());

        System.out.println("[INFO] Planned freight IDs: " + plannedFreights);

        assertTrue(plannedFreights.stream().allMatch(valid::contains),
                "Planned freights must be subset of valid pending IDs");

        System.out.println("[OK] Invalid IDs were not included in planned routes.");
    }

    // ---------- helpers ----------

    private void printRoutesSummary(List<RoutePlanDTO> routes) {
        if (routes == null || routes.isEmpty()) {
            System.out.println("  (no routes)");
            return;
        }

        int idx = 1;
        for (RoutePlanDTO r : routes) {
            System.out.println("  Route " + (idx++) + " freights=" + r.getFreightIds());

            int sIdx = 1;
            for (StopPlanDTO s : r.getStops()) {
                int loads = s.getToLoad() == null ? 0 : s.getToLoad().size();
                int unloads = s.getToUnload() == null ? 0 : s.getToUnload().size();

                System.out.printf("    Stop %d: %s | load=%d unload=%d%n",
                        sIdx++, s.getFacilityName(), loads, unloads);
            }
        }
    }
}
