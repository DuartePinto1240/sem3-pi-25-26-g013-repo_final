package main.java.pt.ipp.isep.dei.domain;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Loader genérico para todos os CSVs usados nas USEI01 e USEI02.
 * Suporta delimitadores ',' e ';' automaticamente.
 */
public class CsvLoader {

    // Detecta se o delimitador é ';' ou ','
    private String detectDelimiter(String headerLine) {
        if (headerLine.contains(";")) return ";";
        else return ",";
    }

    // 🔹 Carrega Items (metadados dos SKUs)
    public List<Item> loadItems(String filePath) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null) return items;
            String delimiter = detectDelimiter(header);
            String line;

            while ((line = br.readLine()) != null) {
                String[] f = line.split(delimiter);
                if (f.length < 6) continue;
                items.add(new Item(
                        f[0], f[1], f[2], f[3],
                        Double.parseDouble(f[4]), Double.parseDouble(f[5])
                ));
            }
        } catch (IOException e) {
            System.err.println("Erro a ler items: " + e.getMessage());
        }
        return items;
    }

    // 🔹 Carrega Bays (estrutura do armazém)
    public List<Bay> loadBays(String filePath) {
        List<Bay> bays = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null) return bays;
            String delimiter = detectDelimiter(header);
            String line;

            while ((line = br.readLine()) != null) {
                String[] f = line.split(delimiter);
                if (f.length < 4) continue;
                bays.add(new Bay(f[0],
                        Integer.parseInt(f[1]),
                        Integer.parseInt(f[2]),
                        Integer.parseInt(f[3])));
            }
        } catch (IOException e) {
            System.err.println("Erro a ler bays: " + e.getMessage());
        }
        return bays;
    }

    // 🔹 Carrega Wagons e suas Boxes
    public List<Wagon> loadWagons(String filePath) {
        Map<String, Wagon> wagonMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null) return new ArrayList<>();
            String delimiter = detectDelimiter(header);
            String line;

            while ((line = br.readLine()) != null) {
                String[] f = line.split(delimiter);
                if (f.length < 6) continue;

                String wagonId = f[0];
                String boxId = f[1];
                String sku = f[2];
                int qty = Integer.parseInt(f[3]);
                LocalDate expiry = f[4].isBlank() ? null : LocalDate.parse(f[4]);
                LocalDateTime received = LocalDateTime.parse(f[5]);

                Box box = new Box(boxId, sku, qty, expiry, received);

                Wagon wagon = wagonMap.computeIfAbsent(wagonId, k -> new Wagon(wagonId));
                wagon.addBox(box);
            }
        } catch (IOException e) {
            System.err.println("Erro a ler wagons: " + e.getMessage());
        }
        return new ArrayList<>(wagonMap.values());
    }

    // 🔹 Carrega Orders (cabeçalho)
    public List<Order> loadOrders(String ordersPath, String orderLinesPath) {
        List<Order> orders = new ArrayList<>();
        Map<String, List<OrderLine>> orderLinesMap = loadOrderLines(orderLinesPath);
        try (BufferedReader br = new BufferedReader(new FileReader(ordersPath))) {
            String header = br.readLine();
            if (header == null) return orders;
            String delimiter = detectDelimiter(header);
            String line;

            while ((line = br.readLine()) != null) {
                String[] f = line.split(delimiter);
                if (f.length < 3) continue;
                String orderId = f[0];
                LocalDate dueDate = LocalDate.parse(f[1].substring(0, 10)); // extrai apenas a data
                int priority = Integer.parseInt(f[2]);

                List<OrderLine> lines = orderLinesMap.getOrDefault(orderId, new ArrayList<>());
                orders.add(new Order(orderId, priority, dueDate, lines));
            }
        } catch (IOException e) {
            System.err.println("Erro a ler orders: " + e.getMessage());
        }
        return orders;
    }

    // 🔹 Carrega OrderLines (linhas das encomendas)
    private Map<String, List<OrderLine>> loadOrderLines(String filePath) {
        Map<String, List<OrderLine>> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            if (header == null) return map;
            String delimiter = detectDelimiter(header);
            String line;

            while ((line = br.readLine()) != null) {
                String[] f = line.split(delimiter);
                if (f.length < 4) continue;
                String orderId = f[0];
                int lineNo = Integer.parseInt(f[1]);
                String sku = f[2];
                int qty = Integer.parseInt(f[3]);

                map.computeIfAbsent(orderId, k -> new ArrayList<>())
                        .add(new OrderLine(lineNo, sku, qty));
            }
        } catch (IOException e) {
            System.err.println("Erro a ler order_lines: " + e.getMessage());
        }
        return map;
    }
}




