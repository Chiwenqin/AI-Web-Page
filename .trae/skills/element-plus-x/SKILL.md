# Element-Plus-X 项目技能

## 项目概述
Element-Plus-X 是一个基于 Vue 3 + Element-Plus 的企业级 AI 组件库，提供了丰富的 AI 相关组件，如聊天气泡、会话管理、语音交互等。

## 核心特性
- 企业级 AI 组件：内置聊天机器人、语音交互等场景化组件
- 零配置集成：基于 Element-Plus 设计体系，开箱即用
- 按需加载：提供 Tree Shaking 优化

## 安装方式
```bash
# NPM
npm install vue-element-plus-x

# PNPM（推荐）
pnpm install vue-element-plus-x

# Yarn
yarn install vue-element-plus-x
```

## 主要组件

| 组件名 | 描述 | 文档链接 |
| --- | --- | --- |
| Typewriter | 打字动画组件 | [文档](https://element-plus-x.com/zh/components/typewriter/) |
| Bubble | 气泡消息组件（拓展） | [文档](https://element-plus-x.com/zh/components/bubble/) |
| BubbleList | 气泡消息列表（拓展） | [文档](https://element-plus-x.com/zh/components/bubbleList/) |
| Conversations | 会话管理组件（拓展） | [文档](https://element-plus-x.com/zh/components/conversations/) |
| Welcome | 欢迎组件 | [文档](https://element-plus-x.com/zh/components/welcome/) |
| Prompts | 提示集组件 | [文档](https://element-plus-x.com/zh/components/prompts/) |
| FilesCard | 文件卡片组件 | [文档](https://element-plus-x.com/zh/components/filesCard/) |
| Attachments | 上传附件组件 | [文档](https://element-plus-x.com/zh/components/attachments/) |
| Sender | 智能输入框（含语音交互、内置指令操作） | [文档](https://element-plus-x.com/zh/components/sender/) |
| MentionSender | 指令输入框（提及列表） | [文档](https://element-plus-x.com/zh/components/mentionSender/) |
| Thinking | 思考中组件（拓展） | [文档](https://element-plus-x.com/zh/components/thinking/) |
| ThoughtChain | 思考链组件 | [文档](https://element-plus-x.com/zh/components/thoughtChain/) |

## Hooks

| Hook 名 | 描述 | 文档链接 |
| --- | --- | --- |
| useRecord | 浏览器内置语音识别 API Hooks | [文档](https://element-plus-x.com/zh/components/useRecord/) |
| useXStream | 流模式接口 Hooks | [文档](https://element-plus-x.com/zh/components/useXStream/) |
| useSend & XRequest | 流模式 hooks 的拆分（拓展） | [文档](https://element-plus-x.com/zh/components/useSend/) |

## 使用示例

### 按需引入
```vue
<script setup>
import { BubbleList, Sender } from 'vue-element-plus-x';

const list = [
  {
    content: 'Hello, Element Plus X',
    role: 'user'
  }
];
</script>

<template>
  <div
    style="display: flex; flex-direction: column; height: 230px; justify-content: space-between;"
  >
    <BubbleList :list="list" />
    <Sender />
  </div>
</template>
```

### 全局引入
```ts
// main.ts
import { createApp } from 'vue';
import ElementPlusX from 'vue-element-plus-x';
import App from './App.vue';

const app = createApp(App);
// 使用 app.use() 全局引入
app.use(ElementPlusX);
app.mount('#app');
```

## 项目结构
- apps/：包含文档和内部工具
- packages/core/：核心组件库
  - src/components/：组件实现
  - src/hooks/：Hooks 实现
  - src/utils/：工具函数

## 开发指南
- 项目使用 PNPM 管理依赖
- 使用 Vite 构建
- 支持 TypeScript
- 提供 Storybook 用于组件展示

## 注意事项
- Element-Plus-X 依赖 Element-Plus，使用前需要确保已安装 Element-Plus
- 组件库支持 Vue 3.2+ 版本
- 部分组件（如 useRecord）依赖浏览器 API，需要在浏览器环境中使用

## 资源链接
- [官方文档](https://element-plus-x.com)
- [在线演示](https://v.element-plus-x.com)
- [GitHub 仓库](https://github.com/element-plus-x/Element-Plus-X)
- [NPM 包](https://www.npmjs.com/package/vue-element-plus-x)