/*
SQLyog Community v13.1.2 (64 bit)
MySQL - 10.1.36-MariaDB : Database - membership_management_app
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`membership_management_app` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `membership_management_app`;

/*Table structure for table `fee` */

DROP TABLE IF EXISTS `fee`;

CREATE TABLE `fee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unit_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `fee_type_id` int(11) NOT NULL,
  `time_added` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_fee_unit` (`unit_id`),
  KEY `FK_fee_user` (`user_id`),
  KEY `FK_fee_fee_type` (`fee_type_id`),
  CONSTRAINT `FK_fee_fee_type` FOREIGN KEY (`fee_type_id`) REFERENCES `fee_type` (`id`),
  CONSTRAINT `FK_fee_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`id`),
  CONSTRAINT `FK_fee_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;


/*Table structure for table `fee_type` */

DROP TABLE IF EXISTS `fee_type`;

CREATE TABLE `fee_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unit_id` int(11) NOT NULL,
  `name` varchar(120) NOT NULL,
  `description` longtext,
  `price` double NOT NULL,
  `time_added` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_fee_type_unit` (`unit_id`),
  CONSTRAINT `FK_fee_type_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

/*Table structure for table `unit` */

DROP TABLE IF EXISTS `unit`;

CREATE TABLE `unit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` enum('ACTIVE','BLOCKED','DELETED') NOT NULL DEFAULT 'ACTIVE',
  `name` varchar(50) NOT NULL,
  `support_email` varchar(50) NOT NULL,
  `time_added` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Data for the table `unit` */

insert  into `unit`(`id`,`status`,`name`,`support_email`,`time_added`,`last_modified`) values 
(1,'ACTIVE','AEK','aek@aek.com','2017-03-07 09:33:59','2019-05-01 13:47:54'),

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `unit_id` int(11) NOT NULL,
  `status` enum('ENABLED','DISABLED','DELETED') NOT NULL DEFAULT 'ENABLED',
  `name` varchar(100) NOT NULL,
  `email` varchar(120) NOT NULL,
  `email_token` varchar(64) DEFAULT NULL,
  `email_time_requested` datetime DEFAULT NULL,
  `email_time_confirmed` datetime DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `time_added` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_user_unit` (`unit_id`),
  KEY `FK_user_role` (`role_id`),
  CONSTRAINT `FK_user_role` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`id`),
  CONSTRAINT `FK_user_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`role_id`,`unit_id`,`status`,`name`,`email`,`email_token`,`email_time_requested`,`email_time_confirmed`,`password`,`time_added`,`last_modified`) values 
(1,1,1,'ENABLED','Admin Admin','admin@aek.com',NULL,NULL,NULL,'$2a$10$T6j2wsbRUzGKsV7JMPyIrOFkImzqC5S5bfGcek62vXgTJZB5Zt6LC','2017-03-21 15:27:58','2019-05-01 21:14:34'),

/*Table structure for table `user_right` */

DROP TABLE IF EXISTS `user_right`;

CREATE TABLE `user_right` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `category` enum('USERS','FEE_TYPES','FEES','REPORTS') NOT NULL DEFAULT 'USERS',
  `view` tinyint(1) NOT NULL,
  `edit` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_user_right` (`user_id`),
  CONSTRAINT `FK_user_right` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

/*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `user_role` */

insert  into `user_role`(`id`,`name`,`description`) values 
(1,'ADMIN','Administrator'),
(2,'MODERATOR','Moderator'),
(3,'USER','User');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
