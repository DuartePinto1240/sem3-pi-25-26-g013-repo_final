package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Collections;
import java.util.List;
import main.java.pt.ipp.isep.dei.dto.UserRoleDTO;

/**
 * Represents the current session of an authenticated user.
 * <p>
 * This class tracks whether a user is logged in and provides access to
 * the authenticated {@link User} object and their assigned roles.
 * </p>
 */
public class UserSession {

    /** The authenticated user for this session, or null if logged out. */
    private User user = null;

    /**
     * Creates an unauthenticated session.
     */
    public UserSession() {
        this.user = null;
    }

    /**
     * Creates an authenticated session for the given user.
     *
     * @param user the user who has successfully logged in
     * @throws IllegalArgumentException if the user argument is null
     */
    public UserSession(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Argument cannot be null.");
        } else {
            this.user = user;
        }
    }

    /**
     * Logs out the user by clearing the session data.
     */
    public void doLogout() {
        this.user = null;
    }

    /**
     * Checks if a user is currently logged into this session.
     *
     * @return {@code true} if the session is authenticated, {@code false} otherwise
     */
    public boolean isLoggedIn() {
        return this.user != null;
    }

    /**
     * Gets a list of DTOs representing the roles of the logged-in user.
     *
     * @return a {@link List} of {@link UserRoleDTO}s, or an empty list if no user is logged in
     */
    public List<UserRoleDTO> getUserRoles() {
        if (this.isLoggedIn()) {
            UserRoleMapper mapper = new UserRoleMapper();
            return mapper.toDTO(this.user.getRoles());
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}