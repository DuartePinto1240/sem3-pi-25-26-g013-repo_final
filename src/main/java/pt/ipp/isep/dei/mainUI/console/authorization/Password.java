package main.java.pt.ipp.isep.dei.mainUI.console.authorization;

import java.util.Objects;
import main.java.pt.ipp.isep.dei.util.StringUtils;

public class Password {
    private final String password;

    public Password(String password) {
        if (!validate(password)) {
            throw new IllegalArgumentException("Invalid password.");
        }
        this.password = password;
    }

    private boolean validate(String password) {
        return !StringUtils.isBlank(password);
    }

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password other = (Password) o;
        return Objects.equals(this.password, other.password);
    }
}
