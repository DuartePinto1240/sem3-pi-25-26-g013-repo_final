package main.java.pt.ipp.isep.dei.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe utilitária responsável por carregar ficheiros CSV contendo dados de entidades do domínio
 * (como {@link Item}, {@link Bay}, {@link Wagon}, {@link Order}, {@link OrderLine} e {@link ReturnItem}).
 *
 * <p>É usada nas funcionalidades USEI01 e USEI02, oferecendo suporte automático aos delimitadores
 * {@code ','} e {@code ';'}. O delimitador é detetado automaticamente com base na primeira linha do ficheiro.</p>
 *
 * <p>Cada método devolve uma lista de objetos de domínio ou, no caso das linhas de encomenda,
 * um mapa que associa IDs de encomenda às suas respetivas linhas.</p>
 */
public class CsvLoader {

    /**
     * Deteta o delimitador usado num ficheiro CSV com base na primeira linha (header).
     * Caso o header contenha ponto e vírgula ({@code ;}), assume-se esse como delimitador.
     * Caso contrário, é usado o delimitador vírgula ({@code ,}).
     *
     * @param headerLine linha de cabeçalho do ficheiro CSV
     * @return o delimitador identificado ({@code ","} ou {@code ";"})
     */
    private String detectDelimiter(String headerLine) {
        if (headerLine.contains(";")) return ";";
        else return ",";
    }

    /**
     * Carrega uma lista de {@link Item}s a partir de um ficheiro CSV.
     * <p>Esperado formato: {@code sku;description;category;type;weight;width}</p>
     *
     * @param filePath caminho do ficheiro CSV
     * @return lista de {@link Item}s carregados
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
     * Carrega uma lista de {@link Bay}s a partir de um ficheiro CSV.
     * <p>Esperado formato: {@code warehouseId;aisle;bay;capacityBoxes}</p>
     *
     * @param filePath caminho do ficheiro CSV
     * @return lista de {@link Bay}s carregados
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
     * Carrega uma lista de {@link Wagon}s e as suas respetivas {@link Box}es a partir de um ficheiro CSV.
     * <p>Esperado formato: {@code wagonId;boxId;sku;quantity;expiryDate;receivedAt}</p>
     * <p>Os {@link Wagon}s são agrupados com base no seu ID.</p>
     *
     * @param filePath caminho do ficheiro CSV
     * @return lista de {@link Wagon}s com as {@link Box}es associadas
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
     * Carrega uma lista de {@link Order}s (encomendas), associando-as às suas respetivas {@link OrderLine}s.
     *
     * @param ordersPath      caminho do ficheiro CSV com cabeçalhos de encomenda
     * @param orderLinesPath  caminho do ficheiro CSV com as linhas de encomenda
     * @return lista de {@link Order}s completas com as suas {@link OrderLine}s
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

    /**
     * Carrega as linhas de encomenda ({@link OrderLine}) e organiza-as num mapa
     * associando cada ID de encomenda às suas linhas correspondentes.
     *
     * @param filePath caminho do ficheiro CSV com linhas de encomenda
     * @return mapa de {@code orderId -> lista de OrderLine}
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
     * Carrega uma lista de {@link ReturnItem}s (devoluções de produtos) a partir de um ficheiro CSV.
     * <p>Formato esperado: {@code returnId,sku,qty,reason,timestamp,expiryDate}</p>
     *
     * @param path caminho do ficheiro CSV de devoluções
     * @return lista de {@link ReturnItem}s carregados
     */
    public List<ReturnItem> loadReturns(String path) {
        List<ReturnItem> list = new ArrayList<>();
        try (var br = new java.io.BufferedReader(new java.io.FileReader(path))) {
            String header = br.readLine(); // ignora o cabeçalho
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

                // Lidar com data de validade opcional
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





