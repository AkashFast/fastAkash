/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3852
 Source Schema         : scoring2

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 10/03/2021 15:27:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cr_engine
-- ----------------------------
DROP TABLE IF EXISTS `cr_engine`;
CREATE TABLE `cr_engine`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据/逻辑引擎仓库主键编号',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据/逻辑引擎仓库Code值',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据/逻辑引擎仓库名称',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据/逻辑引擎仓库功能简介',
  `state` int(11) NULL DEFAULT NULL COMMENT '数据/逻辑引擎启用状态（0-启用/1-禁用/2-审核中）',
  `executeVail` int(11) NULL DEFAULT NULL COMMENT '数据/逻辑执行验证状态（0-未通过逻辑验证/1-已通过）',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号',
  `engineType` int(11) NULL DEFAULT NULL COMMENT '引擎类型（ 0增 1删 2改 3查）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心：数据引擎仓库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cr_engineexecute
-- ----------------------------
DROP TABLE IF EXISTS `cr_engineexecute`;
CREATE TABLE `cr_engineexecute`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据引擎流程核心主键编号',
  `eid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据引擎编号',
  `executeTag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据引擎执行引导标识',
  `executeData` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '待执行数据参数',
  `isChild` int(1) NULL DEFAULT NULL COMMENT '当前环节是否为嵌套子查询（0-否/1-是）',
  `sorts` int(11) NULL DEFAULT NULL COMMENT '数据引擎流程环节序列号',
  `state` int(1) NULL DEFAULT NULL COMMENT '当前环节是否启用（0-未启用/1-已启用）',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心：数据引擎执行' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cr_engineout
-- ----------------------------
DROP TABLE IF EXISTS `cr_engineout`;
CREATE TABLE `cr_engineout`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输出字段CODE',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输出字段备注',
  `engineId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联引擎ID',
  `version` int(11) NULL DEFAULT NULL COMMENT '版本',
  `state` int(11) NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心：数据输出字段表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cr_engineparam
-- ----------------------------
DROP TABLE IF EXISTS `cr_engineparam`;
CREATE TABLE `cr_engineparam`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据引擎需求字段主键编号',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段标识Code',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段标识名称',
  `engineId` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联引擎主键编号',
  `version` int(11) NULL DEFAULT NULL COMMENT '版本',
  `state` int(11) NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心：必要字段关联信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cr_field
-- ----------------------------
DROP TABLE IF EXISTS `cr_field`;
CREATE TABLE `cr_field`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '核心字段表主键',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核心字段Code值',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核心字段名称',
  `tid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联核心表主键编号',
  `sorts` int(11) NULL DEFAULT NULL COMMENT '核心字段序列',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核心字段类型',
  `size` double NULL DEFAULT NULL COMMENT '核心字段长度',
  `state` int(11) NULL DEFAULT 0 COMMENT '核心字段状态（1-禁用/0-启用）',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心：数据字段信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cr_field
-- ----------------------------

