package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.util.CsvReaderUtil;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderUtilTest {

    private Path tempCsv;

    @BeforeEach
    void setUp() throws IOException {
        // Cria um ficheiro temporário com conteúdo CSV
        tempCsv = Files.createTempFile("test", ".csv");
        String csvContent = "id,name,weight\n" +
                "1,Apple,0.3\n" +
                "2,Banana,0.2\n" +
                "\n" +  // linha vazia para testar o ignore
                "3,Orange,0.4\n";

        Files.write(tempCsv, csvContent.getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempCsv);
    }

    @Test
    void testReadCsvFile_ReadsValidFileCorrectly() {
        List<String[]> records = CsvReaderUtil.readCsvFile(tempCsv.toString());

        assertEquals(3, records.size(), "Deve ler 3 linhas (excluindo o cabeçalho e vazias)");

        assertArrayEquals(new String[]{"1", "Apple", "0.3"}, records.get(0));
        assertArrayEquals(new String[]{"2", "Banana", "0.2"}, records.get(1));
        assertArrayEquals(new String[]{"3", "Orange", "0.4"}, records.get(2));
    }

    @Test
    void testReadCsvFile_IgnoresEmptyLines() {
        List<String[]> records = CsvReaderUtil.readCsvFile(tempCsv.toString());
        // A linha vazia não deve ser incluída
        assertEquals(3, records.size());
    }

    @Test
    void testReadCsvFile_FileNotFoundThrowsException() {
        String invalidPath = "ficheiro_inexistente.csv";

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> CsvReaderUtil.readCsvFile(invalidPath));

        assertTrue(ex.getMessage().contains("Ficheiro não encontrado"));
    }


}

