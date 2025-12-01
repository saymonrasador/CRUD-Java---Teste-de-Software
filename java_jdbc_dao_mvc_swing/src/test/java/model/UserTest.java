package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testCreateUserAndGetters() {
        User user = new User("João Silva", "joao.silva");

        // Nós verificamos se os getters retornam os valores corretos
        assertNotNull(user); // Garante que o usuário foi criado
        assertEquals("João Silva", user.getName());
        assertEquals("joao.silva", user.getLogin());
        assertNull(user.getId()); // O ID deve ser nulo, pois não foi salvo no BD
    }

    @Test
    void testSetters() {
        User user = new User();

        // Usamos os métodos "set"
        user.setName("Maria Santos");
        user.setLogin("maria.santos");
        user.setId(123L); // Define um ID (Long)

        // 3. Verificação (Assert)
        assertEquals("Maria Santos", user.getName());
        assertEquals("maria.santos", user.getLogin());
        assertEquals(123L, user.getId());
    }

    @Test
    void testToString() {
        User user = new User("Carlos", "carlos123");
        user.setId(1L);

        String output = user.toString();

        // Verifica se a string contém as informações
        assertTrue(output.contains("Carlos"));
        assertTrue(output.contains("carlos123"));
        assertTrue(output.contains("id: 1"));
    }
}
