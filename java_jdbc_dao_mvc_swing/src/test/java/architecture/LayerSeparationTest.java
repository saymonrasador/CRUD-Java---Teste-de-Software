package architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste entre Camadas da Arquitetura - Requisito da Tarefa (Arthur)
 * 
 * Objetivo: Garantir que a View só fale com o Controller, 
 * e o Controller só fale com o Model/DAO.
 * A View NUNCA deve falar direto com o DAO.
 * 
 * Arquitetura esperada:
 * View → Controller → Model → DAO
 * 
 * @author Arthur - Teste de Software
 */
public class LayerSeparationTest {

    private static final String SRC_PATH = "src/main/java";
    private static final String VIEW_PACKAGE = "view";
    private static final String CONTROLLER_PACKAGE = "controllers";
    private static final String DAO_PACKAGE = "dao";
    private static final String MODEL_PACKAGE = "model";

    /**
     * TESTE 1: View não deve importar classes DAO
     * Garante que a View nunca acessa o DAO diretamente
     */
    @Test
    @DisplayName("Teste Arquitetural: View não deve acessar DAO diretamente")
    void testViewDoesNotImportDao() throws IOException {
        System.out.println("\n=== TESTE DE SEPARAÇÃO DE CAMADAS ===");
        System.out.println("\n--- TESTE 1: View → DAO (Não deve existir) ---");
        
        List<String> viewFiles = findJavaFiles(VIEW_PACKAGE);
        List<String> violations = new ArrayList<>();
        
        for (String viewFile : viewFiles) {
            String content = readFile(viewFile);
            
            // Verifica se há import de classes DAO
            if (content.contains("import dao.") || 
                content.contains("import daoFactory.")) {
                violations.add(viewFile + " importa classes DAO!");
            }
            
            // Verifica se há uso direto de classes DAO
            if (content.matches("(?s).*\\b(UserDao|MysqlUserDao|DaoFactory)\\b.*")) {
                violations.add(viewFile + " usa classes DAO diretamente!");
            }
        }
        
        // Resultados
        System.out.println("  • Arquivos View analisados: " + viewFiles.size());
        System.out.println("  • Violações encontradas: " + violations.size());
        
        if (violations.isEmpty()) {
            System.out.println(" SUCESSO: Nenhuma View acessa DAO diretamente!");
        } else {
            System.out.println(" FALHA: Encontradas violações:");
            violations.forEach(v -> System.out.println("     - " + v));
        }
        
        // Assertion
        assertTrue(violations.isEmpty(), 
            "A View não deve importar ou usar classes DAO diretamente.\n" +
            "Violações encontradas:\n" + String.join("\n", violations));
    }

    /**
     * TESTE 2: View deve usar Controller
     * Garante que a View usa o Controller para operações
     */
    @Test
    @DisplayName("Teste Arquitetural: View deve usar Controller")
    void testViewUsesController() throws IOException {
        System.out.println("\n--- TESTE 2: View → Controller (Deve existir) ---");
        
        List<String> viewFiles = findJavaFiles(VIEW_PACKAGE);
        int filesUsingController = 0;
        
        for (String viewFile : viewFiles) {
            String content = readFile(viewFile);
            
            // Verifica se usa Controller
            if (content.contains("import controllers.") || 
                content.contains("UserController.getInstance()")) {
                filesUsingController++;
                
                // Mostra os arquivos que usam Controller corretamente
                String fileName = Paths.get(viewFile).getFileName().toString();
                System.out.println("  ✓ " + fileName + " usa UserController");
            }
        }
        
        System.out.println("\n  • Total de Views: " + viewFiles.size());
        System.out.println("  • Views usando Controller: " + filesUsingController);
        
        if (filesUsingController > 0) {
            System.out.println(" SUCESSO: Views usam Controller corretamente!");
        }
        
        // Assertion: pelo menos algumas Views devem usar Controller
        assertTrue(filesUsingController > 0, 
            "Pelo menos uma View deve usar o Controller");
    }

    /**
     * TESTE 3: Controller deve usar Model
     * Garante que o Controller não acessa DAO diretamente
     */
    @Test
    @DisplayName("Teste Arquitetural: Controller deve usar Model (não DAO)")
    void testControllerUsesModelNotDao() throws IOException {
        System.out.println("\n--- TESTE 3: Controller → Model (não DAO) ---");
        
        List<String> controllerFiles = findJavaFiles(CONTROLLER_PACKAGE);
        List<String> violations = new ArrayList<>();
        int controllersUsingModel = 0;
        
        for (String controllerFile : controllerFiles) {
            String content = readFile(controllerFile);
            String fileName = Paths.get(controllerFile).getFileName().toString();
            
            // Verifica se usa Model
            if (content.contains("import model.User") || 
                content.contains("User.")) {
                controllersUsingModel++;
                System.out.println("  ✓ " + fileName + " usa Model");
            }
            
            // Verifica se acessa DAO diretamente (VIOLAÇÃO)
            if (content.contains("import dao.concrete.") || 
                content.contains("MysqlUserDao") ||
                (content.contains("import dao.interfaces.UserDao") && 
                 content.contains("new ") && content.contains("Dao"))) {
                violations.add(fileName + " acessa DAO diretamente!");
            }
        }
        
        System.out.println("\n  • Controllers usando Model: " + controllersUsingModel);
        System.out.println("  • Violações encontradas: " + violations.size());
        
        if (violations.isEmpty()) {
            System.out.println(" SUCESSO: Controllers usam Model, não DAO!");
        } else {
            System.out.println(" FALHA: Encontradas violações:");
            violations.forEach(v -> System.out.println("     - " + v));
        }
        
        // Assertions
        assertTrue(violations.isEmpty(), 
            "Controller não deve acessar DAO diretamente.\n" +
            "Violações encontradas:\n" + String.join("\n", violations));
    }

