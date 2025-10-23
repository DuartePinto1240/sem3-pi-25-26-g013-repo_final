package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Collections;
import java.util.List;
import main.java.pt.ipp.isep.dei.dto.UserRoleDTO;

public class UserSession {
    private User user = null;

    public UserSession() {
        this.user = null;
    }

    public UserSession(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Argument cannot be null.");
        } else {
            this.user = user;
        }
    }

    public void doLogout() {
        this.user = null;
    }

    public boolean isLoggedIn() {
        return this.user != null;
    }


    public List<UserRoleDTO> getUserRoles() {
        if (this.isLoggedIn()) {
            UserRoleMapper mapper = new UserRoleMapper();
            return mapper.toDTO(this.user.getRoles());
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
