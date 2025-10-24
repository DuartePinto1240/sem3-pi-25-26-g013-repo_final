package main.java.pt.ipp.isep.dei.mainUI.console.menu;

import main.java.pt.ipp.isep.dei.ui.*;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Console User Interface (UI) that presents the main menu options
 * available to a user with the **Administrator/Editor** role.
 * <p>
 * This UI allows the administrator to select and execute various
 * use cases relevant to their role (e.g., USEI01, USEI02, etc.).
 * </p>
 */
public class AdminUI implements Runnable {

    /**
     * Constructs an {@code AdminUI}.
     */
    public AdminUI() {}

    /**
     * Executes the main menu loop for the Administrator role.
     * <p>
     * Displays the menu options and redirects execution to the selected use case UI.
     * The loop continues until the user selects the exit option (-1).
     * </p>
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("USEI01", new USEI01UI()));
        options.add(new MenuItem("USEI02", new USEI02UI()));
        options.add(new MenuItem("USEI03", new USEI03UI()));
        options.add(new MenuItem("USEI04", new USEI04UI()));
        options.add(new MenuItem("USEI05", new USEI05UI()));
        options.add(new MenuItem("USPL03",new USLP03UI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- EDITOR MENU -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}