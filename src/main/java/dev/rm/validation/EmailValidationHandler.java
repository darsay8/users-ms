package dev.rm.validation;

import dev.rm.model.User;

public class EmailValidationHandler implements ValidationHandler {

    @Override
    public void handle(User user) throws Exception {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
