package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Objects;
import java.util.regex.Pattern;
import main.java.pt.ipp.isep.dei.util.StringUtils;

/**
 * Represents a value object for an email address.
 * <p>
 * This class ensures that the email address is valid upon creation
 * and provides methods for comparison and hashing based on the email string.
 * </p>
 */
public class Email {
    /** The validated email address string. */
    private String email;

    /**
     * Constructs an {@code Email} object, validating the provided string.
     *
     * @param email the email address string
     * @throws IllegalArgumentException if the email address is invalid (null, blank, or improperly formatted)
     */
    public Email(String email) {
        if (!this.validate(email)) {
            throw new IllegalArgumentException("Invalid Email Address.");
        } else {
            this.email = email;
        }
    }

    /**
     * Validates the email address. Checks if it's not blank and follows the correct format.
     *
     * @param email the email address string to validate
     * @return {@code true} if the email is valid, {@code false} otherwise
     */
    private boolean validate(String email) {
        return StringUtils.isBlank(email) ? false : this.checkFormat(email);
    }

    /**
     * Checks the format of the email address using a regular expression.
     *
     * @param email the email address string
     * @return {@code true} if the format is correct, {@code false} otherwise
     */
    private boolean checkFormat(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    /**
     * Gets the email address string.
     *
     * @return the email address
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Computes the hash code for this {@code Email} based on the email string.
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.email.hashCode();
        return hash;
    }

    /**
     * Compares this {@code Email} object to the specified object. The result is {@code true}
     * if and only if the argument is not null and is an {@code Email} object that represents
     * the same email address as this object.
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are the same; {@code false} otherwise
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (this.getClass() != o.getClass()) {
            return false;
        } else {
            Email obj = (Email)o;
            return Objects.equals(this.email, obj.email);
        }
    }

    /**
     * Returns a string representation of the {@code Email} object, which is the email address itself.
     *
     * @return the email address string
     */
    public String toString() {
        return String.format("%s", this.email);
    }
}

