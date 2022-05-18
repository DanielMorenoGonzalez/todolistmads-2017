-- MySQL dump 10.13  Distrib 5.7.19, for Linux (x86_64)
--
-- Host: localhost    Database: mads
-- ------------------------------------------------------
-- Server version	5.7.19

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
-- Table structure for table `Etiqueta`
--

DROP TABLE IF EXISTS `Etiqueta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Etiqueta` (
  `id` bigint(20) NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `tableroId` bigint(20) DEFAULT NULL,
  `usuarioId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqhvnrs00mpblhwhp2ondwu2vo` (`tableroId`),
  KEY `FK50l4987uvmbkiqihaqymy2hlo` (`usuarioId`),
  CONSTRAINT `FK50l4987uvmbkiqihaqymy2hlo` FOREIGN KEY (`usuarioId`) REFERENCES `Usuario` (`id`),
  CONSTRAINT `FKqhvnrs00mpblhwhp2ondwu2vo` FOREIGN KEY (`tableroId`) REFERENCES `Tablero` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Persona_Tablero`
--

DROP TABLE IF EXISTS `Persona_Tablero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Persona_Tablero` (
  `tableros_id` bigint(20) NOT NULL,
  `participantes_id` bigint(20) NOT NULL,
  PRIMARY KEY (`tableros_id`,`participantes_id`),
  KEY `FKnghbrhyh7eal30o78h3293n72` (`participantes_id`),
  CONSTRAINT `FKbpw5yq3ofgud0ra8a916kddjm` FOREIGN KEY (`tableros_id`) REFERENCES `Tablero` (`id`),
  CONSTRAINT `FKnghbrhyh7eal30o78h3293n72` FOREIGN KEY (`participantes_id`) REFERENCES `Usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Tablero`
--

DROP TABLE IF EXISTS `Tablero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tablero` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `privado` bit(1) DEFAULT NULL,
  `administradorId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq82919iay2b8h77msdj8289p0` (`administradorId`),
  CONSTRAINT `FKq82919iay2b8h77msdj8289p0` FOREIGN KEY (`administradorId`) REFERENCES `Usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Tarea`
--

DROP TABLE IF EXISTS `Tarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tarea` (
  `id` bigint(20) NOT NULL,
  `cuerpo` varchar(255) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT NULL,
  `fechaInicio` datetime DEFAULT NULL,
  `fechaLimite` datetime DEFAULT NULL,
  `terminada` bit(1) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `etiquetaId` bigint(20) DEFAULT NULL,
  `tableroId` bigint(20) DEFAULT NULL,
  `usuarioId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh8to8ffsrne4k6xq3k3jqq1p9` (`etiquetaId`),
  KEY `FK2r7tsv4xu3bjvst83o8xuspud` (`tableroId`),
  KEY `FKepne2t52y8dmn8l9da0dd7l51` (`usuarioId`),
  CONSTRAINT `FK2r7tsv4xu3bjvst83o8xuspud` FOREIGN KEY (`tableroId`) REFERENCES `Tablero` (`id`),
  CONSTRAINT `FKepne2t52y8dmn8l9da0dd7l51` FOREIGN KEY (`usuarioId`) REFERENCES `Usuario` (`id`),
  CONSTRAINT `FKh8to8ffsrne4k6xq3k3jqq1p9` FOREIGN KEY (`etiquetaId`) REFERENCES `Etiqueta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Usuario`
--

DROP TABLE IF EXISTS `Usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Usuario` (
  `id` bigint(20) NOT NULL,
  `apellidos` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `vistaCalendario` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (1),(1);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-12-21 21:38:34
