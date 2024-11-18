package dev.rm.validation;

import dev.rm.model.User;

public class RoleValidationHandler implements ValidationHandler {
    @Override
    public void handle(User user) throws Exception {
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
}
