package main.java.pt.ipp.isep.dei.mainUI.console.menu;

import main.java.pt.ipp.isep.dei.ui.*;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdminUI implements Runnable {

    public AdminUI() {}

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("USEI01", new USEI01UI()));
        options.add(new MenuItem("USEI02", new USEI02UI()));
        options.add(new MenuItem("USEI03", new USEI03UI()));
        options.add(new MenuItem("USEI04", new USEI04UI()));
        options.add(new MenuItem("USEI05", new USEI05UI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- EDITOR MENU -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
