package test.java.pt.ipp.isep.dei.domain.USEI06;

import main.java.pt.ipp.isep.dei.domain.TimeZoneIndexSystem;
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class USEI06Test {

    private static TimeZoneIndexSystem tzSystem;
    private static List<TrainStation> allStations;

    @BeforeAll
    static void setup() {
        String csvPath = "train_station_dataset_v2/train_stations_europe.csv";

        TrainStationLoader loader = new TrainStationLoader();
        allStations = loader.load(csvPath);

        tzSystem = new TimeZoneIndexSystem(allStations);

        assertNotNull(tzSystem);
        assertTrue(tzSystem.getValidStationsCount() > 0, "Must have valid stations");
    }

    @Test
    void testValidAndRejectedCounts() {
        int valid = tzSystem.getValidStationsCount();
        int rejected = tzSystem.getRejectedStationsCount();

        assertTrue(valid > 0, "Should have valid stations");
        assertTrue(rejected >= 0, "Rejected count should be non-negative");
        assertEquals(valid + rejected, allStations.size(), "All stations must be counted");
    }

    @Test
    void testFindByLatitudeAndLongitude() {
        int testCount = Math.min(100, allStations.size());

        for (int i = 0; i < testCount; i++) {
            TrainStation st = allStations.get(i);
            if (st.getStation() != null) {
                List<TrainStation> byLat = tzSystem.findByLatitude(st.getLatitude());
                List<TrainStation> byLon = tzSystem.findByLongitude(st.getLongitude());

                assertNotNull(byLat);
                assertNotNull(byLon);
                assertTrue(byLat.contains(st));
                assertTrue(byLon.contains(st));
            }
        }
    }

    @Test
    void testInvalidStationsRejected() {
        TrainStation invalidStation1 = new TrainStation("", "('Europe/Paris',)", "CET", "StationX", 50, 10, false, false, false);
        TrainStation invalidStation2 = new TrainStation("FR", "('Europe/Paris',)", "", "StationY", 50, 10, false, false, false);
        TrainStation invalidStation3 = new TrainStation("FR", "('Europe/Paris',)", "CET", "StationZ", 100, 10, false, false, false);

        TimeZoneIndexSystem tempSystem = new TimeZoneIndexSystem(List.of(invalidStation1, invalidStation2, invalidStation3));

        assertEquals(0, tempSystem.getValidStationsCount());
        assertEquals(3, tempSystem.getRejectedStationsCount());
    }

    @Test
    void testStationDistanceAccuracy() {
        if (allStations.size() >= 2) {
            TrainStation st1 = allStations.get(0);
            TrainStation st2 = allStations.get(1);
            double distance = st1.distanceTo(st2);

            assertTrue(distance >= 0);
            assertTrue(distance < 10000);
        }
    }

    @Test
    void testDistanceSameStation() {
        if (!allStations.isEmpty()) {
            TrainStation st = allStations.get(0);
            assertEquals(0.0, st.distanceTo(st), 0.01);
        }
    }

    @Test
    void testTimeZoneIndexTreeHeight() {
        int height = tzSystem.getTimeZoneIndexHeight();
        int tzCount = tzSystem.getTimeZoneCount();
        assertTrue(height >= 0);

        if (tzCount > 0) {
            double maxHeight = 1.44 * Math.log(tzCount + 2) / Math.log(2);
            assertTrue(height <= maxHeight + 1);
        }
    }

    @Test
    void testIndexConsistencyAcrossTrees() {
        int testCount = Math.min(100, allStations.size());

        for (int i = 0; i < testCount; i++) {
            TrainStation st = allStations.get(i);
            if (st.getStation() != null) {
                List<TrainStation> byLat = tzSystem.findByLatitude(st.getLatitude());
                List<TrainStation> byLon = tzSystem.findByLongitude(st.getLongitude());
                List<TrainStation> byTzCountry = tzSystem.findByTimeZoneAndCountry(st.getTimeZoneGroup(), st.getCountry());

                assertTrue(byLat.contains(st));
                assertTrue(byLon.contains(st));
                assertTrue(byTzCountry.contains(st));
            }
        }
    }

    @Test
    void testLatitudeRangeNoMatches() {
        List<TrainStation> result = tzSystem.findByLatitudeRange(100, 200);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testWindowedQueryNonExistingGroups() {
        Map<String, Map<String, List<TrainStation>>> result = tzSystem.windowedQuery("NON_EXIST_TZ", "FAKE_ZONE");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testWindowedQueryExistingZones() {
        List<String> allTimeZones = tzSystem.getAllTimeZones();
        if (allTimeZones.size() >= 2) {
            String tz1 = allTimeZones.get(0);
            String tz2 = allTimeZones.get(1);

            Map<String, Map<String, List<TrainStation>>> windowed = tzSystem.windowedQuery(tz1, tz2);
            assertNotNull(windowed);
            assertFalse(windowed.isEmpty());

            windowed.forEach((tz, countryMap) -> countryMap.forEach((country, stations) -> {
                for (TrainStation st : stations) {
                    assertEquals(tz, st.getTimeZoneGroup());
                    assertEquals(country, st.getCountry());
                }
            }));
        }
    }

    @Test
    void testLatitudeRangeQuery() {
        List<TrainStation> range = tzSystem.findByLatitudeRange(40, 60);
        assertNotNull(range);

        for (TrainStation st : range) {
            assertTrue(st.getLatitude() >= 40 && st.getLatitude() <= 60);
        }
    }

    @Test
    void testFindByTimeZoneAndCountry() {
        List<String> timeZones = tzSystem.getAllTimeZones();
        if (!timeZones.isEmpty()) {
            String tz = timeZones.get(0);
            Map<String, List<TrainStation>> byCountry = tzSystem.findByTimeZoneGroup(tz);

            if (!byCountry.isEmpty()) {
                String country = byCountry.keySet().iterator().next();
                List<TrainStation> stations = tzSystem.findByTimeZoneAndCountry(tz, country);
                assertNotNull(stations);
                assertFalse(stations.isEmpty());

                for (TrainStation st : stations) {
                    assertEquals(tz, st.getTimeZoneGroup());
                    assertEquals(country, st.getCountry());
                }
            }
        }
    }

    @Test
    void testSortedOrderInIndexes() {
        List<String> timeZones = tzSystem.getAllTimeZones();
        if (!timeZones.isEmpty()) {
            String tz = timeZones.get(0);
            Map<String, List<TrainStation>> tzGroupMap = tzSystem.findByTimeZoneGroup(tz);

            tzGroupMap.values().forEach(list -> {
                for (int i = 1; i < list.size(); i++) {
                    assertTrue(list.get(i-1).getStation().compareTo(list.get(i).getStation()) <= 0);
                }
            });
        }
    }

    @Test
    void testDuplicateCoordinatesSorted() {
        for (int i = 0; i < Math.min(100, allStations.size()); i++) {
            TrainStation st = allStations.get(i);
            List<TrainStation> atSameCoord = tzSystem.findByLatitude(st.getLatitude());

            if (atSameCoord.size() > 1) {
                for (int j = 1; j < atSameCoord.size(); j++) {
                    assertTrue(atSameCoord.get(j-1).getStation().compareTo(atSameCoord.get(j).getStation()) <= 0);
                }
                break;
            }
        }
    }
}
