package dev.rm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.rm.model.Role;
import dev.rm.model.User;
import dev.rm.service.UserService;
import dev.rm.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        if (users.isEmpty()) {
            log.info("No users found.");
            return ResponseEntity.noContent().build();
        } else {
            log.info("Returning {} users.", users.size());
            return ResponseEntity.ok(users);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            log.info("Returning user with id {}", id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.error("Error fetching user with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            log.warn("Creation failed: username, email, or password is missing.");
            return ResponseEntity.badRequest().build();
        }

        try {
            User createdUser = userService.createUser(user);
            log.info("Created user with id {}", createdUser.getId());
            return ResponseEntity.status(201).body(createdUser);
        } catch (RuntimeException e) {
            log.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {

        log.info("Updating user with id {}", id);
        log.info("User: {}", user);

        if (user.getUsername() == null || user.getEmail() == null) {
            log.warn("Update failed: username or email is missing.");
            return ResponseEntity.badRequest().body(Map.of("message", "Username and email are required."));
        }

        try {
            User existingUser = userService.getUserById(id);
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            }

            if (user.getRole() != null && !user.getRole().equals(existingUser.getRole())) {

                Role updatedRole = Role.valueOf(user.getRole().name());
                existingUser.setRole(updatedRole);
            } else {
                existingUser.setRole(existingUser.getRole());
            }

            User updatedUser = userService.updateUser(id, existingUser);
            log.info("Updated user with id {}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating user with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            log.info("Deleted user with id {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting user with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        try {
            User authenticatedUser = userService.authenticate(email, password);
            log.info("User '{}' authenticated successfully.", email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Authentication successful");
            response.put("user", authenticatedUser);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Authentication failed for user '{}': {}", email, e.getMessage());
            return ResponseEntity.status(401).body(Map.of("message", "Authentication failed"));
        }
    }

}
