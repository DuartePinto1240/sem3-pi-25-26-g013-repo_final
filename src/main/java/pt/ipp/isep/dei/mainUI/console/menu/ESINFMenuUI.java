package main.java.pt.ipp.isep.dei.mainUI.console.menu;

import main.java.pt.ipp.isep.dei.ui.*;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ESINFMenuUI implements Runnable {

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("Wagon Unloading - USEI01", new USEI01UI()));
        options.add(new MenuItem("Order Eligibility & Allocation - USEI02", new USEI02UI()));
        options.add(new MenuItem("Picking Plans - USEI03", new USEI03UI()));
        options.add(new MenuItem("Pick Path Sequencing - USEI04", new USEI04UI()));
        options.add(new MenuItem("Returns & Quarantine - USEI05", new USEI05UI()));
        options.add(new MenuItem("Time-Zone Index and Windowed Queries - USEI06", new USEI06UI()));
        options.add(new MenuItem("Build a balanced 2D-Tree - USEI07", new USEI07UI()));
        options.add(new MenuItem("Search Stations by Geographical Area - USEI08", new USEI08UI()));
        options.add(new MenuItem("Proximity Search - USEI09", new USEI09UI()));
        options.add(new MenuItem("Radius search and density summary - USEI10", new USEI10UI()));
        options.add(new MenuItem("Directed Line Upgrade Plan - USEI11", new USEI11UI()));
        options.add(new MenuItem("Minimal Backbone Network - USEI12", new USEI12UI()));
        options.add(new MenuItem("Rail Hub Centrality Analysis - USEI13", new USEI13UI()));
        options.add(new MenuItem("Maximum throughput between two Hubs - USEI14", new USEI14UI()));
        options.add(new MenuItem("Risk-Aware Shortest Paths - USEI15", new USEI15UI()));

        int option;
        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- ESINF MENU (USEI) -------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}

