package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import main.java.pt.ipp.isep.dei.util.ObjectUtils;
import main.java.pt.ipp.isep.dei.util.StringUtils;

/**
 * Represents a user within the system, identified by an {@link Email} address.
 * <p>
 * A user has a unique ID (Email), a {@link Password}, a name, and a set of
 * assigned {@link UserRole}s.
 * </p>
 * <p>
 * Users are considered equal if they have the same email ID.
 * </p>
 */
public class User {
    /** The unique ID (Email) of the user. */
    private Email id;

    /** The user's password. */
    private Password password;

    /** The name of the user. */
    private String name;

    /** The set of roles assigned to the user. */
    private Set<UserRole> roles = new HashSet();

    /**
     * Constructs a {@code User} object.
     *
     * @param id the unique email ID of the user (cannot be null)
     * @param pwd the user's password (cannot be null)
     * @param name the user's name (cannot be null or blank)
     * @throws IllegalArgumentException if the ID, password, or name is null or blank.
     */
    public User(Email id, Password pwd, String name) {
        if (ObjectUtils.allNotNull(new Object[]{id, pwd}) && !StringUtils.isBlank(name)) {
            this.id = id;
            this.password = pwd;
            this.name = name.trim();
        } else {
            throw new IllegalArgumentException("User cannot have an id, password or name as null/blank.");
        }
    }

    /**
     * Gets the unique email ID of the user.
     *
     * @return the {@link Email} ID
     */
    public Email getId() {
        return this.id;
    }

    /**
     * Gets the name of the user.
     *
     * @return the user's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Checks if the provided string matches the user's password.
     *
     * @param pwd the password string to check
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    public boolean hasPassword(String pwd) {
        return this.password.checkPassword(pwd);
    }

    /**
     * Adds a role to the user's set of roles.
     *
     * @param role the {@link UserRole} to add (must not be null)
     * @return {@code true} if the role was successfully added (was not already present), {@code false} otherwise
     */
    public boolean addRole(UserRole role) {
        return role != null ? this.roles.add(role) : false;
    }

    /**
     * Checks if this user has the given email ID.
     *
     * @param id the {@link Email} to compare with
     * @return {@code true} if the IDs are equal, {@code false} otherwise
     */
    public boolean hasId(Email id) {
        return this.id.equals(id);
    }


    /**
     * Gets an unmodifiable list of all roles assigned to the user.
     *
     * @return an unmodifiable {@link List} of {@link UserRole}s
     */
    public List<UserRole> getRoles() {
        List<UserRole> list = new ArrayList();

        for(UserRole role : this.roles) {
            list.add(role);
        }

        return Collections.unmodifiableList(list);
    }


    /**
     * Computes the hash code for this {@code User}, based only on the email ID.
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id.hashCode();
        return hash;
    }

    /**
     * Compares this {@code User} object to the specified object.
     * Two users are equal if they have the same email ID.
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
            User obj = (User)o;
            return Objects.equals(this.id, obj.id);
        }
    }
}