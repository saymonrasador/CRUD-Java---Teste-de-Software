package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testCreateUser() {
        User user = new User("João", "joao123");
        assertEquals("João", user.getName());
        assertEquals("joao123", user.getLogin());
    }

    @Test
    void testToStringContainsName() {
        User user = new User("Maria", "maria123");
        assertTrue(user.toString().contains("Maria"));
    }
}
