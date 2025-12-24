package main.java.pt.ipp.isep.dei.mainUI.console.menu;

import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdminUI implements Runnable {

    public AdminUI() {}

    @Override
    public void run() {

        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("ESINF - User Stories (USEI)", new ESINFMenuUI()));
        options.add(new MenuItem("LAPR3 - User Stories (USLP)", new LAPR3MenuUI()));

        int option;
        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- ADMIN MENU -------------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
