/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : community

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 14/07/2022 17:01:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '发布评论的作者',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论id',
  `entity_type` int NOT NULL COMMENT '评论目标的类型（1-帖子；2-评论）',
  `entity_id` bigint DEFAULT NULL COMMENT '评论目标的 id',
  `target_id` bigint DEFAULT NULL COMMENT '指明对哪个用户进行评论(用户 id)',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_comment
-- ----------------------------
BEGIN;
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (1, 1, NULL, 1, 2, NULL, '不错', 1656129330230);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (2, 1, NULL, 1, 1, NULL, 'dolore sed et', 1656139490268);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (3, 1, 2, 2, 2, 1, '你在说啥？', 1656141072338);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (4, 5, 2, 2, 3, 1, '我也不知道', 1656145197637);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (5, 1, 2, 2, 4, 5, '123', 1656146619579);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (6, 5, 1, 2, 1, 1, '我也觉得不错', 1656170926442);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (7, 1, NULL, 1, 2, NULL, 'sit occaecat tempor fugiat', 1656205948199);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (9, 5, 7, 2, 7, 1, '说啥呢？', 1656206171550);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (10, 5, NULL, 1, 1, NULL, '不存在', 1656215588797);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (11, 5, NULL, 1, 1, NULL, 'laborum', 1656688460930);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (12, 1, 11, 2, 11, 5, 'amet', 1656726715610);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (13, 1, NULL, 1, 1, NULL, '欢迎大家积极评论', 1656728957029);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (14, 5, 13, 2, 13, 1, '好的', 1656729062061);
INSERT INTO `t_comment` (`id`, `user_id`, `parent_id`, `entity_type`, `entity_id`, `target_id`, `content`, `create_time`) VALUES (15, 1, 13, 2, 14, 5, '加油', 1656729154657);
COMMIT;

-- ----------------------------
-- Table structure for t_discuss_post
-- ----------------------------
DROP TABLE IF EXISTS `t_discuss_post`;
CREATE TABLE `t_discuss_post` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '发布作者',
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `type` bit(1) NOT NULL DEFAULT b'0' COMMENT '0-普通帖子；1-精华帖子',
  `status` bit(1) NOT NULL DEFAULT b'0' COMMENT '0-正常；1-拉黑',
  `comment_count` bigint DEFAULT '0' COMMENT '评论数量',
  `score` double NOT NULL DEFAULT '0' COMMENT '热帖分数',
  `topic_id` int NOT NULL COMMENT '所属板块',
  `create_time` bigint NOT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_discuss_post
