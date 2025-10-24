package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Optional;

/**
 * Provides a simplified interface (Facade pattern) for core authentication
 * and authorization functionalities, managing {@link UserSession},
 * {@link UserRoleStore}, and {@link UserStore}.
 * <p>
 * This class handles operations like adding users, assigning roles, logging in,
 * and managing the current user session.
 * </p>
 */
public class AuthFacade {
    /** The current user session instance. */
    private UserSession userSession = new UserSession();

    /** Store for managing user roles. */
    private UserRoleStore roles = new UserRoleStore();

    /** Store for managing users. */
    private UserStore users = new UserStore();

    /**
     * Constructs a new AuthFacade.
     */
    public AuthFacade() {
    }

    /**
     * Creates and adds a new user role to the role store.
     *
     * @param id the unique identifier of the role
     * @param description the human-readable description of the role
     * @return {@code true} if the role was successfully added, {@code false} otherwise
     */
    public boolean addUserRole(String id, String description) {
        UserRole role = this.roles.create(id, description);
        return this.roles.add(role);
    }


    /**
     * Creates a new user and attempts to assign a role to them before adding them to the user store.
     *
     * @param name the user's name
     * @param email the user's email (used as unique ID)
     * @param pwd the user's password
     * @param roleId the ID of the role to assign
     * @return {@code true} if the role exists and the user was successfully added, {@code false} otherwise
     */
    public boolean addUserWithRole(String name, String email, String pwd, String roleId) {
        Optional<UserRole> roleResult = this.roles.getById(roleId);
        if (!roleResult.isPresent()) {
            return false;
        } else {
            User user = this.users.create(name, email, pwd);
            user.addRole((UserRole)roleResult.get());
            return this.users.add(user);
        }
    }


    /**
     * Attempts to log in a user with the provided credentials.
     * <p>
     * If successful, a new {@link UserSession} is created and set as the current session.
     * </p>
     *
     * @param email the user's email
     * @param password the user's password
     * @return the current {@link UserSession} (either a new authenticated one or the existing unauthenticated one)
     */
    public UserSession doLogin(String email, String password) {
        Optional<User> result = this.users.getById(email);
        if (result.isPresent()) {
            User user = (User)result.get();
            if (user.hasPassword(password)) {
                this.userSession = new UserSession(user);
            }
        }

        return this.userSession;
    }

    /**
     * Logs out the current user, invalidating the current session.
     */
    public void doLogout() {
        this.userSession.doLogout();
    }

    /**
     * Gets the current user session.
     *
     * @return the current {@link UserSession}
     */
    public UserSession getCurrentUserSession() {
        return this.userSession;
    }
}