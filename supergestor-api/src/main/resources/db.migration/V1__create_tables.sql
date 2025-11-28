-- V1__create_initial_schema.sql

CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,

    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,

    foto_perfil OID
);

CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,

    nome VARCHAR(255) UNIQUE
);

CREATE TABLE promocao (
    id BIGSERIAL PRIMARY KEY,

    nome VARCHAR(255),

    taxa_de_desconto NUMERIC(5, 4),

    inicio DATE,
    fim DATE,

    ativada BOOLEAN
);

CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,

    nome VARCHAR(255),

    preco NUMERIC(10, 2),

    produto_ativo BOOLEAN,
    descricao VARCHAR(255),
    data_validade DATE,

    categoria_id BIGINT NOT NULL,

    promocao_id BIGINT,

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria (id),

    CONSTRAINT fk_produto_promocao
        FOREIGN KEY (promocao_id)
        REFERENCES promocao (id)
);

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

    tempo_fidelidade DATE,

    user_id UUID NOT NULL UNIQUE,

    CONSTRAINT fk_cliente_usuario
        FOREIGN KEY (user_id)
        REFERENCES usuario (id)
        ON DELETE CASCADE
);