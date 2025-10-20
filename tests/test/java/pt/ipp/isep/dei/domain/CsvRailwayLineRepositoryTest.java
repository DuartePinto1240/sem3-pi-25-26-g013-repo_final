package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import main.java.pt.ipp.isep.dei.repository.CsvRailwayLineRepository;
import main.java.pt.ipp.isep.dei.repository.CsvStationRepository;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CsvRailwayLineRepositoryTest {

    private Path linesCsv;
    private Path segmentsCsv;
    private Path stationsCsv;
    private CsvStationRepository stationRepo;

    @BeforeEach
    void setUp() throws IOException {
        // Criar ficheiro CSV de estações
        stationsCsv = Files.createTempFile("stations", ".csv");
        String stationContent = """
                S1,Lisboa,38.7169,-9.1399
                S2,Coimbra,40.2056,-8.4196
                S3,Porto,41.1496,-8.6109
                """;
        Files.writeString(stationsCsv, stationContent);
        stationRepo = new CsvStationRepository(stationsCsv.toString());

        // Criar ficheiro CSV de segmentos
        segmentsCsv = Files.createTempFile("segments", ".csv");
        String segmentContent = """
                L1,,S1,S2,200,120,1
                L1,,S2,S3,100,90,0
                L2,,S3,S1,300,100,1
                """;
        Files.writeString(segmentsCsv, segmentContent);

        // Criar ficheiro CSV de linhas
        linesCsv = Files.createTempFile("lines", ".csv");
        String lineContent = """
                L1, Linha Norte, ativo
                L2, Linha Sul, ativo
                """;
        Files.writeString(linesCsv, lineContent);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(linesCsv);
        Files.deleteIfExists(segmentsCsv);
        Files.deleteIfExists(stationsCsv);
    }



    @Test
    void testFindDirectLine_ReturnsNullIfNotFound() {
        CsvRailwayLineRepository repo = new CsvRailwayLineRepository(
                linesCsv.toString(),
                segmentsCsv.toString(),
                stationRepo
        );

        Station s1 = stationRepo.findById("S1");
        Station s3 = stationRepo.findById("S3");

        assertNull(repo.findDirectLine(s3, s3), "Não deve existir linha S3->S3");
    }



    @Test
    void testDoesNotCreateLineWithoutSegments() throws IOException {
        // Limpar ficheiro de segmentos
        Files.writeString(segmentsCsv, "");

        CsvRailwayLineRepository repo = new CsvRailwayLineRepository(
                linesCsv.toString(),
                segmentsCsv.toString(),
                stationRepo
        );

        RailwayLine line = repo.findDirectLine(
                stationRepo.findById("S1"),
                stationRepo.findById("S2")
        );

        assertNull(line, "Sem segmentos, nenhuma linha deve ser criada");
    }
}

