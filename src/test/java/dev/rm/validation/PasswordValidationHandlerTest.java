package dev.rm.validation;

import org.junit.jupiter.api.Test;

import dev.rm.model.User;
import dev.rm.utils.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidationHandlerTest {

    private final PasswordValidationHandler passwordValidationHandler = new PasswordValidationHandler();

    @Test
    public void testHandleValidPassword() {
        User user = User.builder()
                .password(PasswordUtil.hashPassword("password123"))
                .build();

        assertDoesNotThrow(() -> passwordValidationHandler.handle(user));
    }

    @Test
    public void testHandleNullPassword() {
        User user = User.builder()
                .password(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordValidationHandler.handle(user);
        });

        assertEquals("Password must be at least 6 characters long", exception.getMessage());
    }

    @Test
    public void testHandleShortPassword() {
        User user = User.builder()
                .password("123")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordValidationHandler.handle(user);
        });

        assertEquals("Password must be at least 6 characters long", exception.getMessage());
    }

    @Test
    public void testHandleExactLengthPassword() {
        User user = User.builder()
                .password(PasswordUtil.hashPassword("123456"))
                .build();

        assertDoesNotThrow(() -> passwordValidationHandler.handle(user));
    }
}
