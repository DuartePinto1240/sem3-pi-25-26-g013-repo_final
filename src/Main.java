import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Criando o Warehouse e Bays
        Warehouse warehouse = new Warehouse("W1");

        Bay bay1 = new Bay("W1", 1, 1, 10);
        Bay bay2 = new Bay("W1", 1, 2, 10);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);

        // Criando as Caixas (Boxes)
        Box box1 = new Box("BOX00001", "SKU0022", 7, null, LocalDateTime.of(2025, 7, 5, 15, 42, 0, 0));
        Box box2 = new Box("BOX00002", "SKU0008", 26, LocalDate.of(2026, 1, 5), LocalDateTime.of(2025, 7, 21, 22, 2, 0, 0));

        // Criando o Wagon com as Caixas
        List<Box> boxes = new ArrayList<>();
        boxes.add(box1);
        boxes.add(box2);
        Wagon wagon = new Wagon("WGN001", boxes);

        // Criando o WagonUnloader e descarregando o vagão
        WagonUnloader unloader = new WagonUnloader(warehouse);
        List<Wagon> wagons = new ArrayList<>();
        wagons.add(wagon);

        // Descarregando e alocando as caixas
        unloader.unload(wagons);
    }
}

