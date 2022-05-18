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

ALTER TABLE `Tablero`
ADD `privado` bit(1) DEFAULT NULL;

ALTER TABLE `Tarea`
ADD `fechaInicio` datetime DEFAULT NULL,
ADD CONSTRAINT `FKh8to8ffsrne4k6xq3k3jqq1p9` FOREIGN KEY (`etiquetaId`) REFERENCES `Etiqueta` (`id`);

ALTER TABLE `Usuario`
ADD `vistaCalendario` int(11) DEFAULT NULL;
