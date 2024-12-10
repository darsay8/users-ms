package dev.rm.factory;

import dev.rm.model.Role;
import dev.rm.model.User;
import dev.rm.utils.PasswordUtil;

public class UserFactory {
    public static User createUser(String username, String email, String password, Role role) {
        return User.builder()
                .username(username)
                .email(email)
                .password(PasswordUtil.hashPassword(password))
                .role(role)
                .build();
    }
}
