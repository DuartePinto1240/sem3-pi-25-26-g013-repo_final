package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link TravelTimeResult} class.
 * <p>
 * These tests validate that the {@code TravelTimeResult} class correctly stores and
 * presents the results of a calculated train journey, including total travel time,
 * locomotive used, route line, and individual travel sections.
 * </p>
 */
public class TravelTimeResultTest {

    private Station porto;
    private Station lisboa;
    private LineSegment segment1;
    private LineSegment segment2;
    private Locomotive loco;
    private RailwayLine line;
    private TravelSection section1;
    private TravelSection section2;
    private List<TravelSection> sections;

    /**
     * Sets up a consistent test environment before each test case.
     * <p>
     * Creates two main stations (Porto and Lisboa), two intermediate line segments,
     * one locomotive, a full {@link RailwayLine}, and corresponding {@link TravelSection}
     * objects representing the travel route.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        porto = new Station("ST01", "Porto");
        lisboa = new Station("ST02", "Lisboa");

        // Create two line segments representing the Porto–Lisboa route via Coimbra
        segment1 = new LineSegment(porto, new Station("STX", "Coimbra"), 120.0, 160.0, true);
        segment2 = new LineSegment(new Station("STX", "Coimbra"), lisboa, 193.0, 160.0, true);

        loco = new Locomotive("L001", "Siemens Vectron", 160.0, 85.0, true);

        // Create a railway line composed of both segments
        line = new RailwayLine(List.of(segment1, segment2));

        // Create travel sections based on the line segments
        section1 = new TravelSection(segment1, 150.0, 60.0);
        section2 = new TravelSection(segment2, 130.0, 90.0);
        sections = List.of(section1, section2);
    }

    /**
     * Verifies that the {@link TravelTimeResult} constructor correctly stores
     * all provided arguments (travel sections, total time, locomotive, and line).
     */
    @Test
    public void testConstructor_ShouldStoreAllValuesCorrectly() {
        TravelTimeResult result = new TravelTimeResult(sections, 150.0, loco, line);

        assertNotNull(result);
        assertEquals(loco, getField(result, "locomotive"));
        assertEquals(line, getField(result, "line"));
        assertEquals(sections, getField(result, "travelSections"));
        assertEquals(150.0, getField(result, "totalTimeMinutes"));
    }

    /**
     * Tests the {@link TravelTimeResult#printResult()} method to ensure
     * that travel time is formatted and displayed correctly in hours and minutes.
     * <p>
     * Example: 125 minutes should be printed as "2 horas e 5 minutos".
     * </p>
     */
    @Test
    public void testPrintResult_ShouldDisplayHoursAndMinutesCorrectly() {
        TravelTimeResult result = new TravelTimeResult(sections, 125.0, loco, line);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        result.printResult();

        System.setOut(originalOut);

        String output = outputStream.toString();

        // 125 minutes = 2 hours and 5 minutes
        assertTrue(output.contains("2 horas e 5 minutos"));
    }

    /**
     * Utility method for reading private fields without complex reflection frameworks.
     *
     * @param obj       the object whose private field should be accessed
     * @param fieldName the name of the field to retrieve
     * @return the value of the field, or {@code null} if access fails
     */
    private Object getField(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            fail("Error accessing field " + fieldName + ": " + e.getMessage());
            return null;
        }
    }
}



