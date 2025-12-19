package test.java.pt.ipp.isep.dei.domain.USEI14;

import main.java.pt.ipp.isep.dei.controller.USEI14Controller;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class USEI14Test {

    private static USEI14Controller controller;

    private static final String STATIONS_FILE =
            "train_station_dataset_v2/stations.csv";
    private static final String LINES_FILE =
            "train_station_dataset_v2/lines.csv";

    @BeforeAll
    static void setup() {
        controller = new USEI14Controller();
        System.out.println("\n=== USEI14 TEST INITIALIZATION ===");
    }

    @Test
    void testValidStationsReturnFlow() {

        String sourceId = "258";
        String sinkId   = "259";

        double flow = controller.maximumThroughput(
                STATIONS_FILE,
                LINES_FILE,
                sourceId,
                sinkId
        );

        assertTrue(flow >= 0,"Maximum flow should be non-negative");
    }


    @Test
    void testDeterministicResult() {

        String sourceId = "259";
        String sinkId   = "404";

        double flow1 = controller.maximumThroughput(
                STATIONS_FILE,
                LINES_FILE,
                sourceId,
                sinkId
        );

        double flow2 = controller.maximumThroughput(
                STATIONS_FILE,
                LINES_FILE,
                sourceId,
                sinkId
        );

        assertEquals(flow1, flow2, 1e-9,
                "Max flow should be deterministic");
    }


    @Test
    void testSameSourceAndSink() {

        String stationId = "6";

        double flow = controller.maximumThroughput(
                STATIONS_FILE,
                LINES_FILE,
                stationId,
                stationId
        );

        assertEquals(0.0, flow, 1e-9,
                "Flow from a node to itself should be zero");
    }


    @Test
    void testInvalidSourceStation() {

        String sourceId = "INVALID";
        String sinkId   = "1";

        assertThrows(IllegalArgumentException.class, () ->
                controller.maximumThroughput(
                        STATIONS_FILE,
                        LINES_FILE,
                        sourceId,
                        sinkId
                )
        );
    }


    @Test
    void testInvalidSinkStation() {

        String sourceId = "1";
        String sinkId   = "INVALID";

        assertThrows(IllegalArgumentException.class, () ->
                controller.maximumThroughput(
                        STATIONS_FILE,
                        LINES_FILE,
                        sourceId,
                        sinkId
                )
        );
    }


    @Test
    void testDisconnectedStations() {

        String sourceId = "1";
        String sinkId   = "999";

        try {
            double flow = controller.maximumThroughput(
                    STATIONS_FILE,
                    LINES_FILE,
                    sourceId,
                    sinkId
            );

            assertEquals(0.0, flow, 1e-9,"Disconnected nodes should have zero max flow");

        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
