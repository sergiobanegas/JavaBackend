package springskeleton.model;

import java.util.Arrays;

public enum Role {
    ADMIN, USER;

    public String roleName() {
        return "ROLE_" + this.toString();
    }

    public static boolean contains(final String role) {
        return Arrays.stream(Role.values()).anyMatch(r -> r.roleName().equals(role));
    }

}
