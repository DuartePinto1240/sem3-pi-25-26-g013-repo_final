package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.repository.AuthenticationRepository;
import main.java.pt.ipp.isep.dei.repository.Repositories;
import main.java.pt.ipp.isep.dei.dto.UserRoleDTO;

import java.util.List;

public class AuthenticationController {

    public static final String ROLE_ADMIN = "ADMIN";


    private final AuthenticationRepository authenticationRepository;

    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    public boolean doLogin(String email, String pwd) {
        try {
            return authenticationRepository.doLogin(email, pwd);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public List<UserRoleDTO> getUserRoles() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserSession().getUserRoles();
        }
        return null;
    }

    public void doLogout() {
        authenticationRepository.doLogout();
    }
}
