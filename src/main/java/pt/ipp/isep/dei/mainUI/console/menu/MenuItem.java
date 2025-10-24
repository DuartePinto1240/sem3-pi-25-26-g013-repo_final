package main.java.pt.ipp.isep.dei.mainUI.console.menu;

import main.java.pt.ipp.isep.dei.util.StringUtils;

import java.util.Objects;

/**
 * Represents a single item in a console menu.
 * <p>
 * Each menu item has a descriptive string and a corresponding action
 * (a {@link Runnable} object, typically a console UI class) to execute
 * when the item is selected.
 * </p>
 */
public class MenuItem {

    /** The text description displayed in the menu. */
    private final String description;

    /** The runnable task (UI) to execute when the item is selected. */
    private final Runnable ui;

    /**
     * Constructs a {@code MenuItem}.
     *
     * @param description the description of the menu option (cannot be null or empty)
     * @param ui the {@link Runnable} interface to execute (cannot be null)
     * @throws IllegalArgumentException if the description is blank or the UI is null
     */
    public MenuItem(String description, Runnable ui) {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("MenuItem description cannot be null or empty.");
        }
        if (Objects.isNull(ui)) {
            throw new IllegalArgumentException("MenuItem does not support a null UI.");
        }

        this.description = description;
        this.ui = ui;
    }

    /**
     * Executes the action (UI) associated with this menu item.
     */
    public void run() {
        this.ui.run();
    }

    /**
     * Checks if this menu item's description matches the given string exactly.
     *
     * @param description the string to compare against the item's description
     * @return {@code true} if the descriptions are equal, {@code false} otherwise
     */
    public boolean hasDescription(String description) {
        return this.description.equals(description);
    }

    /**
     * Returns the description of the menu item.
     *
     * @return the description string
     */
    public String toString() {
        return this.description;
    }
}