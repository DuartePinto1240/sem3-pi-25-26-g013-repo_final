package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USEI15Controller;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.dto.RiskPathResult;
import main.java.pt.ipp.isep.dei.dto.RiskPathSuccessDTO;
import main.java.pt.ipp.isep.dei.dto.RiskPathFailureDTO;

import java.util.Scanner;

public class USEI15UI implements Runnable {

    private final USEI15Controller controller;

    // No parameters needed here anymore
    public USEI15UI() {
        this.controller = new USEI15Controller();
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- USEI15: Calculate Risk-Minimizing Path ---");

        System.out.print("Enter Source Station ID: ");
        String srcId = sc.nextLine().trim();

        System.out.print("Enter Target Station ID: ");
        String destId = sc.nextLine().trim();

        try {
            // Controller handles logic and graph building
            RiskPathResult result = controller.calculateRiskPath(srcId, destId);

            System.out.println("\n--- Temporal Analysis ---");
            System.out.println("Execution Time: " + result.getExecutionTimeMs() + " ms");
            System.out.println("Complexity: " + result.getComplexityAnalysis());
            System.out.println("-------------------------\n");

            if (result.isSuccess()) {
                RiskPathSuccessDTO success = (RiskPathSuccessDTO) result;
                if (success.getPath().isEmpty()) {
                    System.out.println("No path exists between these stations.");
                } else {
                    System.out.println("### Path Found ###");
                    System.out.printf("Total Risk Cost: %.2f\n", success.getTotalCost());
                    System.out.println("Sequence:");
                    int step = 1;
                    for (Station s : success.getPath()) {
                        System.out.printf("%d. %s (%s)\n", step++, s.getName(), s.getId());
                    }
                }
            } else {
                RiskPathFailureDTO fail = (RiskPathFailureDTO) result;
                System.out.println("!!! CALCULATION FAILED !!!");
                System.out.println(fail.getMessage());
                System.out.println("Negative Cycle Elements:");
                for (Station s : fail.getNegativeCycle()) {
                    System.out.print(s.getId() + " -> ");
                }
                System.out.println("REPEAT");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}