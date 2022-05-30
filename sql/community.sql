/*
 Navicat Premium Data Transfer

 Source Server         : MySQL 8
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : community

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 24/05/2022 16:44:48
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
  `entity_type` int NOT NULL COMMENT '评论目标的类型（0-帖子；1-评论）',
  `entity_id` bigint DEFAULT NULL COMMENT '评论目标的 id',
  `target_id` bigint DEFAULT NULL COMMENT '指明对哪个用户进行评论(用户 id)',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_comment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_discuss_post
-- ----------------------------
DROP TABLE IF EXISTS `t_discuss_post`;
CREATE TABLE `t_discuss_post` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '发布作者',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `type` bit(1) NOT NULL DEFAULT b'0' COMMENT '0-普通帖子；1-精华帖子',
  `status` bit(1) NOT NULL DEFAULT b'0' COMMENT '0-正常；1-拉黑',
  `like_count` bigint DEFAULT '0' COMMENT '点赞数量',
  `comment_count` bigint DEFAULT '0' COMMENT '评论数量',
  `score` double NOT NULL DEFAULT '0' COMMENT '热帖分数',
  `plate_id` int NOT NULL COMMENT '所属板块',
  `create_time` bigint NOT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_discuss_post
-- ----------------------------
BEGIN;
INSERT INTO `t_discuss_post` VALUES (1, 2, '第一条帖子', '欢迎大家使用社区', b'0', b'0', 0, 0, 0, 1, 1653288936883);
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_message
-- ----------------------------
BEGIN;
INSERT INTO `t_message` VALUES (6, 1, 4, 0, '{}', 0, 1652629684141);
INSERT INTO `t_message` VALUES (7, 1, 4, 0, '{\"content\":\"欢迎您加入 Community 社区！\"}', 0, 1652630787280);
INSERT INTO `t_message` VALUES (8, 1, 5, 0, '{\"content\":\"欢迎您加入 Community 社区！\"}', 0, 1653186471713);
COMMIT;

-- ----------------------------
-- Table structure for t_plate
-- ----------------------------
DROP TABLE IF EXISTS `t_plate`;
CREATE TABLE `t_plate` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '板块名字',
  `summary` varchar(500) NOT NULL COMMENT '板块简介',
  `create_time` bigint DEFAULT NULL COMMENT '板块创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_plate
-- ----------------------------
BEGIN;
INSERT INTO `t_plate` VALUES (1, '娱乐圈', '不饭圈、不控评、不玻璃心，要玩就玩真实的娱乐圈', 1653288936883);
INSERT INTO `t_plate` VALUES (2, '影视区', '有话值说，以理服人，来一场光影的艺术家之旅！', 1653288936883);
COMMIT;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` varchar(125) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮箱地址',
  `password` varchar(125) NOT NULL COMMENT '密码',
  `nickname` varchar(25) DEFAULT NULL COMMENT '昵称',
  `sex` bit(1) DEFAULT NULL COMMENT '性别 0-男 1-女',
  `location` varchar(100) DEFAULT NULL COMMENT '所在地：省份/市区',
  `role` int NOT NULL DEFAULT '2' COMMENT '0-管理员;1-版主;2-版主',
  `status` int NOT NULL DEFAULT '0' COMMENT '0-未激活;1-已激活',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `t_user` VALUES (1, '380996870@qq.com', '$2a$10$T7QOp/PLNLjh/3MR5EXinOcPyBvOnhgNHENS4ZO6isl122zvvUCgW', 'fanfan', b'0', '广东省深圳市', 0, 1, NULL, 1651380441308);
INSERT INTO `t_user` VALUES (5, 'fzb380996870@163.com', '$2a$10$C2N1EOQMXnr3e5Vv4nCCjeB3AHn9yfe4CPgcdWHVbrgl1ltmvO1H6', NULL, NULL, NULL, 2, 1, NULL, 1653186432289);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
