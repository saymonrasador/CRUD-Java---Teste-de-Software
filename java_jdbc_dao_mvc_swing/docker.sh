GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║                                                                ║"
echo "║         GERENCIADOR DOCKER - MYSQL PARA TESTES              ║"
echo "║                                                                ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

if ! command -v docker &> /dev/null; then
    echo -e "${RED} Docker não encontrado!${NC}"
    echo ""
    echo "Por favor, instale o Docker:"
    echo "  https://docs.docker.com/engine/install/"
    echo ""
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! sudo docker compose version &> /dev/null; then
    echo -e "${RED} Docker Compose não encontrado!${NC}"
    echo ""
    echo "Por favor, instale o Docker Compose:"
    echo "  https://docs.docker.com/compose/install/"
    echo ""
    exit 1
fi

DOCKER_COMPOSE="sudo docker compose"
if ! sudo docker compose version &> /dev/null; then
    DOCKER_COMPOSE="sudo docker-compose"
fi

echo "Escolha uma opção:"
echo ""
echo "  1)  Iniciar MySQL (criar e iniciar containers)"
echo "  2)  Parar MySQL (parar containers)"
echo "  3)  Reiniciar MySQL (reiniciar containers)"
echo "  4)  Remover MySQL (parar e remover tudo)"
echo "  5)  Status dos containers"
echo "  6)  Ver logs do MySQL"
echo "  7)  Executar testes após iniciar MySQL"
echo "  8)  Abrir PhpMyAdmin no navegador"
echo ""
read -p "Opção [1-8]: " opcao

echo ""
echo "════════════════════════════════════════════════════════════════"

case $opcao in
    1)
        echo -e "${BLUE} Iniciando containers...${NC}"
        echo ""
        $DOCKER_COMPOSE up -d
        
        if [ $? -eq 0 ]; then
            echo ""
            echo -e "${GREEN} MySQL iniciado com sucesso!${NC}"
            echo ""
            echo " Informações de conexão:"
            echo "  Host: localhost"
            echo "  Porta: 3307"
            echo "  Usuário: root"
            echo "  Senha: @GSkpx87"
            echo "  Banco: mailsystem"
            echo ""
            echo " PhpMyAdmin disponível em: http://localhost:8080"
            echo "  Usuário: root"
            echo "  Senha: @GSkpx87"
            echo ""
            echo "Aguardando MySQL inicializar..."
            sleep 10
            
            echo -e "${GREEN} Pronto para executar os testes!${NC}"
            echo ""
            echo "Execute: ./run_tests.sh"
        else
            echo -e "${RED} Erro ao iniciar containers${NC}"
            exit 1
        fi
        ;;
        
    2)
        echo -e "${YELLOW} Parando containers...${NC}"
        $DOCKER_COMPOSE stop
        echo -e "${GREEN} Containers parados${NC}"
        ;;
        
    3)
        echo -e "${BLUE} Reiniciando containers...${NC}"
        $DOCKER_COMPOSE restart
        echo -e "${GREEN} Containers reiniciados${NC}"
        echo ""
        echo "Aguardando MySQL inicializar..."
        sleep 5
        echo -e "${GREEN} Pronto!${NC}"
        ;;
        
    4)
        echo -e "${RED} Removendo containers e volumes...${NC}"
        echo ""
        read -p "  Isso vai apagar TODOS os dados! Confirma? (s/n): " confirm
        
        if [ "$confirm" = "s" ] || [ "$confirm" = "S" ]; then
            $DOCKER_COMPOSE down -v
            echo -e "${GREEN} Tudo removido${NC}"
        else
            echo "Operação cancelada"
        fi
        ;;
        
    5)
        echo -e "${CYAN} Status dos containers:${NC}"
        echo ""
        $DOCKER_COMPOSE ps
        echo ""
        echo "Logs recentes:"
        $DOCKER_COMPOSE logs --tail=20
        ;;
        
    6)
        echo -e "${CYAN} Logs do MySQL (Ctrl+C para sair):${NC}"
        echo ""
        $DOCKER_COMPOSE logs -f mysql
        ;;
        
    7)
        echo -e "${BLUE} Iniciando MySQL e executando testes...${NC}"
        echo ""
        
        # Iniciar containers
        $DOCKER_COMPOSE up -d
        
        echo "Aguardando MySQL inicializar..."
        sleep 10
        
        # Verificar se está rodando
        if sudo docker ps | grep -q "crud-java-mysql"; then
            echo -e "${GREEN} MySQL está rodando${NC}"
            echo ""
            
            # Executar testes
            if [ -f "./run_tests.sh" ]; then
                ./run_tests.sh
            else
                echo -e "${YELLOW}  Script run_tests.sh não encontrado${NC}"
                echo "Execute manualmente: mvn test"
            fi
        else
            echo -e "${RED} Erro: MySQL não está rodando${NC}"
            $DOCKER_COMPOSE logs mysql
        fi
        ;;
        
    8)
        echo -e "${BLUE} Abrindo PhpMyAdmin no navegador...${NC}"
        echo ""
        
        # Verificar se está rodando
        if sudo docker ps | grep -q "crud-java-phpmyadmin"; then
            echo -e "${GREEN} PhpMyAdmin está rodando${NC}"
            echo ""
            echo "Abrindo: http://localhost:8080"
            echo ""
            echo "Credenciais:"
            echo "  Usuário: root"
            echo "  Senha: @GSkpx87"
            echo ""
            
            # Tentar abrir no navegador
            if command -v xdg-open &> /dev/null; then
                xdg-open http://localhost:8080
            elif command -v open &> /dev/null; then
                open http://localhost:8080
            else
                echo "Abra manualmente: http://localhost:8080"
            fi
        else
            echo -e "${RED} PhpMyAdmin não está rodando${NC}"
            echo "Execute: ./docker.sh e escolha opção 1"
        fi
        ;;
        
    *)
        echo -e "${RED} Opção inválida!${NC}"
        exit 1
        ;;
esac

echo ""
echo "════════════════════════════════════════════════════════════════"
echo ""
