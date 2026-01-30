-- 插入初始数据
USE rbac_db;

-- 插入默认管理员用户 (用户名: admin, 密码: admin123 的MD5值)
INSERT INTO `sys_user` (`username`, `password`, `status`) 
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 1);

-- 插入默认角色
INSERT INTO `sys_role` (`role_code`, `role_name`, `role_desc`) 
VALUES 
('ADMIN', '超级管理员', '拥有所有权限'),
('USER', '普通用户', '普通用户角色');

-- 插入默认权限
INSERT INTO `sys_permission` (`perm_code`, `perm_name`, `parent_id`) 
VALUES 
('system', '系统管理', 0),
('system:user', '用户管理', 1),
('system:user:add', '新增用户', 2),
('system:user:edit', '编辑用户', 2),
('system:user:delete', '删除用户', 2),
('system:role', '角色管理', 1),
('system:role:add', '新增角色', 6),
('system:role:edit', '编辑角色', 6),
('system:role:delete', '删除角色', 6),
('system:permission', '权限管理', 1),
('system:permission:add', '新增权限', 10),
('system:permission:edit', '编辑权限', 10),
('system:permission:delete', '删除权限', 10);

-- 为管理员分配角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) 
VALUES (1, 1);

-- 为管理员角色分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `perm_id`) 
SELECT 1, id FROM `sys_permission`;
