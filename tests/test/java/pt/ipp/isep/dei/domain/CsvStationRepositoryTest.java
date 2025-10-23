package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.repository.CsvStationRepository;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CsvStationRepository}.
 * <p>
 * These tests verify the behavior of loading stations from a CSV file,
 * handling missing data, and searching for stations by ID.
 * </p>
 */
class CsvStationRepositoryTest {

    private Path tempCsvFile;


    /**
     * Sets up a temporary CSV file with station data before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary CSV file
        tempCsvFile = Files.createTempFile("stations", ".csv");

        // CSV content (id,name,latitude,longitude)
        String csvContent = """
                S1,Lisboa,38.7169,-9.1399
                S2,Porto,41.1496,-8.6109
                S3,Coimbra,40.2056,-8.4196
                S4
                """;

        Files.writeString(tempCsvFile, csvContent);
    }

    /**
     * Deletes the temporary CSV file after each test.
     */
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempCsvFile);
    }

    /**
     * Verifies that a station with missing name and coordinates
     * uses default values.
     */
    @Test
    void testLoadStations_MissingNameUsesDefault() {
        CsvStationRepository repo = new CsvStationRepository(tempCsvFile.toString());

        Station s4 = repo.findById("S4");
        assertNotNull(s4, "Even incomplete lines should produce a valid station");
        assertEquals("Estacao S4", s4.getName());
        assertEquals(0.0, s4.getLatitude());
        assertEquals(0.0, s4.getLongitude());
    }

    /**
     * Verifies that searching for a non-existing station ID returns null.
     */
    @Test
    void testFindById_NotExisting_ReturnsNull() {
        CsvStationRepository repo = new CsvStationRepository(tempCsvFile.toString());
        assertNull(repo.findById("S999"));
    }
}

