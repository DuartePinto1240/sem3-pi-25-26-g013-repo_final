package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import main.java.pt.ipp.isep.dei.controller.AuthenticationController;
import main.java.pt.ipp.isep.dei.mainUI.console.menu.AdminUI;
import main.java.pt.ipp.isep.dei.mainUI.console.menu.MenuItem;
import main.java.pt.ipp.isep.dei.util.Utils;
import main.java.pt.ipp.isep.dei.dto.UserRoleDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Console User Interface (UI) responsible for handling user authentication
 * and role selection after a successful login.
 * <p>
 * It interacts with the {@link AuthenticationController} to manage the login,
 * role retrieval, and logout processes.
 * </p>
 */
public class AuthenticationUI implements Runnable {

    /** The controller handling authentication logic. */
    private final AuthenticationController ctrl;

    /**
     * Constructs an {@code AuthenticationUI} and initializes the controller.
     */
    public AuthenticationUI() {
        ctrl = new AuthenticationController();
    }

    /**
     * Executes the authentication process: login, role selection,
     * redirection to the corresponding UI, and logout.
     */
    public void run() {
        boolean success = doLogin();

        if (success) {
            List<UserRoleDTO> roles = this.ctrl.getUserRoles();
            if ((roles == null) || (roles.isEmpty())) {
                System.out.println("No role assigned to user.");
            } else {
                UserRoleDTO role = selectsRole(roles);
                if (!Objects.isNull(role)) {
                    List<MenuItem> rolesUI = getMenuItemForRoles();
                    this.redirectToRoleUI(rolesUI, role);
                } else {
                    System.out.println("No role selected.");
                }
            }
        }
        this.logout();
    }

    /**
     * Maps the user roles to their respective starting console UIs (as {@link MenuItem}s).
     *
     * @return a list of {@link MenuItem} objects, one for each supported role.
     */
    private List<MenuItem> getMenuItemForRoles() {
        List<MenuItem> rolesUI = new ArrayList<>();
        rolesUI.add(new MenuItem(AuthenticationController.ROLE_ADMIN, new AdminUI()));


        //TODO: Complete with other user roles
        return rolesUI;
    }

    /**
     * Prompts the user for login credentials and attempts to authenticate up to 3 times.
     *
     * @return {@code true} if login is successful, {@code false} otherwise.
     */
    private boolean doLogin() {
        System.out.println("\n\n--- LOGIN UI ---------------------------");

        int maxAttempts = 3;
        boolean success = false;
        do {
            maxAttempts--;
            String id = Utils.readLineFromConsole("Enter UserId/Email: ");
            String pwd = Utils.readLineFromConsole("Enter Password: ");

            success = ctrl.doLogin(id, pwd);
            if (!success) {
                System.out.println("Invalid UserId and/or Password. \n You have  " + maxAttempts + " more attempt(s).");
            }

        } while (!success && maxAttempts > 0);
        return success;
    }

    /**
     * Logs the currently authenticated user out.
     */
    private void logout() {
        ctrl.doLogout();
    }

    /**
     * Finds and executes the main console UI associated with the selected user role.
     *
     * @param rolesUI the list of available {@link MenuItem} UIs mapped to roles.
     * @param role the {@link UserRoleDTO} selected by the user.
     */
    private void redirectToRoleUI(List<MenuItem> rolesUI, UserRoleDTO role) {
        boolean found = false;
        Iterator<MenuItem> it = rolesUI.iterator();
        while (it.hasNext() && !found) {
            MenuItem item = it.next();
            found = item.hasDescription(role.getDescription());
            if (found) {
                item.run();
            }
        }
        if (!found) {
            System.out.println("There is no UI for users with role '" + role.getDescription() + "'");
        }
    }

    /**
     * Allows the user to select one role if multiple are assigned.
     *
     * @param roles the list of {@link UserRoleDTO} assigned to the user.
     * @return the selected {@link UserRoleDTO}, or {@code null} if no role is selected.
     */
    private UserRoleDTO selectsRole(List<UserRoleDTO> roles) {
        if (roles.size() == 1) {
            return roles.get(0);
        } else {
            return (UserRoleDTO) Utils.showAndSelectOne(roles, "Select the role you want to adopt in this session:");
        }
    }
}