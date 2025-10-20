package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.CsvStationRepository;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CsvStationRepositoryTest {

    private Path tempCsvFile;

    @BeforeEach
    void setUp() throws IOException {
        // Criar ficheiro CSV temporário
        tempCsvFile = Files.createTempFile("stations", ".csv");

        // Conteúdo do CSV (id,name,latitude,longitude)
        String csvContent = """
                S1,Lisboa,38.7169,-9.1399
                S2,Porto,41.1496,-8.6109
                S3,Coimbra,40.2056,-8.4196
                S4
                """;

        Files.writeString(tempCsvFile, csvContent);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempCsvFile);
    }



    @Test
    void testLoadStations_MissingNameUsesDefault() {
        CsvStationRepository repo = new CsvStationRepository(tempCsvFile.toString());

        Station s4 = repo.findById("S4");
        assertNotNull(s4, "Mesmo linhas incompletas devem gerar uma estação válida");
        assertEquals("Estacao S4", s4.getName());
        assertEquals(0.0, s4.getLatitude());
        assertEquals(0.0, s4.getLongitude());
    }

    @Test
    void testFindById_NotExisting_ReturnsNull() {
        CsvStationRepository repo = new CsvStationRepository(tempCsvFile.toString());
        assertNull(repo.findById("S999"));
    }


}
