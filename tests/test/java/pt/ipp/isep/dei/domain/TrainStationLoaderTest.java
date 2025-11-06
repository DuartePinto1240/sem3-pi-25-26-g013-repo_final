package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TrainStationLoaderTest {

    private TrainStationLoader loader;
    private Path tempFile;

    @BeforeEach
    void setup() throws IOException {
        loader = new TrainStationLoader();
        tempFile = Files.createTempFile("stations_test", ".csv");
    }

    void writeCSV(String content) throws IOException {
        try (FileWriter fw = new FileWriter(tempFile.toFile())) {
            fw.write(content);
        }
    }

    @Test
    void testLoadTrainStationsWithCommaDelimiter() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Porto Campanhã,41.1456,-8.6077,true,true,false
                """);

        List<TrainStation> result = loader.loadTrainStations(tempFile.toString());

        assertEquals(1, result.size());
        assertEquals("PT", result.get(0).getCountry());
        assertEquals("Porto Campanhã", result.get(0).getStation());
        assertTrue(result.get(0).isCity());
    }

    @Test
    void testLoadTrainStationsWithSemicolonDelimiter() throws IOException {
        writeCSV("""
                country;time_zone;time_zone_group;station;latitude;longitude;is_city;is_main_station;is_airport
                SE;UTC+1;Group2;Stockholm Central;59.3293;18.0686;true;true;false
                """);

        List<TrainStation> result = loader.loadTrainStations(tempFile.toString());

        assertEquals(1, result.size());
        assertEquals("SE", result.get(0).getCountry());
    }

    @Test
    void testIgnoreInvalidLines() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,IncompleteLine
                """);

        List<TrainStation> result = loader.loadTrainStations(tempFile.toString());

        assertTrue(result.isEmpty());
    }

    @Test
    void testLoadTrainStationsByCountry() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Porto Campanhã,41.1456,-8.6077,true,true,false
                ES,UTC+1,Group2,Madrid Atocha,40.4065,-3.6891,true,true,false
                PT,UTC,Group1,Lisboa Santa Apolónia,38.7139,-9.1225,true,true,false
                """);

        List<TrainStation> result = loader.loadTrainStationsByCountry(tempFile.toString(), "PT");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getCountry().equals("PT")));
    }
}
