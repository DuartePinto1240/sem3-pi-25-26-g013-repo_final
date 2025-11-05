package test.java.pt.ipp.isep.dei.domain;

import main.java.pt.ipp.isep.dei.domain.Item;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Item}.
 * <p>
 * These tests verify the creation of Item objects, the correctness of getters,
 * the string representation, and the behavior with identical or negative values.
 * </p>
 */
public class ItemTest {

    /**
     * Verifies that the constructor initializes all fields correctly
     * and getters return the expected values.
     */
    @Test
    public void testConstructorAndGetters() {
        String sku = "SKU001";
        String name = "Produto Teste";
        String category = "Categoria A";
        String unit = "kg";
        double volume = 0.45;
        double unitWeight = 2.3;

        Item item = new Item(sku, name, category, unit, volume, unitWeight);

        assertEquals(sku, item.getSku());
        assertEquals(name, item.getName());
        assertEquals(category, item.getCategory());
        assertEquals(unit, item.getUnit());
        assertEquals(volume, item.getVolume());
        assertEquals(unitWeight, item.getUnitWeight());
    }

    /**
     * Ensures that the {@link Item#toString()} method includes all fields.
     */
    @Test
    public void testToStringContainsAllFields() {
        Item item = new Item("SKU999", "Caixa Madeira", "Materiais", "un", 1.2, 4.8);

        String text = item.toString();

        assertTrue(text.contains("SKU999"));
        assertTrue(text.contains("Caixa Madeira"));
        assertTrue(text.contains("Materiais"));
        assertTrue(text.contains("un"));
        assertTrue(text.contains("1.2"));
        assertTrue(text.contains("4.8"));
    }

    /**
     * Verifies that two different Item objects with the same data
     * have identical field values, even without overriding equals().
     */
    @Test
    public void testDifferentObjectsWithSameData() {
        Item i1 = new Item("SKU100", "Produto", "Categoria", "kg", 0.3, 1.5);
        Item i2 = new Item("SKU100", "Produto", "Categoria", "kg", 0.3, 1.5);

        assertEquals(i1.getSku(), i2.getSku());
        assertEquals(i1.getName(), i2.getName());
        assertEquals(i1.getVolume(), i2.getVolume());
    }

    /**
     * Ensures that negative values for volume and unitWeight
     * are stored as-is in the Item object.
     */
    @Test
    public void testNegativeValuesAreStoredAsIs() {
        Item item = new Item("SKU_NEG", "ErroTeste", "Debug", "unit", -1.0, -5.0);

        assertEquals(-1.0, item.getVolume());
        assertEquals(-5.0, item.getUnitWeight());
    }
}

