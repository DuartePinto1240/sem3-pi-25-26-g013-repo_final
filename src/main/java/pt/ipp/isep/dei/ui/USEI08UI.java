package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.domain.KDTreeStations;
import main.java.pt.ipp.isep.dei.controller.USEI08Controller;
import main.java.pt.ipp.isep.dei.domain.TrainStation;
import main.java.pt.ipp.isep.dei.domain.TrainStationLoader;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Console UI for USEI08 - Search by Geographical Area.
 * <p>
 * Requests from the user a latitude/longitude rectangle and optional filters,
 * invokes the controller and prints the matching stations.
 * At the end, it also executes sample queries and prints a temporal complexity
 * analysis, according to the USEI08 specification.
 */
public class USEI08UI implements Runnable {

    private static final String CSV_PATH = "train_station_dataset_v2/train_stations_europe.csv";

    private final USEI08Controller controller;
    private final List<TrainStation> allStations;
    private final long buildTimeMs;

    /**
     * Creates the UI for USEI08.
     * <p>
     * Loads all stations from the CSV file, builds the KD-tree index
     * and instantiates the controller.
     */
    public USEI08UI() {
        List<TrainStation> loadedStations = loadStationsForEsinf();
        this.allStations = loadedStations;

        long start = System.nanoTime();
        KDTreeStations kdTreeStations = new KDTreeStations();
        kdTreeStations.buildFromStations(loadedStations);
        long end = System.nanoTime();
        this.buildTimeMs = (end - start) / 1_000_000;

        this.controller = new USEI08Controller(kdTreeStations);
    }

    /**
     * Executes the console flow for USEI08:
     * <ol>
     *     <li>Prints KD-tree index statistics</li>
     *     <li>Reads an interactive query from the user</li>
     *     <li>Executes the search and prints results</li>
     *     <li>Runs the three sample queries and prints complexity analysis</li>
     * </ol>
     */
    @Override
    public void run() {
        System.out.println("\n=== USEI08 — Search Stations by Geographical Area ===");

        System.out.println("\n🏗️  KD-tree index ready");
        System.out.printf("📊 Stations indexed: %d%n", allStations.size());
        System.out.printf("⏱️ KD-tree build time (this run): %d ms%n", buildTimeMs);

        System.out.println("\n--- INTERACTIVE QUERY --------------------------------");

        double[] latRange = readLatitudeRange();
        double[] lonRange = readLongitudeRange();

        double latMin = latRange[0];
        double latMax = latRange[1];
        double lonMin = lonRange[0];
        double lonMax = lonRange[1];

        Boolean isCityFilter = askBooleanFilter(
                "Filtrar por isCity? (S/N): ",
                "Pretende apenas estações de cidade? (S/N): ");
        Boolean isMainStationFilter = askBooleanFilter(
                "Filtrar por isMainStation? (S/N): ",
                "Pretende apenas estações principais? (S/N): ");
        String countryFilter = askCountryFilter();

        List<TrainStation> result = controller.searchStationsInArea(
                latMin, latMax,
                lonMin, lonMax,
                isCityFilter,
                isMainStationFilter,
                countryFilter
        );

        printQueryResults("INTERACTIVE QUERY", latMin, latMax, lonMin, lonMax,
                isCityFilter, isMainStationFilter, countryFilter, result, true);

        printSampleQueriesAndComplexity();

        System.out.println("\n[Enter para continuar]");
        Utils.readLineFromConsole("");
    }

    /**
     * Loads all train stations to be used in ESINF, using the same loader
     * as other use cases (e.g. USEI07).
     *
     * @return list of all stations loaded from the CSV file, or an empty list
     * if loading fails
     */
    private List<TrainStation> loadStationsForEsinf() {
        System.out.printf("A carregar estações do ficheiro: %s...%n", CSV_PATH);
        TrainStationLoader loader = new TrainStationLoader();
        List<TrainStation> stations = loader.load(CSV_PATH);

        if (stations == null || stations.isEmpty()) {
            System.out.println("⚠️ Não foi possível carregar os dados das estações. A USEI08 não pode continuar.");
            return new ArrayList<>();
        }

        return stations;
    }

    /**
     * Reads and validates a latitude range from the console.
     * The user must provide values in the interval [-90, 90] and
     * latMin must be less than or equal to latMax.
     *
     * @return an array [latMin, latMax] with a valid range
     */
    private double[] readLatitudeRange() {
        while (true) {
            double latMin = readLatitude("Latitude mínima (-90 a 90): ");
            double latMax = readLatitude("Latitude máxima (-90 a 90): ");

            if (latMin > latMax) {
                System.out.println("Intervalo de latitude inválido: mínima > máxima. Tente novamente.");
            } else {
                return new double[]{latMin, latMax};
            }
        }
    }

