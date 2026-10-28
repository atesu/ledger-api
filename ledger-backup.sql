-- MySQL dump 10.13  Distrib 8.4.13, for Linux (x86_64)
--
-- Host: host.docker.internal    Database: ledger
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` varchar(36) NOT NULL,
  `balance` decimal(19,2) NOT NULL,
  `currency` enum('EUR','JPY','USD') NOT NULL,
  `overdraft_limit` decimal(19,2) NOT NULL,
  `version` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES ('00b6e7ab-4acd-43b6-ba84-c0e37f901708',0.00,'EUR',100.00,0),('26c324ab-4c7e-4214-8ef3-4c6e3a33bbe2',0.00,'EUR',0.00,0),('330235a1-df7f-4842-8b5b-af5a067ea62b',0.00,'EUR',25.00,0),('458f039c-202e-4a14-9a7c-2138a90e86b0',1.00,'EUR',0.00,1),('459fea63-c39f-4c15-b67f-140da6913392',30.00,'EUR',225.00,3),('484cf0d7-c0a6-4cff-a720-574813cf4edc',0.00,'EUR',100.00,0),('60da8d7e-3f37-43f7-90c4-af38a4c0fd05',0.00,'EUR',25.00,0),('63cdd7b7-0a1c-4d78-9654-5d3e9f3bc1b8',18.00,'EUR',425.00,1),('6a3bc1e3-ef76-4b84-b051-1d0d39007ae7',0.00,'EUR',25.00,0),('6a6abaf7-f120-4611-af1b-6d7db98dbca2',0.00,'EUR',100.00,0),('70e8ee66-7826-472d-8c4e-b71d9c3f9fff',0.00,'EUR',25.00,0),('718ac27c-998c-4af7-9a58-1fd9f600457a',-18.00,'EUR',225.00,1),('88f6d44e-68c3-4a0f-b657-5d4448d56fe0',0.00,'EUR',100.00,0),('93090b02-99ce-4d9b-88a1-71c0d567292b',-10.00,'EUR',25.00,1),('9515ac5b-9947-4bfa-81ab-350bc38fda64',-1.00,'EUR',10.00,1),('959921ab-9306-413c-ae7e-79c54f739a1d',0.00,'EUR',25.00,0),('9990305e-8b5d-4158-b9f6-7aa6a9088bb5',10.00,'EUR',25.00,1),('9cc71cd8-e571-4e71-aa32-7b97a5c1d427',0.00,'EUR',0.00,0),('9f6720b6-bafe-4864-9e06-a15c1f80d187',-112.00,'JPY',12000.00,7),('ac80997a-30ac-499f-b9e8-ee5bce730622',-30.00,'EUR',225.00,3),('bb3db319-a775-4dd7-bd73-d0898d12b75a',0.00,'EUR',25.00,0),('e1636d09-4f88-4ac2-b4bc-83905d5eb322',0.00,'EUR',25.00,0),('fc2e83d9-7df4-43b2-af67-5b38d46d4e1f',112.00,'JPY',120.00,7);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ledger_entries`
--

