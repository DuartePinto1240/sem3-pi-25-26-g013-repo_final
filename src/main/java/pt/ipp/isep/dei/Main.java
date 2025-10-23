package main.java.pt.ipp.isep.dei;

import main.java.pt.ipp.isep.dei.mainUI.console.menu.MainMenuUI;

public class Main {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.run();

        try {
            MainMenuUI menu = new MainMenuUI();
            menu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
