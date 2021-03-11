/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3852
 Source Schema         : fastakash

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 11/03/2021 14:37:59
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
-- Records of cr_engineout
-- ----------------------------
INSERT INTO `cr_engineout` VALUES ('0a79892c9bf545e6bc0d6ccca56c76ca', 'version', '版本', '493c6cefcb8c470193fca0a394ac63cf', 0, 0);
INSERT INTO `cr_engineout` VALUES ('0b7c3aa5fc7f48c986cd3d63ccb813cd', 'code', '字段标识Code', '493c6cefcb8c470193fca0a394ac63cf', 0, 0);
INSERT INTO `cr_engineout` VALUES ('1361c1dffeee46598b3e2d7fe073510e', 'e.code', '引擎CODE', '5f0ee5c127c24961997a1be205499d0c', 0, 0);
INSERT INTO `cr_engineout` VALUES ('1dd23e95b4d8402394cb7d8d8615b64e', 'cr_engineout.id', '输出字段ID', 'ebe00aacddf94c87bfbe4cd236de015d', 0, 0);
INSERT INTO `cr_engineout` VALUES ('21cb1b94dffd4667b9cae65acc9b359a', 'cr_engineout.name', '输出字段名称', 'ebe00aacddf94c87bfbe4cd236de015d', 0, 0);
INSERT INTO `cr_engineout` VALUES ('5393100f42764587a230dc71bb257757', 'id', '数据引擎需求字段主键编号', '493c6cefcb8c470193fca0a394ac63cf', 0, 0);
INSERT INTO `cr_engineout` VALUES ('5b81c9f8fffd4fcfa5fbc0e45eb8d8e7', 'cr_engineout.code', '输出字段CODE', 'ebe00aacddf94c87bfbe4cd236de015d', 0, 0);
INSERT INTO `cr_engineout` VALUES ('632c22df57964bb9b5601dea0e396bfa', 'e.name', '引擎名称', '5f0ee5c127c24961997a1be205499d0c', 0, 0);
INSERT INTO `cr_engineout` VALUES ('7e2777b55ced4343aaba2512b012b4b6', 'e.id', '引擎ID', '5f0ee5c127c24961997a1be205499d0c', 0, 0);
INSERT INTO `cr_engineout` VALUES ('9913d03b93364b5e9f2c23a7f5bdf48c', 'state', '状态', '493c6cefcb8c470193fca0a394ac63cf', 0, 0);
INSERT INTO `cr_engineout` VALUES ('aa036967351a4052ba27bd38d5e68f28', 'e.name', '表头名称', 'e2ff616ec5cc403899a20d3063a80929', 9, 0);
INSERT INTO `cr_engineout` VALUES ('c34758c94e244f10b8a8cf14bf2e4ab4', 'engineId', '关联引擎主键编号', '493c6cefcb8c470193fca0a394ac63cf', 0, 0);
INSERT INTO `cr_engineout` VALUES ('d1b3bff09efa487da660b4d5208f680e', 'name', '字段标识名称', '493c6cefcb8c470193fca0a394ac63cf', 0, 0);

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
-- Records of cr_engineparam
-- ----------------------------
INSERT INTO `cr_engineparam` VALUES ('055e78da9d1d4586850a147decb91ca2', 'like_code', '匹配Code', '5f0ee5c127c24961997a1be205499d0c', 0, 0);
INSERT INTO `cr_engineparam` VALUES ('071d231eb1794dac9a4b62db5a543e57', 'outId', '关联引擎ID', 'ebe00aacddf94c87bfbe4cd236de015d', 0, 0);
INSERT INTO `cr_engineparam` VALUES ('1d2b22394a3744d0be247ab136f16098', 'eid', '引擎ID', '493c6cefcb8c470193fca0a394ac63cf', 0, 0);
INSERT INTO `cr_engineparam` VALUES ('493278910e6f4ff6abf0d2f6c850bd53', 'tid', '数据表ID', 'bf899190164146738625f81e65d874cf', 0, 0);
INSERT INTO `cr_engineparam` VALUES ('5ce51142cc074984bee47cbe0a27b22b', 'like_name', '匹配名称', '5f0ee5c127c24961997a1be205499d0c', 0, 0);
INSERT INTO `cr_engineparam` VALUES ('f0a4f1106f244578882e34a418afe05d', 'eid', '引擎编号', 'e2ff616ec5cc403899a20d3063a80929', 4, 0);

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
INSERT INTO `cr_field` VALUES ('02ab9176b60b40a291e6be518a9ca7e9', 'version', '当前数据版本序号「Lock」', '4707a06325ce4b2bbf8346534f4d0872', 9, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('0732487b1b094144940af43a54849553', 'isChild', '当前环节是否为嵌套子查询（0-否/1-是）', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 8, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('0840ba3c7da844469f2f860da12ed7ff', 'id', '权限数据匹配表主键编号「Lock」', '72442342cfc946c6a2508ac8c9e64f83', 10, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('09c59e7fd3144ac8be02366898112490', 'state', '当前授权状态（0-正常/1-禁用/2-其他）「Lock」', '8dc7b111e3404e979b8a74821322d9be', 8, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('09d0cc15b5134c7aa81fc54f064ac242', 'mid', '菜单ID', '833c2915eba94675a177cf811f02b524', 7, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('0f07503abc04457293f178341324ad5f', 'note', '逻辑执行功能简介', '125fe34b85224d08a4b88445688dbc28', 1, 'VARCHAR', 255, 0, 0);
INSERT INTO `cr_field` VALUES ('0f2c5a4c7fca4114b8cf5f4abe807451', 'state', '当前用户状态（0-正常/1-禁用/2-其他）「Lock」', '72442342cfc946c6a2508ac8c9e64f83', 11, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('10a34f971ef5439490da27cf97cd41ee', 'page_role', '管理员页面相关按钮逻辑（0 - 隐藏 / 1 - 显示） 增删改查导出导入（010010）', '06e32bc351d64ff789cc9c877b4a9e0f', 5, 'VARCHAR', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('129045faf3ac40d7b2c6753cbdc0061a', 'mid', '关联菜单ID', '06e32bc351d64ff789cc9c877b4a9e0f', 6, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('12a4d80db0dd4d38979b864656ab6c85', 'instructions', '具体操作', '125fe34b85224d08a4b88445688dbc28', 2, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('12d5355dac1f4785ba4040f7d9db7bf8', 'name', '核心表名称（中文）', '633766ff0f2e4dbba2968b038bcbecbd', 3, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('12e7815730a64578b6bb18ef0c6c183b', 'role_type', '数据操作类型「1 ： 只允许操作自己及自己权限下的数据  0：可以操作全部数据」', '06e32bc351d64ff789cc9c877b4a9e0f', 3, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('13b163e00f154704b8bbdacc8cc687c5', 'note', '权限备注', 'bdada100a2ce459a82d49bf1249f234a', 1, 'VARCHAR', 200, 0, 0);
INSERT INTO `cr_field` VALUES ('1478e342a2b2421c99d79a65944640f8', 'last_time', '最后访问时间「Lock」', '72442342cfc946c6a2508ac8c9e64f83', 8, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('152ff195b8544926a5afa6348ccc3212', 'reson', '操作类型（0-登陆 1-接口请求 2-上传 3-数据导出）', '54bda48c3f7243eeb33b1143ee7a4d18', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('1590d29f17474b2297f6751fdf144090', 'type', '变更类型（0-表/1-字段）', '54bda48c3f7243eeb33b1143ee7a4d18', 10, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('15b44b311a4e404193c5c44d34591e82', 'code', '字段标识Code', 'aae50cc5a20f4a4cacaccdda11c35b90', 1, 'VARCHAR', 50, 0, 0);
INSERT INTO `cr_field` VALUES ('16b2691b1ac74ca6b78e9344df75eb9f', 'name', '输出字段备注', '2a6d662b1c624e289c0daf57ce462a7b', 2, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('1946e6dcc50542dd92be42094d7a0a53', 'table_id', '关联的表id', '72442342cfc946c6a2508ac8c9e64f83', 5, 'VARCHAR', 100, 0, 0);
INSERT INTO `cr_field` VALUES ('194bd7da726a4499bec4c2a5d26a2d1d', 'order_number', '菜单序号', '06e32bc351d64ff789cc9c877b4a9e0f', 4, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('194fa92e237d4b6aa4720a9c81efb2a3', 'version', '当前数据版本序号「Lock」', 'fe62792431d24c66830878371b7a3c36', 7, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('1ba980aa62984ef0865255677a87d9d8', 'name', '数据/逻辑引擎仓库名称', '1501f72de1684428bd5662cda7802ec2', 3, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('1c16f50066a54b078aa338be0d0ab4e3', 'version', '当前数据版本序号', '1501f72de1684428bd5662cda7802ec2', 8, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('1c90923663174f12a3147948d7f57686', 'tid', '关联核心表主键编号', '083f09883f9e4a75b773b2dfe3e1518d', 9, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('2183113dd8334e0fbf9d62e3dee9cb12', 'order_number', '菜单序号', '4707a06325ce4b2bbf8346534f4d0872', 4, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('22619272c8194bb2905586a3d10817f6', 'email', '用户企业邮箱账号', 'fe62792431d24c66830878371b7a3c36', 14, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('233221de1cf24d538936ef8b963ae535', 'state', '当前权限数据状态（0-正常/1-禁用/2-其他）「Lock」', '833c2915eba94675a177cf811f02b524', 9, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('234932be465546919b670ee17fd67319', 'last_time', '最后操作时间「Lock」', '8dc7b111e3404e979b8a74821322d9be', 4, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('2502a3488b544a1c9db96947422cb43e', 'note', '数据/逻辑引擎仓库功能简介', '1501f72de1684428bd5662cda7802ec2', 1, 'VARCHAR', 255, 0, 0);
INSERT INTO `cr_field` VALUES ('27aef4ca144a4415b19c4a5b28acafc4', 'methodName', '调用的逻辑类及方法名称', '54bda48c3f7243eeb33b1143ee7a4d18', 4, 'VARCHAR', 200, 0, 0);
INSERT INTO `cr_field` VALUES ('293871be489044f5ba21db81aa338c56', 'type', '当前数据类型「0-表 1-schema逻辑」', '633766ff0f2e4dbba2968b038bcbecbd', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('2a73a205d88042bfa48796cda8c00548', 'method_name', '关联的方法名称', '72442342cfc946c6a2508ac8c9e64f83', 7, 'VARCHAR', 100, 0, 0);
INSERT INTO `cr_field` VALUES ('2aafdd8501b94c2c8a4981c6245fb239', 'last_time', '最后操作时间「Lock」', 'bdada100a2ce459a82d49bf1249f234a', 8, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('2d814e81e8fc43638424dab9694dec1a', 'redirect_page', '指定路由跳转的页面', '4707a06325ce4b2bbf8346534f4d0872', 8, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('318e64fec0774fcf8785824088f1375d', 'id', '核心表主键', '633766ff0f2e4dbba2968b038bcbecbd', 4, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('35ac3b7783a54de48269ea4496732908', 'engineType', '引擎类型（ 0增 1删 2改 3查）', '1501f72de1684428bd5662cda7802ec2', 4, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('35cae6e3747040799e34bdbd464a73fe', 'create_time', '数据创建时间「Lock」', '4707a06325ce4b2bbf8346534f4d0872', 3, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('35e9625226c34bf09e129ad1e9094a3c', 'code', 'vue路由code编码值', '4707a06325ce4b2bbf8346534f4d0872', 2, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('363007ee645b4ba1a5943f0fba61f5e4', 'id', '权限数据关联主键编号「Lock」', '833c2915eba94675a177cf811f02b524', 8, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('37a88d99399f47d79898813429967765', 'eid', '数据引擎编号', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 1, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('3841a5b024d94a1d995310b5d58ca70f', 'pid', '父级菜单编号（默认为-1：顶层）', '4707a06325ce4b2bbf8346534f4d0872', 6, 'VARCHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('3a0cf9253b9b474b9c0ca847b15aab67', 'note', '用户备注', 'fe62792431d24c66830878371b7a3c36', 2, 'VARCHAR', 200, 0, 0);
INSERT INTO `cr_field` VALUES ('3e1b610307e54de2a23706d01d0b5c21', 'version', '版本', '2a6d662b1c624e289c0daf57ce462a7b', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('3fa9c0b2b8c24aec83eee9450b29fc4e', 'is_lock', '是否锁定当前菜单权限关联关系（0-否 / 1-是），锁定后无法更新及删除', '06e32bc351d64ff789cc9c877b4a9e0f', 11, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('40636ce50c774e25b8b04e027f8e9d90', 'state', '当前权限状态（0-正常/1-禁用/2-其他）「Lock」', '06e32bc351d64ff789cc9c877b4a9e0f', 13, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('42561fabe55145fca5ca3aee24abdfa0', 'version', '当前数据版本序号「Lock」', 'bdada100a2ce459a82d49bf1249f234a', 7, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('469c3c90fb054ec1a098b5d46d72f430', 'tid', '当前权限可访问数据表ID', '833c2915eba94675a177cf811f02b524', 12, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('4719cf0be31244f2b08336eb48931437', 'rid', '权限ID', '8dc7b111e3404e979b8a74821322d9be', 9, 'VARCHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('47e189ae931f4d56a1983f27d0461b24', 'id', '主键ID', '2a6d662b1c624e289c0daf57ce462a7b', 3, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('492b249d2fe04ecca8028f687e3acac9', 'password', '系统访问密码/二级密码', 'fe62792431d24c66830878371b7a3c36', 8, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('495fe360efb74d96b255231c42c6e88f', 'id', '数据/逻辑引擎仓库主键编号', '1501f72de1684428bd5662cda7802ec2', 5, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('49c7e15a13bc4ff982718feec2818069', 'size', '核心字段长度', '083f09883f9e4a75b773b2dfe3e1518d', 2, 'DOUBLE', 22, 0, 0);
INSERT INTO `cr_field` VALUES ('4a462f631ed84c94902b81dfc024d7e4', 'executeVail', '数据/逻辑执行验证状态（0-未通过逻辑验证/1-已通过）', '1501f72de1684428bd5662cda7802ec2', 7, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('4b5f0e479ba9478e81c14e4379bc1931', 'openid', '微信绑定-openId', 'fe62792431d24c66830878371b7a3c36', 4, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('4cb8bb4405dd447eb9f73c296a88977c', 'last_time', '最后操作时间「Lock」', '833c2915eba94675a177cf811f02b524', 3, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('52216dbc9b674366add20d106fd58b1b', 'mobile', '手机号（限中国大陆+86）', 'fe62792431d24c66830878371b7a3c36', 5, 'VARCHAR', 11, 0, 0);
INSERT INTO `cr_field` VALUES ('5259988770694844a48dd13e4a845e4d', 'repeat_val', '强制替换更新值/绑定的引擎id', '72442342cfc946c6a2508ac8c9e64f83', 13, 'VARCHAR', 255, 0, 0);
INSERT INTO `cr_field` VALUES ('5526ede03e0049b6a0f3e4c53d7c0545', 'sourceData', '执行结果', '54bda48c3f7243eeb33b1143ee7a4d18', 11, 'TEXT', 65535, 0, 0);
INSERT INTO `cr_field` VALUES ('5608baa7a0c64805aa3d8789d3e4dca0', 'expTag', '特殊指令', '125fe34b85224d08a4b88445688dbc28', 3, 'VARCHAR', 50, 0, 0);
INSERT INTO `cr_field` VALUES ('564b5ff3c7a54beb97bb688befeb39c1', 'code', '核心字段Code值', '083f09883f9e4a75b773b2dfe3e1518d', 1, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('56bfabbbbe7d4146beb495d8fa9b9de4', 'is_lock', '是否锁定当前权限（0-否 / 1-是），锁定后无法更新及删除', 'bdada100a2ce459a82d49bf1249f234a', 10, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('589cf565214f421ca3e05820a4344074', 'order_number', '权限序号', 'bdada100a2ce459a82d49bf1249f234a', 4, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('58efb00143144b2183e194aa010fc065', 'order_number', '数据表关联序号', '833c2915eba94675a177cf811f02b524', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('59401fe57f484a309aa39e68a18c12a6', 'version', '当前数据版本序号「Lock」', '8dc7b111e3404e979b8a74821322d9be', 10, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('5b58ba8da75440d09d6ae86b866c89ae', 'id', '菜单权限关联表主键编号「Lock」', '06e32bc351d64ff789cc9c877b4a9e0f', 12, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('5dd874360df7419f84ea810ca9dbe32e', 'state', '核心表状态（0-已失效/1-正常）', '633766ff0f2e4dbba2968b038bcbecbd', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('60354c1c862147e88eeb01d14409015a', 'executeTag', '数据引擎执行引导标识', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 2, 'VARCHAR', 50, 0, 0);
INSERT INTO `cr_field` VALUES ('6232330e08104699b078b1cfbae2b42c', 'note', '菜单备注', '4707a06325ce4b2bbf8346534f4d0872', 1, 'VARCHAR', 200, 0, 0);
INSERT INTO `cr_field` VALUES ('63ba2cca12d746d5b2b1968762417859', 'last_ip', '最后访问IP', 'fe62792431d24c66830878371b7a3c36', 1, 'VARCHAR', 50, 0, 0);
INSERT INTO `cr_field` VALUES ('68c0003045b548bcaa6673d3e3be9528', 'updateTime', '执行时间', '54bda48c3f7243eeb33b1143ee7a4d18', 6, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('6a6c06973dc544568a49b49e3429323f', 'id', '表信息变更日志主键', '54bda48c3f7243eeb33b1143ee7a4d18', 7, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('6b91ef695e844a9ebb5869fea68e3c04', 'executeData', '待执行数据参数', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 3, 'VARCHAR', 255, 0, 0);
INSERT INTO `cr_field` VALUES ('6c65d3b5c6d74f65b88f203c92274421', 'name', '权限名称', 'bdada100a2ce459a82d49bf1249f234a', 9, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('6d078ceb9e6840698e9c1015d36bd779', 'is_supervisor', '是否系统超级管理员（0 - 否 / 1-是 ），超管具有全数据操作权限，不受权限制约', 'bdada100a2ce459a82d49bf1249f234a', 2, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('7238845fa8f54e38b41251f49b1f14ef', 'methods', '可用方法「仅type为1时使用」引擎为id,逻辑类为方法名称,-1为全部方法', '833c2915eba94675a177cf811f02b524', 4, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('72bfc55cbf634682be833f4f78621135', 'schema_name', '关联的逻辑类名称', '72442342cfc946c6a2508ac8c9e64f83', 3, 'VARCHAR', 100, 0, 0);
INSERT INTO `cr_field` VALUES ('759ada599f914089af1d938881af6982', 'state', '数据状态', '54bda48c3f7243eeb33b1143ee7a4d18', 9, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('75bb38baafaa40b6a6d1e07132a6dcdd', 'version', '逻辑执行当前版本序号', '125fe34b85224d08a4b88445688dbc28', 9, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('7646beab6c54450bbd78f7f15ae260ad', 'last_time', '最后操作时间「Lock」', '06e32bc351d64ff789cc9c877b4a9e0f', 10, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('76ad45d0ac834f3f92e18abb4900b530', 'code', '数据/逻辑引擎仓库Code值', '1501f72de1684428bd5662cda7802ec2', 2, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('781560c6f11c41deb64d9223e0ee46c1', 'order_number', '授权序号（优先级）', '8dc7b111e3404e979b8a74821322d9be', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('7c4db721b63b42d1b24d996f5b732223', 'sorts', '核心字段序列', '083f09883f9e4a75b773b2dfe3e1518d', 7, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('7c5204b5bfe5422d9462b085cd6e5e79', 'id', '菜单表主键编号「Lock」', '4707a06325ce4b2bbf8346534f4d0872', 15, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('7ec3323c9f2a41378a25c06826c8d2bc', 'name', '核心字段名称', '083f09883f9e4a75b773b2dfe3e1518d', 3, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('80e598845d55414fb297d857adabc194', 'index_page', '指定当前权限访问的首页路由地址', 'bdada100a2ce459a82d49bf1249f234a', 11, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('8106be10a01e47f88b5847f58bbb37bf', 'state', '当前环节是否启用（0-未启用/1-已启用）', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('856b97fd1c844275b6f0025def4278dc', 'version', '版本', 'aae50cc5a20f4a4cacaccdda11c35b90', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('8b797cfabd0846a68505b1cbe0494e9b', 'page_normal_role', '普通用户页面相关按钮逻辑（0 - 隐藏 / 1 - 显示） 增删改查导出导入（010010）', '06e32bc351d64ff789cc9c877b4a9e0f', 1, 'VARCHAR', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('8b8d99ca6a204c6a9364c7acdcc1b147', 'is_lock', '是否锁定当前菜单（0-否 / 1-是），锁定后无法更新及删除', '4707a06325ce4b2bbf8346534f4d0872', 14, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('916e8c5badec4820a7cca442d930e652', 'name', '菜单名称', '4707a06325ce4b2bbf8346534f4d0872', 13, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('970ed0da072f449fa5122762e2cea3e8', 'create_time', '数据创建时间「Lock」', '833c2915eba94675a177cf811f02b524', 1, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('97ec5444323b4e0a9cf426efe2ac27a5', 'id', '数据引擎需求字段主键编号', 'aae50cc5a20f4a4cacaccdda11c35b90', 3, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('991d9c0d9f7c49f785572ab7c880d762', 'id', '数据引擎流程核心主键编号', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 4, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('9abae0dc1297433190610e6d0e001ba9', 'state', '数据/逻辑引擎启用状态（0-启用/1-禁用/2-审核中）', '1501f72de1684428bd5662cda7802ec2', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('9baf534ece1745dab081ae8a949489e6', 'version', '当前数据版本序号「Lock」', '833c2915eba94675a177cf811f02b524', 11, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('9c17ab858a10465db33e279af1475d70', 'rid', '关联的权限id', '72442342cfc946c6a2508ac8c9e64f83', 4, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('9c4ff9c3c0d640a89972b63631c47c64', 'type', '数据类型（0 - cr_table 数据表/ 1-cr_engine 引擎）', '833c2915eba94675a177cf811f02b524', 10, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('9e3717ddf331473fb149b930e76c5f67', 'state', '核心字段状态（1-禁用/0-启用）', '083f09883f9e4a75b773b2dfe3e1518d', 5, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('a329e1fdbd15440f9f9bb8ed2de9f0b9', 'patch_type', '匹配类型「1-等于，2-sql引擎」', '72442342cfc946c6a2508ac8c9e64f83', 12, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('ab12b9b7afdc4e9ab8750aad1c38136f', 'patch_filed', '关联的字段（方法/表内)', '72442342cfc946c6a2508ac8c9e64f83', 9, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('adb4b27299954dff90f7138e74f86380', 'log_ip', '访问ip', '54bda48c3f7243eeb33b1143ee7a4d18', 3, 'VARCHAR', 100, 0, 0);
INSERT INTO `cr_field` VALUES ('adfc9871d6d3446497eec1bd9dcf297e', 'name', '字段标识名称', 'aae50cc5a20f4a4cacaccdda11c35b90', 2, 'VARCHAR', 50, 0, 0);
INSERT INTO `cr_field` VALUES ('af1d7ee3239a4805b1148abc9340e5bd', 'engine_params', '引擎关联字段赋值「type为2时生效」', '72442342cfc946c6a2508ac8c9e64f83', 1, 'TEXT', 65535, 0, 0);
INSERT INTO `cr_field` VALUES ('b0edf096201a4b71b1a4b09eea3d5d14', 'rid', '关联权限ID', '06e32bc351d64ff789cc9c877b4a9e0f', 8, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('b178eeefac93429a9d68a22df3a2bc01', 'last_time', '最后操作时间「Lock」', '4707a06325ce4b2bbf8346534f4d0872', 12, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('b1f407b2cb984d77996113775de7015e', 'id', '授权主键编号「Lock」', '8dc7b111e3404e979b8a74821322d9be', 7, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('b3a91da022e84a23b0dba9bc867d604f', 'is_lock', '是否锁定当前绑定关系（0-否 / 1-是），锁定后无法更新及删除', '833c2915eba94675a177cf811f02b524', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('b3ca35850f884bc3963c2311fb699f9a', 'last_time', '最后访问时间「Lock」', 'fe62792431d24c66830878371b7a3c36', 9, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('b4e7049ee1c94329a73e56db852f5f44', 'version', '当前数据版本序号「Lock」', '72442342cfc946c6a2508ac8c9e64f83', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('b4fe1337fd8c47ac9f523069ce3ea0ea', 'sorts', '指令执行序列', '125fe34b85224d08a4b88445688dbc28', 10, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('b5bb214d4cda4a1bbbfca541a0b3f6a0', 'state', '逻辑执行启用状态（0-启用/1-禁用/2-审核中）', '125fe34b85224d08a4b88445688dbc28', 8, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('b65b2aa3ba914794959694c7656103c0', 'create_time', '数据创建时间「Lock」', '8dc7b111e3404e979b8a74821322d9be', 3, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('b70250dae15c42ceb4b7e5b518db2ec2', 'is_parent', '是否父节点（0-否/1-是）', 'bdada100a2ce459a82d49bf1249f234a', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('ba3d38a1b6ef47fbad8d9fc7d06d9da1', 'create_time', '数据创建时间「Lock」', '72442342cfc946c6a2508ac8c9e64f83', 2, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('bae04909c79149f4a5de7157cd419c38', 'path', 'vue路由path路径', '4707a06325ce4b2bbf8346534f4d0872', 10, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('be19af6395bc4222aa2e9db75f3f751a', 'version', '当前数据版本序号', '633766ff0f2e4dbba2968b038bcbecbd', 7, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('bea70fd031eb41198d45de2b219179e1', 'fieldBan', '字段禁用「仅type为0时使用」-1为全字段', '833c2915eba94675a177cf811f02b524', 2, 'VARCHAR', 255, 0, 0);
INSERT INTO `cr_field` VALUES ('c3b210e6b0ce4dba999ee28157624636', 'id', '核心字段表主键', '083f09883f9e4a75b773b2dfe3e1518d', 4, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('c401346256a642e9a1167a33545df6a0', 'uid', '用户ID', '8dc7b111e3404e979b8a74821322d9be', 2, 'VARCHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('c428639fd31c46c297d3c1f81d6e71bb', 'is_admin', '是否管理员「0-否，1-是」', '8dc7b111e3404e979b8a74821322d9be', 1, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('c6cdfe2e5a0a429d821fc93d243f97b2', 'expCute', '特殊操作', '125fe34b85224d08a4b88445688dbc28', 4, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('c8e257cdbf6b4494aed23bc3e2eaeff2', 'id', '用户表主键编号「Lock」', 'fe62792431d24c66830878371b7a3c36', 12, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('c8e4cff8ff98408d978dcfa20efd538f', 'component', 'vue路由component组件路由', '4707a06325ce4b2bbf8346534f4d0872', 11, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('c9ddb4feb2b047dabb86708e0fe19816', 'state', '当前用户状态（0-正常/1-禁用/2-其他）「Lock」', 'fe62792431d24c66830878371b7a3c36', 13, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('ca6fea5d25284bd3a25f87925009678f', 'version', '当前数据版本序号', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 7, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('caec47918d6f49e7ba2765d465965eb7', 'exe_data', '请求参数', '54bda48c3f7243eeb33b1143ee7a4d18', 1, 'TEXT', 65535, 0, 0);
INSERT INTO `cr_field` VALUES ('cb3d7b526a4a4aa6af917dcf768092eb', 'alias_name', '菜单别名（用于菜单个性化）', '06e32bc351d64ff789cc9c877b4a9e0f', 7, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('cba9f35c7e464a13af684b26c64e72dd', 'create_time', '数据创建时间「Lock」', 'bdada100a2ce459a82d49bf1249f234a', 3, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('cf0ea432f5194b02808c4a93f3769e49', 'engineId', '关联引擎主键编号', 'aae50cc5a20f4a4cacaccdda11c35b90', 6, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('d3ef27ec937e40cca1125a1214e3463e', 'create_time', '数据创建时间「Lock」', '06e32bc351d64ff789cc9c877b4a9e0f', 2, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('d42f199ab647404c953ffa8cef3c0852', 'icon', 'vue路由可用的icon图标值', '4707a06325ce4b2bbf8346534f4d0872', 5, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('d54e7fe512d84bdb99c985ba98005100', 'state', '状态', '2a6d662b1c624e289c0daf57ce462a7b', 4, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('d6863c25622147dd90d1259eb6c551f6', 'type', '访问权限（0-普通用户 / 1-管理员）', 'fe62792431d24c66830878371b7a3c36', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('d6bf2076e4304a33a2dcd3fb02e0aa3c', 'is_parent', '是否为父级节点（0-否/1-是）', '4707a06325ce4b2bbf8346534f4d0872', 7, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('d9d7c578a13e4a568011f90d4a036dbd', 'code', '输出字段CODE', '2a6d662b1c624e289c0daf57ce462a7b', 1, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('daea847b196d4a20a66707f12d7f9584', 'state', '当前权限状态（0-正常/1-禁用/2-其他）「Lock」', '4707a06325ce4b2bbf8346534f4d0872', 16, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('dd6a1b29ee2c43c1b901b01a93735d30', 'sourceTag', '执行的结果标识', '54bda48c3f7243eeb33b1143ee7a4d18', 8, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('dfbf864b874d48318862eb0de1933980', 'state', '状态', 'aae50cc5a20f4a4cacaccdda11c35b90', 4, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('dfe555c4a76d425b9464b96955fe23cf', 'id', '权限表主键编号「Lock」', 'bdada100a2ce459a82d49bf1249f234a', 12, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('e0526ff0c8a0444a833045b2a6a2bafd', 'version', '当前数据版本序号「Lock」', '06e32bc351d64ff789cc9c877b4a9e0f', 9, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('e1a24e81e8b04af085b6bff2ae950127', 'code', '核心表Code值', '633766ff0f2e4dbba2968b038bcbecbd', 2, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('e2660c772def473ab79e302b888d5bda', 'type', '核心字段类型', '083f09883f9e4a75b773b2dfe3e1518d', 6, 'VARCHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('e486facdf1274451a8540a3928854ad8', 'version', '当前数据版本序号', '083f09883f9e4a75b773b2dfe3e1518d', 8, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('e568b74b1393420eb7a18c8c909211c5', 'name', '用户名', 'fe62792431d24c66830878371b7a3c36', 10, 'VARCHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('e58e5fcc6929490bb474fc5187a66bbb', 'id', '逻辑执行主键编号', '125fe34b85224d08a4b88445688dbc28', 7, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('e78d827c1a8e44e48b7b39838197bf76', 'executor', '逻辑执行操作指令', '125fe34b85224d08a4b88445688dbc28', 5, 'VARCHAR', 50, 0, 0);
INSERT INTO `cr_field` VALUES ('ea34e0d3ef38485292d50fbc609ac721', 'create_time', '数据创建时间「Lock」', 'fe62792431d24c66830878371b7a3c36', 3, 'DATETIME', 26, 0, 0);
INSERT INTO `cr_field` VALUES ('ea459d46e59945f0848827eee0150c6b', 'is_lock', '是否锁定当前授权（0-否 / 1-是），锁定后无法更新及删除', '8dc7b111e3404e979b8a74821322d9be', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('eaff9eb1defa48b981399c0590d4a4b8', 'state', '当前权限状态（0-正常/1-禁用/2-其他）「Lock」', 'bdada100a2ce459a82d49bf1249f234a', 13, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('ebcd0fa72b2d4a8bbeeb2f49790b0485', 'is_lock', '是否锁定当前权限（0-否 / 1-是），锁定后无法更新及删除', 'fe62792431d24c66830878371b7a3c36', 11, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('f0b06e35646d48f2a548534841afb912', 'pid', '父级权限编号（默认为-1：顶层）', 'bdada100a2ce459a82d49bf1249f234a', 5, 'VARCHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('f1141e3c71524779b3297f9316895b79', 'engineId', '关联引擎ID', '2a6d662b1c624e289c0daf57ce462a7b', 6, 'VARCHAR', 64, 0, 0);
INSERT INTO `cr_field` VALUES ('f33cb263dbc74fbebe9d8d691bcb65e9', 'sorts', '数据引擎流程环节序列号', 'bd2b7b22e54644d58f5e8eb17dba1e8f', 6, 'INT', 10, 0, 0);
INSERT INTO `cr_field` VALUES ('fb345efcbf544db6937be511dfbb1c48', 'note', '核心表备注信息', '633766ff0f2e4dbba2968b038bcbecbd', 1, 'VARCHAR', 128, 0, 0);
INSERT INTO `cr_field` VALUES ('ff484e7432f04b64b24eec9a3ff993f8', 'executorId', '操作者id', '54bda48c3f7243eeb33b1143ee7a4d18', 2, 'CHAR', 32, 0, 0);
INSERT INTO `cr_field` VALUES ('ffa623fabe124facbbd201d742a121f4', 'logicTag', '逻辑执行标识字段', '125fe34b85224d08a4b88445688dbc28', 6, 'VARCHAR', 50, 0, 0);

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
INSERT INTO `cr_logger` VALUES ('6f6a0ca48d924b7b964011c7cd715aab', NULL, 'roleMenu - bindRoleMenu - ', 'c052d6525a444cada1a0809c7f5f9a57', 33, '2021-03-11 14:32:33', '方法执行成功', 1, '127.0.0.1', 0, '{\"upload\":\"1\",\"mid\":\"5064ecb799ea4f0798a8380e1690e8ef\",\"del\":\"1\",\"rid\":\"074e19ca81b944629773fff101ea759e\",\"exportData\":\"1\",\"download\":\"1\",\"download2\":\"0\",\"sel\":\"1\",\"add2\":\"0\",\"upd2\":\"0\",\"add\":\"1\",\"role_type\":\"1\",\"upd\":\"1\",\"del2\":\"0\",\"exportData2\":\"0\",\"upload2\":\"0\",\"sel2\":\"1\",\"name\":\"超级管理员「日志管理」菜单：数据权限设定\"}');
INSERT INTO `cr_logger` VALUES ('8d097f8389a1458a89c1b3d3a06623f1', NULL, 'roleMenu - bindRoleMenu - ', 'c052d6525a444cada1a0809c7f5f9a57', 33, '2021-03-11 14:32:30', '方法执行成功', 1, '127.0.0.1', 0, '{\"upload\":\"1\",\"mid\":\"9951f52dc3ec4815a4698bd384b4c070\",\"del\":\"1\",\"rid\":\"074e19ca81b944629773fff101ea759e\",\"exportData\":\"1\",\"download\":\"1\",\"download2\":\"0\",\"sel\":\"1\",\"add2\":\"0\",\"upd2\":\"0\",\"add\":\"1\",\"role_type\":\"1\",\"upd\":\"1\",\"del2\":\"0\",\"exportData2\":\"0\",\"upload2\":\"0\",\"sel2\":\"1\",\"name\":\"超级管理员「用户管理」菜单：数据权限设定\"}');

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
INSERT INTO `cr_tables` VALUES ('06e32bc351d64ff789cc9c877b4a9e0f', 'sys_rolemenu', '系统：菜单→权限关系表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('083f09883f9e4a75b773b2dfe3e1518d', 'cr_field', '核心：数据字段信息', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('125fe34b85224d08a4b88445688dbc28', 'cr_logicrule', '核心表：逻辑执行引擎', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('1501f72de1684428bd5662cda7802ec2', 'cr_engine', '核心：数据引擎仓库', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('2a6d662b1c624e289c0daf57ce462a7b', 'cr_engineout', '核心：数据输出字段表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('385b7c3227e3427d860e407a9219f4e7', 'sc_userRole', '系统用户权限「授权」', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('40dce86ecd064e8a9286c63c6178e57a', 'sc_menu', '系统菜单', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('4707a06325ce4b2bbf8346534f4d0872', 'sys_menu', '系统：菜单信息表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('54bda48c3f7243eeb33b1143ee7a4d18', 'cr_logger', '核心：系统日志', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('633766ff0f2e4dbba2968b038bcbecbd', 'cr_tables', '核心：数据表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('66ca52cd6f7d48b59e03e025ed147b8e', 'sc_user', '系统用户', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('72442342cfc946c6a2508ac8c9e64f83', 'sys_role_patch_data', '系统：权限数据分配信息表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('833c2915eba94675a177cf811f02b524', 'sys_menudata', '系统：菜单可访问数据对应关系表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('8dc7b111e3404e979b8a74821322d9be', 'sys_userrole', '系统：用户多权限授权信息表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('9eb94bcab18640d880b8075282cdcb01', 'sc_roleMenu', '系统权限菜单关系', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('aae50cc5a20f4a4cacaccdda11c35b90', 'cr_engineparam', '核心：必要字段关联信息表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('b2c5f868cc684f4d90d993302bbbf497', 'sc_logger', '核心：系统日志', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('b79d4303f5a041f0899b61ac3c0dc4b2', 'sc_analysis', '业务：数据分析·驾驶舱', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('bd2b7b22e54644d58f5e8eb17dba1e8f', 'cr_engineexecute', '核心：数据引擎执行', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('bdada100a2ce459a82d49bf1249f234a', 'sys_role', '系统：权限信息表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('c2f2c52e37e84b4ea1dc2b921ac7f112', 'sc_rolePatchData', '系统权限「角色」数据分配管理', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('da9060bf16604ecc909e8a58bfc2c56c', 'sc_base', '系统基础方法', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('e7f0637e354544e6824c25d2cada9b7b', 'sc_file', '系统文件管理', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('ee030ac1923c41f8b58c260afe60b78b', 'sc_role', '系统权限', NULL, 0, 0, 1);
INSERT INTO `cr_tables` VALUES ('fe62792431d24c66830878371b7a3c36', 'sys_user', '系统：用户信息表', NULL, 0, 0, 0);
INSERT INTO `cr_tables` VALUES ('ff6c4ba6d02b47faad14ce982681d36f', 'sc_menuData', '系统菜单数据「表/逻辑」访问授权管理', NULL, 0, 0, 1);

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
INSERT INTO `sys_menudata` VALUES ('2c4cf57fc57140538f034e650b06c827', 0, 0, '2021-03-11 14:27:49', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', 'e7f0637e354544e6824c25d2cada9b7b', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('4d526482ed0745d1b2901cefaefab91b', 0, 0, '2021-03-11 14:28:19', NULL, 0, 0, '5064ecb799ea4f0798a8380e1690e8ef', 'b2c5f868cc684f4d90d993302bbbf497', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('4e19472d622142a8b1083ad75700f364', 0, 0, '2021-03-11 14:26:42', NULL, 0, 0, 'c1f54036bce64f01aa315b291852656a', 'ee030ac1923c41f8b58c260afe60b78b', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('52b0ba00da754b2282ca7eaabea51315', 0, 0, '2021-03-11 14:26:24', NULL, 0, 0, '880ab0948185422c82a382f74ee23942', 'ff6c4ba6d02b47faad14ce982681d36f', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('5695aa2ca4c145e1953d783cfc6d314d', 0, 0, '2021-03-11 14:26:15', NULL, 0, 0, '880ab0948185422c82a382f74ee23942', '40dce86ecd064e8a9286c63c6178e57a', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('628068e2311641508f6fe733f952897c', 0, 0, '2021-03-11 14:28:12', NULL, 0, 0, '5064ecb799ea4f0798a8380e1690e8ef', '54bda48c3f7243eeb33b1143ee7a4d18', 0, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('6f613219dc6a48edbe4abfe1b4ce7950', 0, 0, '2021-03-11 14:26:49', NULL, 0, 0, 'c1f54036bce64f01aa315b291852656a', 'c2f2c52e37e84b4ea1dc2b921ac7f112', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('a004f1b49c2c49e49b34367115f499a7', 0, 0, '2021-03-11 14:27:13', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', 'fe62792431d24c66830878371b7a3c36', 0, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('b967a365416d42c191cb0e48a6110a5c', 0, 0, '2021-03-11 14:28:06', NULL, 0, 0, '5064ecb799ea4f0798a8380e1690e8ef', 'e7f0637e354544e6824c25d2cada9b7b', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('baffac559b444586873126ce272491d8', 0, 0, '2021-03-11 14:26:58', NULL, 0, 0, 'c1f54036bce64f01aa315b291852656a', '9eb94bcab18640d880b8075282cdcb01', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('c12598f3ccc748848cc8f76bfcb7d2a8', 0, 0, '2021-03-11 14:27:28', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', '385b7c3227e3427d860e407a9219f4e7', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('c92f7c4614c44066b65274f2be5fbfaf', 0, 0, '2021-03-11 14:25:41', NULL, 0, 0, '8704fef9dba642c5bd9a7ee54c18959c', 'b79d4303f5a041f0899b61ac3c0dc4b2', 1, NULL, NULL);
INSERT INTO `sys_menudata` VALUES ('dffb08a9920541529d684c96bf64761f', 0, 0, '2021-03-11 14:27:22', NULL, 0, 0, '9951f52dc3ec4815a4698bd384b4c070', '66ca52cd6f7d48b59e03e025ed147b8e', 1, NULL, NULL);

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
INSERT INTO `sys_rolemenu` VALUES ('1880b73900e841df961c69b78e8b61c9', 0, 0, '2021-03-11 14:32:22', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '8704fef9dba642c5bd9a7ee54c18959c', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('2f430eddf08d48a982d247926fc948b6', 0, 0, '2021-03-11 14:32:24', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '880ab0948185422c82a382f74ee23942', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('659aff53dbc644b98f4abc29d25feff0', 0, 0, '2021-03-11 14:32:33', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '5064ecb799ea4f0798a8380e1690e8ef', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('66c411a0e45a42a58c17880c2028be8a', 0, 0, '2021-03-11 14:32:30', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', '9951f52dc3ec4815a4698bd384b4c070', 0, '1111111', '0001000', 1);
INSERT INTO `sys_rolemenu` VALUES ('d3ab877b0be34539828a045bc7d76dc9', 0, 0, '2021-03-11 14:32:26', NULL, 0, NULL, '074e19ca81b944629773fff101ea759e', 'c1f54036bce64f01aa315b291852656a', 0, '1111111', '0001000', 1);

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
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户企业邮箱账号',
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
INSERT INTO `sys_userrole` VALUES ('7a6a742c930d4efdb54beb5827f94ba9', 0, 0, '2020-10-30 12:28:01', NULL, 0, 0, 'c052d6525a444cada1a0809c7f5f9a57', '074e19ca81b944629773fff101ea759e', 1);

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
