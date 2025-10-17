package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Quarantine;
import main.java.pt.ipp.isep.dei.domain.ReturnItem;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class QuarantineTest {

    @Test
    void testAddAndNextFollowLifoOrder() {
        Quarantine quarantine = new Quarantine();
        ReturnItem older = new ReturnItem("RET001", "SKU1", 5, "damaged",
                LocalDateTime.parse("2025-09-15T10:00:00"), null);
        ReturnItem newer = new ReturnItem("RET002", "SKU2", 3, "cycle-count",
                LocalDateTime.parse("2025-09-22T10:00:00"), null);

        quarantine.add(older);
        quarantine.add(newer);

        ReturnItem first = quarantine.next();
        assertEquals("RET002", first.getReturnId());
        assertFalse(quarantine.isEmpty());
    }

    @Test
    void testIsEmptyAfterProcessing() {
        Quarantine quarantine = new Quarantine();
        ReturnItem item = new ReturnItem("RET003", "SKU3", 5, "damaged",
                LocalDateTime.now(), null);
        quarantine.add(item);

        assertFalse(quarantine.isEmpty());
        quarantine.next();
        assertTrue(quarantine.isEmpty());
    }
}
