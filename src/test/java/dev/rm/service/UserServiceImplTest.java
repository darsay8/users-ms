package dev.rm.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import dev.rm.model.Role;
import dev.rm.model.User;
import dev.rm.repository.UserRepository;
import dev.rm.utils.PasswordUtil;

import java.util.Optional;
import java.util.List;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordUtil PasswordUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User validUser;
    private User existingUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        validUser = User.builder()
                .username("user")
                .email("user@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

        existingUser = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();

    }

    @Test
    public void testGetUsers() {

        when(userRepository.findAll()).thenReturn(List.of(validUser, existingUser));

        List<User> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(validUser));
        assertTrue(result.contains(existingUser));
    }

    @Test
    public void testGetUserByIdUserFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        User result = userService.getUserById(1L);

        assertEquals(existingUser, result);
    }

    @Test
    public void testGetUserByIdUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("User not found with id 1", exception.getMessage());
    }

    @Test
    public void testCreateUser() {

        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(validUser.getUsername())).thenReturn(false);
        when(userRepository.save(validUser)).thenReturn(validUser);

        User result = userService.createUser(validUser);

        assertNotNull(result);
        assertEquals(validUser.getEmail(), result.getEmail());
        assertEquals(validUser.getUsername(), result.getUsername());
        assertEquals(Role.USER, result.getRole());
        verify(userRepository).save(validUser); // Ensure save was called
    }

    @Test
    public void testCreateUserEmailAlreadyInUse() {

        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(validUser);
        });

        assertEquals("Email already in use.", exception.getMessage());
    }

    @Test
    public void testCreateUserUsernameAlreadyInUse() {

        when(userRepository.existsByEmail(validUser.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(validUser.getUsername())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(validUser);
        });

        assertEquals("Username already in use.", exception.getMessage());
    }

    @Test
    public void testUpdateUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = User.builder()
                .username("newUsername")
                .email("updated@example.com")
                .password(PasswordUtil.hashPassword("newPassword123"))
                .role(Role.ADMIN)
                .build();

        User result = userService.updateUser(1L, updatedUser);

        assertEquals("updated@example.com", result.getEmail());
        assertEquals("newUsername", result.getUsername());
        assertEquals(Role.ADMIN, result.getRole());
        verify(userRepository).save(existingUser);
    }

    @Test
    public void testUpdateUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, validUser);
        });

        assertEquals("User not found with id 1", exception.getMessage());
    }

    @Test
    public void testDeleteUser() {

        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    public void testDeleteUserNotFound() {

        when(userRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("User not found with id 1", exception.getMessage());
    }

    @Test
    public void testAuthenticateUserNotFound() {

        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticate(validUser.getEmail(), validUser.getPassword());
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testAuthenticateSuccess() {
        try (MockedStatic<PasswordUtil> mockedPasswordUtil = mockStatic(PasswordUtil.class)) {
            when(userRepository.findByEmail(validUser.getEmail())).thenReturn(validUser);
            mockedPasswordUtil.when(() -> PasswordUtil.matches(validUser.getPassword(), validUser.getPassword()))
                    .thenReturn(true);
            User result = userService.authenticate(validUser.getEmail(), validUser.getPassword());
            assertEquals(validUser, result);
        }
    }

    @Test
    public void testAuthenticateInvalidCredentials() {
        try (MockedStatic<PasswordUtil> mockedPasswordUtil = mockStatic(PasswordUtil.class)) {
            when(userRepository.findByEmail(validUser.getEmail())).thenReturn(validUser);
            mockedPasswordUtil.when(() -> PasswordUtil.matches(validUser.getPassword(), "wrongPassword"))
                    .thenReturn(false);
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userService.authenticate(validUser.getEmail(), "wrongPassword");
            });
            assertEquals("Invalid credentials", exception.getMessage());
        }
    }
}
