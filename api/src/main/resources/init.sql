SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_customer
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_customer`  (
  `id` bigint NOT NULL COMMENT '主键ID (雪花算法)',
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公司名称',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发货地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除, 1:已删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_error_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_error_log`  (
  `id` bigint NOT NULL COMMENT '雪花算法ID',
  `request_userid` bigint NOT NULL COMMENT '请求的用户id',
  `request_username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求的用户名',
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求的URL地址',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方式(GET/POST等)',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '前端传来的请求参数(JSON格式)',
  `error_brief` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报错简述(截取前500字符)',
  `error_stack` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '完整报错堆栈信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报错发生时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '按时间查询的索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统报错日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_inventory_flow
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_inventory_flow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_id` bigint NOT NULL COMMENT '关联的物品ID',
  `material_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快照: 物品名称',
  `material_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '快照: 物品类型',
  `change_type` tinyint NOT NULL COMMENT '变动类型: 1-入库, 2-出库',
  `change_num` decimal(10, 2) NOT NULL COMMENT '变动数量 (绝对值)',
  `before_num` decimal(10, 2) NOT NULL COMMENT '变动前总数',
  `after_num` decimal(10, 2) NOT NULL COMMENT '变动后总数 (预计算值)',
  `relation_order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联订单号/业务单号',
  `create_user_id` bigint NOT NULL COMMENT '发起人ID (业务员)',
  `create_user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '快照: 发起人姓名',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发起时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态: 0-待确认, 1-已完成, 2-已驳回',
  `audit_user_id` bigint NULL DEFAULT NULL COMMENT '审核人ID (库管)',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作流实例ID(预留)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注/驳回原因',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_material_id`(`material_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_material_status_time`(`material_id` ASC, `status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存出入库流水审核表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_material
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_material`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物品名称',
  `material_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物品类型（如：原材料、半成品、成品、耗材）',
  `stock_num` decimal(18, 4) NULL DEFAULT 0.0000 COMMENT '物品数量（使用decimal防精度丢失）',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '件' COMMENT '计量单位',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物品描述',
  `purpose` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物品用途',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在地址（如：1号仓库）',
  `location_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '位置图片URL（OSS或MinIO地址）',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`material_name` ASC) USING BTREE,
  INDEX `idx_type`(`material_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物品基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人姓名(冗余存储，拒绝联表查询)',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块 (如: 库存管理)',
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行操作 (如: 逻辑下料/订单发货)',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作物理IP(追踪电脑溯源)',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统与浏览器(追踪电脑溯源)',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求接口路径',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求具体参数(留底防抵赖)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '执行结果: 1成功, 0报错',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报错原因(如有)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作精准时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_module_action`(`module` ASC, `action_type` ASC) USING BTREE,
  INDEX `idx_operator`(`operator_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2436 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '全局系统操作溯源日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_order
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_order`  (
  `id` bigint NOT NULL COMMENT '订单主键ID (雪花算法)',
  `order_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单说明',
  `customer_id` bigint NOT NULL COMMENT '关联的客户ID (用于后续按客户统计)',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户姓名 (快照)',
  `company_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户公司(快照)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系方式 (快照)',
  `total_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `paid_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '已支付金额',
  `unpaid_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '未支付金额',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '交付时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '完成状态 (0:待交付, 2:部分交付, 1:已完成)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除, 1:已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_order_time`(`order_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '归属部门ID',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1:正常 0:停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Function structure for currval
-- ----------------------------
DROP FUNCTION IF EXISTS `currval`;
delimiter ;;
CREATE FUNCTION `currval`(v_seq_name VARCHAR(50))
 RETURNS int
  DETERMINISTIC
BEGIN
 declare value integer;
 set value = 0;
 select current_val into value  from sequence where seq_name = v_seq_name;
   return value;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for nextval
-- ----------------------------
DROP FUNCTION IF EXISTS `nextval`;
delimiter ;;
CREATE FUNCTION `nextval`(v_seq_name VARCHAR(50))
 RETURNS int
  DETERMINISTIC
begin
    update sequence set current_val = current_val + increment_val  where seq_name = v_seq_name;
	return currval(v_seq_name);
end
;;
delimiter ;

-- ----------------------------
-- 初始化管理员账号数据 (使用 INSERT IGNORE 防重复插入报错，修复 is_deleted=0)
-- ----------------------------
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `real_name`, `dept_id`, `avatar`, `status`, `create_time`, `is_deleted`) 
VALUES (1, 'admin', 'admin', 'c83c5c7c45f035799381aa2a9d588d8cf8704163f054580e503fbf15bb69ec64', NULL, NULL, 1, '2026-04-09 14:20:04', 0);

SET FOREIGN_KEY_CHECKS = 1;