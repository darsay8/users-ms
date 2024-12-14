package dev.rm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.rm.model.Role;
import dev.rm.model.User;
import dev.rm.service.UserService;
import dev.rm.utils.PasswordUtil;
import dev.rm.validation.ValidationChain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ValidationChain validationChain;

    @InjectMocks
    private UserController userController;

    private User validUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validUser = User.builder()
                .username("validUsername")
                .email("valid@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(validUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("validUsername"))
                .andExpect(jsonPath("$[0].email").value("valid@example.com"));
    }

    @Test
    public void testGetAllUsersNoContent() throws Exception {
        when(userService.getUsers()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(validUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("validUsername"))
                .andExpect(jsonPath("$.email").value("valid@example.com"));
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateUser() throws Exception {

        User newUser = User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password(PasswordUtil.hashPassword("password123"))
                .role(Role.USER)
                .build();
        when(userService.createUser(any(User.class))).thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    public void testCreateUserBadRequest() throws Exception {

        User invalidUser = User.builder()
                .username(" ")
                .email(" ")
                .password(PasswordUtil.hashPassword(" "))
                .role(Role.USER)
                .build();

        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("Error creating user"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updatedUser = User.builder()
                .username("updatedUsername")
                .email("updated@example.com")
                .password(PasswordUtil.hashPassword("newpassword"))
                .role(Role.USER)
                .build();

        updatedUser.setId(1L);
        when(userService.getUserById(1L)).thenReturn(validUser);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.user.username").value("updatedUsername"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(userService).deleteUser(1L);
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        Map<String, Object> response = Map.of(
                "message", "Authentication successful",
                "user", Map.of("id", 1L, "username", "validUsername", "email", "valid@example.com", "role", "USER"));

        when(userService.authenticate(validUser.getEmail(), validUser.getPassword())).thenReturn(validUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Authentication successful"));
    }

    @Test
    public void testAuthenticateFailure() throws Exception {
        when(userService.authenticate(validUser.getEmail(), validUser.getPassword()))
                .thenThrow(new RuntimeException("Authentication failed"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication failed"));
    }

    @Test
    public void testRegisterUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(validUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    // Helper method to convert object to JSON string
    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
