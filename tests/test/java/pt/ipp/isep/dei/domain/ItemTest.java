package test.java.pt.ipp.isep.dei.domain;



import main.java.pt.ipp.isep.dei.domain.Item;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String sku = "SKU001";
        String name = "Produto Teste";
        String category = "Categoria A";
        String unit = "kg";
        double volume = 0.45;
        double unitWeight = 2.3;

        // Act
        Item item = new Item(sku, name, category, unit, volume, unitWeight);

        // Assert
        assertEquals(sku, item.getSku());
        assertEquals(name, item.getName());
        assertEquals(category, item.getCategory());
        assertEquals(unit, item.getUnit());
        assertEquals(volume, item.getVolume());
        assertEquals(unitWeight, item.getUnitWeight());
    }

    @Test
    public void testToStringContainsAllFields() {
        // Arrange
        Item item = new Item("SKU999", "Caixa Madeira", "Materiais", "un", 1.2, 4.8);

        // Act
        String text = item.toString();

        // Assert
        assertTrue(text.contains("SKU999"));
        assertTrue(text.contains("Caixa Madeira"));
        assertTrue(text.contains("Materiais"));
        assertTrue(text.contains("un"));
        assertTrue(text.contains("1.2"));
        assertTrue(text.contains("4.8"));
    }

    @Test
    public void testDifferentObjectsWithSameData() {
        // Arrange
        Item i1 = new Item("SKU100", "Produto", "Categoria", "kg", 0.3, 1.5);
        Item i2 = new Item("SKU100", "Produto", "Categoria", "kg", 0.3, 1.5);

        // Mesmo sem equals definido, os campos devem ser idênticos
        assertEquals(i1.getSku(), i2.getSku());
        assertEquals(i1.getName(), i2.getName());
        assertEquals(i1.getVolume(), i2.getVolume());
    }

    @Test
    public void testNegativeValuesAreStoredAsIs() {
        // Arrange
        Item item = new Item("SKU_NEG", "ErroTeste", "Debug", "unit", -1.0, -5.0);

        // Assert
        assertEquals(-1.0, item.getVolume());
        assertEquals(-5.0, item.getUnitWeight());
    }
}
