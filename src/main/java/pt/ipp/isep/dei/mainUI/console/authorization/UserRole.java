package main.java.pt.ipp.isep.dei.mainUI.console.authorization;


import java.util.Objects;
import main.java.pt.ipp.isep.dei.util.StringUtils;

/**
 * Represents a role within the application for authorization purposes.
 * <p>
 * Each role is uniquely identified by an {@code id} (e.g., "ADMIN", "WAREHOUSE_OPERATOR")
 * and has a human-readable {@code description}.
 * </p>
 * <p>
 * Two roles are considered equal if they have the same ID, regardless of their description.
 * </p>
 */
public class UserRole {

    /** The unique identifier of the role, stored in uppercase. */
    private String id;

    /** The human-readable description of the role. */
    private String description;

    /**
     * Constructs a {@code UserRole} object.
     *
     * @param id the unique identifier for the role (cannot be blank)
     * @param description the description of the role (cannot be blank)
     * @throws IllegalArgumentException if the id or description is blank.
     */
    public UserRole(String id, String description) {
        if (!StringUtils.isBlank(id) && !StringUtils.isBlank(description)) {
            this.id = this.extractId(id);
            this.description = description;
        } else {
            throw new IllegalArgumentException("UserRole id and/or description cannot be blank.");
        }
    }

    /**
     * Extracts and normalizes the ID string by trimming whitespace and converting to uppercase.
     *
     * @param id the raw ID string
     * @return the normalized ID
     */
    private String extractId(String id) {
        return id.trim().toUpperCase();
    }

    /**
     * Gets the unique identifier of the role.
     *
     * @return the role ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the description of the role.
     *
     * @return the role description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Changes the description of the role.
     *
     * @param newDescription the new description (cannot be blank)
     * @return {@code true} if the description was successfully changed, {@code false} otherwise
     */
    public boolean changeDescription(String newDescription) {
        if (StringUtils.isBlank(newDescription)) {
            return false;
        } else {
            this.description = newDescription;
            return true;
        }
    }

    /**
     * Checks if this role's ID matches the provided ID string (case-insensitive and trimmed).
     *
     * @param id the ID string to check against
     * @return {@code true} if the IDs match, {@code false} otherwise
     */
    public boolean hasId(String id) {
        return StringUtils.isBlank(id) ? false : this.id.equals(this.extractId(id));
    }

    /**
     * Computes the hash code for this {@code UserRole}, based only on the ID.
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id.hashCode();
        return hash;
    }

    /**
     * Compares this {@code UserRole} object to the specified object.
     * Two roles are considered equal if they have the same ID.
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
            UserRole obj = (UserRole)o;
            return Objects.equals(this.id, obj.id);
        }
    }

    /**
     * Returns a string representation of the {@code UserRole}.
     *
     * @return a string in the format "ID - Description"
     */
    public String toString() {
        return String.format("%s - %s", this.id, this.description);
    }
}