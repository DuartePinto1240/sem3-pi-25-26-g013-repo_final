package main.java.pt.ipp.isep.dei.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Utility class responsible for loading CSV files containing domain entity data
 * (such as {@link Item}, {@link Bay}, {@link Wagon}, {@link Order}, {@link OrderLine} and {@link ReturnItem}).
 *
 * <p>It is used in functionalities USEI01 and USEI02, offering automatic support for
 * both {@code ','} and {@code ';'} delimiters. The delimiter is automatically detected
 * based on the file's first line.</p>
 *
 * <p>Each method returns a list of domain objects or, in the case of order lines,
 * a map associating order IDs with their respective lines.</p>
 */
public class CsvLoader {

    /**
     * Detects the delimiter used in a CSV file based on the first line (header).
     * If the header contains a semicolon ({@code ;}), that is assumed to be the delimiter.
     * Otherwise, the comma delimiter ({@code ,}) is used.
     *
     * @param headerLine the header line of the CSV file
     * @return the identified delimiter ({@code ","} or {@code ";"})
     */
    private String detectDelimiter(String headerLine) {
        if (headerLine.contains(";")) return ";";
        else return ",";
    }

    /**
     * Loads a list of {@link Item}s from a CSV file.
     * <p>Expected format: {@code sku;description;category;type;weight;width}</p>
     *
     * @param filePath path to the CSV file
     * @return list of loaded {@link Item}s
     */
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

    /**
     * Loads a list of {@link Bay}s from a CSV file.
     * <p>Expected format: {@code warehouseId;aisle;bay;capacityBoxes}</p>
     *
     * @param filePath path to the CSV file
     * @return list of loaded {@link Bay}s
     */
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

    /**
     * Loads a list of {@link Wagon}s and their respective {@link Box}es from a CSV file.
     * <p>Expected format: {@code wagonId;boxId;sku;quantity;expiryDate;receivedAt}</p>
     * <p>{@link Wagon}s are grouped based on their ID.</p>
     *
     * @param filePath path to the CSV file
     * @return list of {@link Wagon}s with associated {@link Box}es
     */
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

    /**
     * Loads a list of {@link Order}s, associating them with their respective {@link OrderLine}s.
     *
     * @param ordersPath      path to the CSV file with order headers
     * @param orderLinesPath  path to the CSV file with order lines
     * @return list of complete {@link Order}s with their {@link OrderLine}s
     */
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
                LocalDate dueDate = LocalDate.parse(f[1].substring(0, 10)); // extracts only the date
                int priority = Integer.parseInt(f[2]);

                List<OrderLine> lines = orderLinesMap.getOrDefault(orderId, new ArrayList<>());
                orders.add(new Order(orderId, priority, dueDate, lines));
            }
        } catch (IOException e) {
            System.err.println("Erro a ler orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Loads order lines ({@link OrderLine}) and organizes them into a map
     * associating each order ID with its corresponding lines.
     *
     * @param filePath path to the CSV file with order lines
     * @return map of {@code orderId -> list of OrderLine}
     */
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

    /**
     * Loads a list of {@link ReturnItem}s (product returns) from a CSV file.
     * <p>Expected format: {@code returnId,sku,qty,reason,timestamp,expiryDate}</p>
     *
     * @param path path to the returns CSV file
     * @return list of loaded {@link ReturnItem}s
     */
    public List<ReturnItem> loadReturns(String path) {
        List<ReturnItem> list = new ArrayList<>();
        try (var br = new java.io.BufferedReader(new java.io.FileReader(path))) {
            String header = br.readLine(); // ignores the header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] f = line.split(",", -1);
                if (f.length < 6) continue;

                String returnId = f[0].trim();
                String sku = f[1].trim();
                int qty = Integer.parseInt(f[2].trim());
                String reason = f[3].trim();
                java.time.LocalDateTime timestamp = java.time.LocalDateTime.parse(f[4].trim());

                // Handles optional expiry date
                java.time.LocalDateTime expiry = null;
                String expiryRaw = f[5].trim();
                if (!expiryRaw.isEmpty()) {
                    try {
                        expiry = java.time.LocalDate.parse(expiryRaw).atStartOfDay();
                    } catch (Exception e) {
                        System.err.println("⚠️ Invalid expiry date for " + returnId + ": " + expiryRaw);
                    }
                }

                list.add(new ReturnItem(returnId, sku, qty, reason, timestamp, expiry));
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading returns.csv: " + e.getMessage());
        }
        return list;
    }
}