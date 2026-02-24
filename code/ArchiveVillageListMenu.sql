-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('小区列', '3', '1', 'ArchiveVillageList', 'biz/ArchiveVillageList/index', 1, 0, 'C', '0', '0', 'biz:ArchiveVillageList:list', '#', 'admin', sysdate(), '', null, '小区列菜单')
    ;
    -- 按钮父菜单ID
    SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('小区列查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'biz:ArchiveVillageList:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('小区列新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'biz:ArchiveVillageList:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('小区列修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'biz:ArchiveVillageList:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('小区列删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'biz:ArchiveVillageList:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('小区列导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'biz:ArchiveVillageList:export',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('小区列导入', @parentId, '6',  '#', '', 1, 0, 'F', '0', '0', 'biz:ArchiveVillageList:import',       '#', 'admin', sysdate(), '', null, '');

