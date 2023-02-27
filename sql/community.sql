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

 Date: 11/08/2022 12:39:21
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_discuss_post
-- ----------------------------
BEGIN;
INSERT INTO `t_discuss_post` (`id`, `user_id`, `title`, `content`, `type`, `status`, `comment_count`, `score`, `topic_id`, `create_time`) VALUES (3, 1, 'Java 并发编程实战', '对于一个 Java 程序员而言，能否熟练掌握并发编程是判断他优秀与否的重要标准之一。因为并发编程是 Java 语言中最为晦涩的知识点，它涉及操作系统、内存、CPU、编程语言等多方面的基础能力，更为考验一个程序员的内功。', b'0', b'0', 0, 42.812883293878976, 1, 1658315763611);
INSERT INTO `t_discuss_post` (`id`, `user_id`, `title`, `content`, `type`, `status`, `comment_count`, `score`, `topic_id`, `create_time`) VALUES (4, 1, 'Java 并发编程实战', '对于一个 Java 程序员而言，能否熟练掌握并发编程是判断他优秀与否的重要标准之一。因为并发编程是 Java 语言中最为晦涩的知识点，它涉及操作系统、内存、CPU、编程语言等多方面的基础能力，更为考验一个程序员的内功。', b'0', b'0', 0, 42.812883293878976, 1, 1658315763611);
INSERT INTO `t_discuss_post` (`id`, `user_id`, `title`, `content`, `type`, `status`, `comment_count`, `score`, `topic_id`, `create_time`) VALUES (10, 1, 'Java 并发编程实战', '对于一个 Java 程序员而言，能否熟练掌握并发编程是判断他优秀与否的重要标准之一。因为并发编程是 Java 语言中最为晦涩的知识点，它涉及操作系统、内存、CPU、编程语言等多方面的基础能力，更为考验一个程序员的内功。', b'0', b'0', 0, 42.812883293878976, 1, 1658315763611);
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
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb3;

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
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (40, 5, 1, 3, '{\"entityType\":1,\"postId\":7}', 0, 1658503375644);
INSERT INTO `t_message` (`id`, `from_id`, `to_id`, `type`, `content`, `status`, `create_time`) VALUES (41, 5, 1, 3, '{\"entityType\":1,\"postId\":8}', 0, 1658506540719);
COMMIT;

-- ----------------------------
-- Table structure for t_private_letter
-- ----------------------------
DROP TABLE IF EXISTS `t_private_letter`;
CREATE TABLE `t_private_letter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_id` bigint NOT NULL COMMENT '发送者',
  `to_id` bigint NOT NULL COMMENT '接收者',
  `conversation_id` varchar(255) DEFAULT NULL COMMENT '会话id',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `status` int DEFAULT '0' COMMENT '状态：0-未读；1-已读',
  `create_time` bigint NOT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_private_letter
-- ----------------------------
BEGIN;
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (1, 1, 13, '1_13', '在吗？', 1, 1657965527436);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (2, 13, 1, '1_13', '在', 0, 1657965584443);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (3, 1, 5, '1_5', '你好', 0, 1657966943479);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (4, 5, 1, '1_5', '你好呀', 0, 1658059622678);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (5, 1, 13, '1_13', '请问这个怎么做？', 0, 1658113611706);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (6, 13, 1, '1_13', '等会教你', 0, 1658125722105);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (7, 5, 1, '1_5', '有什么事吗？', 0, 1658129263030);
COMMIT;

-- ----------------------------
-- Table structure for t_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_resource`;
CREATE TABLE `t_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(75) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资源名称',
  `url` varchar(200) DEFAULT NULL COMMENT '资源URL',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_resource
