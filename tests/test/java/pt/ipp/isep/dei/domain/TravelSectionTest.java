package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.LineSegment;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.domain.TravelSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe TravelSection.
 */
public class TravelSectionTest {

    private Station origin;
    private Station destination;
    private LineSegment segment;

    @BeforeEach
    public void setUp() {
        origin = new Station("ST001", "Porto", 41.15, -8.61);
        destination = new Station("ST002", "Lisboa", 38.72, -9.14);
        // ✅ Agora com o argumento electrified (true/false)
        segment = new LineSegment(origin, destination, 313.0, 160.0, true);
    }

    @Test
    public void testConstructorAndGetters_ShouldReturnCorrectValues() {
        TravelSection section = new TravelSection(segment, 120.0, 156.5);

        assertEquals(segment, section.getSegment());
        assertEquals(120.0, section.getEffectiveSpeedKmH());
        assertEquals(156.5, section.getSectionTimeMinutes());
    }





    @Test
    public void testSegmentReferenceIntegrity_ShouldBeSameInstance() {
        TravelSection section = new TravelSection(segment, 100.0, 188.0);
        assertSame(segment, section.getSegment());
    }
}

