package main.java.pt.ipp.isep.dei.domain;

class InventoryManager {
    private Warehouse warehouse;

    // Método para adicionar uma caixa ao estoque
    public void addBox(Box box, String warehouseId, int aisle, int bay) {
        // Lógica para adicionar o box ao warehouse, localizando o bay específico
    }

    // Método para despachar uma caixa do estoque
    public Box dispatch(String sku, int qty) {
        // Lógica para encontrar a caixa com SKU e quantidade solicitada
        return null; // Retorne a caixa ou null, conforme sua lógica
    }

    // Método para relocar uma caixa dentro do estoque
    public void relocateBox(String boxId, String newWarehouseId, int newAisle, int newBay) {
        // Lógica para localizar a caixa e movê-la para a nova localização
    }
}


