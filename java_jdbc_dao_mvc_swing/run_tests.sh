echo "========================================================"
echo "   TESTES DE SOFTWARE - PROJETO CRUD JAVA"
echo "========================================================"
echo ""

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

if [ -f .env ]; then
    echo -e "${BLUE} Carregando variáveis de ambiente do .env...${NC}"
    export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)
    echo -e "${GREEN} [OK] Variáveis carregadas:${NC}"
    echo "  - MYSQL_HOST: $MYSQL_HOST"
    echo "  - MYSQL_PORT: $MYSQL_PORT"
    echo "  - MYSQL_DATABASE: $MYSQL_DATABASE"
    echo "  - MYSQL_USER: $MYSQL_USER"
    echo ""
else
    echo -e "${YELLOW} [AVISO] Arquivo .env não encontrado. Usando valores padrão.${NC}"
    echo ""
fi

if ! command -v mvn &> /dev/null; then
    echo -e "${RED} Maven não encontrado!${NC}"
    echo ""
    echo "Por favor, instale o Maven:"
    echo "  sudo apt install maven"
    echo ""
    exit 1
fi

cd "$(dirname "$0")"

echo -e "${BLUE} Compilando o projeto...${NC}"
mvn clean compile test-compile

if [ $? -ne 0 ]; then
    echo -e "${RED} Erro na compilação!${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN} Compilação concluída com sucesso!${NC}"
echo ""

echo "Escolha qual teste executar:"
echo ""
echo "  1) Teste de Separação de Camadas (Arquitetura)"
echo "  2) Teste de Desempenho (5.000 usuários)"
echo "  3) Todos os testes"
echo "  4) Teste unitário básico (UserTest)"
echo ""
read -p "Opção [1-4]: " opcao

echo ""
echo "========================================================"

case $opcao in
    1)
        echo -e "${BLUE}  EXECUTANDO: Teste de Separação de Camadas${NC}"
        echo "========================================================"
        echo ""
        mvn test -Dtest=LayerSeparationTest
        ;;
    2)
        echo -e "${BLUE}⚡ EXECUTANDO: Teste de Desempenho${NC}"
        echo "========================================================"
        echo ""
        echo -e "${YELLOW}  ATENÇÃO: Este teste irá:${NC}"
        echo "   - Limpar todos os usuários do banco"
        echo "   - Inserir 5.000 novos usuários"
        echo "   - Pode demorar alguns minutos"
        echo ""
        read -p "Deseja continuar? (s/n): " confirm
        if [ "$confirm" = "s" ] || [ "$confirm" = "S" ]; then
            mvn test -Dtest=PerformanceTest
        else
            echo "Teste cancelado."
            exit 0
        fi
        ;;
    3)
        echo -e "${BLUE} EXECUTANDO: Todos os Testes${NC}"
        echo "========================================================"
        echo ""
        mvn clean test
        ;;
    4)
        echo -e "${BLUE} EXECUTANDO: Teste Unitário (UserTest)${NC}"
        echo "========================================================"
        echo ""
        mvn test -Dtest=UserTest
        ;;
    *)
        echo -e "${RED} Opção inválida!${NC}"
        exit 1
        ;;
esac

echo ""
echo "========================================================"
if [ $? -eq 0 ]; then
    echo -e "${GREEN} TESTES CONCLUÍDOS COM SUCESSO!${NC}"
else
    echo -e "${RED} ALGUNS TESTES FALHARAM!${NC}"
fi
echo "========================================================"
echo ""
echo " Consulte TESTES_README.md para mais informações"
echo ""
