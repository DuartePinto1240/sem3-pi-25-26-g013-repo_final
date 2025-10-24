package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Repository (Store) class responsible for managing and persisting {@link User} objects.
 * <p>
 * It ensures that users are uniquely identified by their {@link Email} ID
 * and provides methods for creation, retrieval, and addition of users.
 * </p>
 */
public class UserStore {

    /** The internal set storing the unique user objects. */
    private Set<User> store = new HashSet();

    /**
     * Creates a new {@link User} instance by constructing its {@link Email} and {@link Password} value objects.
     *
     * @param name the name of the user
     * @param email the email address (unique ID) of the user
     * @param password the password of the user
     * @return the newly created {@link User}
     */
    public User create(String name, String email, String password) {
        return new User(new Email(email), new Password(password), name);
    }

    /**
     * Adds a {@link User} to the store if it is not null and does not already exist (based on ID).
     *
     * @param user the user to add
     * @return {@code true} if the user was successfully added, {@code false} otherwise
     */
    public boolean add(User user) {
        return user != null && !this.exists(user) ? this.store.add(user) : false;
    }


    /**
     * Retrieves an unmodifiable set of all {@link User}s in the store.
     *
     * @return an unmodifiable {@link Set} of users
     */
    public Set<User> getAll() {
        return Collections.unmodifiableSet(this.store);
    }


    /**
     * Retrieves a {@link User} by their email address string.
     *
     * @param email the email address (ID) of the user to find
     * @return an {@link Optional} containing the user if found, or an empty {@link Optional} otherwise
     */
    public Optional<User> getById(String email) {
        return this.getById(new Email(email));
    }

    /**
     * Retrieves a {@link User} by their {@link Email} object.
     *
     * @param email the {@link Email} ID of the user to find
     * @return an {@link Optional} containing the user if found, or an empty {@link Optional} otherwise
     */
    public Optional<User> getById(Email email) {
        for(User user : this.store) {
            if (user.hasId(email)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }


    /**
     * Checks if the given {@link User} instance exists in the store.
     * <p>
     * This relies on the {@link User#equals(Object)} method, which compares email IDs.
     * </p>
     *
     * @param user the user instance to check
     * @return {@code true} if the user (by ID) exists, {@code false} otherwise
     */
    public boolean exists(User user) {
        return this.store.contains(user);
    }
}
