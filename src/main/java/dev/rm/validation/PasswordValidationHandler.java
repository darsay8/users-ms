package dev.rm.validation;

import dev.rm.model.User;

public class PasswordValidationHandler implements ValidationHandler {
    @Override
    public void handle(User user) throws Exception {
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }
}
