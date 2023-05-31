-- MySQL dump 10.13  Distrib 8.0.33, for Linux (x86_64)
--
-- Host: localhost    Database: calschedule
-- ------------------------------------------------------
-- Server version	8.0.33

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
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientId` bigint NOT NULL,
  `VendorId` varchar(255) DEFAULT NULL,
  `ResourceId` varchar(255) DEFAULT NULL,
  `UserId` varchar(255) DEFAULT NULL,
  `CreatedDate` datetime DEFAULT NULL,
  `StartTime` datetime DEFAULT NULL,
  `EndTime` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `ClientId` (`ClientId`),
  CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`ClientId`) REFERENCES `clientscreat` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` VALUES (1,4,'-1','abc','aaa','2023-05-30 20:18:47','2023-06-09 12:30:00','2023-06-09 13:00:00'),(2,4,'-1','abc','aaa','2023-05-30 20:19:01','2023-06-08 11:30:00','2023-06-08 12:00:00'),(3,4,'-1','abc','aaa','2023-05-30 20:19:13','2023-06-07 10:30:00','2023-06-07 11:00:00'),(4,4,'-1','abc','aaa','2023-05-30 20:19:20','2023-06-07 13:30:00','2023-06-07 14:00:00'),(5,4,'-1','abc','aaa','2023-05-31 10:02:03','2023-06-06 13:30:00','2023-06-06 14:00:00');
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientcontact`
--

DROP TABLE IF EXISTS `clientcontact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientcontact` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientId` int NOT NULL,
  `orgName` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientcontact`
--

LOCK TABLES `clientcontact` WRITE;
/*!40000 ALTER TABLE `clientcontact` DISABLE KEYS */;
INSERT INTO `clientcontact` VALUES (1,1,'vedic remedi','tirupathi','1234'),(2,2,'vedic remedi','bangalore','1234567890'),(3,3,'shortlisted','bangalore','1234567890'),(4,4,'shopify','bangalore','1234567890');
/*!40000 ALTER TABLE `clientcontact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientdefaultavailability`
--

DROP TABLE IF EXISTS `clientdefaultavailability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientdefaultavailability` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientId` bigint NOT NULL,
  `weekdayId` bigint NOT NULL,
  `startTime` time DEFAULT NULL,
  `endTime` time DEFAULT NULL,
  `working` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `ClientId` (`ClientId`),
  KEY `weekdayId` (`weekdayId`),
  CONSTRAINT `clientdefaultavailability_ibfk_1` FOREIGN KEY (`ClientId`) REFERENCES `clientscreat` (`Id`),
  CONSTRAINT `clientdefaultavailability_ibfk_2` FOREIGN KEY (`weekdayId`) REFERENCES `week` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientdefaultavailability`
--

LOCK TABLES `clientdefaultavailability` WRITE;
/*!40000 ALTER TABLE `clientdefaultavailability` DISABLE KEYS */;
/*!40000 ALTER TABLE `clientdefaultavailability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientscreat`
--

DROP TABLE IF EXISTS `clientscreat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientscreat` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientKey` varchar(255) DEFAULT NULL,
  `CreatedDate` datetime DEFAULT NULL,
  `ExpiryDate` datetime DEFAULT NULL,
  `Status` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientscreat`
--

LOCK TABLES `clientscreat` WRITE;
/*!40000 ALTER TABLE `clientscreat` DISABLE KEYS */;
INSERT INTO `clientscreat` VALUES (1,'320036cf-0d85-436c-8177-1a72ce451707','2023-05-17 11:12:19','2024-05-17 11:12:19',0),(2,'364250d3-0465-4a06-910b-d1494cd30afc','2023-05-17 11:59:55','2024-05-17 11:59:55',0),(3,'2e427eb5-9b8e-47ce-a1a2-1ab39f4d04f4','2023-05-18 10:36:10','2024-05-18 10:36:10',0),(4,'2b3ad54d-075d-464d-9d76-d8abf679fe03','2023-05-18 10:37:45','2024-05-18 10:37:45',0);
/*!40000 ALTER TABLE `clientscreat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientvendor`
--

DROP TABLE IF EXISTS `clientvendor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientvendor` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientId` bigint NOT NULL,
  `VendorId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `ClientId` (`ClientId`),
  CONSTRAINT `clientvendor_ibfk_1` FOREIGN KEY (`ClientId`) REFERENCES `clientscreat` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientvendor`
--

