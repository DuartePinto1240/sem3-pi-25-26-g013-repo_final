package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import main.java.pt.ipp.isep.dei.repository.CsvLocomotiveRepository;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CsvLocomotiveRepository}.
 * <p>
 * These tests check the correct loading of locomotives from a CSV file,
 * including handling of incomplete data, ID lookup, and retrieval of all locomotives.
 * </p>
 */
class CsvLocomotiveRepositoryTest {

    private Path locomotivesCsv;

    /**
     * Creates a temporary CSV file with test data before each test.
     */
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

    /**
     * Deletes the temporary CSV file after each test.
     */
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(locomotivesCsv);
    }

    /**
     * Tests that locomotives with incomplete data are assigned default values correctly.
     */
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

    /**
     * Verifies that {@link CsvLocomotiveRepository#findById(String)} returns the correct locomotive.
     */
    @Test
    void testFindById_ReturnsCorrectLocomotive() {
        CsvLocomotiveRepository repo = new CsvLocomotiveRepository(locomotivesCsv.toString());

        Locomotive loco = repo.findById("L2");
        assertNotNull(loco);
        assertEquals("Beta", loco.getModel());
        assertFalse(loco.isElectric());
    }

    /**
     * Checks that searching for an invalid ID returns null.
     */
    @Test
    void testFindById_InvalidId_ReturnsNull() {
        CsvLocomotiveRepository repo = new CsvLocomotiveRepository(locomotivesCsv.toString());
        assertNull(repo.findById("INVALID"));
    }

    /**
     * Ensures that the list returned by {@link CsvLocomotiveRepository#findAll()} is independent
     * and external modifications do not affect the internal repository.
     */
    @Test
    void testFindAll_ReturnsIndependentList() {
        CsvLocomotiveRepository repo = new CsvLocomotiveRepository(locomotivesCsv.toString());

        List<Locomotive> all = repo.findAll();
        int originalSize = all.size();
        all.clear(); // clearing the list should not affect the internal repository

        assertEquals(originalSize, repo.findAll().size(),
                "The list returned by findAll() should be an independent copy");
    }
}


