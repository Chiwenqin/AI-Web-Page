# 后台管理系统集成 AI 项目入口 Spec

## Why
用户希望在现有的 RuoYi 后台管理系统中添加一个顶级菜单入口，点击后能够访问独立的 AI 项目（ruoyi-element-ai-main），实现两个项目的无缝集成。

## What Changes
- 在后台管理系统左侧菜单栏添加「AI 助手」顶级菜单
- 点击菜单后通过 iframe 方式嵌入 AI 项目页面
- 支持在 iframe 中加载 ruoyi-element-ai-main 项目
- 保持现有系统的完整性，不修改任何现有功能

## Impact
- Affected specs: 后台管理系统菜单配置
- Affected code: 
  - `RuoYi-SpringBoot3-ElementPlus/src/views/` (新增 iframe 页面)
  - `RuoYi-SpringBoot3-ElementPlus/src/router/index.js` (新增路由)
  - 数据库 sys_menu 表 (新增菜单数据)

## ADDED Requirements
### Requirement: AI 助手菜单入口
系统 SHALL 提供一个顶级菜单入口，用户点击后可在系统内访问 AI 项目。

#### Scenario: 用户访问 AI 助手
- **WHEN** 用户点击左侧菜单栏的「AI 助手」顶级菜单
- **THEN** 系统在右侧主区域通过 iframe 加载 ruoyi-element-ai-main 项目页面

#### Scenario: AI 项目未启动
- **WHEN** 用户点击「AI 助手」菜单，但 ruoyi-element-ai-main 项目未启动
- **THEN** iframe 显示连接失败提示或空白页面

### Requirement: 菜单配置
系统 SHALL 提供两种菜单配置方式：
1. **数据库配置**：通过 sys_menu 表添加菜单数据
2. **前端代码配置**：直接添加 Vue 组件和路由

## Implementation Approach
### 推荐方案：iframe 嵌入
- 在现有项目中创建一个 iframe 页面组件
- 路由指向 `/ai` 时加载 ruoyi-element-ai-main 项目的访问地址
- 优点：用户体验流畅，无需重复登录（可配置）
- 缺点：需要处理跨域问题

### 备选方案：新窗口跳转
- 菜单链接直接打开新窗口访问 AI 项目
- 优点：实现简单，无跨域问题
- 缺点：用户体验不如 iframe

## MODIFIED Requirements
### Requirement: 现有功能保持不变
- 现有系统的所有功能和菜单保持不变
- 新增的 AI 助手菜单不影响现有功能
