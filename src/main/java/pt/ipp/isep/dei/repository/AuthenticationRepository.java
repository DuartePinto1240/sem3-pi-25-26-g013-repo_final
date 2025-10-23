package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.mainUI.console.authorization.AuthFacade;
import main.java.pt.ipp.isep.dei.mainUI.console.authorization.UserSession;

public class AuthenticationRepository {
    private final AuthFacade authenticationFacade;

    public AuthenticationRepository() {
        authenticationFacade = new AuthFacade();
    }

    public boolean doLogin(String email, String pwd) {
        return authenticationFacade.doLogin(email, pwd).isLoggedIn();
    }

    public void doLogout() {
        authenticationFacade.doLogout();
    }

    public UserSession getCurrentUserSession() {
        return authenticationFacade.getCurrentUserSession();
    }

    public boolean addUserRole(String id, String description) {
        return authenticationFacade.addUserRole(id, description);
    }

    public boolean addUserWithRole(String name, String email, String pwd, String roleId) {
        return authenticationFacade.addUserWithRole(name, email, pwd, roleId);
    }
}
