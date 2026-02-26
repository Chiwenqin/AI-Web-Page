---
name: "ruoyi-springboot3-pro"
description: "Provides information and guidance on the RuoYi-SpringBoot3-Pro project, including architecture, features, and development guidelines. Invoke when user asks about the project structure, features, or how to use it."
---

# RuoYi-SpringBoot3-Pro

## 项目概述

RuoYi-SpringBoot3-Pro 是一个基于 Spring Boot 3 + MyBatis-Plus 的企业级快速开发框架。它是 RuoYi-Vue 的企业级增强版本，保留了原有的所有功能，并新增了多项企业级特性，全部开源免费。

## 核心特性

### 1. ClassFinal 代码加密
- **字节码加密**：对编译后的 class 文件进行深度加密
- **防反编译**：即使被反编译也无法查看源代码
- **选择性加密**：可指定需要加密的包（如 `com.ruoyi.biz`）
- **配置文件保护**：支持对 yml、properties 等配置文件加密
- **排除机制**：可排除第三方库（如 `org.spring`）
- **密码保护**：使用密码保护加密的代码，防止未授权运行
- **Maven 集成**：打包时自动加密，无需额外操作
- **企业级方案**：适合商业项目代码保护

### 2. 三级等保支持
- **密码更新周期**：可配置密码有效期（0-365 天），超期强制修改
- **登录失败锁定**：支持 N 次失败锁定 M 分钟策略（如 5-30 表示 5 次失败锁定 30 分钟）
- **初始密码强制修改**：首次登录强制修改初始密码
- **密码过期提醒**：登录时自动检测密码是否过期，提示用户更新
- **IP 黑名单**：支持 IP 黑名单配置，支持通配符和网段匹配
- **失败次数跟踪**：数据库记录登录失败次数，自动锁定/解锁
- **可配置化管理**：所有安全策略通过系统配置表动态管理，无需重启
- **Redis 缓存支持**：失败次数缓存到 Redis，提升性能

### 3. AI 能力集成
- **OpenAI SDK 集成**：基于官方 OpenAI Java SDK 封装
- **同步对话**：支持标准的请求-响应模式
- **流式对话**：支持 SSE 流式输出，实时展示生成内容
- **多角色支持**：支持 User、System、Assistant 三种角色
- **代理支持**：支持 HTTP/SOCKS 代理配置
- **自定义 API**：支持配置自定义 API 地址（兼容 OpenAI 协议的 API）
- **开箱即用**：提供完整的工具类和示例代码

### 4. MyBatis-Plus 集成
- 替换原有 MyBatis，提供更强大的 ORM 功能
- **分页插件**：自动识别数据库类型，无需手动配置
- **乐观锁插件**：防止并发修改数据丢失
- **防全表更新删除插件**：避免误操作造成数据丢失
- **多租户插件**：企业级 SaaS 应用必备能力
- Lambda 查询语法，更优雅的代码风格

### 5. Magic API 低代码开发
- **可视化接口开发**：通过 Web 界面快速开发 REST API
- **无需编译**：接口修改即时生效，大幅提升开发效率
- **数据库持久化**：接口脚本存储在数据库，支持版本控制
- **Redis 缓存支持**：内置 Redis 插件，轻松实现接口缓存
- **历史记录**：支持接口变更历史记录和回滚
- 访问地址：`http://localhost:8087/magic/web`（默认账号：jyx / jyx_692483）

### 6. 多数据库支持
- 支持国内外主流数据库，满足不同场景需求

| 数据库 | 支持版本 | 配置文件 | 初始化脚本 |
|--------|----------|----------|------------|
| **MySQL** | 5.7+ | application-devmy.yml | ruoyi-mysql.sql |
| **PostgreSQL** | 12+ | application-devpg.yml | ruoyi-pgsql.sql |
| **达梦数据库** | DM8+ | application-devdm.yml | ruoyi-dm8.dmp |
| **瀚高数据库** | 6.2+ | application-devhg.yml | ruoyi-highgo.sql |
| **高斯数据库** | GaussDB | application-devgs.yml | ruoyi-gauss.sql |

> 💡 切换数据库只需修改 `application.yml` 中的 `spring.profiles.active` 配置

### 7. 多租户支持
- **基于字段隔离**：通过 `tenant_id` 字段自动隔离租户数据
- **自动过滤**：SQL 自动注入租户条件，无需手动处理
- **灵活配置**：支持配置忽略表和忽略用户
- **透明化使用**：业务代码无感知，框架自动处理
- 配置开关：`tenant.enable: true/false`

### 8. ruoyi-biz 业务模块
- **模块化设计**：业务代码与系统代码分离
- **标准化结构**：Controller → Service → Mapper 标准分层
- **Excel 导入导出**：内置自定义 Excel 处理器示例
- **定时任务示例**：提供定时任务开发模板
- 包含地区管理等完整功能示例

