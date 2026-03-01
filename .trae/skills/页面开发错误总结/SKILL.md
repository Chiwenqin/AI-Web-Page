---
name: "页面开发错误总结"
description: "总结RuoYi项目页面开发常见错误和解决方案。在开发新页面时调用，避免重复犯错。"
---

# 页面开发错误总结技能

## 技能目的
本技能总结了在RuoYi-SpringBoot3项目中进行页面开发时遇到的常见错误及其解决方案，帮助开发者避免重复犯错，提高开发效率。

## 主要错误类型与解决方案

### 1. API端点缺失错误
**错误现象**: `No static resource biz/xxx/list`
**解决方案**: 完整实现后端四层架构（Controller → Service → Mapper → Entity）
**检查点**: 确保所有后端组件都已创建并正确配置

### 2. Java包声明缺失错误
**错误现象**: `The declared package "" does not match the expected package`
**解决方案**: 每个Java文件顶部添加正确的package声明
**检查点**: `package com.ruoyi.biz.domain;` 等

### 3. Jakarta EE迁移错误
**错误现象**: `javax.validation.constraints不存在`
**解决方案**: 使用jakarta包替代javax包
**检查点**: `jakarta.validation.constraints`, `jakarta.servlet.http`

### 4. Controller类型转换错误
**错误现象**: `Page<T>无法转换为List<?>`
**解决方案**: 使用`getDataTableByPage()`替代`getDataTable()`
**检查点**: Controller方法返回类型正确

### 5. MyBatis参数绑定错误
**错误现象**: `Parameter 'ew' not found`
**解决方案**: Mapper接口使用`@Param("ew")`，XML使用`${ew.customSqlSegment}`
**检查点**: 参数名称一致性

### 6. SQL列名歧义错误
**错误现象**: `Column 'xxx' in where clause is ambiguous`
**解决方案**: 多表查询时为列名添加表别名前缀
**检查点**: QueryWrapper中使用`表别名.列名`

### 7. 路由权限配置缺失
**错误现象**: 页面无法访问或权限不足
**解决方案**: 配置路由地址、权限字符、组件路径
**检查点**: 提供完整的权限字符列表

## 生成页面避免错误检查清单

### 一、后端代码检查（21项）
1. 包声明正确
2. 导入使用Jakarta EE包
3. 实体类实现Serializable
4. Mapper接口参数使用`@Param("ew")`
5. XML中使用表别名和`${ew.customSqlSegment}`
6. Controller使用`getDataTableByPage()`
7. QueryWrapper列名使用表别名前缀

### 二、数据库检查（4项）
8. SQL脚本完整
9. 包含测试数据
10. 字段类型匹配
11. 索引优化

### 三、前端代码检查（5项）
12. API路径与Controller匹配
13. Vue组件使用组合式API
14. 搜索表单字段名正确
15. 级联联动逻辑正确
16. 包含分页组件

### 四、配置检查（3项）
17. 路由配置正确
18. 权限字符完整
19. 组件路径正确

### 五、测试检查（4项）
20. 编译测试通过
21. API端点可访问

## 最佳实践建议

### 开发顺序
1. 数据库 → 实体类 → Mapper → Service → Controller → 前端API → Vue组件
2. 每层完成后进行简单测试
3. 先解决编译错误，再处理运行时错误

### 代码规范
1. 参考现有成功页面的结构
2. 命名保持一致性
3. 错误处理完善
4. 类型安全（禁止any）

### 调用时机
- 开发新页面时
- 遇到类似错误时
- 代码审查时
- 培训新开发者时