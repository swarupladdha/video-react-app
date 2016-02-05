--
-- Table structure for table `lookuptable`
--

DROP TABLE IF EXISTS `lookuptable`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `lookuptable` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Extension` varchar(1000) DEFAULT NULL,
  `ContentType` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `lookuptable`
--

LOCK TABLES `lookuptable` WRITE;
/*!40000 ALTER TABLE `lookuptable` DISABLE KEYS */;
INSERT INTO `lookuptable` VALUES (1,'doc','application/msword'),(2,'gif','image/gif'),(3,'html','text/html'),(4,'jar','application/java-archive'),(5,'jpg','image/jpeg'),(6,'pdf','application/pdf'),(7,'txt','text/plain'),(8,'xls','application/msexcel'),(9,'xml','application/xml'),(10,'zip','application/x-zip-compressed');
/*!40000 ALTER TABLE `lookuptable` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `messagesintable`
--

DROP TABLE IF EXISTS `messagesintable`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `messagesintable` (
  `MsgId` int(11) NOT NULL AUTO_INCREMENT,
  `MsgType` int(11) DEFAULT NULL,
  `Version` varchar(90) DEFAULT NULL,
  `Address` longtext,
  `Message` longtext,
  `Provider` longtext,
  `Attachment` longblob,
  `Time` time DEFAULT NULL,
  `Date` datetime DEFAULT NULL,
  `CustomData` longtext,
  PRIMARY KEY (`MsgId`)
) ENGINE=MyISAM AUTO_INCREMENT=164601 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `messagesintable`
--

LOCK TABLES `messagesintable` WRITE;
/*!40000 ALTER TABLE `messagesintable` DISABLE KEYS */;
/*!40000 ALTER TABLE `messagesintable` ENABLE KEYS */;
UNLOCK TABLES;

