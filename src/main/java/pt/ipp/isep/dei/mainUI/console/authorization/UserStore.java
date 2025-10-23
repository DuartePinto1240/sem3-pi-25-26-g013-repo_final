package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserStore {
    private Set<User> store = new HashSet();

    public User create(String name, String email, String password) {
        return new User(new Email(email), new Password(password), name);
    }

    public boolean add(User user) {
        return user != null && !this.exists(user) ? this.store.add(user) : false;
    }


    public Set<User> getAll() {
        return Collections.unmodifiableSet(this.store);
    }


    public Optional<User> getById(String email) {
        return this.getById(new Email(email));
    }

    public Optional<User> getById(Email email) {
        for(User user : this.store) {
            if (user.hasId(email)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }


    public boolean exists(User user) {
        return this.store.contains(user);
    }
}

