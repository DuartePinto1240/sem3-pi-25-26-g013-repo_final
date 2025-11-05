package main.java.pt.ipp.isep.dei.mainUI.console.menu;

import main.java.pt.ipp.isep.dei.mainUI.console.authorization.AuthenticationUI;
import main.java.pt.ipp.isep.dei.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Console User Interface (UI) that presents the initial main menu of the application.
 * <p>
 * Its primary function is to offer the option to **Log In** via the {@link AuthenticationUI}.
 * </p>
 */
public class MainMenuUI implements Runnable {

    /**
     * Constructs the main menu UI.
     */
    public MainMenuUI() {

    }

    /**
     * Executes the main menu loop.
     * <p>
     * Displays the menu options and starts the selected UI action.
     * The loop continues until the user selects the exit option (-1).
     * </p>
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Do Login", new AuthenticationUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);

    }
}