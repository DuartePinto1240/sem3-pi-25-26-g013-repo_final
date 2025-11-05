package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CsvReaderUtil}.
 * <p>
 * These tests verify the correct behavior of reading CSV files, including:
 * </p>
 * <ul>
 *     <li>Reading valid CSV data into a list of String arrays.</li>
 *     <li>Ignoring empty lines in the CSV.</li>
 *     <li>Throwing an exception when the file does not exist.</li>
 * </ul>
 */
class CsvReaderUtilTest {

    private Path tempCsv;

    /**
     * Sets up a temporary CSV file before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary CSV file
        tempCsv = Files.createTempFile("test", ".csv");
        String csvContent = "id,name,weight\n" +
                "1,Apple,0.3\n" +
                "2,Banana,0.2\n" +
                "\n" +  // empty line to test ignore
                "3,Orange,0.4\n";

        Files.write(tempCsv, csvContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Deletes the temporary CSV file after each test.
     */
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempCsv);
    }

    /**
     * Verifies that a valid CSV file is read correctly, excluding the header.
     */
    @Test
    void testReadCsvFile_ReadsValidFileCorrectly() {
        List<String[]> records = CsvReaderUtil.readCsvFile(tempCsv.toString());

        assertEquals(3, records.size(), "Should read 3 lines (excluding header and empty lines)");

        assertArrayEquals(new String[]{"1", "Apple", "0.3"}, records.get(0));
        assertArrayEquals(new String[]{"2", "Banana", "0.2"}, records.get(1));
        assertArrayEquals(new String[]{"3", "Orange", "0.4"}, records.get(2));
    }

    /**
     * Verifies that empty lines in the CSV file are ignored.
     */
    @Test
    void testReadCsvFile_IgnoresEmptyLines() {
        List<String[]> records = CsvReaderUtil.readCsvFile(tempCsv.toString());
        // Empty line should not be included
        assertEquals(3, records.size());
    }

    /**
     * Ensures that attempting to read a non-existent CSV file throws a RuntimeException.
     */
    @Test
    void testReadCsvFile_FileNotFoundThrowsException() {
        String invalidPath = "ficheiro_inexistente.csv";

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> CsvReaderUtil.readCsvFile(invalidPath));

        assertTrue(ex.getMessage().contains("Ficheiro não encontrado"));
    }
}


