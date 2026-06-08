-- 车牌识别 + 计时收费功能 数据库脚本
-- 运行于 ry_20250522 之后

-- 1. 车辆进出记录表
DROP TABLE IF EXISTS `pms_vehicle_record`;
CREATE TABLE `pms_vehicle_record` (
    `record_id`        BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `community_id`     BIGINT(20)   DEFAULT NULL COMMENT '小区ID',
    `parking_id`       BIGINT(20)   DEFAULT NULL COMMENT '关联车位ID',
    `plate_number`     VARCHAR(32)  NOT NULL COMMENT '车牌号',
    `entry_time`       DATETIME     DEFAULT NULL COMMENT '入场时间',
    `exit_time`        DATETIME     DEFAULT NULL COMMENT '出场时间',
    `parking_duration` DECIMAL(10,2) DEFAULT NULL COMMENT '停车时长(小时)',
    `unit_price`       DECIMAL(10,2) DEFAULT NULL COMMENT '计费单价(元/小时)',
    `fee_amount`       DECIMAL(10,2) DEFAULT NULL COMMENT '应收金额',
    `paid_amount`      DECIMAL(10,2) DEFAULT NULL COMMENT '实收金额',
    `pay_status`       CHAR(1)      DEFAULT '0' COMMENT '0=未支付 1=已支付',
    `pay_time`         DATETIME     DEFAULT NULL COMMENT '支付时间',
    `vehicle_type`     CHAR(1)      DEFAULT '0' COMMENT '0=临时车 1=月租车 2=业主车',
    `entry_image`      VARCHAR(255) DEFAULT NULL COMMENT '入场照片路径',
    `exit_image`       VARCHAR(255) DEFAULT NULL COMMENT '出场照片路径',
    `del_flag`         CHAR(1)      DEFAULT '0' COMMENT '删除标志',
    `create_by`        VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`      DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`        VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`      DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`record_id`),
    INDEX `idx_plate_number` (`plate_number`),
    INDEX `idx_pay_status` (`pay_status`),
    INDEX `idx_community_id` (`community_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='车辆进出记录表';

-- 2. 费用类型表（如不存在则创建）
CREATE TABLE IF NOT EXISTS `pms_fee_type` (
    `fee_type_id`  BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '费用类型ID',
    `type_name`    VARCHAR(64)  DEFAULT '' COMMENT '费用类型名称',
    `type_code`    VARCHAR(32)  DEFAULT '' COMMENT '费用类型编码',
    `unit_price`   DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    `unit`         VARCHAR(32)  DEFAULT '' COMMENT '计费单位',
    `status`       CHAR(1)      DEFAULT '0' COMMENT '状态(0=正常,1=停用)',
    `del_flag`     CHAR(1)      DEFAULT '0' COMMENT '删除标志',
    `create_by`    VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`    VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`fee_type_id`)
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='费用类型表';

-- 停车费类型种子数据（如已存在则忽略）
INSERT IGNORE INTO `pms_fee_type` (`type_name`, `type_code`, `unit_price`, `unit`, `status`, `del_flag`, `create_by`, `create_time`)
VALUES ('停车费', 'PARKING', 5.00, '小时', '0', '0', 'admin', NOW());

-- 3. 菜单数据 - 车辆管理（使用 MySQL 变量动态处理 parent_id）

-- 查找现有 property 父菜单（物业管理、物业、Property 等可能名称）
SET @property_parent = (
    SELECT menu_id FROM sys_menu
    WHERE (menu_name IN ('物业管理', '物业', '物业官网', 'Property') OR perms LIKE 'property%')
      AND parent_id = 0 AND menu_type = 'M'
    ORDER BY menu_id LIMIT 1
);

-- 如果不存在，创建「物业管理」父菜单（menu_id=2200），然后用变量指向它
INSERT IGNORE INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
VALUES (2200, '物业管理', 0, 5, 'property', NULL, 1, 0, 'M', '0', '0', NULL, 'tree', 'admin', NOW(), '物业管理目录');

-- 重新读取，确保变量有值
SET @property_parent = IFNULL(@property_parent, 2200);
SET @property_parent = (SELECT menu_id FROM sys_menu WHERE menu_name = '物业管理' AND parent_id = 0 LIMIT 1);

-- 3.1 车辆进出记录（列表页）
INSERT IGNORE INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
VALUES ('车辆进出记录', @property_parent, 7, 'vehicleRecord', 'property/vehicleRecord/index', 1, 0, 'C', '0', '0', 'property:vehicleRecord:list', 'car', 'admin', NOW(), '车辆进出记录菜单');

-- 获取子菜单ID
SET @record_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '车辆进出记录' LIMIT 1);

-- 3.2 车辆入场（按钮权限）
INSERT IGNORE INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `create_by`, `create_time`)
VALUES ('车辆入场', @record_menu_id, 1, '', '', 1, 0, 'F', '0', '0', 'property:vehicleRecord:enter', 'admin', NOW());

-- 3.3 车辆出场（按钮权限）
INSERT IGNORE INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `create_by`, `create_time`)
VALUES ('车辆出场', @record_menu_id, 2, '', '', 1, 0, 'F', '0', '0', 'property:vehicleRecord:exit', 'admin', NOW());

-- 3.4 把已有的 property 子菜单统一挂到同一个父菜单下（如果之前散落的话）
UPDATE sys_menu SET parent_id = @property_parent
WHERE menu_name IN ('小区管理', '楼栋管理', '房屋管理', '设备维修', '投诉建议', '费用记录', '费用类型', '停车位管理', '访客管理', '公告管理', '业主管理', '业主绑定')
  AND parent_id != @property_parent;

-- 4. 为管理员角色授予车辆管理权限（role_id=1 超级管理员）
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, menu_id FROM sys_menu WHERE menu_name IN ('车辆进出记录', '车辆入场', '车辆出场');


