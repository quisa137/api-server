CREATE SCHEMA IF NOT EXISTS `api-server` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use `api-server`;
CREATE TABLE IF NOT EXISTS `api-server`.`users` (
	`userno` bigint not null auto_increment,
	`username` varchar(120),
    `email` varchar(200) not null unique,
    `password` varchar(255) not null,
    primary key(`userno`) 
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `api-server`.`groups` (
  `groupno` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  `desc` VARCHAR(500) NULL,
  PRIMARY KEY (`groupno`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `api-server`.`group_user` (
  `groupno` BIGINT NOT NULL,
  `userno` BIGINT NOT NULL,
  PRIMARY KEY (`groupno`,`userno`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;
