package main.java.pt.ipp.isep.dei;

import main.java.pt.ipp.isep.dei.controller.AuthenticationController;
import main.java.pt.ipp.isep.dei.repository.AuthenticationRepository;
import main.java.pt.ipp.isep.dei.repository.Repositories;


public class Bootstrap implements Runnable {


    public void run() {
        addUsers();
    }


    private void addUsers() {
        //get authentication repository
        AuthenticationRepository authenticationRepository = Repositories.getInstance().getAuthenticationRepository();

        authenticationRepository.addUserRole(AuthenticationController.ROLE_ADMIN, AuthenticationController.ROLE_ADMIN);

        authenticationRepository.addUserWithRole("Admin", "admin@isep.pt", "admin", AuthenticationController.ROLE_ADMIN);
    }



}
