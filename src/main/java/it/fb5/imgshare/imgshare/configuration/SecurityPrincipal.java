package it.fb5.imgshare.imgshare.configuration;

import it.fb5.imgshare.imgshare.entity.User;
import java.util.Collections;
import java.util.Objects;
import org.springframework.security.core.Authentication;

public class SecurityPrincipal extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 42L;

    private long userId;

    public SecurityPrincipal(User user) {
        super(user.getUsername(), user.getPassword(), Collections.emptyList());

        this.userId = user.getId();
    }

    public static SecurityPrincipal getPrincipal(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return (SecurityPrincipal) authentication.getPrincipal();
        }

        return null;
    }

    public long getUserId() {
        return this.userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SecurityPrincipal that = (SecurityPrincipal) o;

        return userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
