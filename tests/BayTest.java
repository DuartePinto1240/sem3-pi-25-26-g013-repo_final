import main.java.pt.ipp.isep.dei.domain.Bay;
import main.java.pt.ipp.isep.dei.domain.Box;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BayTest {

    @Test
    public void testBayCapacity() {
        Bay bay = new Bay("W1", 1, 1, 2); // Bay com capacidade para 2 caixas

        assertTrue(bay.canStoreBox(new Box("BOX001", "SKU001", 10, null, null)));
        bay.addBox(new Box("BOX001", "SKU001", 10, null, null));
        assertEquals(1, bay.getStoredBoxes().size());

        // Tentando adicionar mais uma caixa (deve falhar porque a capacidade é 2)
        bay.addBox(new Box("BOX002", "SKU002", 5, null, null));
        assertEquals(2, bay.getStoredBoxes().size());  // Não deve ter mais de 2 caixas
    }

    @Test
    public void testAddBoxToBay() {
        Bay bay = new Bay("W1", 1, 1, 10);
        Box box = new Box("BOX001", "SKU001", 10, null, null);

        bay.addBox(box);

        assertEquals(1, bay.getStoredBoxes().size());
        assertEquals(box, bay.getStoredBoxes().get(0));
    }
}