    /**
     * Reads and validates a longitude range from the console.
     * The user must provide values in the interval [-180, 180] and
     * lonMin must be less than or equal to lonMax.
     *
     * @return an array [lonMin, lonMax] with a valid range
     */
    private double[] readLongitudeRange() {
        while (true) {
            double lonMin = readLongitude("Longitude mínima (-180 a 180): ");
            double lonMax = readLongitude("Longitude máxima (-180 a 180): ");

            if (lonMin > lonMax) {
                System.out.println("Intervalo de longitude inválido: mínima > máxima. Tente novamente.");
            } else {
                return new double[]{lonMin, lonMax};
            }
        }
    }

    /**
     * Reads a single latitude value from the console and validates
     * that it is in the interval [-90, 90].
     *
     * @param prompt message displayed to the user
     * @return a valid latitude
     */
    private double readLatitude(String prompt) {
        while (true) {
            double value = Utils.readDoubleFromConsole(prompt);
            if (value < -90 || value > 90) {
                System.out.println("Latitude inválida. Deve estar entre -90 e 90 graus.");
            } else {
                return value;
            }
        }
    }

    /**
     * Reads a single longitude value from the console and validates
     * that it is in the interval [-180, 180].
     *
     * @param prompt message displayed to the user
     * @return a valid longitude
     */
    private double readLongitude(String prompt) {
        while (true) {
            double value = Utils.readDoubleFromConsole(prompt);
            if (value < -180 || value > 180) {
                System.out.println("Longitude inválida. Deve estar entre -180 e 180 graus.");
            } else {
                return value;
            }
        }
    }

    /**
     * Asks the user whether a boolean filter should be applied and, if so,
     * which value (true or false) it should take.
     *
     * @param askUseFilterMsg message asking if the filter should be used
     * @param askValueMsg     message asking for the filter value (S/N)
     * @return {@code Boolean.TRUE} or {@code Boolean.FALSE} if the filter is applied,
     * or {@code null} if no filter should be used
     */
    private Boolean askBooleanFilter(String askUseFilterMsg, String askValueMsg) {
        String useFilter = Utils.readLineFromConsole(askUseFilterMsg).trim().toUpperCase();
        if (!useFilter.equals("S")) {
            return null;
        }

        while (true) {
            String val = Utils.readLineFromConsole(askValueMsg).trim().toUpperCase();
            if (val.equals("S")) {
                return Boolean.TRUE;
            } else if (val.equals("N")) {
                return Boolean.FALSE;
            } else {
                System.out.println("Opção inválida. Responda com 'S' ou 'N'.");
            }
        }
    }

    /**
     * Asks the user for the country filter according to the USEI08 specification:
     * <ul>
     *     <li>PT → returns "PT"</li>
     *     <li>ES → returns "ES"</li>
     *     <li>ALL or empty → returns null (no filter applied)</li>
     * </ul>
     *
     * @return "PT", "ES" or {@code null} if all countries should be included
     */
    private String askCountryFilter() {
        System.out.println("\nFiltro de país:");
        System.out.println("  PT  - Portugal");
        System.out.println("  ES  - Espanha");
        System.out.println("  ALL - Todos os países (sem filtro)");

        while (true) {
            String country = Utils.readLineFromConsole("País (PT/ES/ALL ou vazio): ")
                    .trim()
                    .toUpperCase();

            if (country.isEmpty() || country.equals("ALL")) {
                return null;
            }

            if (country.equals("PT") || country.equals("ES")) {
                return country;
            }

            System.out.println("Opção inválida. Introduza PT, ES, ALL ou deixe vazio.");
        }
    }