-- ----------------------------
BEGIN;
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (1, '加精帖子', '/discussPost/essence/{postId}', NULL, NULL);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (2, '拉黑帖子', '/discussPost/block/{postId}', NULL, NULL);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (3, '资源权限设置', '/resource/**', NULL, NULL);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (4, '获取uv统计', '/data/uv', NULL, 1659171257338);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (5, '角色权限设置', '/role/**', NULL, NULL);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (6, '给用户分配角色', '/user/allot/role', NULL, 1659273335333);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (7, '话题权限管理', '/topic/**', NULL, 1659349028704);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (8, '获取被拉黑的帖子', '/discussPost/list/block', NULL, 1659349028704);
INSERT INTO `t_resource` (`id`, `name`, `url`, `description`, `create_time`) VALUES (9, '分页获取用户详情列表', '/user/page/list', NULL, 1659349028704);
COMMIT;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_time` bigint DEFAULT NULL COMMENT '创建时间',
  `status` int DEFAULT '1' COMMENT '启用状态：0-禁用；1-启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_role
-- ----------------------------
BEGIN;
INSERT INTO `t_role` (`id`, `name`, `description`, `create_time`, `status`) VALUES (1, 'admin', '管理员：拥有所有权限', 1658994726940, 1);
INSERT INTO `t_role` (`id`, `name`, `description`, `create_time`, `status`) VALUES (2, 'moderator', '版主：可以加精和拉黑帖子', 1658995067947, 1);
INSERT INTO `t_role` (`id`, `name`, `description`, `create_time`, `status`) VALUES (3, 'generalUser', '普通用户', 1659152771382, 1);
COMMIT;

-- ----------------------------
-- Table structure for t_role_resource_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_role_resource_relation`;
CREATE TABLE `t_role_resource_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `resource_id` bigint DEFAULT NULL COMMENT '资源ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_role_resource_relation
-- ----------------------------
BEGIN;
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (14, 2, 1);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (15, 2, 2);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (16, 1, 1);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (17, 1, 2);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (18, 1, 3);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (19, 1, 4);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (20, 1, 5);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (21, 1, 6);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (22, 1, 7);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (23, 1, 8);
INSERT INTO `t_role_resource_relation` (`id`, `role_id`, `resource_id`) VALUES (24, 1, 9);
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

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
INSERT INTO `t_user` (`id`, `email`, `password`, `nickname`, `sex`, `location`, `status`, `avatar`, `create_time`, `del_flag`, `last_login`) VALUES (1, '380996870@qq.com', '$2a$10$3rGQhXwDPZ9iiTwFPWn6xec.K9UMBc/9xcoFYNf6PO7EWLjFgQl1S', '范范', b'0', '广东省深圳市', b'1', 'http://rc0n1vs1q.hn-bkt.clouddn.comf839fea7-839c-494d-96ed-89da216354db.jpeg', 1651380441308, b'0', 1659349854591);
INSERT INTO `t_user` (`id`, `email`, `password`, `nickname`, `sex`, `location`, `status`, `avatar`, `create_time`, `del_flag`, `last_login`) VALUES (5, 'fzb380996870@163.com', '$2a$10$3rGQhXwDPZ9iiTwFPWn6xec.K9UMBc/9xcoFYNf6PO7EWLjFgQl1S', 'fanfan', b'0', '广东省深圳市', b'1', 'http://rc0n1vs1q.hn-bkt.clouddn.comf839fea7-839c-494d-96ed-89da216354db.jpeg', 1653186432289, b'0', 1660188704547);
INSERT INTO `t_user` (`id`, `email`, `password`, `nickname`, `sex`, `location`, `status`, `avatar`, `create_time`, `del_flag`, `last_login`) VALUES (13, '496132278@qq.com', '$2a$10$3rGQhXwDPZ9iiTwFPWn6xec.K9UMBc/9xcoFYNf6PO7EWLjFgQl1S', '我是康康', b'0', '广东省/深圳市/福田区', b'1', 'http://rdmj5rzmc.hn-bkt.clouddn.com/21e23ea6-466f-446d-88f2-18d2007c29fb.jpg', 1655473886466, b'0', 1656669149458);
COMMIT;

-- ----------------------------
-- Table structure for t_user_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role_relation`;
CREATE TABLE `t_user_role_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_user_role_relation
-- ----------------------------
BEGIN;
INSERT INTO `t_user_role_relation` (`id`, `user_id`, `role_id`) VALUES (2, 5, 2);
INSERT INTO `t_user_role_relation` (`id`, `user_id`, `role_id`) VALUES (3, 13, 3);
INSERT INTO `t_user_role_relation` (`id`, `user_id`, `role_id`) VALUES (6, 1, 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
