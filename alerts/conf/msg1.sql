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
INSERT INTO `messagesintable` VALUES (1,' <tolist><to><name>User1</name><contactpersonname>user1</contactpersonname><number>+91.8220551373</number><email>noreply@qk.in</email><prefix>Mr.</prefix><contactpersonprefix>Mr.</contactpersonprefix></to><to><name>User2</name><contactpersonname>user2</contactpersonname><number>+91.9626388351</number><email>noreply@qk.in</email><prefix>Mr.</prefix><contactpersonprefix>Mr.</contactpersonprefix></to></tolist><from><name>quickonnect</name><number>0000</number></from>',NULL,'0,0','2016-12-20','<sid>QKONCT</sid><shorttext>Hi chris ! You have been added as an Customer of PAVAN COMPANY through Quickonnect. To get connected, download the app now https://goo.gl/MVwbn4</shorttext>',1,NULL,'10:38:28',NULL);

UNLOCK TABLES;
