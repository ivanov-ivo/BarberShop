-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema barbershop
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema barbershop
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `barbershop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `barbershop` ;

-- -----------------------------------------------------
-- Table `barbershop`.`appointmentDatabaseEntities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `barbershop`.`appointmentDatabaseEntities` (
  `date` DATETIME NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `phone` CHAR(13) NOT NULL,
  `id` INT NOT NULL,
  `comment` MEDIUMTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`date`, `id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `barbershop`.`barberDatabaseEntities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `barbershop`.`barberDatabaseEntities` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `photo` VARCHAR(1500) NOT NULL,
  `branch` VARCHAR(50) NOT NULL,
  `information` MEDIUMTEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `barbershop`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `barbershop`.`users` (
  `username` VARCHAR(50) NOT NULL,
  `password` CHAR(68) NOT NULL,
  `enabled` TINYINT NOT NULL,
  `id` VARCHAR(50) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


INSERT INTO `users` 
VALUES 
('admin@gmail.com','$2a$12$RBJfkrZTntSXuIXA4uJmcuwgAle5MjN305oWPLR7fUEUa4gNzZL32', 1, 1, 'ADMIN'),
('barberDatabaseEntity@gmail.com', '$2a$12$/XOjV1fUA4tFCG9diOqWuupp4YpUNJ44ct5dgcNKXzq2gzU0TmXPS', 1, 2, 'BARBER');

INSERT INTO `barberDatabaseEntities`
VALUES 
('1', 'Ivo Ivanov','images/barberDatabaseEntity/Shutterstock_2302169201-1024x512.jpg', 'Shumen', 'Admin of the Gentlemen\'s BarberDatabaseEntity Shop'),
('2', 'Peter Johnson','images/barberDatabaseEntity/Shutterstock_2302169201-1024x512.jpg', 'Shumen', 'Worker');

insert into `appointmentDatabaseEntities`
values
('2025-05-31 11:00:00', 'Nikola Zhekov', '+359897371823', 2, 'Buzz Cut'),
('2025-05-31 16:00:00', 'Yavor Vasilev', '+359897371823', 2, 'Low Taper Fade'),
('2025-06-26 15:00:00', 'Velislav Borisov', '+359897371823', 2, 'Mid Taper Fade'),
('2025-06-27 20:00:00', 'Martin Nedev', '+359897371823', 2, 'French Crop'),
('2025-06-25 11:00:00', 'Serkan Rashid', '+359897371823', 2, 'French Crop'),
('2025-06-23 22:00:00', 'Nikola Zhekov', '+359897371823', 2, 'Mid Taper Fade');