DROP TABLE IF EXISTS `ledger_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ledger_entries` (
  `id` varchar(36) NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `account_id` varchar(36) NOT NULL,
  `transaction_id` varchar(36) NOT NULL,
  `direction` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKccr7q587j5bf49xqb0fqieefp` (`account_id`),
  KEY `FKewye0l6hecsa42kd4ylfiuo0h` (`transaction_id`),
  CONSTRAINT `FKccr7q587j5bf49xqb0fqieefp` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FKewye0l6hecsa42kd4ylfiuo0h` FOREIGN KEY (`transaction_id`) REFERENCES `ledger_transactions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ledger_entries`
--

LOCK TABLES `ledger_entries` WRITE;
/*!40000 ALTER TABLE `ledger_entries` DISABLE KEYS */;
INSERT INTO `ledger_entries` VALUES ('01a0d386-ac42-4f35-b618-cd48813af0bc',30.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','c1f51309-7733-4d3b-9f65-a9e9e005c541','CREDIT'),('053214df-6dea-4b2f-9242-9e454c77ace1',38.00,'ac80997a-30ac-499f-b9e8-ee5bce730622','6aed56b4-ced9-4f06-bfea-35e51cb2b1ff','DEBIT'),('08356dec-9647-483f-93e4-0727abc6f717',5.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','e373c261-8edb-4492-a715-f622e478f605','CREDIT'),('0d96f9e3-7ad8-4f3f-aa0f-89feb2bd33cb',38.00,'ac80997a-30ac-499f-b9e8-ee5bce730622','16d73dbc-3c26-4801-8c6c-2049fc0cb439','CREDIT'),('1297d5e1-5d2c-4418-a684-1addab0d89f4',7.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','4e3fe10a-1813-4839-bf11-f893f0f7a284','CREDIT'),('28389bb1-436e-4149-b487-5415bf4983c1',30.00,'459fea63-c39f-4c15-b67f-140da6913392','9b0a30cf-149b-4d1d-bca2-935b98d8011c','CREDIT'),('38f3616c-4595-47c2-80e4-664a4dddca0b',30.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','1e34f1f4-256c-4c7a-8ac6-9dbca7b13d67','DEBIT'),('47e83e81-42ea-4a1c-9703-eb008872cded',30.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','1dc791e4-ae85-431d-9892-c8a117c2ac4e','DEBIT'),('51922c89-8a11-4edd-b7af-6ab7bd36af5d',30.00,'ac80997a-30ac-499f-b9e8-ee5bce730622','9b0a30cf-149b-4d1d-bca2-935b98d8011c','DEBIT'),('56fbc992-6dfc-4ae5-a1bf-964504157abc',30.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','1e34f1f4-256c-4c7a-8ac6-9dbca7b13d67','CREDIT'),('5fa279e9-ffe4-4024-be3b-3599cc71c30f',30.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','ff8ee9c4-bad3-4908-8d68-0987e39407c3','DEBIT'),('5fb3ae9b-325d-4150-9cc7-2bd82fa81807',38.00,'459fea63-c39f-4c15-b67f-140da6913392','16d73dbc-3c26-4801-8c6c-2049fc0cb439','DEBIT'),('724c24d7-8be0-4188-8e9d-9808b82fa267',5.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','a70ba826-6b8f-4958-a8e8-4798ee4adc7e','DEBIT'),('73559ed4-04b5-4173-8394-b8398a7334fc',1.00,'458f039c-202e-4a14-9a7c-2138a90e86b0','5d35f7cf-e6f5-45de-85d6-b7f165b54517','CREDIT'),('74a9e60d-87ea-44d6-95ea-99142c0cca3f',5.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','e373c261-8edb-4492-a715-f622e478f605','DEBIT'),('7b4b7a5c-03f9-495f-96ed-1ad0969d831e',5.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','c8e08bfd-3a42-4cc9-9817-165fc158ada8','DEBIT'),('7c3ec28d-594e-4b0d-b861-d7987094a7ed',18.00,'718ac27c-998c-4af7-9a58-1fd9f600457a','39507ec8-10e6-4874-a9aa-f0eb93c9a5cb','DEBIT'),('7dfec57b-9567-42f7-aa09-2ea6b3318d2a',-10.00,'93090b02-99ce-4d9b-88a1-71c0d567292b','a107044c-01fa-4146-848f-d8f11978dc16',NULL),('93af5ecc-9b44-4299-89af-fcf37951ae30',38.00,'459fea63-c39f-4c15-b67f-140da6913392','6aed56b4-ced9-4f06-bfea-35e51cb2b1ff','CREDIT'),('94fd6b22-6f3d-49e5-9354-b4700177d529',30.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','c1f51309-7733-4d3b-9f65-a9e9e005c541','DEBIT'),('9573f91e-df38-4efa-9cca-e3ba4f54ee45',30.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','ff8ee9c4-bad3-4908-8d68-0987e39407c3','CREDIT'),('a11bbbe6-3ced-4c6b-bbba-f4a73afadc9e',7.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','4e3fe10a-1813-4839-bf11-f893f0f7a284','DEBIT'),('a3b720a3-d0bc-460e-8b5c-091de4d46b1b',30.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187','1dc791e4-ae85-431d-9892-c8a117c2ac4e','CREDIT'),('b4c2cf3a-a174-4dcc-a38c-7c6843845cec',5.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','c8e08bfd-3a42-4cc9-9817-165fc158ada8','CREDIT'),('cdf46b41-a195-424d-92d5-9b1fcfa80bff',5.00,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','a70ba826-6b8f-4958-a8e8-4798ee4adc7e','CREDIT'),('ce9d647d-dd9c-4922-acbb-3029506347c0',1.00,'9515ac5b-9947-4bfa-81ab-350bc38fda64','5d35f7cf-e6f5-45de-85d6-b7f165b54517','DEBIT'),('d3898674-d85a-4d53-9457-51d7e4d0659b',10.00,'9990305e-8b5d-4158-b9f6-7aa6a9088bb5','a107044c-01fa-4146-848f-d8f11978dc16',NULL),('e9b3a102-c780-4653-9b49-ce409bf5c692',18.00,'63cdd7b7-0a1c-4d78-9654-5d3e9f3bc1b8','39507ec8-10e6-4874-a9aa-f0eb93c9a5cb','CREDIT');
/*!40000 ALTER TABLE `ledger_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ledger_transactions`
--

DROP TABLE IF EXISTS `ledger_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ledger_transactions` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) NOT NULL,
  `reverses_transaction_id` varchar(36) DEFAULT NULL,
  `destination_account_id` varchar(36) DEFAULT NULL,
  `idempotency_key` varchar(36) DEFAULT NULL,
  `request_amount` decimal(19,2) DEFAULT NULL,
  `source_account_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg0l0q4o6c8lhintnsj714lq3c` (`reverses_transaction_id`),
  UNIQUE KEY `UK78vsrmtcnjtjj7isnp7rwk1s6` (`idempotency_key`),
  CONSTRAINT `FKlwkj4v5il32c7aa95v3ax0jdy` FOREIGN KEY (`reverses_transaction_id`) REFERENCES `ledger_transactions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ledger_transactions`
--

LOCK TABLES `ledger_transactions` WRITE;
/*!40000 ALTER TABLE `ledger_transactions` DISABLE KEYS */;
INSERT INTO `ledger_transactions` VALUES ('16d73dbc-3c26-4801-8c6c-2049fc0cb439','2026-10-28 13:50:32.355770','Reversal of transaction 6aed56b4-ced9-4f06-bfea-35e51cb2b1ff','6aed56b4-ced9-4f06-bfea-35e51cb2b1ff',NULL,NULL,NULL,NULL),('1dc791e4-ae85-431d-9892-c8a117c2ac4e','2026-10-28 13:29:20.644811','Reversal of transaction 1e34f1f4-256c-4c7a-8ac6-9dbca7b13d67','1e34f1f4-256c-4c7a-8ac6-9dbca7b13d67',NULL,NULL,NULL,NULL),('1e34f1f4-256c-4c7a-8ac6-9dbca7b13d67','2026-10-28 11:59:13.541117','Account transfer',NULL,NULL,NULL,NULL,NULL),('39507ec8-10e6-4874-a9aa-f0eb93c9a5cb','2026-10-28 14:26:24.916798','Account transfer',NULL,'63cdd7b7-0a1c-4d78-9654-5d3e9f3bc1b8','7a1e5d61-6e6b-4a38-9f82-45ad6dfc8130',18.00,'718ac27c-998c-4af7-9a58-1fd9f600457a'),('4e3fe10a-1813-4839-bf11-f893f0f7a284','2026-10-28 16:26:42.679673','Account transfer',NULL,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','a4f1c8d2-7b63-4e91-9a25-6d0b3c8e5f73',7.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187'),('5d35f7cf-e6f5-45de-85d6-b7f165b54517','2026-10-28 17:06:17.065383','Account transfer',NULL,'458f039c-202e-4a14-9a7c-2138a90e86b0','b7c2e9a1-5d43-4f86-8a21-3c6e0d9f7254',1.00,'9515ac5b-9947-4bfa-81ab-350bc38fda64'),('6aed56b4-ced9-4f06-bfea-35e51cb2b1ff','2026-10-28 13:49:24.222484','Account transfer',NULL,NULL,NULL,NULL,NULL),('9b0a30cf-149b-4d1d-bca2-935b98d8011c','2026-10-28 13:47:03.958461','Account transfer',NULL,NULL,NULL,NULL,NULL),('a107044c-01fa-4146-848f-d8f11978dc16','2026-10-28 11:36:07.753597','',NULL,NULL,NULL,NULL,NULL),('a70ba826-6b8f-4958-a8e8-4798ee4adc7e','2026-10-28 15:01:16.825186','Account transfer',NULL,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','a4f1c8d2-7b63-4e91-9a25-6d0b3c8e5f72',5.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187'),('c1f51309-7733-4d3b-9f65-a9e9e005c541','2026-10-28 11:59:05.028043','Account transfer',NULL,NULL,NULL,NULL,NULL),('c8e08bfd-3a42-4cc9-9817-165fc158ada8','2026-10-28 14:32:42.779565','Account transfer',NULL,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','2c4b0b6e-2e9a-4f25-a413-3e5a52d7c901',5.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187'),('e373c261-8edb-4492-a715-f622e478f605','2026-10-28 14:38:54.340932','Account transfer',NULL,'fc2e83d9-7df4-43b2-af67-5b38d46d4e1f','8a9b7c6d-1234-4abc-9def-0123456789ab',5.00,'9f6720b6-bafe-4864-9e06-a15c1f80d187'),('ff8ee9c4-bad3-4908-8d68-0987e39407c3','2026-10-28 11:53:43.516955','',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `ledger_transactions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-28 17:34:03
