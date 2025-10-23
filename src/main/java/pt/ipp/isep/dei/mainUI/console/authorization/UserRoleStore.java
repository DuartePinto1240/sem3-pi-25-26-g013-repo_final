package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserRoleStore {
    private Set<UserRole> store = new HashSet();

    public UserRole create(String id, String description) {
        return new UserRole(id, description);
    }

    public boolean add(UserRole role) {
        return role != null && !this.exists(role) ? this.store.add(role) : false;
    }

    public boolean remove(UserRole role) {
        return role != null ? this.store.remove(role) : false;
    }

    public Set<UserRole> getAll() {
        return Collections.unmodifiableSet(this.store);
    }

    public Optional<UserRole> getById(String id) {
        for(UserRole role : this.store) {
            if (role.hasId(id)) {
                return Optional.of(role);
            }
        }

        return Optional.empty();
    }

    public boolean exists(String id) {
        Optional<UserRole> result = this.getById(id);
        return result.isPresent();
    }

    public boolean exists(UserRole role) {
        return this.store.contains(role);
    }
}

