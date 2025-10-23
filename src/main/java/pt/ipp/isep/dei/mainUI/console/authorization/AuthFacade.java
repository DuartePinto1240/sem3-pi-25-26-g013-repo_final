package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Optional;

public class AuthFacade {
    private UserSession userSession = new UserSession();
    private UserRoleStore roles = new UserRoleStore();
    private UserStore users = new UserStore();

    public AuthFacade() {
    }

    public boolean addUserRole(String id, String description) {
        UserRole role = this.roles.create(id, description);
        return this.roles.add(role);
    }


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

    public void doLogout() {
        this.userSession.doLogout();
    }

    public UserSession getCurrentUserSession() {
        return this.userSession;
    }
}