package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe TravelTimeResult.
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

    @BeforeEach
    public void setUp() {
        porto = new Station("ST01", "Porto");
        lisboa = new Station("ST02", "Lisboa");

        // Criar dois segmentos para a linha
        segment1 = new LineSegment(porto, new Station("STX", "Coimbra"), 120.0, 160.0, true);
        segment2 = new LineSegment(new Station("STX", "Coimbra"), lisboa, 193.0, 160.0, true);

        loco = new Locomotive("L001", "Siemens Vectron", 160.0, 85.0, true);

        // Criar a linha com os dois segmentos
        line = new RailwayLine(List.of(segment1, segment2));

        // Criar secções da viagem (cada uma baseada em um segmento)
        section1 = new TravelSection(segment1, 150.0, 60.0);
        section2 = new TravelSection(segment2, 130.0, 90.0);
        sections = List.of(section1, section2);
    }

    @Test
    public void testConstructor_ShouldStoreAllValuesCorrectly() {
        TravelTimeResult result = new TravelTimeResult(sections, 150.0, loco, line);

        assertNotNull(result);
        assertEquals(loco, getField(result, "locomotive"));
        assertEquals(line, getField(result, "line"));
        assertEquals(sections, getField(result, "travelSections"));
        assertEquals(150.0, getField(result, "totalTimeMinutes"));
    }



    @Test
    public void testPrintResult_ShouldDisplayHoursAndMinutesCorrectly() {
        TravelTimeResult result = new TravelTimeResult(sections, 125.0, loco, line);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        result.printResult();

        System.setOut(originalOut);

        String output = outputStream.toString();

        // 125 minutos = 2 horas e 5 minutos
        assertTrue(output.contains("2 horas e 5 minutos"));
    }

    /**
     * Método auxiliar para ler campos privados sem reflection pesada
     */
    private Object getField(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            fail("Erro ao aceder ao campo " + fieldName + ": " + e.getMessage());
            return null;
        }
    }
}


