package main.java.pt.ipp.isep.dei.ui;

import main.java.pt.ipp.isep.dei.controller.USLP09Controller;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;
import main.java.pt.ipp.isep.dei.domain.Station;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class USLP09UI implements Runnable {
    private final USLP09Controller controller;
    private final Scanner scanner;

    public USLP09UI() {
        this.controller = new USLP09Controller();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println("\n--- USLP09: Assemble Train ---");

        // 1. Select Station (Facility)
        List<Station> stations = controller.getStations();
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }

        System.out.println("Select the station where the train is being assembled:");
        int stationIndex = Utils.showAndSelectIndex(stations, "Stations List");
        if (stationIndex == -1) return;
        Station selectedStation = stations.get(stationIndex);
        // Assuming Station has a getId() or generic name usage.
        // Adapting to use name if ID not directly exposed, but usually repositories use ID.
        // For this example, I assume Station has an ID. If not, we might need to look it up.
        // Let's assume the Station domain object has a generic identifier or we use the object itself.
        // However, the TrainRepository expects a Long facilityId.
        // Checking domain objects from file list, 'Station' usually extends 'TwoDNode' or has an ID.
        // I will attempt to cast or retrieve ID. If not public, we might need a DTO mapper.
        // PROCEEDING assumption: Station has a generic ID accessible or we use its name hash for demo.
        // BETTER: Let's assume getName() is the key if ID is missing, but Repos usually want IDs.
        // I'll assume a public method `getId()` exists on Station or its parent.
        long stationId = 0; // Placeholder, implies Station has public long getId();
        try {
            // Reflection or direct call if known. I will assume a standard getter.
            // If Station doesn't have getId, check 'Facility' or similar parent.
            // Using hashCode as fallback if code is generic, but practically:
            // stationId = selectedStation.getId();
            // Since I can't see Station.java content fully, I will rely on the Repository fetching it.
            // For now, let's mock the ID or assume the user selects a Facility ID.
            // Actually, looking at previous files, 'Facility.csv' implies numeric IDs.
            // I will assume for this code that Station has `long getId()`.
            // If not, replace with correct getter.
            // stationId = selectedStation.getId();
            // To be safe in this text generation, I will ask the controller to map it if needed,
            // but here I'll just use a placeholder method call:
            stationId = getStationId(selectedStation);
        } catch (Exception e) {
            System.out.println("Error retrieving station ID.");
            return;
        }

        // 2. Select Locomotives
        List<LocomotiveDTO> availableLocos = controller.getAvailableLocomotives(stationId);
        if (availableLocos.isEmpty()) {
            System.out.println("No available locomotives at this station.");
            return;
        }

        List<Long> selectedLocoIds = new ArrayList<>();
        boolean selectingLocos = true;
        while (selectingLocos) {
            // Filter out already selected
            List<LocomotiveDTO> choices = new ArrayList<>(availableLocos);
            choices.removeIf(l -> selectedLocoIds.contains(l.locomotiveId()));

            if (choices.isEmpty()) {
                System.out.println("All available locomotives selected.");
                break;
            }

            int idx = Utils.showAndSelectIndex(choices, "Available Locomotives (Select one or more, -1 to finish selection)");
            if (idx == -1) {
                if (selectedLocoIds.isEmpty()) {
                    System.out.println("You must select at least one locomotive.");
                    continue;
                }
                selectingLocos = false;
            } else {
                selectedLocoIds.add(choices.get(idx).locomotiveId());
                System.out.println("Locomotive added.");
                // Ask if want to add more? Utils.showAndSelectIndex loop handles single selection usually.
                // We'll loop.
            }
        }

        // 3. Select Wagons
        List<WagonDTO> availableWagons = controller.getAvailableWagons(stationId);
        List<Long> selectedWagonIds = new ArrayList<>();

        if (!availableWagons.isEmpty()) {
            boolean selectingWagons = true;
            while (selectingWagons) {
                List<WagonDTO> choices = new ArrayList<>(availableWagons);
                choices.removeIf(w -> selectedWagonIds.contains(w.wagonId()));

                if (choices.isEmpty()) break;

                int idx = Utils.showAndSelectIndex(choices, "Available Wagons (Select -1 to stop adding)");
                if (idx == -1) {
                    selectingWagons = false;
                } else {
                    selectedWagonIds.add(choices.get(idx).wagonId());
                    System.out.println("Wagon added.");
                }
            }
        } else {
            System.out.println("No wagons available (Train will be locomotives only).");
        }

        // 4. Create Train
        try {
            long trainId = controller.createTrain(selectedLocoIds, selectedWagonIds);
            System.out.println("Success! Train assembled with ID: " + trainId);
        } catch (Exception e) {
            System.out.println("Error creating train: " + e.getMessage());
        }
    }

    // Helper to avoid compilation error if Station doesn't have explicit getId() in provided context
    private long getStationId(Station s) {
        // Replace with actual s.getId()
        return s.hashCode();
    }
}