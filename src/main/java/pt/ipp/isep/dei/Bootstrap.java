package main.java.pt.ipp.isep.dei;

import main.java.pt.ipp.isep.dei.controller.AuthenticationController;
import main.java.pt.ipp.isep.dei.repository.AuthenticationRepository;
import main.java.pt.ipp.isep.dei.repository.Repositories;


/**
 * Initializes the system with essential startup data.
 * <p>
 * This class is responsible for performing initial setup tasks, such as
 * creating default user roles and administrative users.
 * </p>
 */
public class Bootstrap implements Runnable {


    /**
     * Executes the bootstrap process.
     */
    public void run() {
        addUsers();
    }


    /**
     * Creates and adds the default user roles and the initial administrative user
     * to the authentication repository.
     */
    private void addUsers() {
        //get authentication repository
        AuthenticationRepository authenticationRepository = Repositories.getInstance().getAuthenticationRepository();

        authenticationRepository.addUserRole(AuthenticationController.ROLE_ADMIN, AuthenticationController.ROLE_ADMIN);

        authenticationRepository.addUserWithRole("Admin", "admin@isep.pt", "admin", AuthenticationController.ROLE_ADMIN);
    }
}