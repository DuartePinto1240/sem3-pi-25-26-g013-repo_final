import main.java.pt.ipp.isep.dei.domain.Box;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BoxTest {

    @Test
    public void testBoxCreation() {
        Box box = new Box("BOX001", "SKU001", 10, LocalDate.of(2025, 12, 31), LocalDateTime.of(2025, 7, 5, 15, 42, 0));

        assertEquals("BOX001", box.getBoxId());
        assertEquals("SKU001", box.getSku());
        assertEquals(10, box.getQuantity());
        assertEquals(LocalDate.of(2025, 12, 31), box.getExpiryDate());
        assertEquals(LocalDateTime.of(2025, 7, 5, 15, 42, 0), box.getReceivedAt());
    }

    @Test
    public void testNonPerishableBox() {
        Box box = new Box("BOX002", "SKU002", 5, null, LocalDateTime.of(2025, 8, 22, 12, 30, 0));

        assertNull(box.getExpiryDate()); // A caixa não é perecível
        assertNotNull(box.getReceivedAt());
    }
}

