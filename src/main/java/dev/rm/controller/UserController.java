package dev.rm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.rm.factory.UserFactory;
import dev.rm.model.Role;
import dev.rm.model.User;
import dev.rm.service.UserService;
import dev.rm.utils.PasswordUtil;
import dev.rm.validation.ValidationChain;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ValidationChain validationChain;

    public UserController(UserService userService, ValidationChain validationChain) {
        this.userService = userService;
        this.validationChain = validationChain;
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
        try {

            if (user.getRole() == null) {
                user.setRole(Role.USER);
            }

            validationChain.validate(user);

            User newUser = UserFactory.createUser(user.getUsername(), user.getEmail(), user.getPassword(),
                    user.getRole());

            User createdUser = userService.createUser(newUser);
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

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        try {
            User authenticatedUser = userService.authenticate(email, password);
            log.info("User '{}' authenticated successfully.", email);

            Map<String, Object> userResponse = new LinkedHashMap<>();
            userResponse.put("id", authenticatedUser.getId());
            userResponse.put("username", authenticatedUser.getUsername());
            userResponse.put("email", authenticatedUser.getEmail());
            userResponse.put("role", authenticatedUser.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Authentication successful");
            response.put("user", userResponse);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Authentication failed for user '{}': {}", email, e.getMessage());
            return ResponseEntity.status(401).body(Map.of("message", "Authentication failed"));
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.createUser(user);
            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "user", registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

}
