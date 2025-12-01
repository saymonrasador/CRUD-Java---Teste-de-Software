package dao.concrete;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.List;

public class MysqlUserDaoTest {
    private MysqlUserDao dao = new MysqlUserDao();

    /**
     * Este método roda DEPOIS de cada teste (@Test).
     * Ele limpa a tabela 'users' para que um teste
     * não interfira no próximo.
     */
    @AfterEach
    void tearDown() throws SQLException {
        dao.deleteAll();
    }

    @Test
    void testInsertAndFindById() throws SQLException {
        User user = new User("Teste Insert", "test.insert");

        // (C)RUD - Criar
        User insertedUser = dao.insert(user);
        assertNotNull(insertedUser.getId()); // O banco deve ter dado um ID

        // C(R)UD - Ler
        User foundUser = dao.findById(insertedUser.getId());
        assertNotNull(foundUser);
        assertEquals("Teste Insert", foundUser.getName());
    }

    @Test
    void testUpdate() throws SQLException {
        User user = new User("Usuário Original", "original@test.com");
        user = dao.insert(user);

        // CR(U)D - Atualizar
        user.setName("Usuário Editado");
        user.setLogin("editado@test.com");
        dao.update(user); // Chama o método de update

        // Verificação (Assert) - Busca o usuário de novo
        User updatedUser = dao.findById(user.getId());
        assertEquals("Usuário Editado", updatedUser.getName());
        assertEquals("editado@test.com", updatedUser.getLogin());
    }

    @Test
    void testDelete() throws SQLException {
        User user = new User("Para Deletar", "delete@test.com");
        user = dao.insert(user);

        assertNotNull(dao.findById(user.getId()));

        // CRU(D) - Deletar
        dao.delete(user);

        // Verificação (Assert) - Tenta buscar e espera 'null'
        assertNull(dao.findById(user.getId()));
    }

    @Test
    void testAll() throws SQLException {
        dao.insert(new User("Usuário 1", "u1@test.com"));
        dao.insert(new User("Usuário 2", "u2@test.com"));

        List<User> users = dao.all();

        assertNotNull(users);
        assertEquals(2, users.size()); // Verifica se os dois usuários foram encontrados
    }
}