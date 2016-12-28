CREATE SCHEMA IF NOT EXISTS `api-server` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use `api-server`;

CREATE TABLE IF NOT EXISTS `group_user` (
  `groupno` bigint(20) NOT NULL,
  `userno` bigint(20) NOT NULL,
  `writedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `addeduserno` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`groupno`,`userno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `groups` (
  `groupno` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `addeduserno` bigint(20) DEFAULT NULL,
  `writedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`groupno`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `role_target` (
  `roleauthno` bigint(20) NOT NULL AUTO_INCREMENT,
  `roleno` bigint(20) DEFAULT NULL COMMENT '소속된 롤의 번',
  `targetURI` varchar(120) DEFAULT NULL,
  `targetMethod` char(1) DEFAULT 'A' COMMENT 'A = ALL, G = GET, P = POST, U = PUT, D = DELETE',
  `isDenied` char(1) DEFAULT 'Y',
  `addeduserno` bigint(20) DEFAULT NULL,
  `writedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`roleauthno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='권한대상에 대한 테이블, 해당 URI로 접근시 막는 역할';

CREATE TABLE IF NOT EXISTS `role_user` (
  `userno` bigint(20) NOT NULL,
  `groupno` bigint(20) NOT NULL,
  `roleno` bigint(20) NOT NULL,
  `writedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `addeduserno` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`userno`,`groupno`,`roleno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='user가 소유하고 있는 롤의 정보, 말로 표현하면 "A 유저는 B 그룹의 C 롤을 소유하고 있다"가 된다.';

CREATE TABLE IF NOT EXISTS `roles` (
  `roleno` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  `groupno` bigint(20) DEFAULT NULL COMMENT '롤이 소속된 그룹의 id',
  `addeduserno` bigint(20) DEFAULT NULL,
  `writedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`roleno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `users` (
  `userno` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(120) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(200) NOT NULL,
  `isdeleted` char(1) DEFAULT 'N',
  `isEmailAuth` char(1) DEFAULT 'N',
  `lastLogin` timestamp NULL DEFAULT NULL,
  `writedate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `grantuserno` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userno`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
