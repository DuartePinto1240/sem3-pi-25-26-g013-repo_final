package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import main.java.pt.ipp.isep.dei.util.ObjectUtils;
import main.java.pt.ipp.isep.dei.util.StringUtils;

public class User {
    private Email id;
    private Password password;
    private String name;
    private Set<UserRole> roles = new HashSet();

    public User(Email id, Password pwd, String name) {
        if (ObjectUtils.allNotNull(new Object[]{id, pwd}) && !StringUtils.isBlank(name)) {
            this.id = id;
            this.password = pwd;
            this.name = name.trim();
        } else {
            throw new IllegalArgumentException("User cannot have an id, password or name as null/blank.");
        }
    }

    public Email getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasPassword(String pwd) {
        return this.password.checkPassword(pwd);
    }

    public boolean addRole(UserRole role) {
        return role != null ? this.roles.add(role) : false;
    }

    public boolean hasId(Email id) {
        return this.id.equals(id);
    }


    public List<UserRole> getRoles() {
        List<UserRole> list = new ArrayList();

        for(UserRole role : this.roles) {
            list.add(role);
        }

        return Collections.unmodifiableList(list);
    }


    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id.hashCode();
        return hash;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (this.getClass() != o.getClass()) {
            return false;
        } else {
            User obj = (User)o;
            return Objects.equals(this.id, obj.id);
        }
    }
}

