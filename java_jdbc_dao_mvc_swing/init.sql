USE mailsystem;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_login (login),
    INDEX idx_id (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (name, login) VALUES 
    ('Usuario Demo 1', 'demo1@exemplo.com'),
    ('Usuario Demo 2', 'demo2@exemplo.com'),
    ('Usuario Demo 3', 'demo3@exemplo.com')
ON DUPLICATE KEY UPDATE name=name;

SELECT 'Banco de dados inicializado com sucesso!' AS Status;
SELECT COUNT(*) as 'Total de usuarios' FROM users;
