package dev.rm.utils;

import org.junit.jupiter.api.Test;

import dev.rm.model.Role;
import dev.rm.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    @Test
    public void testValidateUserWithNullUsername() {
        User user = User.builder()
                .username(null)
                .email("user@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserValidation.validateUser(user);
        });

        assertEquals("Username is required.", exception.getMessage());
    }

    @Test
    public void testValidateUserWithEmptyUsername() {
        User user = User.builder()
                .username(" ")
                .email("user@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserValidation.validateUser(user);
        });

        assertEquals("Username is required.", exception.getMessage());
    }

    @Test
    public void testValidateUserWithNullEmail() {
        User user = User.builder()
                .username("user")
                .email(null)
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserValidation.validateUser(user);
        });

        assertEquals("Valid email is required.", exception.getMessage());
    }

    @Test
    public void testValidateUserWithInvalidEmail() {
        User user = User.builder()
                .username("user")
                .email("invalid-email")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserValidation.validateUser(user);
        });

        assertEquals("Valid email is required.", exception.getMessage());
    }

    @Test
    public void testValidateUserWithShortPassword() {
        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password("123")
                .role(Role.USER)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UserValidation.validateUser(user);
        });

        assertEquals("Password must be at least 6 characters long.", exception.getMessage());
    }

    @Test
    public void testValidateUserWithValidUser() {
        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        assertDoesNotThrow(() -> UserValidation.validateUser(user));
    }
}