### 9. Dify 数据库建表工作流
- 提供 Dify 工作流配置文件，通过 AI 快速生成多数据库建表语句
- **多数据库支持**：支持 MySQL、PostgreSQL/瀚高、openGauss、SQLite 四种数据库
- **智能字段命名**：支持拼音或英文两种字段命名规范
- **标准化表结构**：自动生成包含租户 ID、用户 ID、部门 ID、状态、创建/更新信息等标准字段
- **索引自动创建**：自动为常用字段创建索引（tenant_id、user_id、dept_id）
- **简洁输入格式**：只需输入"表名：字段（类型）"即可生成完整建表语句
- **开箱即用**：导入 `sql/Dify_数据库建表.yml` 到 Dify 即可使用

### 10. 代码生成模板优化
- 针对 MyBatis-Plus 优化代码生成模板
- **MyBatis-Plus 适配**：生成的 Mapper 继承 BaseMapper，自动拥有 CRUD 方法
- **Lambda 查询支持**：生成的代码支持 Lambda 表达式查询
- **多数据库兼容**：生成的 SQL 和实体类适配多种数据库
- **注解增强**：自动添加 `@TableName`、`@TableId` 等 MyBatis-Plus 注解
- **分页优化**：使用 MyBatis-Plus Page 对象，无需手动配置
- **代码更精简**：减少样板代码，提高开发效率
- **低代码结合**：可配合 Magic API 快速开发，双管齐下

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.5 | 容器 + MVC 框架 |
| Spring Security | 6.x | 认证和授权框架 |
| MyBatis-Plus | 3.5+ | ORM 框架（增强版） |
| OpenAI Java SDK | Latest | AI 对话能力 |
| Magic API | 2.2.2 | 低代码快速开发平台 |
| Redis | - | 分布式缓存（可选） |
| JWT | 0.9.1 | JWT 令牌 |
| Druid | 1.2.23 | 数据库连接池 |
| Springdoc | 2.6.0 | API 文档 |
| Quartz | - | 定时任务 |
| Velocity | 2.3 | 代码生成模板 |

### 前端技术

- **框架**：Vue 3
- **UI 库**：Element Plus
- **构建工具**：Vite
- **状态管理**：Pinia
- **路由**：Vue Router

### 数据库支持

- MySQL 5.7+
- PostgreSQL 12+
- 达梦数据库 DM8
- 瀚高数据库 6.2+
- 高斯数据库 GaussDB

## 项目结构

```
RuoYi-SpringBoot3-Pro
├── bin                     # 启动脚本
│   ├── clean.bat          # 清理脚本
│   ├── package.bat        # 打包脚本
│   └── run.bat            # 启动脚本
├── sql                     # SQL脚本
│   ├── ruoyi-mysql.sql    # MySQL 初始化脚本
│   ├── ruoyi-pgsql.sql    # PostgreSQL 初始化脚本
│   ├── ruoyi-dm8.dmp      # 达梦数据库脚本
│   ├── magic-api-*.sql    # Magic API 脚本
│   ├── region-*.sql       # 地区数据脚本
│   └── Dify_数据库建表.yml  # Dify 建表工作流
├── ruoyi-admin             # 管理后台模块
├── ruoyi-framework         # 框架核心模块
├── ruoyi-system            # 系统模块
├── ruoyi-common            # 通用模块
├── ruoyi-biz               # 业务模块（新增）
├── ruoyi-quartz            # 定时任务模块
└── ruoyi-generator         # 代码生成模块
```

## 快速开始

### 环境要求

- JDK 17 或 JDK 21
- Maven 3.6+
- 选择以下数据库之一：
  - MySQL 5.7+
  - PostgreSQL 12+
  - 达梦数据库 DM8
  - 瀚高数据库 6.2+
  - 高斯数据库 GaussDB
- Redis（可选）

### 安装步骤

1. **导入数据库脚本**

```bash
# MySQL 示例
mysql -u root -p < sql/ruoyi-mysql.sql
mysql -u root -p < sql/magic-api-mysql.sql
mysql -u root -p < sql/region-mysql.sql

# PostgreSQL 示例
psql -U postgres -d ruoyi < sql/ruoyi-pgsql.sql
psql -U postgres -d ruoyi < sql/magic-api-pgsql.sql
psql -U postgres -d ruoyi < sql/region-pgsql.sql
```

2. **修改配置文件**

编辑 `ruoyi-admin/src/main/resources/application.yml`：

```yaml
spring:
  profiles:
    active: devmy # 选择对应的数据库配置文件
```

然后编辑对应的配置文件（如 `application-devmy.yml`），修改数据库连接信息。

3. **编译打包**

```bash
# 使用 Maven 编译
mvn clean package

# 或使用提供的脚本（Windows）
bin\package.bat
```

4. **启动项目**

```bash
# 方式一：直接运行 jar
java -jar ruoyi-admin/target/ruoyi-admin.jar

# 方式二：使用 Maven 运行
mvn spring-boot:run -pl ruoyi-admin

# 方式三：使用提供的脚本（Windows）
bin\run.bat
```

5. **访问系统**

- 系统地址：http://localhost:8087
- 接口文档：http://localhost:8087/doc.html
- Magic API：http://localhost:8087/magic/web
- 默认账号：admin / jyx_692483

6. **启动前端**

**Element Plus 版本（推荐企业级应用）：**

