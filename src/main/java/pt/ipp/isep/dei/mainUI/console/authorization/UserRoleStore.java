package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Repository (Store) class responsible for managing and persisting {@link UserRole} objects.
 * <p>
 * It ensures that no duplicate roles are added and provides methods for
 * creation, retrieval, addition, and removal of roles.
 * </p>
 */
public class UserRoleStore {

    /** The internal set storing the unique user roles. */
    private Set<UserRole> store = new HashSet();

    /**
     * Creates a new {@link UserRole} instance.
     *
     * @param id the unique identifier for the role
     * @param description the description of the role
     * @return the newly created {@link UserRole}
     */
    public UserRole create(String id, String description) {
        return new UserRole(id, description);
    }

    /**
     * Adds a {@link UserRole} to the store if it is not null and does not already exist.
     *
     * @param role the role to add
     * @return {@code true} if the role was successfully added, {@code false} otherwise
     */
    public boolean add(UserRole role) {
        return role != null && !this.exists(role) ? this.store.add(role) : false;
    }

    /**
     * Removes a {@link UserRole} from the store.
     *
     * @param role the role to remove
     * @return {@code true} if the role was present and removed, {@code false} otherwise
     */
    public boolean remove(UserRole role) {
        return role != null ? this.store.remove(role) : false;
    }

    /**
     * Retrieves an unmodifiable set of all {@link UserRole}s in the store.
     *
     * @return an unmodifiable {@link Set} of roles
     */
    public Set<UserRole> getAll() {
        return Collections.unmodifiableSet(this.store);
    }

    /**
     * Retrieves a {@link UserRole} by its ID.
     *
     * @param id the ID of the role to find
     * @return an {@link Optional} containing the role if found, or an empty {@link Optional} otherwise
     */
    public Optional<UserRole> getById(String id) {
        for(UserRole role : this.store) {
            if (role.hasId(id)) {
                return Optional.of(role);
            }
        }

        return Optional.empty();
    }

    /**
     * Checks if a role with the given ID exists in the store.
     *
     * @param id the ID to check
     * @return {@code true} if a role with the ID exists, {@code false} otherwise
     */
    public boolean exists(String id) {
        Optional<UserRole> result = this.getById(id);
        return result.isPresent();
    }

    /**
     * Checks if the given {@link UserRole} instance exists in the store.
     * <p>
     * This relies on the {@link UserRole#equals(Object)} method, which compares IDs.
     * </p>
     *
     * @param role the role instance to check
     * @return {@code true} if the role (by ID) exists, {@code false} otherwise
     */
    public boolean exists(UserRole role) {
        return this.store.contains(role);
    }
}