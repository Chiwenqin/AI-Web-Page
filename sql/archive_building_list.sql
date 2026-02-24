/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : kinit

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 31/12/2025 14:39:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for archive_building_list
-- ----------------------------
DROP TABLE IF EXISTS `archive_building_list`;
CREATE TABLE `archive_building_list`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `building_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '楼栋编码',
  `building_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '楼栋名称',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `entrance_count` int NULL DEFAULT 0 COMMENT '楼门数量',
  `house_count` int NULL DEFAULT 0 COMMENT '房屋数量',
  `building_area` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '建筑面积',
  `billing_area` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '计费面积',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 (0:停用, 1:启用)',
  `village_id` bigint NULL DEFAULT NULL COMMENT '所属小区ID',
  `create_person` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_person` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_village_id`(`village_id` ASC) USING BTREE COMMENT '小区ID索引',
  INDEX `idx_building_code`(`building_code` ASC) USING BTREE COMMENT '楼栋编码索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '楼栋信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of archive_building_list
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