    /**
     * Prints the header, filters and stations of a single query execution.
     * The list of stations may be truncated depending on the {@code showAll} flag.
     *
     * @param title         title of the query block
     * @param latMin        minimum latitude
     * @param latMax        maximum latitude
     * @param lonMin        minimum longitude
     * @param lonMax        maximum longitude
     * @param isCity        isCity filter used, or null
     * @param isMainStation isMainStation filter used, or null
     * @param country       country filter used, or null
     * @param result        list of stations returned by the query
     * @param showAll       if true, prints all stations; if false, prints only the first 10
     */
    private void printQueryResults(String title,
                                   double latMin, double latMax,
                                   double lonMin, double lonMax,
                                   Boolean isCity,
                                   Boolean isMainStation,
                                   String country,
                                   List<TrainStation> result,
                                   boolean showAll) {

        System.out.println("\n┌──────────────────────────────────────────────────────────┐");
        System.out.printf("│ %s%n", padRight(title, 58));
        System.out.println("└──────────────────────────────────────────────────────────┘");

        System.out.printf("📐 Range: lat ∈ [%.2f, %.2f], lon ∈ [%.2f, %.2f]%n",
                latMin, latMax, lonMin, lonMax);

        System.out.print("🔎 Filters: ");
        System.out.printf("isCity=%s, isMainStation=%s, country=%s%n",
                isCity == null ? "any" : isCity,
                isMainStation == null ? "any" : isMainStation,
                country == null ? "all" : country);

        System.out.printf("✅ Found %d station(s).%n", result.size());

        if (result.isEmpty()) return;

        int maxToShow = showAll ? result.size() : Math.min(10, result.size());

        System.out.println("   Stations:");
        for (int i = 0; i < maxToShow; i++) {
            TrainStation s = result.get(i);
            System.out.printf("   • %s (%s) - [lat=%.5f, lon=%.5f]%n",
                    s.getStation(),
                    s.getCountry(),
                    s.getLatitude(),
                    s.getLongitude());
        }

        if (!showAll && result.size() > maxToShow) {
            System.out.printf("   ... and %d more%n", result.size() - maxToShow);
        }
    }

    /**
     * Pads a text on the right with spaces until the given width.
     *
     * @param text  original text
     * @param width target width
     * @return the padded text
     */
    private String padRight(String text, int width) {
        if (text.length() >= width) return text;
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    /**
     * Executes and prints the three sample queries required by USEI08
     * and prints a final table summarizing the temporal complexity.
     */
    private void printSampleQueriesAndComplexity() {
        System.out.println("\n\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                 USEI08 - SAMPLE QUERIES                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        {
            double latMin = 36.0;
            double latMax = 43.0;
            double lonMin = -10.0;
            double lonMax = -5.0;
            Boolean isCity = true;
            Boolean isMainStation = null;
            String country = "PT";

            List<TrainStation> res = controller.searchStationsInArea(
                    latMin, latMax, lonMin, lonMax,
                    isCity, isMainStation, country);

            printQueryResults("QUERY 1: Portuguese cities in bounding box", latMin, latMax,
                    lonMin, lonMax, isCity, isMainStation, country, res, false);
            System.out.println("⏱️  Expected complexity: O(k + √N) average, O(N) worst-case");
        }

        {
            double latMin = 36.0;
            double latMax = 44.0;
            double lonMin = -10.0;
            double lonMax = 4.0;
            Boolean isCity = null;
            Boolean isMainStation = true;
            String country = "ES";

            List<TrainStation> res = controller.searchStationsInArea(
                    latMin, latMax, lonMin, lonMax,
                    isCity, isMainStation, country);

            printQueryResults("QUERY 2: Spanish main stations (Iberian bounding box)", latMin, latMax,
                    lonMin, lonMax, isCity, isMainStation, country, res, false);
            System.out.println("⏱️  Expected complexity: O(k + √N) average, O(N) worst-case");
        }

        {
            double latMin = 37.0;
            double latMax = 39.0;
            double lonMin = -10.0;
            double lonMax = 3.0;
            Boolean isCity = true;
            Boolean isMainStation = null;
            String country = null;

            List<TrainStation> res = controller.searchStationsInArea(
                    latMin, latMax, lonMin, lonMax,
                    isCity, isMainStation, country);

            printQueryResults("QUERY 3: PT+ES cities in latitude band [37,39]", latMin, latMax,
                    lonMin, lonMax, isCity, isMainStation, country, res, false);
            System.out.println("⏱️  Expected complexity: O(k + √N) average, O(N) worst-case");
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║            TEMPORAL COMPLEXITY ANALYSIS (USEI08)          ║");
        System.out.println("╠───────────────────────────────────────────────────────────╣");
        System.out.printf ("║ N = total stations indexed: %7d                       ║%n", allStations.size());
        System.out.println("╟───────────────────────────────────────────────────────────╢");
        System.out.println("║ KD-tree build (incremental insertion)   │ O(N log N)      ║");
        System.out.println("║ Single range search (average case)      │ O(k + √N)       ║");
        System.out.println("║ Single range search (worst case)        │ O(N)            ║");
        System.out.println("║ Filter application over results         │ O(k)            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("Where:");
        System.out.println("  N = total number of stations in the KD-tree");
        System.out.println("  k = number of stations returned by the query");
    }
}
