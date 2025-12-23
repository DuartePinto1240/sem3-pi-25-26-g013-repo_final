package test.java.pt.ipp.isep.dei.domain.USEI13;

import main.java.pt.ipp.isep.dei.controller.USEI13Controller;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.Track;
import main.java.pt.ipp.isep.dei.dto.HubCentralityResultDTO;
import main.java.pt.ipp.isep.dei.service.Graph;
import main.java.pt.ipp.isep.dei.util.RailwayLoader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class USEI13Test {

    // Mesmos paths usados na UI/loader do projeto (dados reais do sprint)
    private static final String STATIONS_FILE = "train_station_dataset_v2/stations.csv";
    private static final String TRACKS_FILE   = "train_station_dataset_v2/lines.csv";

    // Tolerâncias para doubles (não uses igualdade exata)
    private static final double EPS_SCORE = 1e-6;
    private static final double EPS_HC    = 1e-6;
    private static final double EPS_BETW  = 1e-6;
    private static final double EPS_STR   = 1e-6;

    private Graph<Station, Track> loadSprintGraph() {
        Graph<Station, Track> g = RailwayLoader.loadGraph(STATIONS_FILE, TRACKS_FILE);
        assertNotNull(g, "RailwayLoader devolveu null (paths errados ou ficheiros não disponíveis nos testes)");
        return g;
    }

    @Test
    void testLoadSprintGraph_hasExpectedSize() {
        Graph<Station, Track> g = loadSprintGraph();

        // Isto valida que estás mesmo a usar o dataset do sprint
        assertEquals(559, g.numVertices(), "Número de vértices (estações) não bate certo com o sprint");
        assertEquals(1382, g.numEdges(), "Número de arestas (ligações) não bate certo com o sprint");
    }

    @Test
    void testComputeHubRanking_returnsFullRankingSorted() {
        Graph<Station, Track> g = loadSprintGraph();
        USEI13Controller controller = new USEI13Controller();

        List<HubCentralityResultDTO> ranking = controller.computeHubRanking(g);
        assertNotNull(ranking);
        assertEquals(559, ranking.size(), "Ranking deve ter 1 resultado por estação");

        // Confirma ordenação decrescente por hubScore
        for (int i = 1; i < ranking.size(); i++) {
            assertTrue(
                    ranking.get(i - 1).hubScore >= ranking.get(i).hubScore,
                    "Ranking não está ordenado por hubScore desc na posição " + i
            );
        }
    }

    @Test
    void testTop10MatchesSprintExpectedValues() {
        Graph<Station, Track> g = loadSprintGraph();
        USEI13Controller controller = new USEI13Controller();

        List<HubCentralityResultDTO> r = controller.computeHubRanking(g);
        assertTrue(r.size() >= 10);

        // Esperado (dados reais do sprint): TOP 10 exatamente como o teu CSV/output
        assertHub(r.get(0),  "1761", "DELTA",         4, 145.00, 28139.000000, 20.668666, 0.850540);
        assertHub(r.get(1),  "1979", "TOUR-TAXIS",    5, 135.00, 29281.000000, 19.260486, 0.817393);
        assertHub(r.get(2),  "380",  "ETTERBEEK",     5, 190.00, 16722.000000, 20.194967, 0.796609);
        assertHub(r.get(3),  "1048", "SCHAERBEEK",   11, 197.00, 17893.000000, 18.162124, 0.777622);
        assertHub(r.get(4),  "1663", "HAREN",         6, 145.00, 23094.000000, 19.106408, 0.767680);
        assertHub(r.get(5),  "325",  "DIEGEM",        7, 169.00, 18797.000000, 18.786286, 0.755563);
        assertHub(r.get(6),  "227",  "BRUX-SCHUMAN",  6, 163.00, 17479.000000, 19.650751, 0.750637);
        assertHub(r.get(7),  "936",  "OTTIGNIES",     5, 124.00, 32618.000000, 14.604783, 0.738042);
        assertHub(r.get(8),  "1219", "WATERMAEL",     3, 102.00, 27620.000000, 18.527191, 0.733782);
        assertHub(r.get(9),  "1912", "ARCADES",       3, 120.00, 21468.000000, 19.657704, 0.723378);
    }

    @Test
    void testExportToCsv_createsValidFileWithHeaderAndLines() throws Exception {
        Graph<Station, Track> g = loadSprintGraph();
        USEI13Controller controller = new USEI13Controller();
        List<HubCentralityResultDTO> ranking = controller.computeHubRanking(g);

        // Act → método void (não se testa retorno)
        controller.exportToCsv(ranking);

        // Path conhecido do controller
        String expectedPath = "USEI13-Hubs/USEI13_Hubs.csv";
        File f = new File(expectedPath);

        // Assert → efeitos
        assertTrue(f.exists(), "CSV não foi criado em: " + f.getAbsolutePath());
        assertTrue(f.length() > 0, "CSV foi criado mas está vazio");

        // Verificar conteúdo
        int totalLines = 0;
        String firstLine;
        String secondLine;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            firstLine = br.readLine();   // header
            secondLine = br.readLine();  // 1ª linha de dados
            totalLines = 2;
            while (br.readLine() != null) totalLines++;
        }

        assertEquals(
                "stid,stname,degree,strength,betweenness,harmonic_closeness,hubscore",
                firstLine
        );

        assertNotNull(secondLine);
        assertTrue(
                secondLine.startsWith("1761,\"DELTA\","),
                "Primeira linha não corresponde ao TOP1 esperado"
        );

        // 1 header + 559 hubs
        assertEquals(1 + 559, totalLines, "CSV exportado não tem o nº correto de linhas");
    }

    // Helper: valida campos principais com tolerâncias
    private void assertHub(HubCentralityResultDTO a,
                           String stid, String stname,
                           int degree,
                           double strength,
                           double betweenness,
                           double harmonicCloseness,
                           double hubScore) {

        assertEquals(stid, a.stid);
        assertEquals(stname, a.stname);
        assertEquals(degree, a.degree);

        assertEquals(strength, a.strength, EPS_STR);
        assertEquals(betweenness, a.betweenness, EPS_BETW);
        assertEquals(harmonicCloseness, a.harmonicCloseness, EPS_HC);
        assertEquals(hubScore, a.hubScore, EPS_SCORE);
    }
}