-- ----------------------------
BEGIN;
INSERT INTO `t_discuss_post` (`id`, `user_id`, `title`, `content`, `type`, `status`, `comment_count`, `score`, `topic_id`, `create_time`) VALUES (1, 1, '欢迎大家', '123123撒打算打算的撒打算打算的', b'0', b'0', 5, 0, 1, 1656688256797);
COMMIT;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_id` bigint NOT NULL COMMENT '消息发出人',
  `to_id` bigint NOT NULL COMMENT '消息接收人',
  `type` int DEFAULT NULL COMMENT '消息类型',
  `content` varchar(500) DEFAULT NULL COMMENT '内容',
  `status` int NOT NULL COMMENT '通知状态：0-未读 1-已读',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_message
-- ----------------------------
BEGIN;
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (7, 1, 1, 0, '{\"content\":\"欢迎您加入 Community 社区！\"}', 0, 1652630787280);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (8, 1, 5, 0, '{\"content\":\"欢迎您加入 Community 社区！\"}', 0, 1653186471713);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (28, 5, 1, 1, '{\"entityType\":1,\"commentId\":11,\"postId\":1}', 0, 1656688461497);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (29, 1, 5, 1, '{\"entityType\":2,\"commentId\":12,\"postId\":1}', 0, 1656726715888);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (30, 5, 1, 1, '{\"entityType\":2,\"commentId\":14,\"postId\":1}', 0, 1656729062280);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (31, 1, 5, 1, '{\"entityType\":2,\"commentId\":15,\"postId\":1}', 0, 1656729154694);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (33, 1, 5, 2, NULL, 0, 1656733209127);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (34, 1, 5, 2, NULL, 0, 1656733267970);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (35, 5, 1, 3, '{\"entityType\":1,\"postId\":1}', 0, 1656770048219);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (36, 5, 1, 3, '{\"entityType\":2,\"commentId\":15,\"postId\":1}', 0, 1656770278855);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (37, 1, 5, 2, NULL, 0, 1656774420910);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (38, 1, 5, 0, '{\"content\":\"测试\"}', 0, 1657707606693);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (39, 1, 13, 0, '{\"content\":\"测试\"}', 0, 1657707606862);
COMMIT;

-- ----------------------------
-- Table structure for t_topic
-- ----------------------------
DROP TABLE IF EXISTS `t_topic`;
CREATE TABLE `t_topic` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '板块名字',
  `summary` varchar(500) NOT NULL COMMENT '板块简介',
  `create_time` bigint DEFAULT NULL COMMENT '板块创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_topic
-- ----------------------------
BEGIN;
INSERT INTO `t_topic` (`id`, `name`, `summary`, `create_time`) VALUES (1, '娱乐圈', '不饭圈、不控评、不玻璃心，要玩就玩真实的娱乐圈', 1653288936883);
INSERT INTO `t_topic` (`id`, `name`, `summary`, `create_time`) VALUES (2, '影视区', '有话值说，以理服人，来一场光影的艺术家之旅！', 1653288936883);
COMMIT;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` varchar(125) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮箱地址',
  `password` varchar(125) NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '昵称',
  `sex` bit(1) DEFAULT NULL COMMENT '性别： 0-男 1-女',
  `location` varchar(100) DEFAULT NULL COMMENT '所在地：省份/市区',
  `role` int NOT NULL DEFAULT '2' COMMENT '0-管理员;1-版主;2-版主',
  `status` bit(1) NOT NULL DEFAULT b'0' COMMENT '激活状态：0-未激活;1-已激活',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `del_flag` bit(1) DEFAULT b'0' COMMENT '是否删除：0-未删除；1-已删除',
  `last_login` bigint DEFAULT NULL COMMENT '最后一次登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `t_user` (`id`, `email`, `password`, `nickname`, `sex`, `location`, `role`, `status`, `avatar`, `create_time`, `del_flag`, `last_login`) VALUES (1, '380996870@qq.com', '$2a$10$3rGQhXwDPZ9iiTwFPWn6xec.K9UMBc/9xcoFYNf6PO7EWLjFgQl1S', '范范', b'0', '广东省深圳市', 0, b'1', 'http://rc0n1vs1q.hn-bkt.clouddn.comf839fea7-839c-494d-96ed-89da216354db.jpeg', 1651380441308, b'0', 1655522900283);
INSERT INTO `t_user` (`id`, `email`, `password`, `nickname`, `sex`, `location`, `role`, `status`, `avatar`, `create_time`, `del_flag`, `last_login`) VALUES (5, 'fzb380996870@163.com', '$2a$10$C2N1EOQMXnr3e5Vv4nCCjeB3AHn9yfe4CPgcdWHVbrgl1ltmvO1H6', 'fanfan', b'0', '广东省深圳市', 2, b'1', 'http://rc0n1vs1q.hn-bkt.clouddn.comf839fea7-839c-494d-96ed-89da216354db.jpeg', 1653186432289, b'0', 1655519092331);
INSERT INTO `t_user` (`id`, `email`, `password`, `nickname`, `sex`, `location`, `role`, `status`, `avatar`, `create_time`, `del_flag`, `last_login`) VALUES (13, '496132278@qq.com', '$2a$10$h/BGkgAg25wuyhHj7/gnS.P99aDAuLmtl8uZPuESeMcXK0dyYpH36', '我是康康', b'0', '广东省/深圳市/福田区', 2, b'1', 'http://rdmj5rzmc.hn-bkt.clouddn.com/21e23ea6-466f-446d-88f2-18d2007c29fb.jpg', 1655473886466, b'0', 1656669149458);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
