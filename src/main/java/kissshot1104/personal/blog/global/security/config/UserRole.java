package kissshot1104.personal.blog.global.security.config;

import lombok.Getter;


@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    PAID("ROLE_PAID");

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
