package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USEI14Controller;

import java.util.Scanner;

public class USEI14UI implements Runnable {

    private static final String STATIONS_FILE = "train_station_dataset_v2/stations.csv";
    private static final String LINES_FILE = "train_station_dataset_v2/lines.csv";

    private final USEI14Controller controller = new USEI14Controller();

    @Override
    public void run() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Source station ID: ");
        String srcId = sc.nextLine();

        System.out.print("Destination station ID: ");
        String dstId = sc.nextLine();

        try {
            long start = System.nanoTime();
            double maxFlow = controller.maximumThroughput(
                    STATIONS_FILE,
                    LINES_FILE,
                    srcId,
                    srcId
            );
            long end = System.nanoTime();

            System.out.println("\n=== USEI14 – Maximum Throughput ===");
            System.out.println("Source: " + srcId);
            System.out.println("Destination: " + dstId);
            System.out.println("Maximum Throughput: " + maxFlow);
            System.out.println("Algorithm: Edmonds-Karp");
            System.out.println("Complexity: O(V * E^2)");
            System.out.printf("Execution time: %.3f ms%n",
                    (end - start) / 1_000_000.0);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}