/**
 * 数据库基本结构
 * @author hoshinosena
 * @version 1.1
 */
create database ffs;

use ffs;

create table user(
uid int primary key auto_increment,
role tinyint,
# 注销的用户 username 设为 null
# mysql 中 unique 属性允许重复的 null
username varchar(20) unique,
password varchar(20),
name varchar(25),
# 1.1添加, 使用 uuid(32字符) 作为头像地址
image char(32),
tel varchar(20),
address varchar(50),
info varchar(50),
# 删除标志位
del tinyint);

create table _order(
oid int primary key auto_increment,
# 交易状态(未支付,支付,商家已接单,骑手已取得,配送完了)
state tinyint,
# 下注用户
bid int,
# 受注商家
# NULL表示空
sid int,
# 配送骑手
# NULL表示空
did int,
# 付款凭证
tid varchar(50),
date datetime,
info varchar(50),
# 删除标志位
del tinyint,
foreign key(bid) references user(uid),
foreign key(sid) references user(uid),
foreign key(did) references user(uid));

create table product(
pid int primary key auto_increment,
uid int,
name varchar(10),
# 使用 uuid(32字符) 作为地址
image char(32),
price float,
# 最低10分(1.0)
# 默认0分表示没有评价
score tinyint,
info varchar(50),
# 删除标志位
del tinyint,
foreign key(uid) references user(uid));

create table review(
rid int primary key auto_increment,
oid int,
# 1.1修改 pid 为 uid
# 只能对商家评论
uid int,
score tinyint,
detail varchar(100),
date datetime,
# 删除标志位
del tinyint,
foreign key (oid) references _order(oid),
foreign key(uid) references user(uid));

# 伺服器内部中间表, 不对外开放
create table listing(
lid int primary key auto_increment,
# 冗余, 鉴权时不必查询
uid int,
oid int,
pid int,
# product 的 update 使用 insert+delete 实现
# 节省以下字段
# name varchar(10),
# image char(32),
# price float,
# info varchar(50),
amount tinyint,
foreign key(uid) references user(uid),
foreign key(pid) references product(pid),
foreign key(oid) references _order(oid));

insert into user(role,username,password,name,tel,address,info,del)
values(0,'admin','admin','admin','','','',0);