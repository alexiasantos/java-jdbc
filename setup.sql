create database byte_bank

CREATE TABLE `conta` (
  `numero` int NOT NULL,
  `saldo` decimal(10,0) DEFAULT NULL,
  `cliente_nome` varchar(50) DEFAULT NULL,
  `cliente_cpf` varchar(11) DEFAULT NULL,
  `cliente_email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`numero`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE conta MODIFY COLUMN saldo DECIMAL(10,2);

ALTER TABLE conta add column esta_ativa boolean default true;