package main.java.pt.ipp.isep.dei.repository;

public class Repositories {
    private static Repositories instance;
    private final AuthenticationRepository authenticationRepository;


    private Repositories() {
        authenticationRepository = new AuthenticationRepository();

    }


    public static Repositories getInstance() {
        if (instance == null) {
            synchronized (Repositories.class) {
                instance = new Repositories();
            }
        }
        return instance;
    }


    public AuthenticationRepository getAuthenticationRepository() {
        return authenticationRepository;
    }
}