```bash
# 克隆项目
git clone https://github.com/undsky/RuoYi-SpringBoot3-ElementPlus.git
cd RuoYi-SpringBoot3-ElementPlus

# 安装依赖
npm install

# 启动项目
npm run dev

# 访问地址：http://localhost:80
```

## 使用指南

### ClassFinal 代码加密使用

Pro 版本集成了 ClassFinal 代码加密插件，保护核心业务代码：

**加密配置（ruoyi-admin/pom.xml）：**

```xml
<plugin>
    <groupId>com.gitee.lcm742320521</groupId>
    <artifactId>classfinal-maven-plugin</artifactId>
    <version>1.4.1</version>
    <configuration>
        <!-- 需要加密的包，多个用逗号分隔 -->
        <packages>com.ruoyi.biz</packages>

        <!-- 需要加密的配置文件 -->
        <cfgfiles>*.yml</cfgfiles>

        <!-- 排除的包，不进行加密 -->
        <excludes>org.spring</excludes>

        <!-- 加密密码，运行时需要此密码 -->
        <password>RuoyiSpringBoot3@123456!</password>
    </configuration>
</plugin>
```

**使用步骤：**

1. **打包加密**

```bash
# 执行 Maven 打包，自动触发加密
mvn clean package

# 加密后的 jar 文件在 target 目录
```

2. **运行加密的 jar**

```bash
# 需要提供密码参数才能运行
java -jar RuoyiSpringBoot3.jar -pwd=RuoyiSpringBoot3@123456!
```

### AI 对话功能使用

Pro 版本内置了 OpenAI 工具类，可快速集成 AI 对话能力：

**同步对话示例：**

```java
import com.ruoyi.common.utils.ai.*;
import com.openai.client.OpenAIClient;
import java.util.Arrays;

// 1. 创建客户端
OpenAIClient client = OpenAI.chatClient(
    "your-api-key",
    "https://api.openai.com/v1",
    null  // 如需代理，传入 Proxy 对象
);

// 2. 构建消息列表
List<AIMessage> messages = Arrays.asList(
    new AIMessage(AIRole.SYSTEM, "你是一个有帮助的助手"),
    new AIMessage(AIRole.USER, "介绍一下 Spring Boot")
);

// 3. 创建对话参数
ChatCompletionCreateParams params = OpenAI.chatParams("gpt-3.5-turbo", messages);

// 4. 发送请求并获取响应
String response = OpenAI.chat(client, params);
System.out.println(response);
```

### MyBatis-Plus 使用示例

```java
// 1. 实体类继承 BaseEntity
@TableName("sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String userName;
    // ...
}

// 2. Mapper 继承 BaseMapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 无需写任何代码，已自动拥有 CRUD 方法
}

// 3. Service 使用
// Lambda 查询
List<SysUser> users = userMapper.selectList(
    new LambdaQueryWrapper<SysUser>()
        .eq(SysUser::getStatus, "0")
        .like(SysUser::getUserName, "admin")
);

// 分页查询
Page<SysUser> page = new Page<>(1, 10);
userMapper.selectPage(page,
    new LambdaQueryWrapper<SysUser>()
        .orderByDesc(SysUser::getCreateTime)
);
```

### Magic API 使用

1. 访问 Magic API 管理界面：`http://localhost:8087/magic/web`
2. 使用账号登录：jyx / jyx_692483
3. 创建接口分组和接口
4. 编写接口脚本（支持 SQL、JavaScript 等）
5. 测试接口
6. 发布接口，前端即可调用

## 开发指南

### 前端开发

- 使用 Vue 3 Composition API
- 遵循项目的组件命名和目录结构约定
- 使用 Element Plus 组件构建 UI
- 实现响应式设计，适配不同屏幕尺寸
- 使用懒加载和代码分割优化前端性能

### 后端开发

- 遵循项目的目录结构和命名约定
- 使用 MyBatis-Plus 进行数据库操作
- 实现适当的错误处理和日志记录
- 遵循安全最佳实践
- 使用缓存和高效查询优化后端性能

### 安全指南

- 遵循三级等保安全要求
- 使用 ClassFinal 对核心业务逻辑进行代码加密
- 实现适当的认证和授权
- 使用 HTTPS 进行安全通信
- 定期更新依赖以解决安全漏洞

## 部署指南

- 使用提供的脚本进行构建和部署
- 为不同环境配置适当的环境变量
- 设置适当的监控和日志记录
- 实现备份和恢复策略
- 如果使用 Docker，遵循容器化和编排的最佳实践

## 故障排除

- **数据库连接问题**：检查数据库配置和网络连接
- **加密错误**：确保在运行加密的 jar 时提供正确的密码
- **AI 集成问题**：检查 API 密钥和与 AI 服务的网络连接
- **性能问题**：优化数据库查询并实现缓存
- **安全问题**：遵循安全最佳实践并定期更新依赖

## 总结

RuoYi-SpringBoot3-Pro 是一个功能强大、安全可靠、灵活多变的企业级开发框架，为构建现代应用提供了全面的功能集。通过遵循本技能中概述的指南和最佳实践，开发人员可以使用此框架高效地构建和部署高质量的企业应用。