package dev.rm.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashPassword() {
        String rawPassword = "mySecretPassword";
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        // Check that the hashed password is not null and not equal to the raw password
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
    }

    @Test
    public void testMatchesCorrectPassword() {
        String rawPassword = "mySecretPassword";
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        // Check that the raw password matches the hashed password
        assertTrue(PasswordUtil.matches(rawPassword, hashedPassword));
    }

    @Test
    public void testMatchesIncorrectPassword() {
        String rawPassword = "mySecretPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        // Check that the wrong password does not match the hashed password
        assertFalse(PasswordUtil.matches(wrongPassword, hashedPassword));
    }
}
