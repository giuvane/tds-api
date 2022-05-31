CREATE TABLE usuario (
	codigo BIGINT(20) PRIMARY KEY,
	nome VARCHAR(50) NOT NULL,
	email VARCHAR(50) NOT NULL,
	senha VARCHAR(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE permissao (
	codigo BIGINT(20) PRIMARY KEY,
	descricao VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE usuario_permissao (
	codigo_usuario BIGINT(20) NOT NULL,
	codigo_permissao BIGINT(20) NOT NULL,
	PRIMARY KEY (codigo_usuario, codigo_permissao),
	FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo),
	FOREIGN KEY (codigo_permissao) REFERENCES permissao(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO usuario (codigo, nome, email, senha) values (1, 'Administrador', 'admin@algamoney.com', '$2a$10$BrrvQGkWOB5T1FsNC.nBGODTK16j1LP2ux/zSr.MOjb.ujgjpX0na');
INSERT INTO usuario (codigo, nome, email, senha) values (2, 'Maria Silva', 'maria@algamoney.com', '$2a$10$Zc3w6HyuPOPXamaMhh.PQOXvDnEsadztbfi6/RyZWJDzimE8WQjaq');
INSERT INTO usuario (codigo, nome, email, senha) values (3, 'PGEAGRI', 'pgeagri', '$2a$10$yaq4D2MapPdc6zYw5q0rNuwbMNXnPQOzqtvXzDRkauXfGb54rWKAO');
INSERT INTO usuario (codigo, nome, email, senha) values (4, 'Teste', 'teste', '$2a$10$qKmnnI5s6MQpPln4dQyp/uAhtTH2yGH1qULavRrfJsGrJU8x/o4Qi');
INSERT INTO usuario (codigo, nome, email, senha) values (5, 'Giuvane Conti', 'giuvane.conti@gmail.com', '$2a$10$aMlqZQnAWobpvmJ/vCzpTecf9/IzD61Fj4Yze3iTsq7a8XdBbVIr2');

INSERT INTO permissao (codigo, descricao) values (1, 'ROLE_CADASTRAR_CATEGORIA');
INSERT INTO permissao (codigo, descricao) values (2, 'ROLE_PESQUISAR_CATEGORIA');

INSERT INTO permissao (codigo, descricao) values (3, 'ROLE_CADASTRAR_PESSOA');
INSERT INTO permissao (codigo, descricao) values (4, 'ROLE_REMOVER_PESSOA');
INSERT INTO permissao (codigo, descricao) values (5, 'ROLE_PESQUISAR_PESSOA');

INSERT INTO permissao (codigo, descricao) values (6, 'ROLE_CADASTRAR_LANCAMENTO');
INSERT INTO permissao (codigo, descricao) values (7, 'ROLE_REMOVER_LANCAMENTO');
INSERT INTO permissao (codigo, descricao) values (8, 'ROLE_PESQUISAR_LANCAMENTO');

-- admin
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 1);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 3);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 4);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 6);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 7);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (1, 8);

-- maria
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (2, 2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (2, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (2, 8);

-- pgeagri
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 1);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 3);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 4);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 6);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 7);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (3, 8);

-- teste
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (4, 2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (4, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (4, 6);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (4, 8);

-- giuvane
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 1);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 3);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 4);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 6);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 7);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) values (5, 8);