package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.repository.CsvLocomotiveRepository;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CsvLocomotiveRepositoryTest {

    private Path locomotivesCsv;

    @BeforeEach
    void setUp() throws IOException {
        locomotivesCsv = Files.createTempFile("locomotives", ".csv");

        String csvContent = """
                L1,Alpha,150,80,1
                L2,Beta,100,60,0
                L3,,,
                L4
                """;

        Files.writeString(locomotivesCsv, csvContent);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(locomotivesCsv);
    }



    @Test
    void testLoadLocomotives_IncompleteData_UsesDefaults() {
        CsvLocomotiveRepository repo = new CsvLocomotiveRepository(locomotivesCsv.toString());

        Locomotive l4 = repo.findById("L4");
        assertNotNull(l4);
        assertEquals("Modelo Desconhecido", l4.getModel());
        assertEquals(120, l4.getMaxSpeedKmH());
        assertEquals(50, l4.getWeightTons());
        assertFalse(l4.isElectric());
    }

    @Test
    void testFindById_ReturnsCorrectLocomotive() {
        CsvLocomotiveRepository repo = new CsvLocomotiveRepository(locomotivesCsv.toString());

        Locomotive loco = repo.findById("L2");
        assertNotNull(loco);
        assertEquals("Beta", loco.getModel());
        assertFalse(loco.isElectric());
    }

    @Test
    void testFindById_InvalidId_ReturnsNull() {
        CsvLocomotiveRepository repo = new CsvLocomotiveRepository(locomotivesCsv.toString());
        assertNull(repo.findById("INVALID"));
    }

    @Test
    void testFindAll_ReturnsIndependentList() {
        CsvLocomotiveRepository repo = new CsvLocomotiveRepository(locomotivesCsv.toString());

        List<Locomotive> all = repo.findAll();
        int originalSize = all.size();
        all.clear(); // limpar a lista não deve afetar o repositório interno

        assertEquals(originalSize, repo.findAll().size(),
                "A lista devolvida por findAll() deve ser uma cópia independente");
    }
}

