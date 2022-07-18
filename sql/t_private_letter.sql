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

 Date: 17/07/2022 18:43:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of t_private_letter
-- ----------------------------
BEGIN;
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (1, 1, 13, '1_13', '在吗？', 0, 1657965527436);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (2, 13, 1, '1_13', '在', 0, 1657965584443);
INSERT INTO `t_private_letter` (`id`, `from_id`, `to_id`, `conversation_id`, `content`, `status`, `create_time`) VALUES (3, 1, 5, '1_5', '你好', 0, 1657966943479);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
