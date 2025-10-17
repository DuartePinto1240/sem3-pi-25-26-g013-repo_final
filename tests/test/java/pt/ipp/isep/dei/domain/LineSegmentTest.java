package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.Station;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LineSegmentTest {

    // 🔹 Helper simples para criar estações falsas (mock-like)
    private Station createStation(String name) {
        return new Station(name, "City_" + name, 0, 0);
    }

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        Station s1 = createStation("A");
        Station s2 = createStation("B");

        // Act
        LineSegment segment = new LineSegment(s1, s2, 12.5, 120.0, true);

        // Assert
        assertEquals(s1, segment.getOrigin());
        assertEquals(s2, segment.getDestination());
        assertEquals(12.5, segment.getDistanceKm());
        assertEquals(120.0, segment.getMaxSpeedKmH());
        assertTrue(segment.isElectrified());
    }

    @Test
    public void testToStringFormat() {
        // Arrange
        Station origin = createStation("Lisboa");
        Station destination = createStation("Porto");
        LineSegment seg = new LineSegment(origin, destination, 313.0, 200.0, true);

        // Act
        String text = seg.toString();
        System.out.println("toString() => " + text);

        // Assert
        assertTrue(text.contains("Lisboa"));
        assertTrue(text.contains("Porto"));
        assertTrue(text.contains("km"));            // menos específico, mas robusto
        assertTrue(text.contains("200"));
        assertTrue(text.contains("electrified=true"));
    }


    // 🔸 Validação de argumentos nulos
    @Test
    public void testConstructorRejectsNullStations() {
        Station s1 = createStation("Valid");

        Exception ex1 = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(null, s1, 10.0, 100.0, true));
        assertTrue(ex1.getMessage().contains("Origin"));

        Exception ex2 = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(s1, null, 10.0, 100.0, true));
        assertTrue(ex2.getMessage().contains("Destination"));
    }

    // 🔸 Validação de distância
    @Test
    public void testConstructorRejectsNonPositiveDistance() {
        Station s1 = createStation("A");
        Station s2 = createStation("B");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(s1, s2, 0, 100.0, false));
        assertTrue(ex.getMessage().contains("Distance"));
    }

    // 🔸 Validação de velocidade máxima
    @Test
    public void testConstructorRejectsNonPositiveSpeed() {
        Station s1 = createStation("A");
        Station s2 = createStation("B");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new LineSegment(s1, s2, 10.0, -50.0, false));
        assertTrue(ex.getMessage().contains("Max Speed"));
    }

    @Test
    public void testElectrifiedFalse() {
        Station s1 = createStation("X");
        Station s2 = createStation("Y");
        LineSegment segment = new LineSegment(s1, s2, 8.0, 80.0, false);

        assertFalse(segment.isElectrified());
    }
}

