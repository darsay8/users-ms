package dev.rm.validation;

import org.junit.jupiter.api.Test;

import dev.rm.model.Role;
import dev.rm.model.User;
import dev.rm.utils.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class ValidationChainTest {

    @Test
    public void testValidateWithAllValidHandlers() {

        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        List<ValidationHandler> handlers = Arrays.asList(
                new EmailValidationHandler(),
                new PasswordValidationHandler(),
                new RoleValidationHandler());
        ValidationChain validationChain = new ValidationChain(handlers);

        assertDoesNotThrow(() -> validationChain.validate(user));
    }

    @Test
    public void testValidateWithEmailValidationFailure() {

        User user = User.builder()
                .username("user")
                .email("user-example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        List<ValidationHandler> handlers = Arrays.asList(
                new EmailValidationHandler(),
                new PasswordValidationHandler(),
                new RoleValidationHandler());
        ValidationChain validationChain = new ValidationChain(handlers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            validationChain.validate(user);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Invalid email format", exception.getCause().getMessage());
    }

    @Test
    public void testValidateWithPasswordValidationFailure() {

        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password("123")
                .role(Role.USER)
                .build();
        List<ValidationHandler> handlers = Arrays.asList(
                new EmailValidationHandler(),
                new PasswordValidationHandler(),
                new RoleValidationHandler());
        ValidationChain validationChain = new ValidationChain(handlers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            validationChain.validate(user);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Password must be at least 6 characters long", exception.getCause().getMessage());
    }

    @Test
    public void testValidateWithRoleValidationFailure() {
        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(null)
                .build();
        List<ValidationHandler> handlers = Arrays.asList(
                new EmailValidationHandler(),
                new PasswordValidationHandler(),
                new RoleValidationHandler());
        ValidationChain validationChain = new ValidationChain(handlers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            validationChain.validate(user);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Role is required", exception.getCause().getMessage());
    }

    @Test
    public void testValidateWithMultipleFailures() {
        User user = User.builder()
                .username("user")
                .email("user-example.com")
                .password("123")
                .role(Role.USER)
                .build();

        List<ValidationHandler> handlers = Arrays.asList(
                new EmailValidationHandler(),
                new PasswordValidationHandler(),
                new RoleValidationHandler());
        ValidationChain validationChain = new ValidationChain(handlers);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            validationChain.validate(user);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Invalid email format", exception.getCause().getMessage());
    }

    @Test
    public void testValidateEmptyChain() {
        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();
        List<ValidationHandler> handlers = Arrays.asList();
        ValidationChain validationChain = new ValidationChain(handlers);

        assertDoesNotThrow(() -> validationChain.validate(user));
    }
}
