create database ffs;

use ffs;

create table user(uid int primary key auto_increment,
role tinyint,
username varchar(20) unique,
password varchar(20),
name varchar(25),
tel varchar(20),
address varchar(50),
info varchar(50));

create table orders(oid int primary key auto_increment,
cid int,
sid int,
bid int,
state tinyint,
info varchar(50));

create table goods(gid int primary key auto_increment,
name varchar(25),
image varchar(50) unique,
price float,
score tinyint,
info varchar(50));

create table goods_list(lid int primary key auto_increment,
gid int,
oid int,
foreign key(gid) references goods(gid),
foreign key(oid) references orders(oid));