-- ----------------------------
-- Table structure for cr_logger
-- ----------------------------
DROP TABLE IF EXISTS `cr_logger`;
CREATE TABLE `cr_logger`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表信息变更日志主键',
  `type` int(11) NULL DEFAULT NULL COMMENT '变更类型（0-表/1-字段）',
  `methodName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调用的逻辑类及方法名称',
  `executorId` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作者id',
  `reson` int(11) NULL DEFAULT NULL COMMENT '操作类型（0-登陆 1-接口请求 2-上传 3-数据导出）',
  `updateTime` datetime(0) NULL DEFAULT NULL COMMENT '执行时间',
  `sourceData` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行结果',
  `sourceTag` int(1) NULL DEFAULT NULL COMMENT '执行的结果标识',
  `log_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问ip',
  `state` int(1) NULL DEFAULT NULL COMMENT '数据状态',
  `exe_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心：系统日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cr_logger
-- ----------------------------

-- ----------------------------
-- Table structure for cr_logicrule
-- ----------------------------
DROP TABLE IF EXISTS `cr_logicrule`;
CREATE TABLE `cr_logicrule`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '逻辑执行主键编号',
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑执行功能简介',
  `state` int(11) NULL DEFAULT NULL COMMENT '逻辑执行启用状态（0-启用/1-禁用/2-审核中）',
  `version` int(11) NULL DEFAULT NULL COMMENT '逻辑执行当前版本序号',
  `logicTag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑执行标识字段',
  `executor` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑执行操作指令',
  `instructions` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '具体操作',
  `expTag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '特殊指令',
  `expCute` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '特殊操作',
  `sorts` int(11) NULL DEFAULT NULL COMMENT '指令执行序列',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心表：逻辑执行引擎' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cr_tables
-- ----------------------------
DROP TABLE IF EXISTS `cr_tables`;
CREATE TABLE `cr_tables`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '核心表主键',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核心表Code值',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核心表名称（中文）',
  `note` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '核心表备注信息',
  `state` int(11) NULL DEFAULT NULL COMMENT '核心表状态（0-已失效/1-正常）',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号',
  `type` int(1) NULL DEFAULT NULL COMMENT '当前数据类型「0-表 1-schema逻辑」',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核心：数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cr_tables
-- ----------------------------


-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单表主键编号「Lock」',
  `state` int(11) NULL DEFAULT NULL COMMENT '当前权限状态（0-正常/1-禁用/2-其他）「Lock」',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号「Lock」',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '数据创建时间「Lock」',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后操作时间「Lock」',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `note` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单备注',
  `pid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '-1' COMMENT '父级菜单编号（默认为-1：顶层）',
  `is_lock` int(1) NULL DEFAULT 0 COMMENT '是否锁定当前菜单（0-否 / 1-是），锁定后无法更新及删除',
  `order_number` int(11) NULL DEFAULT NULL COMMENT '菜单序号',
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vue路由code编码值',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vue路由可用的icon图标值',
  `path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vue路由path路径',
  `component` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'vue路由component组件路由',
  `is_parent` int(1) NULL DEFAULT 1 COMMENT '是否为父级节点（0-否/1-是）',
  `redirect_page` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定路由跳转的页面',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统：菜单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('4ac81c3bfed045ac9c207319205fff0b', 0, 2, '2020-09-24 10:12:07', '2020-10-28 11:06:24', '系统管理', NULL, '-1', 1, 0, 'sys_akash', 'setting', '/sys_akash', 'RouteView', 1, NULL);
INSERT INTO `sys_menu` VALUES ('5064ecb799ea4f0798a8380e1690e8ef', 0, 1, '2020-11-13 16:31:58', '2020-11-13 16:32:11', '日志管理', '日志管理', '4ac81c3bfed045ac9c207319205fff0b', 0, 4, 'Log', NULL, '/sys_akash/log', 'system/log/log', 0, NULL);
INSERT INTO `sys_menu` VALUES ('8704fef9dba642c5bd9a7ee54c18959c', 0, 1, '2020-11-30 17:52:25', '2020-11-30 18:24:53', '驾驶舱', '驾驶舱', '4ac81c3bfed045ac9c207319205fff0b', 0, -1, 'Analysis', NULL, '/dashboard/analysis/:pageNo([1-9]d*)?', 'dashboard/Analysis', 0, NULL);
INSERT INTO `sys_menu` VALUES ('880ab0948185422c82a382f74ee23942', 0, 5, '2020-09-24 10:13:17', '2020-10-29 11:14:31', '菜单管理', NULL, '4ac81c3bfed045ac9c207319205fff0b', 0, 0, 'Menu', NULL, '/sys_akash/menu', 'system/menu/menu', 0, NULL);
INSERT INTO `sys_menu` VALUES ('9951f52dc3ec4815a4698bd384b4c070', 0, 4, '2020-09-24 10:12:43', '2020-10-28 11:28:47', '用户管理', NULL, '4ac81c3bfed045ac9c207319205fff0b', 1, 2, 'User', NULL, '/sys_akash/user', 'system/user/user', 0, NULL);
INSERT INTO `sys_menu` VALUES ('c1f54036bce64f01aa315b291852656a', 0, 4, '2020-09-24 10:12:59', '2020-10-28 11:28:54', '权限管理', NULL, '4ac81c3bfed045ac9c207319205fff0b', 0, 1, 'Campus', NULL, '/sys_akash/campus', 'system/role/role', 0, NULL);
-- ----------------------------
-- Table structure for sys_menudata
-- ----------------------------
DROP TABLE IF EXISTS `sys_menudata`;
CREATE TABLE `sys_menudata`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限数据关联主键编号「Lock」',
  `state` int(11) NULL DEFAULT NULL COMMENT '当前权限数据状态（0-正常/1-禁用/2-其他）「Lock」',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号「Lock」',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '数据创建时间「Lock」',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后操作时间「Lock」',
  `is_lock` int(1) NULL DEFAULT 0 COMMENT '是否锁定当前绑定关系（0-否 / 1-是），锁定后无法更新及删除',
  `order_number` int(11) NULL DEFAULT NULL COMMENT '数据表关联序号',
  `mid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单ID',
  `tid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前权限可访问数据表ID',
  `type` int(1) NULL DEFAULT 0 COMMENT '数据类型（0 - cr_table 数据表/ 1-cr_engine 引擎）',
  `fieldBan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段禁用「仅type为0时使用」-1为全字段',
  `methods` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可用方法「仅type为1时使用」引擎为id,逻辑类为方法名称,-1为全部方法',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统：菜单可访问数据对应关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menudata