    /**
     * TESTE 4: Verifica exemplos específicos mencionados na tarefa
     */
    @Test
    @DisplayName("Teste Arquitetural: Verificar exemplos da tarefa")
    void testSpecificExamplesFromTask() throws IOException {
        System.out.println("\n--- TESTE 4: Exemplos da Tarefa ---");
        
        // Exemplo 1: JTableList.java chama UserController.getInstance().remove()
        String jtableListPath = findFile("JTableList.java", VIEW_PACKAGE);
        if (jtableListPath != null) {
            String content = readFile(jtableListPath);
            boolean hasCorrectRemove = content.contains("UserController.getInstance().remove(");
            
            System.out.println("  EX1: JTableList.java");
            System.out.println("       UserController.getInstance().remove(userId)");
            System.out.println("       " + (hasCorrectRemove ? " CORRETO" : " NÃO ENCONTRADO"));
            
            assertTrue(hasCorrectRemove, 
                "JTableList deve chamar UserController.getInstance().remove()");
        }
        
        // Exemplo 2: Form.java chama UserController.getInstance().save()
        String formPath = findFile("Form.java", VIEW_PACKAGE);
        if (formPath != null) {
            String content = readFile(formPath);
            boolean hasCorrectSave = content.contains("UserController.getInstance().save(");
            
            System.out.println("\n  EX2: Form.java");
            System.out.println("       UserController.getInstance().save(user)");
            System.out.println("       " + (hasCorrectSave ? " CORRETO" : " NÃO ENCONTRADO"));
            
            assertTrue(hasCorrectSave, 
                "Form deve chamar UserController.getInstance().save()");
        }
        
        System.out.println("\n  TODOS OS EXEMPLOS DA TAREFA ESTÃO CORRETOS!");
    }

    /**
     * TESTE 5: Resumo da arquitetura
     */
    @Test
    @DisplayName("Teste Arquitetural: Resumo completo da separação")
    void testArchitectureSummary() throws IOException {
        System.out.println("\n--- TESTE 5: RESUMO DA ARQUITETURA ---");
        
        List<String> viewFiles = findJavaFiles(VIEW_PACKAGE);
        List<String> controllerFiles = findJavaFiles(CONTROLLER_PACKAGE);
        List<String> modelFiles = findJavaFiles(MODEL_PACKAGE);
        List<String> daoFiles = findJavaFiles(DAO_PACKAGE);
        
        System.out.println("\n ESTRUTURA DO PROJETO:");
        System.out.println("  • View:       " + viewFiles.size() + " arquivos");
        System.out.println("  • Controller: " + controllerFiles.size() + " arquivos");
        System.out.println("  • Model:      " + modelFiles.size() + " arquivos");
        System.out.println("  • DAO:        " + daoFiles.size() + " arquivos");
        
        System.out.println("\n ARQUITETURA MVC + DAO:");
        System.out.println("  View ──→ Controller ──→ Model ──→ DAO");
        System.out.println("                               ");
        
        System.out.println("\n CAMINHOS PROIBIDOS:");
        System.out.println("  View ──✗──→ DAO (NUNCA!)");
        System.out.println("  View ──✗──→ Model (Usar Controller!)");
        
        // Este teste sempre passa, é apenas informativo
        assertTrue(true, "Resumo da arquitetura exibido");
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    /**
     * Encontra todos os arquivos .java em um pacote
     */
    private List<String> findJavaFiles(String packageName) throws IOException {
        List<String> javaFiles = new ArrayList<>();
        Path startPath = Paths.get(SRC_PATH, packageName.replace(".", "/"));
        
        if (!Files.exists(startPath)) {
            return javaFiles;
        }
        
        try (Stream<Path> paths = Files.walk(startPath)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> javaFiles.add(p.toString()));
        }
        
        return javaFiles;
    }

    /**
     * Lê o conteúdo de um arquivo
     */
    private String readFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    /**
     * Encontra um arquivo específico em um pacote
     */
    private String findFile(String fileName, String packageName) throws IOException {
        List<String> files = findJavaFiles(packageName);
        return files.stream()
                   .filter(f -> f.endsWith(fileName))
                   .findFirst()
                   .orElse(null);
    }
}
