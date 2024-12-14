package dev.rm.validation;

import org.junit.jupiter.api.Test;

import dev.rm.model.Role;
import dev.rm.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class RoleValidationHandlerTest {

    private final RoleValidationHandler roleValidationHandler = new RoleValidationHandler();

    @Test
    public void testHandleValidRole() {

        User user = User.builder()
                .role(Role.ADMIN)
                .build();

        assertDoesNotThrow(() -> roleValidationHandler.handle(user));
    }

    @Test
    public void testHandleNullRole() {
        User user = User.builder()
                .role(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleValidationHandler.handle(user);
        });

        assertEquals("Role is required", exception.getMessage());
    }

}
