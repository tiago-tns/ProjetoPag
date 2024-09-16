--Essa API não salva nenhum dado no DB, somente checagens no DB, verifica e atualiza dos dados e envia para a fila SQS correta.


Arquitetura:
![image](https://github.com/user-attachments/assets/2ffb7f80-530c-42b8-a4df-f2f6d18d3b1b)

KAFKA:

Abrir Server:

Pasta kafka/bin:

1- ./zookeeper-server-start.sh ../config/zookeeper.properties

2- ./kafka-server-start.sh ../config/server.properties


Criação dos topics:

./kafka-topics.sh --create --bootstrap-server [::1]:9092 --topic full_payments

./kafka-topics.sh --create --bootstrap-server [::1]:9092 --topic excess_payments

./kafka-topics.sh --create --bootstrap-server [::1]:9092 --topic partial_payments


Verificar os Listenings:

./kafka-console-consumer.sh --topic excess_payments --bootstrap-server [::1]:9092 --from-beginning

./kafka-console-consumer.sh --topic partial_payments --bootstrap-server [::1]:9092 --from-beginning

./kafka-console-consumer.sh --topic full_payments --bootstrap-server [::1]:9092 --from-beginning




Database (MariaDB):

Criação das Tabelas

Tabela Charger (antiga payment)

CREATE TABLE `charger` (
  `idcharger` int NOT NULL AUTO_INCREMENT,
  `merchant_id` int NOT NULL,
  `order_item_id` varchar(45) NOT NULL,
  `original_amount` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`idcharger`),
  UNIQUE KEY `idcharger_UNIQUE` (`idcharger`),
  KEY `merchant_id_fk_idx` (`merchant_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


Tabela Merchant (conferencia dos dados do cliente)

CREATE TABLE `merchant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) NOT NULL,
  `cnpj` varchar(18) NOT NULL,
  `address` varchar(255) NOT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(2) DEFAULT NULL,
  `postal_code` varchar(10) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `market_type` varchar(50) DEFAULT NULL,
  `registration_date` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cnpj` (`cnpj`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