LOCK TABLES `clientvendor` WRITE;
/*!40000 ALTER TABLE `clientvendor` DISABLE KEYS */;
INSERT INTO `clientvendor` VALUES (1,4,'abc'),(3,4,'-1');
/*!40000 ALTER TABLE `clientvendor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resourcegeneralavailability`
--

DROP TABLE IF EXISTS `resourcegeneralavailability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resourcegeneralavailability` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientId` bigint NOT NULL,
  `VendorId` varchar(255) DEFAULT NULL,
  `ClientVendorId` bigint NOT NULL,
  `ResourceId` varchar(255) DEFAULT NULL,
  `VendorResourceId` bigint NOT NULL,
  `CreatedDate` datetime DEFAULT NULL,
  `UpdatedDate` datetime DEFAULT NULL,
  `weekdayId` bigint NOT NULL,
  `startTime` time DEFAULT NULL,
  `endTime` time DEFAULT NULL,
  `working` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `ClientId` (`ClientId`),
  KEY `weekdayId` (`weekdayId`),
  KEY `ClientVendorId` (`ClientVendorId`),
  KEY `VendorResourceId` (`VendorResourceId`),
  CONSTRAINT `resourcegeneralavailability_ibfk_1` FOREIGN KEY (`ClientId`) REFERENCES `clientscreat` (`Id`),
  CONSTRAINT `resourcegeneralavailability_ibfk_2` FOREIGN KEY (`weekdayId`) REFERENCES `week` (`Id`),
  CONSTRAINT `resourcegeneralavailability_ibfk_3` FOREIGN KEY (`ClientVendorId`) REFERENCES `clientvendor` (`Id`),
  CONSTRAINT `resourcegeneralavailability_ibfk_4` FOREIGN KEY (`VendorResourceId`) REFERENCES `vendorresource` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resourcegeneralavailability`
--

LOCK TABLES `resourcegeneralavailability` WRITE;
/*!40000 ALTER TABLE `resourcegeneralavailability` DISABLE KEYS */;
INSERT INTO `resourcegeneralavailability` VALUES (1,4,'-1',3,'abc',4,'2023-05-31 01:47:38','2023-05-31 01:47:38',2,'09:00:00','17:00:00',1),(2,4,'-1',3,'abc',4,'2023-05-31 01:47:57','2023-05-31 01:47:57',3,'08:00:00','15:00:00',1),(3,4,'-1',3,'abc',4,'2023-05-31 01:48:20','2023-05-31 01:48:20',4,'09:00:00','16:00:00',1),(4,4,'-1',3,'abc',4,'2023-05-31 01:48:28','2023-05-31 01:48:28',5,'09:00:00','16:00:00',1),(5,4,'-1',3,'abc',4,'2023-05-31 01:48:35','2023-05-31 01:48:35',6,'09:00:00','16:00:00',1),(6,4,'-1',3,'aaa',6,'2023-05-31 15:31:49','2023-05-31 15:31:49',6,'09:00:00','16:00:00',1);
/*!40000 ALTER TABLE `resourcegeneralavailability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `slot`
--

DROP TABLE IF EXISTS `slot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slot` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `StartTime` time DEFAULT NULL,
  `EndTime` time DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slot`
--

LOCK TABLES `slot` WRITE;
/*!40000 ALTER TABLE `slot` DISABLE KEYS */;
/*!40000 ALTER TABLE `slot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `slottime`
--

DROP TABLE IF EXISTS `slottime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slottime` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `slotTime` time DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slottime`
--

LOCK TABLES `slottime` WRITE;
/*!40000 ALTER TABLE `slottime` DISABLE KEYS */;
INSERT INTO `slottime` VALUES (1,'00:30:00'),(2,'00:45:00'),(3,'01:00:00');
/*!40000 ALTER TABLE `slottime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendordefaultavailability`
--

DROP TABLE IF EXISTS `vendordefaultavailability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendordefaultavailability` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientId` bigint NOT NULL,
  `VendorId` varchar(255) DEFAULT NULL,
  `ClientVendorId` bigint NOT NULL,
  `weekdayId` bigint NOT NULL,
  `startTime` time DEFAULT NULL,
  `endTime` time DEFAULT NULL,
  `working` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `ClientId` (`ClientId`),
  KEY `weekdayId` (`weekdayId`),
  KEY `ClientVendorId` (`ClientVendorId`),
  CONSTRAINT `vendordefaultavailability_ibfk_1` FOREIGN KEY (`ClientId`) REFERENCES `clientscreat` (`Id`),
  CONSTRAINT `vendordefaultavailability_ibfk_2` FOREIGN KEY (`weekdayId`) REFERENCES `week` (`Id`),
  CONSTRAINT `vendordefaultavailability_ibfk_3` FOREIGN KEY (`ClientVendorId`) REFERENCES `clientvendor` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendordefaultavailability`
--

LOCK TABLES `vendordefaultavailability` WRITE;
/*!40000 ALTER TABLE `vendordefaultavailability` DISABLE KEYS */;
/*!40000 ALTER TABLE `vendordefaultavailability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendorresource`
--

DROP TABLE IF EXISTS `vendorresource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendorresource` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `ClientId` bigint NOT NULL,
  `VendorId` varchar(255) DEFAULT NULL,
  `ResourceId` varchar(255) DEFAULT NULL,
  `ClientVendorId` bigint DEFAULT NULL,
  `SlotTimeId` bigint DEFAULT NULL,
  `TimeZone` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `ClientId` (`ClientId`),
  KEY `resourcevendor_ibfk_2` (`ClientVendorId`),
  CONSTRAINT `vendorresource_ibfk_1` FOREIGN KEY (`ClientId`) REFERENCES `clientscreat` (`Id`),
  CONSTRAINT `vendorresource_ibfk_2` FOREIGN KEY (`ClientVendorId`) REFERENCES `clientvendor` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendorresource`
--

LOCK TABLES `vendorresource` WRITE;
/*!40000 ALTER TABLE `vendorresource` DISABLE KEYS */;
INSERT INTO `vendorresource` VALUES (1,4,'abc','bbb',1,1,NULL),(2,4,'abc','aaa',1,1,NULL),(3,4,'-1','cba',NULL,1,NULL),(4,4,'-1','abc',NULL,1,NULL),(5,4,'-1','aaa12',3,1,'Asia/Kolkota'),(6,4,'-1','aaa',3,1,'Asia/Kolkota');
/*!40000 ALTER TABLE `vendorresource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `week`
--

DROP TABLE IF EXISTS `week`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `week` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `weekday` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `week`
--

LOCK TABLES `week` WRITE;
/*!40000 ALTER TABLE `week` DISABLE KEYS */;
INSERT INTO `week` VALUES (1,'Sunday'),(2,'Monday'),(3,'Tuesday'),(4,'Wednesday'),(5,'Thursday'),(6,'Friday'),(7,'Saturday');
/*!40000 ALTER TABLE `week` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-31 15:34:38
