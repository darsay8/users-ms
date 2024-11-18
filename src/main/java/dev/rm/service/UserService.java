package dev.rm.service;

import java.util.List;
import dev.rm.model.User;

public interface UserService {
    List<User> getUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    User authenticate(String email, String password);
}
