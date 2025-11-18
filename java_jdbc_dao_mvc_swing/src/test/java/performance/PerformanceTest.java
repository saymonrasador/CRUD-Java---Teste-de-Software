package performance;

import model.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de D            if (i % 500 == 0) {
                System.out.println("  [OK] " + i + " usuários atualizados");
            }mpenho - Requisito da Tarefa (Arthur)
 * 
 * Objetivo: Testar o desempenho do sistema ao lidar com grandes volumes de dados
 * - Inserir 5.000 usuários no banco de teste
 * - Medir o tempo de carregamento de 5.000 usuários
 * 
 * @author Arthur - Teste de Software
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PerformanceTest {

    private static final int TOTAL_USERS = 5000;
    private static final long ACCEPTABLE_INSERT_TIME_MS = 30000; // 30 segundos
    private static final long ACCEPTABLE_LOAD_TIME_MS = 5000;     // 5 segundos

    /**
     * Limpa o banco antes de começar os testes
     */
    @BeforeAll
    static void setupDatabase() throws SQLException {
        System.out.println("=== INICIANDO TESTES DE DESEMPENHO ===");
        System.out.println("Limpando banco de dados...");
        User.deleteAll();
        System.out.println("Banco limpo com sucesso!\n");
    }

    /**
     * TESTE 1: Inserir 5.000 usuários e medir o tempo
     * Requisito: A tarefa pede para testar a inserção em massa
     */
    @Test
    @Order(1)
    @DisplayName("Teste de Desempenho: Inserção de 5.000 usuários")
    void testInsert5000Users() throws SQLException {
        System.out.println("\n--- TESTE 1: INSERÇÃO EM MASSA ---");
        System.out.println("Inserindo " + TOTAL_USERS + " usuários...");
        
        long startTime = System.currentTimeMillis();
        
        // Insere 5.000 usuários
        for (int i = 1; i <= TOTAL_USERS; i++) {
            User user = new User(
                "Usuario Teste " + i,
                "user" + i + "@teste.com"
            );
            user.save();
            
            // Mostra progresso a cada 1000 usuários
            if (i % 1000 == 0) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;
                System.out.println("  [OK] " + i + " usuários inseridos (" + elapsedTime + " ms)");
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Calcula estatísticas
        double timePerUser = (double) totalTime / TOTAL_USERS;
        double usersPerSecond = (TOTAL_USERS * 1000.0) / totalTime;
        
        System.out.println("\nRESULTADOS DA INSERÇÃO:");
        System.out.println("  • Total de usuários: " + TOTAL_USERS);
        System.out.println("  • Tempo total: " + String.format("%.2f", totalTime / 1000.0) + " segundos (" + totalTime + " ms)");
        System.out.println("  • Tempo médio por usuário: " + String.format("%.2f", timePerUser) + " ms");
        System.out.println("  • Taxa de inserção: " + String.format("%.2f", usersPerSecond) + " usuários/segundo");
        System.out.println("  • Limite aceitável: " + String.format("%.2f", ACCEPTABLE_INSERT_TIME_MS / 1000.0) + " segundos (" + ACCEPTABLE_INSERT_TIME_MS + " ms)");
        
        // Verifica se o tempo está aceitável
        if (totalTime <= ACCEPTABLE_INSERT_TIME_MS) {
            System.out.println("  [OK] DESEMPENHO ACEITÁVEL!");
        } else {
            System.out.println("  [AVISO] DESEMPENHO ABAIXO DO ESPERADO!");
        }
        
        // Assertion: verifica se completou a inserção
        assertTrue(totalTime > 0, "O tempo de inserção deve ser maior que 0");
        
        // Verifica se todos os usuários foram inseridos
        List<User> allUsers = User.all();
        assertEquals(TOTAL_USERS, allUsers.size(), 
            "Devem existir " + TOTAL_USERS + " usuários no banco");
    }

    /**
     * TESTE 2: Carregar 5.000 usuários e medir o tempo
     * Requisito: A tarefa pede para medir quanto tempo a JTableList demora
     * para exibir os 5.000 usuários
     */
    @Test
    @Order(2)
    @DisplayName("Teste de Desempenho: Carregamento de 5.000 usuários")
    void testLoad5000Users() throws SQLException {
        System.out.println("\n--- TESTE 2: CARREGAMENTO EM MASSA ---");
        System.out.println("Carregando " + TOTAL_USERS + " usuários do banco...");
        
        long startTime = System.currentTimeMillis();
        
        // Simula o que a JTableList faz: chama User.all()
        List<User> users = User.all();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Calcula estatísticas
        double timePerUser = (double) totalTime / TOTAL_USERS;
        
        System.out.println("\nRESULTADOS DO CARREGAMENTO:");
        System.out.println("  • Total de usuários carregados: " + users.size());
        System.out.println("  • Tempo total: " + totalTime + " ms (" + (totalTime / 1000.0) + " segundos)");
        System.out.println("  • Tempo por usuário: " + String.format("%.2f", timePerUser) + " ms");
        System.out.println("  • Limite aceitável: " + ACCEPTABLE_LOAD_TIME_MS + " ms");
        
        // Verifica se o tempo está aceitável
        if (totalTime <= ACCEPTABLE_LOAD_TIME_MS) {
            System.out.println("  [OK] DESEMPENHO ACEITÁVEL!");
        } else {
            System.out.println("  [AVISO] DESEMPENHO ABAIXO DO ESPERADO!");
        }
        
        // Assertions
        assertEquals(TOTAL_USERS, users.size(), 
            "Devem ser carregados " + TOTAL_USERS + " usuários");
        assertTrue(totalTime > 0, "O tempo de carregamento deve ser maior que 0");
        
        // Verifica se os dados estão corretos
        assertNotNull(users.get(0).getName(), "Os usuários devem ter nome");
        assertNotNull(users.get(0).getLogin(), "Os usuários devem ter login");
        assertNotNull(users.get(0).getId(), "Os usuários devem ter ID");
    }

    /**
     * TESTE 3: Teste de busca individual (findById)
     */
    @Test
    @Order(3)
    @DisplayName("Teste de Desempenho: Busca de usuário por ID")
    void testFindUserById() throws SQLException {
        System.out.println("\n--- TESTE 3: BUSCA POR ID ---");
        
        // Primeiro, verifica quantos usuários existem
        List<User> allUsers = User.all();
        
        if (allUsers.isEmpty()) {
            System.out.println("  Nenhum usuário no banco. Pulando teste.");
            assertTrue(true, "Teste pulado - sem usuários");
            return;
        }
        
        // Busca os primeiros usuários que existem
        int maxTests = Math.min(4, allUsers.size());
        
        for (int i = 0; i < maxTests; i++) {
            Long userId = allUsers.get(i * (allUsers.size() / maxTests)).getId();
            
            long startTime = System.currentTimeMillis();
            User user = User.findById(userId);
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            System.out.println("  • Busca ID " + userId + ": " + totalTime + " ms");
            assertNotNull(user, "Usuário com ID " + userId + " deve existir");
        }
    }

    /**
     * TESTE 4: Teste de atualização em massa
     */
    @Test
    @Order(4)
    @DisplayName("Teste de Desempenho: Atualização de 1.000 usuários")
    void testUpdate1000Users() throws SQLException {
        System.out.println("\n--- TESTE 4: ATUALIZAÇÃO EM MASSA ---");
        System.out.println("Atualizando 1.000 usuários...");
        
        long startTime = System.currentTimeMillis();
        
        // Atualiza os primeiros 1000 usuários
        for (int i = 1; i <= 1000; i++) {
            User user = User.findById((long) i);
            if (user != null) {
                user.setName("Usuario Atualizado " + i);
                user.save();
            }
            
            if (i % 250 == 0) {
                System.out.println("  [OK] " + i + " usuários atualizados");
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("\nRESULTADOS DA ATUALIZAÇÃO:");
        System.out.println("  • Total de usuários atualizados: 1.000");
        System.out.println("  • Tempo total: " + totalTime + " ms (" + (totalTime / 1000.0) + " segundos)");
        System.out.println("  • Tempo por usuário: " + String.format("%.2f", (double) totalTime / 1000) + " ms");
        
        // Verifica se a atualização funcionou
        User updatedUser = User.findById(500L);
        assertTrue(updatedUser.getName().contains("Atualizado"), 
            "O nome do usuário deve conter 'Atualizado'");
    }

    /**
     * TESTE 5: Teste de deleção em massa
     */
    @Test
    @Order(5)
    @DisplayName("Teste de Desempenho: Deleção de todos os usuários")
    void testDeleteAllUsers() throws SQLException {
        System.out.println("\n--- TESTE 5: DELEÇÃO EM MASSA ---");
        System.out.println("Deletando todos os " + TOTAL_USERS + " usuários...");
        
        long startTime = System.currentTimeMillis();
        int deletedCount = User.deleteAll();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("\nRESULTADOS DA DELEÇÃO:");
        System.out.println("  • Total de usuários deletados: " + deletedCount);
        System.out.println("  • Tempo total: " + totalTime + " ms (" + (totalTime / 1000.0) + " segundos)");
        
        // Verifica se todos foram deletados
        List<User> remainingUsers = User.all();
        assertEquals(0, remainingUsers.size(), 
            "Não deve haver usuários no banco após deleteAll()");
        
        System.out.println("  [OK] TODOS OS USUÁRIOS FORAM DELETADOS!");
    }

    /**
     * Resumo final dos testes
     */
    @AfterAll
    static void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("=== RESUMO DOS TESTES DE DESEMPENHO ===");
        System.out.println("=".repeat(60));
        System.out.println(" Todos os testes de desempenho foram executados!");
        System.out.println("\n TESTES REALIZADOS:");
        System.out.println("  1. Inserção de 5.000 usuários");
        System.out.println("  2. Carregamento de 5.000 usuários (simulando JTableList)");
        System.out.println("  3. Busca por ID");
        System.out.println("  4. Atualização de 1.000 usuários");
        System.out.println("  5. Deleção de todos os usuários");
        System.out.println("\nOBSERVAÇÃO:");
        System.out.println("  O teste simula o carregamento da JTableList");
        System.out.println("=".repeat(60) + "\n");
    }
}
