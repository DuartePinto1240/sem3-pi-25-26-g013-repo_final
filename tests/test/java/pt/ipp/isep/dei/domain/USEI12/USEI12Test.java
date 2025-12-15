package test.java.pt.ipp.isep.dei.domain.USEI12;

import main.java.pt.ipp.isep.dei.controller.RailwayController;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.service.MapGraph;
import main.java.pt.ipp.isep.dei.util.RailwayLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class USEI12Test {

    private static RailwayController controller;
    private static Graph<Station, Track> originalGraph;
    private static final String STATIONS_FILE = "train_station_dataset_v2/stations.csv";
    private static final String TRACKS_FILE = "train_station_dataset_v2/lines.csv";

    @BeforeAll
    static void setup() {
        originalGraph = RailwayLoader.loadGraph(STATIONS_FILE, TRACKS_FILE);
        controller = new RailwayController();

        assertNotNull(originalGraph);
        assertTrue(originalGraph.numVertices() > 0);
    }

    @Test
    void testOriginalGraphLoadedCorrectly() {
        assertEquals(559, originalGraph.numVertices());
        assertTrue(originalGraph.numEdges() > 0);
    }

    @Test
    void testMinimalBackboneStructure() {
        Graph<Station, Track> backbone = controller.getMinimalBackboneNetwork(originalGraph);

        assertNotNull(backbone);
        assertEquals(originalGraph.numVertices(), backbone.numVertices());

        int expectedEdges = (backbone.numVertices() - 1) * 2;
        assertEquals(expectedEdges, backbone.numEdges());
    }

    @Test
    void testBackboneIsLighterThanOriginal() {
        Graph<Station, Track> backbone = controller.getMinimalBackboneNetwork(originalGraph);

        double totalWeightOriginal = 0;
        for (var edge : originalGraph.edges()) {
            totalWeightOriginal += edge.getWeight().getDistance();
        }

        double totalWeightBackbone = 0;
        for (var edge : backbone.edges()) {
            totalWeightBackbone += edge.getWeight().getDistance();
        }

        assertTrue(totalWeightBackbone < totalWeightOriginal);
    }

    @Test
    void testExportToDOTFile() {
        Graph<Station, Track> backbone = controller.getMinimalBackboneNetwork(originalGraph);
        String testFilename = "test_backbone.dot";

        boolean success = controller.exportGraphToDOT(backbone, testFilename);
        assertTrue(success);

        File file = new File(testFilename);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        file.delete();
    }

    @Test
    void testSmallGraphLogic() {
        RailwayController smallController = new RailwayController();
        Graph<Station, Track> smallGraph = new MapGraph<>(false);

        Station s1 = new Station("1", "A", 0, 0, 0, 0);
        Station s2 = new Station("2", "B", 0, 0, 0, 0);
        Station s3 = new Station("3", "C", 0, 0, 0, 0);

        smallGraph.addVertex(s1);
        smallGraph.addVertex(s2);
        smallGraph.addVertex(s3);

        smallGraph.addEdge(s1, s2, new Track(10, 0, 0));
        smallGraph.addEdge(s2, s3, new Track(10, 0, 0));
        smallGraph.addEdge(s1, s3, new Track(100, 0, 0));

        Graph<Station, Track> smallBackbone = smallController.getMinimalBackboneNetwork(smallGraph);

        assertEquals(3, smallBackbone.numVertices());
        assertEquals(4, smallBackbone.numEdges());

        assertNull(smallBackbone.edge(s1, s3));
        assertNotNull(smallBackbone.edge(s1, s2));
    }
}