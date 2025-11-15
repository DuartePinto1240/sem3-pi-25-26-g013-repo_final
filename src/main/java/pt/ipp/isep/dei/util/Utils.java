package main.java.pt.ipp.isep.dei.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Utility class providing common functionality for console interaction,
 * such as reading user input and presenting selection menus.
 */
public class Utils {

    /**
     * Reads a line of text input from the console after displaying a prompt.
     *
     * @param prompt the message to display to the user before reading input
     * @return the line read from the console, or {@code null} if an exception occurs
     */
    static public String readLineFromConsole(String prompt) {
        try {
            System.out.print("\n" + prompt);

            InputStreamReader converter = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(converter);

            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double readDoubleFromConsole(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = readLineFromConsole("");
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Introduza um número (double).");
            }
        }
    }

    /**
     * Displays a list of objects and prompts the user to select one of them.
     *
     * @param list the {@link List} of objects to display and select from
     * @param header the header/title to display above the list
     * @return the selected object from the list, or {@code null} if the user cancels
     */
    static public Object showAndSelectOne(List list, String header) {
        showList(list, header);
        return selectsObject(list);
    }

    /**
     * Displays a list of objects and prompts the user to select an index.
     *
     * @param list the {@link List} of objects to display and select from
     * @param header the header/title to display above the list
     * @return the zero-based index of the selected item, or -1 if the user cancels
     */
    static public int showAndSelectIndex(List list, String header) {
        showList(list, header);
        return selectsIndex(list);
    }

    /**
     * Prints the contents of a list to the console, prefixed with sequential numbers.
     * Includes a "0 - Cancel" option at the end.
     *
     * @param list the {@link List} of objects to display
     * @param header the header/title to display above the list
     */
    static public void showList(List list, String header) {
        System.out.println(header);

        int index = 0;
        for (Object o : list) {
            index++;

            System.out.println("  " + index + " - " + o.toString());
        }
        //System.out.println();
        System.out.println("  0 - Cancel");
    }

    /**
     * Prompts the user to enter a number corresponding to an item in the list
     * and returns the selected object.
     *
     * @param list the {@link List} of objects
     * @return the selected object, or {@code null} if the user chooses option 0 (cancel)
     */
    static public Object selectsObject(List list) {
        String input;
        int value;
        do {
            input = Utils.readLineFromConsole("Type your option: ");
            value = Integer.valueOf(input);
        } while (value < 0 || value > list.size());

        if (value == 0) {
            return null;
        } else {
            return list.get(value - 1);
        }
    }

    /**
     * Prompts the user to enter a number corresponding to an item in the list
     * and returns the zero-based index. Handles number format exceptions gracefully.
     *
     * @param list the {@link List} of objects
     * @return the zero-based index of the selected item, or -1 if the user chooses option 0 (cancel)
     */
    static public int selectsIndex(List list) {
        String input;
        int value;
        do {
            input = Utils.readLineFromConsole("Type your option: ");
            try {
                value = Integer.valueOf(input);
            } catch (NumberFormatException ex) {
                value = -1;
            }
        } while (value < 0 || value > list.size());

        return value - 1;
    }
}