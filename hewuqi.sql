/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2018/3/15 17:09:57                           */
/*==============================================================*/


drop table if exists tb_user;

/*==============================================================*/
/* Table: tb_user                                               */
/*==============================================================*/
create table tb_user
(
   ID                   int not null,
   username             varchar(20),
   password             varchar(20),
   idCardNum            varchar(20) not null,
   registDate           date not null,
   expiryDate           date,
   isValid              tinyint not null,
   primary key (ID)
);
