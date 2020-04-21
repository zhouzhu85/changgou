/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50642
Source Host           : localhost:3306
Source Database       : changgou_content

Target Server Type    : MYSQL
Target Server Version : 50642
File Encoding         : 65001

Date: 2020-04-21 15:21:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_content
-- ----------------------------
DROP TABLE IF EXISTS `tb_content`;
CREATE TABLE `tb_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `url` varchar(500) DEFAULT NULL,
  `pic` varchar(300) DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `sort_order` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_content
-- ----------------------------
INSERT INTO `tb_content` VALUES ('28', '1', '京东广告', 'http://www.sdfs.com', 'sdfs', '1', '1');
INSERT INTO `tb_content` VALUES ('29', '1', '是横着士大夫流口水', 'dsdfjsd', 'dsfs', '1', '1');

-- ----------------------------
-- Table structure for tb_content_category
-- ----------------------------
DROP TABLE IF EXISTS `tb_content_category`;
CREATE TABLE `tb_content_category` (
  `id` bigint(20) NOT NULL,
  `name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tb_content_category
-- ----------------------------
INSERT INTO `tb_content_category` VALUES ('1', '首页轮播广告');
INSERT INTO `tb_content_category` VALUES ('2', '今日推荐A');
INSERT INTO `tb_content_category` VALUES ('3', '活动专区');
INSERT INTO `tb_content_category` VALUES ('4', '猜你喜欢');