-- ----------------------------
INSERT INTO `sys_menudata` VALUES ('0abe944747f249449b885d593be55d02', 0, 0, '2021-02-26 15:07:12', NULL, 0, 0, 'b601ed32a0d847eeb290ca61f77a8736', 'bdb96d70e8a841cb8ed43e8473f42575', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('0aeb863ab00e4ca895db584811e82785', 0, 0, '2021-02-26 15:03:12', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', 'db89f0172cf346aa939cfa79c50cb308', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('2b53bcb9db1144dcb1a09e8aa26dae9b', 0, 0, '2021-02-26 15:06:34', NULL, 0, 0, '34578a29f7aa42e189b9cd953f6347e1', '15e240ba85bd410c92ef9a2fc8f96bac', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('2d18009ec8cd4dcf84b4cbb08d6fc2bc', 0, 0, '2021-02-26 15:07:06', NULL, 0, 0, 'b601ed32a0d847eeb290ca61f77a8736', '15e240ba85bd410c92ef9a2fc8f96bac', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('3ab101b936874114aa52e307e9a32905', 0, 0, '2021-02-26 15:02:51', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', '33e9e36c61ee4e6f9a0e6efcb4609754', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('413d05bed93849c7887f7f3483c6910c', 0, 0, '2021-02-26 15:08:23', NULL, 0, 0, '34578a29f7aa42e189b9cd953f6347e1', 'db89f0172cf346aa939cfa79c50cb308', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('4c8a87254a4f4b52931bed3cebfaa7e2', 0, 0, '2021-02-26 15:08:00', NULL, 0, 0, 'b77b0f806a944a84b4171f186d37e077', 'db89f0172cf346aa939cfa79c50cb308', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('4ed1a01998344ae9be17fe70a664f963', 0, 0, '2021-02-26 15:02:45', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', 'bf26f251ac3e48399d482a9c6c69b51a', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('53034dcfbfa54d03a2acceedb91b35dd', 0, 0, '2021-02-26 15:03:29', NULL, 0, 0, '5064ecb799ea4f0798a8380e1690e8ef', '8de466d48e944ad4bde14b05728e6591', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('5dcbae359d8142f0986335508136f09c', 0, 0, '2021-02-26 15:07:46', NULL, 0, 0, '11461d7297a543469b971ecff1ee643f', 'db89f0172cf346aa939cfa79c50cb308', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('65edc4adda974951afb33df2f4c41fa7', 0, 0, '2021-02-26 15:06:43', NULL, 0, 0, '34578a29f7aa42e189b9cd953f6347e1', 'bdb96d70e8a841cb8ed43e8473f42575', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('75aff7967c4343ca8a083a641ac351af', 0, 0, '2021-02-26 15:06:51', NULL, 0, 0, 'b77b0f806a944a84b4171f186d37e077', '15e240ba85bd410c92ef9a2fc8f96bac', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('75c95c8f5cb24b4fb45d3225f621c289', 0, 0, '2021-02-26 15:02:33', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', '9a044ca1ced7491c8102643a46d5ec8f', 0, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('7819171f156544f39b45d7bdf4d33edf', 0, 0, '2021-02-26 15:02:03', NULL, 0, 0, 'c1f54036bce64f01aa315b291852656a', 'd0829e41420d49dba4e89ed5015a6a98', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('7fa21be925f1401495527bb71ed6743e', 0, 0, '2021-02-26 15:07:54', NULL, 0, 0, 'b601ed32a0d847eeb290ca61f77a8736', 'db89f0172cf346aa939cfa79c50cb308', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('9e34b9b90c6d48a69287a9de3dee04e2', 0, 0, '2021-02-26 15:01:44', NULL, 0, 0, '880ab0948185422c82a382f74ee23942', 'd4d91313448c4d5cac1741b991d59216', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('a0be32e6f58e48a6b59109c07a551771', 0, 0, '2021-02-26 15:01:35', NULL, 0, 0, '880ab0948185422c82a382f74ee23942', 'bcd0f780dc4046cd83941b73ebcb9591', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('a636861a7d894cffb094e1e394afab27', 0, 0, '2021-02-26 15:01:57', NULL, 0, 0, 'c1f54036bce64f01aa315b291852656a', '458b298a0648462f96a4fe1c44926596', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('a6e4e5f0783b4c3bbb8da6c579da1574', 0, 0, '2021-02-26 15:01:01', NULL, 0, 0, '8704fef9dba642c5bd9a7ee54c18959c', '4c652a5362bf482ebe80de4f8dd5afe5', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('bf183949e40e479dbc027ee9837536dc', 0, 0, '2021-02-26 15:03:44', NULL, 0, 0, '5064ecb799ea4f0798a8380e1690e8ef', '28c13efa802b4822a001f272dce40611', 0, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('c661d4c934294588bf3356594ca0240b', 0, 0, '2021-02-26 15:07:28', NULL, 0, 0, '11461d7297a543469b971ecff1ee643f', 'bdb96d70e8a841cb8ed43e8473f42575', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('d15cc2d765894033a9c57b8b21cada98', 0, 0, '2021-02-26 15:03:37', NULL, 0, 0, '5064ecb799ea4f0798a8380e1690e8ef', 'db89f0172cf346aa939cfa79c50cb308', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('d8f68fa29f80411d88e86ca5f680669f', 0, 0, '2021-02-26 15:07:21', NULL, 0, 0, '11461d7297a543469b971ecff1ee643f', '15e240ba85bd410c92ef9a2fc8f96bac', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('f53e084ec4b8418ca57429de45cea6a6', 0, 0, '2021-02-26 15:02:13', NULL, 0, 0, 'c1f54036bce64f01aa315b291852656a', '41d14517e5134baaacff26f21241de23', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('fbf061adb8cb4e48afc3f435cd9020ad', 0, 0, '2021-02-26 15:06:58', NULL, 0, 0, 'b77b0f806a944a84b4171f186d37e077', 'bdb96d70e8a841cb8ed43e8473f42575', 1, NULL, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限表主键编号「Lock」',
  `state` int(11) NULL DEFAULT NULL COMMENT '当前权限状态（0-正常/1-禁用/2-其他）「Lock」',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号「Lock」',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '数据创建时间「Lock」',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后操作时间「Lock」',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `note` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限备注',
  `pid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '-1' COMMENT '父级权限编号（默认为-1：顶层）',
  `is_lock` int(1) NULL DEFAULT 0 COMMENT '是否锁定当前权限（0-否 / 1-是），锁定后无法更新及删除',
  `order_number` int(11) NULL DEFAULT NULL COMMENT '权限序号',
  `is_supervisor` int(1) NULL DEFAULT 0 COMMENT '是否系统超级管理员（0 - 否 / 1-是 ），超管具有全数据操作权限，不受权限制约',
  `is_parent` int(1) NULL DEFAULT 1 COMMENT '是否父节点（0-否/1-是）',
  `index_page` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指定当前权限访问的首页路由地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统：权限信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('074e19ca81b944629773fff101ea759e', 0, 1, '2020-09-24 10:18:49', '2020-11-30 18:12:51', '超级管理员', 'supervisor', '-1', 1, 0, 1, 1, '/sys_akash/user');

-- ----------------------------
-- Table structure for sys_role_patch_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_patch_data`;
CREATE TABLE `sys_role_patch_data`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限数据匹配表主键编号「Lock」',
  `state` int(11) NULL DEFAULT NULL COMMENT '当前用户状态（0-正常/1-禁用/2-其他）「Lock」',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号「Lock」',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '数据创建时间「Lock」',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后访问时间「Lock」',
  `rid` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的权限id',
  `schema_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的逻辑类名称',
  `method_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的方法名称',
  `table_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的表id',
  `patch_filed` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的字段（方法/表内)',
  `patch_type` int(2) NULL DEFAULT NULL COMMENT '匹配类型「1-等于，2-sql引擎」',
  `repeat_val` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '强制替换更新值/绑定的引擎id',
  `engine_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '引擎关联字段赋值「type为2时生效」',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统：权限数据分配信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_rolemenu
-- ----------------------------
DROP TABLE IF EXISTS `sys_rolemenu`;
CREATE TABLE `sys_rolemenu`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单权限关联表主键编号「Lock」',
  `state` int(11) NULL DEFAULT NULL COMMENT '当前权限状态（0-正常/1-禁用/2-其他）「Lock」',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号「Lock」',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '数据创建时间「Lock」',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后操作时间「Lock」',
  `is_lock` int(1) NULL DEFAULT 0 COMMENT '是否锁定当前菜单权限关联关系（0-否 / 1-是），锁定后无法更新及删除',
  `alias_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单别名（用于菜单个性化）',
  `rid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联权限ID',
  `mid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联菜单ID',
  `order_number` int(11) NULL DEFAULT NULL COMMENT '菜单序号',
  `page_role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '111111' COMMENT '管理员页面相关按钮逻辑（0 - 隐藏 / 1 - 显示） 增删改查导出导入（010010）',
  `page_normal_role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '000100' COMMENT '普通用户页面相关按钮逻辑（0 - 隐藏 / 1 - 显示） 增删改查导出导入（010010）',
  `role_type` int(1) NULL DEFAULT NULL COMMENT '数据操作类型「1 ： 只允许操作自己及自己权限下的数据  0：可以操作全部数据」',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统：菜单→权限关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_rolemenu
-- ----------------------------
INSERT INTO `sys_rolemenu` VALUES ('06346930f4d7479fb13ce0829afa76d5', 0, 0, '2021-02-26 15:08:42', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '9951f52dc3ec4815a4698bd384b4c070', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('0f198c52e0794e5cbda9d3542dd4fa89', 0, 0, '2021-02-26 15:10:30', NULL, 0, NULL, 'e7c51d410acf4f9f95403a8de2bcf18e', '34578a29f7aa42e189b9cd953f6347e1', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('1b4af301015a46cf9c8a348587537b13', 0, 0, '2021-02-26 15:08:39', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', 'c1f54036bce64f01aa315b291852656a', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('2e53e0852d704e6f9743fa5d6b239c70', 0, 0, '2021-02-26 15:10:38', NULL, 0, NULL, 'e7c51d410acf4f9f95403a8de2bcf18e', '11461d7297a543469b971ecff1ee643f', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('37187938f69246fea2ab0f98fe5da102', 0, 0, '2021-02-26 15:08:48', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '34578a29f7aa42e189b9cd953f6347e1', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('4b62559c14374c2fb35a55419faf2eb7', 0, 0, '2021-02-26 15:08:34', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '8704fef9dba642c5bd9a7ee54c18959c', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('4fd9f43b6ecf4c52871bf11cb24ea6fd', 0, 0, '2021-02-26 15:10:33', NULL, 0, NULL, 'e7c51d410acf4f9f95403a8de2bcf18e', 'b77b0f806a944a84b4171f186d37e077', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('5a765c4790334ba48cbd6c5f19cf951c', 0, 0, '2021-02-26 15:08:53', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', 'b601ed32a0d847eeb290ca61f77a8736', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('7515cf43df50448e8646cb60260b2f7c', 0, 0, '2021-02-26 15:08:37', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '880ab0948185422c82a382f74ee23942', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('8c06fa9794ca430fa2b5c2ea4b70d033', 0, 0, '2021-02-26 15:10:36', NULL, 0, NULL, 'e7c51d410acf4f9f95403a8de2bcf18e', 'b601ed32a0d847eeb290ca61f77a8736', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('9e5191b625d348ad837da3e148f9b19d', 0, 0, '2021-02-26 15:08:57', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '11461d7297a543469b971ecff1ee643f', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('de8d18a8e36e4a909cc0e08391caad33', 0, 0, '2021-02-26 15:08:51', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', 'b77b0f806a944a84b4171f186d37e077', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('e19981fc85764cc8b2b720790e4013ee', 0, 0, '2021-02-26 15:08:45', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '5064ecb799ea4f0798a8380e1690e8ef', 0, '1111111', '0001000', 1);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户表主键编号「Lock」',
  `state` int(11) NULL DEFAULT NULL COMMENT '当前用户状态（0-正常/1-禁用/2-其他）「Lock」',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号「Lock」',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '数据创建时间「Lock」',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后访问时间「Lock」',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统访问密码/二级密码',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱账号',
  `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号（限中国大陆+86）',
  `type` int(1) NULL DEFAULT 0 COMMENT '访问权限（0-普通用户 / 1-管理员）',
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信绑定-openId',
  `last_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后访问IP',
  `note` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户备注',
  `is_lock` int(1) NULL DEFAULT 0 COMMENT '是否锁定当前权限（0-否 / 1-是），锁定后无法更新及删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统：用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('c052d6525a444cada1a0809c7f5f9a57', 0, 0, '2020-09-24 10:11:06', '2020-09-24 10:11:06', '超级管理员', NULL, 'admin', '1111', 1, NULL, NULL, NULL, 1);

-- ----------------------------
-- Table structure for sys_userrole
-- ----------------------------
DROP TABLE IF EXISTS `sys_userrole`;
CREATE TABLE `sys_userrole`  (
  `id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '授权主键编号「Lock」',
  `state` int(11) NULL DEFAULT NULL COMMENT '当前授权状态（0-正常/1-禁用/2-其他）「Lock」',
  `version` int(11) NULL DEFAULT NULL COMMENT '当前数据版本序号「Lock」',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '数据创建时间「Lock」',
  `last_time` datetime(0) NULL DEFAULT NULL COMMENT '最后操作时间「Lock」',
  `is_lock` int(1) NULL DEFAULT 0 COMMENT '是否锁定当前授权（0-否 / 1-是），锁定后无法更新及删除',
  `order_number` int(11) NULL DEFAULT NULL COMMENT '授权序号（优先级）',
  `uid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `rid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限ID',
  `is_admin` int(1) NULL DEFAULT NULL COMMENT '是否管理员「0-否，1-是」',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统：用户多权限授权信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_userrole
-- ----------------------------
INSERT INTO `sys_userrole` VALUES ('453b1aa899ea4758a654c72d4b656d23', 0, 0, '2021-03-03 10:25:42', NULL, 0, 1, 'c052d6525a444cada1a0809c7f5f9a57', 'e7c51d410acf4f9f95403a8de2bcf18e', 1);
INSERT INTO `sys_userrole` VALUES ('c3dbf1188e5a4c2aa0ea644f9ea5b3cb', 0, 0, '2021-01-19 17:02:56', NULL, 0, 0, 'c052d6525a444cada1a0809c7f5f9a57', '074e19ca81b944629773fff101ea759e', 1);

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Function structure for getChildLst
-- ----------------------------
DROP FUNCTION IF EXISTS `getChildLst`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `getChildLst`( rootId VARCHAR ( 1000 ) ) RETURNS varchar(10000) CHARSET utf8mb4
BEGIN DECLARE sTemp VARCHAR ( 10000 ); DECLARE sTempChd VARCHAR ( 10000 );SET sTemp = '$';SET sTempChd = cast( rootId AS CHAR );WHILE sTempChd IS NOT NULL DO SET sTemp = concat( sTemp, ',', sTempChd ); SELECT group_concat( id ) INTO sTempChd  FROM sys_role  WHERE FIND_IN_SET( pid, sTempChd ) > 0;END WHILE;RETURN sTemp;END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;