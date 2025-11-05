package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.repository.AuthenticationRepository;
import main.java.pt.ipp.isep.dei.repository.Repositories;
import main.java.pt.ipp.isep.dei.dto.UserRoleDTO;

import java.util.List;

/**
 * Controller class responsible for handling all user authentication-related actions.
 * <p>
 * It acts as the intermediary between the Presentation Layer (UI) and the Persistence/Domain Layer
 * (via {@link AuthenticationRepository}).
 * </p>
 */
public class AuthenticationController {

    /** Constant defining the ID for the Administrator role. */
    public static final String ROLE_ADMIN = "ADMIN";


    /** The repository used to access authentication and user data. */
    private final AuthenticationRepository authenticationRepository;

    /**
     * Constructs an {@code AuthenticationController} and retrieves the
     * necessary authentication repository instance.
     */
    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    /**
     * Attempts to log in a user with the provided email and password.
     *
     * @param email the user's email/ID
     * @param pwd the user's password
     * @return {@code true} if login is successful, {@code false} otherwise
     */
    public boolean doLogin(String email, String pwd) {
        try {
            return authenticationRepository.doLogin(email, pwd);
        } catch (IllegalArgumentException ex) {
            // Catches exceptions related to invalid email format/construction
            return false;
        }
    }

    /**
     * Retrieves the roles assigned to the currently logged-in user.
     *
     * @return a {@link List} of {@link UserRoleDTO}s if a user is logged in, or {@code null} otherwise
     */
    public List<UserRoleDTO> getUserRoles() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserSession().getUserRoles();
        }
        return null;
    }

    /**
     * Logs out the current user, invalidating the session.
     */
    public void doLogout() {
        authenticationRepository.doLogout();
    }
}