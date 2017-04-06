-- MySQL dump 10.13  Distrib 5.6.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: quickonnect
-- ------------------------------------------------------
-- Server version	5.6.31-0ubuntu0.14.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `messagesintable`
--

DROP TABLE IF EXISTS `messagesintable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messagesintable` (
  `MsgId` int(11) NOT NULL AUTO_INCREMENT,
  `Address` longtext,
  `Attachment` blob,
  `CustomData` text,
  `Date` date DEFAULT NULL,
  `Message` text,
  `MsgType` int(11) DEFAULT NULL,
  `Provider` varchar(255) DEFAULT NULL,
  `Time` time DEFAULT NULL,
  `Version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MsgId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messagesintable`
--

LOCK TABLES `messagesintable` WRITE;
/*!40000 ALTER TABLE `messagesintable` DISABLE KEYS */;
INSERT INTO `messagesintable` VALUES (1,' <tolist><to><name>User</name><contactpersonname>user</contactpersonname><number>+91.8220551373</number><email>noreply@qk.in</email><prefix>Mr.</prefix><contactpersonprefix>Mr.</contactpersonprefix></to></tolist><from><name>quickonnect</name><number>0000</number></from>',NULL,'0,0','2016-12-20','<sid>QKONCT</sid><shorttext>Hi chris ! You have been added as an Customer of PAVAN COMPANY through Quickonnect. To get connected, download the app now https://goo.gl/MVwbn4</shorttext>',1,NULL,'10:38:28',NULL),(2,' <tolist><to><name>User</name><contactpersonname>user</contactpersonname><number>+91.8220551373</number><email>noreply@qk.in</email><prefix>Mr.</prefix><contactpersonprefix>Mr.</contactpersonprefix></to></tolist><from><name>quickonnect</name><number>0000</number></from>',NULL,'0,0','2016-12-21','<sid>QKONCT</sid><shorttext>Hi chris ! You have been added as an Customer of PAVAN COMPANY through Quickonnect. To get connected, download the app now https://goo.gl/MVwbn4</shorttext>',1,NULL,'18:13:55',NULL),(3,' <tolist><to><name>User</name><contactpersonname>user</contactpersonname><number>+91.8220551373</number><email>noreply@qk.in</email><prefix>Mr.</prefix><contactpersonprefix>Mr.</contactpersonprefix></to></tolist><from><name>quickonnect</name><number>0000</number></from>',NULL,'0,0','2016-12-21','<sid>QKONCT</sid><shorttext>Hi chris ! You have been added as an Customer of PAVAN COMPANY through Quickonnect. To get connected, download the app now https://goo.gl/MVwbn4</shorttext>',1,NULL,'18:15:12',NULL),(4,' <tolist><to><name>User</name><contactpersonname>user</contactpersonname><number>+91.8220551373</number><email>noreply@qk.in</email><prefix>Mr.</prefix><contactpersonprefix>Mr.</contactpersonprefix></to></tolist><from><name>quickonnect</name><number>0000</number></from>',NULL,'0,0','2017-01-13','<sid>QKONCT</sid><shorttext>Hi chris ! You have been added as an Employee of PAVAN COMPANY through Quickonnect. To get connected, download the app now https://goo.gl/0hs3G6  and get connected. Business # : 000001</shorttext>',1,NULL,'13:38:09',NULL);
/*!40000 ALTER TABLE `messagesintable` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-02-08 14:35:17
