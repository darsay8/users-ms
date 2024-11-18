package dev.rm.validation;

import dev.rm.model.User;
import java.util.List;

public class ValidationChain {

    private final List<ValidationHandler> handlers;

    public ValidationChain(List<ValidationHandler> handlers) {
        this.handlers = handlers;
    }

    public void validate(User user) {
        for (ValidationHandler handler : handlers) {
            try {
                handler.handle(user);
            } catch (Exception e) {
                throw new RuntimeException("Validation failed: " + e.getMessage(), e);
            }
        }
    }
}
