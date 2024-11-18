package dev.rm.validation;

import dev.rm.model.User;

public interface ValidationHandler {
    void handle(User user) throws Exception;
}
