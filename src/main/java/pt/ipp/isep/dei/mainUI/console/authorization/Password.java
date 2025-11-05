package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Objects;
import main.java.pt.ipp.isep.dei.util.StringUtils;

/**
 * Represents a value object for a user's password.
 * <p>
 * This class primarily ensures that the password string is not blank upon creation
 * and provides a method for checking against an input string.
 * </p>
 */
public class Password {

    /** The stored password string. */
    private final String password;

    /**
     * Constructs a {@code Password} object, validating the provided string.
     *
     * @param password the password string
     * @throws IllegalArgumentException if the password is invalid (e.g., blank or null)
     */
    public Password(String password) {
        if (!validate(password)) {
            throw new IllegalArgumentException("Invalid password.");
        }
        this.password = password;
    }

    /**
     * Validates the password string.
     *
     * @param password the password string to validate
     * @return {@code true} if the password is valid (not blank), {@code false} otherwise
     */
    private boolean validate(String password) {
        return !StringUtils.isBlank(password);
    }

    /**
     * Checks if the provided input string matches the stored password.
     *
     * @param input the string input to check against
     * @return {@code true} if the input matches the stored password, {@code false} otherwise
     */
    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    /**
     * Computes the hash code for this {@code Password} based on the password string.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(password);
    }

    /**
     * Compares this {@code Password} object to the specified object.
     * Two {@code Password} objects are considered equal if they contain the same password string.
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are the same; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password other = (Password) o;
        return Objects.equals(this.password, other.password);
    }
}
