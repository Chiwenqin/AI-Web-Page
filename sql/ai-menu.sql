-- AI 助手菜单 SQL 脚本
-- 在现有菜单表中添加 AI 助手顶级菜单

-- 插入顶级菜单 AI 助手（parent_id=0 表示顶级菜单）
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('AI 助手', 0, 5, 'ai', 'ai/index', '', '', 1, 0, 'M', '0', '0', '', 'chat', 'admin', NOW(), '', NULL, 'AI 助手顶级菜单');

-- 注意：如果需要添加子菜单，可以继续插入
-- 示例：添加 AI 聊天子菜单
-- INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
-- VALUES ('AI 聊天', [menu_id], 1, 'chat', 'ai/index', '', '', 1, 0, 'C', '0', '0', '', 'message', 'admin', NOW(), '', NULL, 'AI 聊天菜单');

-- 查询确认插入结果
-- SELECT * FROM sys_menu WHERE menu_name = 'AI 助手';
