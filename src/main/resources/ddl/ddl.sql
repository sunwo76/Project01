USE edu;

CREATE TABLE `USER` (
 user_no int not null AUTO_INCREMENT PRIMARY KEY,
 user_nm varchar(100) not null,
 user_id varchar(100) not null,
 user_pwd varchar(100) not null,
 user_sex boolean,
 user_age int,
 user_del boolean not null default 0,
 user_reg_date DATETIME not null default now(),
 user_up_date DATETIME
);

create table `BOARD` (
 board_no int not null AUTO_INCREMENT PRIMARY KEY,
 board_title varchar(100) not null,
 board_content varchar(255),
 board_del boolean not null default 0,
 board_reg_date DATETIME not null default now(),
 board_up_date DATETIME,
 user_no int not null,
 INDEX `FK_board_user` (`user_no`) USING BTREE,
 CONSTRAINT `FK_board_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON UPDATE NO ACTION ON DELETE NO ACTION
);
