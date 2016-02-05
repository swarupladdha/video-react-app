-- MySQL dump 10.13  Distrib 5.1.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: emessageservicing
-- ------------------------------------------------------
-- Server version	5.1.31-1ubuntu2

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
-- Current Database: `emessageservicing`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `emessageservicing` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `emessageservicing`;

--
-- Table structure for table `messageprocess`
--

DROP TABLE IF EXISTS `messageprocess`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `messageprocess` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `MsgId` int(11) NOT NULL,
  `MsgType` int(11) DEFAULT NULL,
  `Address` varchar(1000) DEFAULT NULL,
  `Message` varchar(1000) DEFAULT NULL,
  `Date` date DEFAULT NULL,
  `Time` time DEFAULT NULL,
  `Count` int(11) DEFAULT NULL,
  `Status` varchar(200) DEFAULT NULL,
  `BounceRslt` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=372 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `messageprocess`
--

LOCK TABLES `messageprocess` WRITE;
/*!40000 ALTER TABLE `messageprocess` DISABLE KEYS */;
/*!40000 ALTER TABLE `messageprocess` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messagesfailed`
--

DROP TABLE IF EXISTS `messagesfailed`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `messagesfailed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `MsgId` int(11) NOT NULL,
  `MsgType` int(11) NOT NULL,
  `Address` varchar(700) DEFAULT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `Count` int(11) DEFAULT NULL,
  `time` time DEFAULT NULL,
  `Date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=169 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `messagesfailed`
--

LOCK TABLES `messagesfailed` WRITE;
/*!40000 ALTER TABLE `messagesfailed` DISABLE KEYS */;
/*!40000 ALTER TABLE `messagesfailed` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `messagesintable`
--

LOCK TABLES `messagesintable` WRITE;
/*!40000 ALTER TABLE `messagesintable` DISABLE KEYS */;
/*!40000 ALTER TABLE `messagesintable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messagessuccess`
--

DROP TABLE IF EXISTS `messagessuccess`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `messagessuccess` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `MsgId` int(11) NOT NULL,
  `MsgType` int(11) NOT NULL,
  `Address` varchar(700) DEFAULT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `Count` int(11) DEFAULT NULL,
  `time` time DEFAULT NULL,
  `Date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=87 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `messagessuccess`
-- @author Sushma
--

DROP TABLE IF EXISTS `lookuptable`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lookuptable` ( 
  `Id` int(11) NOT NULL AUTO_INCREMENT,   
  `Extension` varchar(1000) DEFAULT NULL,
  `ContentType` varchar(2000) DEFAULT NULL,  
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for `lookuptable`
-- @author Sushma P
--

insert into lookuptable(Extension, ContentType) values ('doc', 'application/msword');

insert into lookuptable(Extension, ContentType) values ('gif', 'image/gif');

insert into lookuptable(Extension, ContentType) values ('html', 'text/html');

insert into lookuptable(Extension, ContentType) values ('jar', 'application/java-archive');

insert into lookuptable(Extension, ContentType) values ('jpg', 'image/jpeg');

insert into lookuptable(Extension, ContentType) values ('pdf', 'application/pdf');

insert into lookuptable(Extension, ContentType) values ('txt', 'text/plain');

insert into lookuptable(Extension, ContentType) values ('xls', 'application/msexcel');

insert into lookuptable(Extension, ContentType) values ('xml', 'application/xml');

insert into lookuptable(Extension, ContentType) values ('zip', 'application/x-zip-compressed');

--
-- Table structure for table `messagesintable`
-- @author Sushma
--

DROP TABLE IF EXISTS `messagesintable`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `messagesintable` (
  `MsgId` int(11) NOT NULL AUTO_INCREMENT,
  `MsgType` int(11) DEFAULT NULL,
  `Version` varchar(90) DEFAULT NULL,
  `Address` longtext DEFAULT NULL,
  `Message` longtext DEFAULT NULL,
  `Attachment`  longblob DEFAULT NULL,
  `Time` time DEFAULT NULL,
  `Date` datetime DEFAULT NULL,
  `CustomData` longtext DEFAULT NULL,
  PRIMARY KEY (`MsgId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `messages_sent_table`
-- @author Sushma
--

DROP TABLE IF EXISTS `messages_sent_table`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `messages_sent_table` ( 
  `Id` int(11) NOT NULL AUTO_INCREMENT, 
  `MsgId` int(11) DEFAULT NULL,   
  `MsgType` int(11) DEFAULT NULL,
  `ResponseId` int(100) DEFAULT NULL,
  `RetryCount` int(90) DEFAULT NULL,
  `Address` longtext DEFAULT NULL,
  `Message` longtext DEFAULT NULL,
  `Provider` longtext DEFAULT NULL,  
  `Received_Date` datetime DEFAULT NULL,
  `CustomData` longtext DEFAULT NULL,
  PRIMARY KEY (`Id`) 	
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `status_table`
-- @author Sushma
--

DROP TABLE IF EXISTS `status_table`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `status_table` ( 
  `Id` int(12) NOT NULL AUTO_INCREMENT, 
  `mobileNumber` varchar(300) DEFAULT NULL,
  `status` int(12) DEFAULT NULL,
  `jobDoneTimeStamp` datetime DEFAULT NULL,  
  PRIMARY KEY (`id`) 	
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `messagessuccess`
--

LOCK TABLES `messagessuccess` WRITE;
/*!40000 ALTER TABLE `messagessuccess` DISABLE KEYS */;
/*!40000 ALTER TABLE `messagessuccess` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-11-02  9:53:33
