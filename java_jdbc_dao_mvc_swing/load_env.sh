if [ ! -f .env ]; then
    echo "[ERRO] Arquivo .env não encontrado!"
    exit 1
fi

export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)

echo "[OK] Variáveis de ambiente carregadas do arquivo .env:"
echo "  - MYSQL_HOST: $MYSQL_HOST"
echo "  - MYSQL_PORT: $MYSQL_PORT"
echo "  - MYSQL_DATABASE: $MYSQL_DATABASE"
echo "  - MYSQL_USER: $MYSQL_USER"
echo "  - MYSQL_PASSWORD: ****"
echo ""
