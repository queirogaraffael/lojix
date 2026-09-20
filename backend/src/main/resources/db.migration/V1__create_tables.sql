-- V1__create_initial_schema.sql

CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,

    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'ATENDENTE', 'ESTOQUISTA', 'CLIENTE')),
    data_nascimento DATE NOT NULL,

    foto_key VARCHAR(300)
);

CREATE UNIQUE INDEX idx_usuario_email_lower ON usuario (LOWER(email));

CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE promocao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    taxa_de_desconto NUMERIC(5, 2) NOT NULL CHECK (taxa_de_desconto BETWEEN 0 AND 100),
    inicio DATE NOT NULL,
    fim DATE NOT NULL,
    ativada BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0 NOT NULL,
    CHECK (fim >= inicio)
);

CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
    produto_ativo BOOLEAN NOT NULL,
    descricao VARCHAR(255),
    data_validade DATE,
    quantidade_estoque INTEGER DEFAULT 0 NOT NULL CHECK (quantidade_estoque >= 0),
    version BIGINT DEFAULT 0 NOT NULL,
    categoria_id BIGINT NOT NULL,
    promocao_id BIGINT,

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_produto_promocao
        FOREIGN KEY (promocao_id)
        REFERENCES promocao (id)
        ON DELETE SET NULL
);

CREATE INDEX idx_produto_categoria_id ON produto(categoria_id);
CREATE INDEX idx_produto_promocao_id ON produto(promocao_id);

CREATE TABLE funcionario (
    id BIGSERIAL PRIMARY KEY,
    cargo VARCHAR(255),
    salario NUMERIC(10, 2),
    ativo BOOLEAN DEFAULT TRUE,
    user_id UUID NOT NULL UNIQUE,

    CONSTRAINT fk_funcionario_usuario
        FOREIGN KEY (user_id)
        REFERENCES usuario (id)
        ON DELETE CASCADE
);

CREATE TABLE cliente (
    id BIGSERIAL PRIMARY KEY,
    membro_desde DATE DEFAULT CURRENT_DATE,
    user_id UUID NOT NULL UNIQUE,

    CONSTRAINT fk_cliente_usuario
        FOREIGN KEY (user_id)
        REFERENCES usuario (id)
        ON DELETE CASCADE
);