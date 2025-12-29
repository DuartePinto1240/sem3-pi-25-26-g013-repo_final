package main.java.pt.ipp.isep.dei.mainUI.console.menu;

import main.java.pt.ipp.isep.dei.ui.USLP03UI;
import main.java.pt.ipp.isep.dei.ui.USLP07UI;
import main.java.pt.ipp.isep.dei.ui.USLP08UI;
import main.java.pt.ipp.isep.dei.ui.USLP09UI;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class LAPR3MenuUI implements Runnable {

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("Estimated Travel Time - USLP03", new USLP03UI()));
        options.add(new MenuItem("Train Schedule - USLP07", new USLP07UI()));
        options.add(new MenuItem("Route Planner (Pending Freights) - USLP08", new USLP08UI()));
        options.add(new MenuItem("Assemble Train - USLP09", new USLP09UI()));

        int option;
        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- LAPR3 MENU (USLP) -------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
