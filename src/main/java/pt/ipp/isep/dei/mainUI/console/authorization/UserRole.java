package main.java.pt.ipp.isep.dei.mainUI.console.authorization;


import java.util.Objects;
import main.java.pt.ipp.isep.dei.util.StringUtils;

public class UserRole {
    private String id;
    private String description;

    public UserRole(String id, String description) {
        if (!StringUtils.isBlank(id) && !StringUtils.isBlank(description)) {
            this.id = this.extractId(id);
            this.description = description;
        } else {
            throw new IllegalArgumentException("UserRole id and/or description cannot be blank.");
        }
    }

    private String extractId(String id) {
        return id.trim().toUpperCase();
    }

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean changeDescription(String newDescription) {
        if (StringUtils.isBlank(newDescription)) {
            return false;
        } else {
            this.description = newDescription;
            return true;
        }
    }

    public boolean hasId(String id) {
        return StringUtils.isBlank(id) ? false : this.id.equals(this.extractId(id));
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
            UserRole obj = (UserRole)o;
            return Objects.equals(this.id, obj.id);
        }
    }

    public String toString() {
        return String.format("%s - %s", this.id, this.description);
    }
}

