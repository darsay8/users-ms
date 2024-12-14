package dev.rm.validation;

import org.junit.jupiter.api.Test;

import dev.rm.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class EmailValidationHandlerTest {

    private final EmailValidationHandler emailValidationHandler = new EmailValidationHandler();

    @Test
    public void testHandleValidEmail() {
        User user = User.builder()
                .email("user@example.com")
                .build();

        // This should not throw any exception
        assertDoesNotThrow(() -> emailValidationHandler.handle(user));
    }

    @Test
    public void testHandleNullEmail() {
        User user = User.builder()
                .email(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailValidationHandler.handle(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testHandleEmailWithoutAtSymbol() {
        User user = User.builder()
                .email("user-example.com")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailValidationHandler.handle(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testHandleEmailWithEmptyString() {
        User user = User.builder()
                .email(" ")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailValidationHandler.handle(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }
}
