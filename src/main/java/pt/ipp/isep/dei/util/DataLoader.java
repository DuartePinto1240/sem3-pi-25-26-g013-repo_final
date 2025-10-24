package main.java.pt.ipp.isep.dei.util;

import main.java.pt.ipp.isep.dei.domain.*;
import java.util.*;

/**
 * Utility class responsible for loading all necessary domain data
 * from CSV files and performing initial validation.
 * <p>
 * This class uses {@link CsvLoader} to read the files and {@link Validator}
 * to check for data integrity and consistency across all entities.
 * </p>
 */
public class DataLoader {

    /**
     * Loads all required domain entities (Bays, Wagons, Orders, Items, Returns)
     * and validates them.
     *
     * @return A {@link Map} containing all validated domain lists, keyed by their type name (e.g., "bays", "wagons").
     * Returns {@code null} if validation fails.
     */
    public static Map<String, Object> loadAndValidate() {
        CsvLoader loader = new CsvLoader();
        Validator validator = new Validator();

        List<Bay> bays = loader.loadBays("sem3-pi-25-26-g013-repo/train_station_dataset_v2/train_station_dataset/bays.csv");
        List<Wagon> wagons = loader.loadWagons("sem3-pi-25-26-g013-repo/train_station_dataset_v2/train_station_dataset/wagons.csv");
        List<Order> orders = loader.loadOrders(
                "sem3-pi-25-26-g013-repo/train_station_dataset_v2/train_station_dataset/orders.csv",
                "sem3-pi-25-26-g013-repo/train_station_dataset_v2/train_station_dataset/order_lines.csv"
        );
        List<Item> items = loader.loadItems("sem3-pi-25-26-g013-repo/train_station_dataset_v2/train_station_dataset/items.csv");
        List<ReturnItem> returns = loader.loadReturns("sem3-pi-25-26-g013-repo/train_station_dataset_v2/train_station_dataset/returns.csv");

        boolean valid = validator.validateItems(items) &
                validator.validateBays(bays) &
                validator.validateWagons(wagons, items) &
                validator.validateOrders(orders, items) &
                validator.validateReturns(returns, items);

        if (!valid) {
            System.err.println("❌ Erros detetados durante a validação. Execução terminada.");
            return null;
        }

        System.out.println("✅ Todos os ficheiros carregados e validados com sucesso.");
        System.out.printf("Bays: %d | Wagons: %d | Orders: %d | Items: %d | Returns: %d\n",
                bays.size(), wagons.size(), orders.size(), items.size(), returns.size());

        Map<String, Object> data = new HashMap<>();
        data.put("bays", bays);
        data.put("wagons", wagons);
        data.put("orders", orders);
        data.put("items", items);
        data.put("returns", returns);
        return data;
    }
}