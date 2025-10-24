package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.mainUI.console.authorization.AuthFacade;
import main.java.pt.ipp.isep.dei.mainUI.console.authorization.UserSession;

/**
 * Repository class that acts as the data access layer for authentication and authorization concerns.
 * <p>
 * It isolates the application's domain/controller logic from the specific
 * implementation details of user management, leveraging the {@link AuthFacade}.
 * </p>
 */
public class AuthenticationRepository {

    /** The facade providing access to user authentication and authorization functionalities. */
    private final AuthFacade authenticationFacade;

    /**
     * Constructs an {@code AuthenticationRepository} and initializes the underlying {@link AuthFacade}.
     */
    public AuthenticationRepository() {
        authenticationFacade = new AuthFacade();
    }

    /**
     * Attempts to log in a user with the provided credentials.
     *
     * @param email the user's email/ID
     * @param pwd the user's password
     * @return {@code true} if the login was successful, {@code false} otherwise
     */
    public boolean doLogin(String email, String pwd) {
        return authenticationFacade.doLogin(email, pwd).isLoggedIn();
    }

    /**
     * Logs out the current user, invalidating the session.
     */
    public void doLogout() {
        authenticationFacade.doLogout();
    }

    /**
     * Retrieves the current user session instance.
     *
     * @return the current {@link UserSession}
     */
    public UserSession getCurrentUserSession() {
        return authenticationFacade.getCurrentUserSession();
    }

    /**
     * Adds a new role definition to the system.
     *
     * @param id the unique ID of the role
     * @param description the description of the role
     * @return {@code true} if the role was added successfully, {@code false} otherwise
     */
    public boolean addUserRole(String id, String description) {
        return authenticationFacade.addUserRole(id, description);
    }

    /**
     * Creates a new user and assigns them a specified role.
     *
     * @param name the user's name
     * @param email the user's email/ID
     * @param pwd the user's password
     * @param roleId the ID of the role to assign
     * @return {@code true} if the user and role were added successfully, {@code false} otherwise
     */
    public boolean addUserWithRole(String name, String email, String pwd, String roleId) {
        return authenticationFacade.addUserWithRole(name, email, pwd, roleId);
    }
}