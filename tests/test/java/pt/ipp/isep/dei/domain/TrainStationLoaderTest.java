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
    void testLoadValidStation() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Porto Campanhã,41.1456,-8.6077,true,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertEquals(1, result.size());
        TrainStation station = result.get(0);
        assertEquals("PT", station.getCountry());
        assertEquals("UTC", station.getTimeZone());
        assertEquals("Group1", station.getTimeZoneGroup());
        assertEquals("Porto Campanhã", station.getStation());
        assertEquals(41.1456, station.getLatitude(), 0.0001);
        assertEquals(-8.6077, station.getLongitude(), 0.0001);
        assertTrue(station.isCity());
        assertTrue(station.isMainStation());
        assertFalse(station.isAirport());
    }

    @Test
    void testLoadMultipleStations() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Porto Campanhã,41.1456,-8.6077,true,true,false
                ES,UTC+1,Group2,Madrid Atocha,40.4065,-3.6891,true,true,false
                PT,UTC,Group1,Lisboa Santa Apolónia,38.7139,-9.1225,true,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertEquals(3, result.size());
    }

    @Test
    void testRejectIncompleteLine() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,IncompleteLine
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertTrue(result.isEmpty());
    }

    @Test
    void testRejectMissingRequiredFields() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                ,,Group1,Porto,41.1456,-8.6077,true,true,false
                PT,UTC,,Porto,41.1456,-8.6077,true,true,false
                PT,UTC,Group1,,41.1456,-8.6077,true,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertEquals(0, result.size());
    }

    @Test
    void testRejectInvalidCoordinates() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Invalid Lat,91.0,-8.6077,true,true,false
                PT,UTC,Group1,Invalid Lon,41.1456,181.0,true,true,false
                PT,UTC,Group1,Invalid Both,-91.0,-181.0,true,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertEquals(0, result.size());
    }

    @Test
    void testRejectInvalidNumberFormat() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Porto,NOT_A_NUMBER,-8.6077,true,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertTrue(result.isEmpty());
    }

    @Test
    void testHandleQuotedFields() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,"Porto, Campanhã",41.1456,-8.6077,true,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertEquals(1, result.size());
        assertEquals("Porto, Campanhã", result.get(0).getStation());
    }

    @Test
    void testMixValidAndInvalidLines() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Porto Campanhã,41.1456,-8.6077,true,true,false
                INVALID_LINE
                ES,UTC+1,Group2,Madrid Atocha,40.4065,-3.6891,true,true,false
                PT,UTC,Group1,Invalid,999.0,-8.6077,true,true,false
                FR,UTC+1,Group3,Paris Gare du Nord,48.8809,2.3553,true,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertEquals(3, result.size());
    }

    @Test
    void testBooleanParsing() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,UTC,Group1,Station1,41.1456,-8.6077,true,false,true
                PT,UTC,Group1,Station2,41.1456,-8.6077,false,true,false
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertEquals(2, result.size());
        assertTrue(result.get(0).isCity());
        assertFalse(result.get(0).isMainStation());
        assertTrue(result.get(0).isAirport());

        assertFalse(result.get(1).isCity());
        assertTrue(result.get(1).isMainStation());
        assertFalse(result.get(1).isAirport());
    }

    @Test
    void testEmptyFile() throws IOException {
        writeCSV("""
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                """);

        List<TrainStation> result = loader.load(tempFile.toString());

        assertTrue(result.isEmpty());
    }
}