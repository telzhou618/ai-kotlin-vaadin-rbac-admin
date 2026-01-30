# 权限管理系统

基于 Kotlin + Spring Boot 3 + Vaadin + MySQL + MyBatis-Plus + Sa-Token 的权限管理系统

## 技术栈

- **后端框架**: Spring Boot 3.2.1
- **开发语言**: Kotlin 1.9.21
- **前端框架**: Vaadin 24.3.1 + Karibu DSL
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis-Plus 3.5.5
- **权限框架**: Sa-Token 1.37.0
- **工具库**: Hutool 5.8.24
- **构建工具**: Gradle 8.x

## 功能特性

### 1. 用户管理
- 用户 CRUD 操作
- 用户启用/禁用
- 为用户分配多个角色
- 密码 MD5 加密

### 2. 角色管理
- 角色 CRUD 操作
- 为角色分配权限
- 权限树状选择

### 3. 权限管理
- 权限树形结构管理
- 支持父子层级关系
- 权限节点的增删改查

### 4. 操作日志
- AOP 统一记录用户操作
- 日志查询和筛选
- 日志导出功能

### 5. 登录认证
- 基于 Sa-Token 的登录认证
- 会话管理
- 自动登录拦截

## 项目结构

```
src/main/kotlin/com/rbac/
├── RbacApplication.kt          # 应用启动类
├── annotation/                 # 注解
│   └── OperationLog.kt        # 操作日志注解
├── aspect/                     # 切面
│   └── OperationLogAspect.kt  # 操作日志切面
├── config/                     # 配置类
│   ├── MybatisPlusConfig.kt   # MyBatis-Plus 配置
│   └── SaTokenConfig.kt       # Sa-Token 配置
├── dto/                        # 数据传输对象
├── entity/                     # 实体类
├── exception/                  # 异常处理
│   └── GlobalExceptionHandler.kt
├── mapper/                     # MyBatis Mapper
├── service/                    # 业务逻辑层
└── ui/                         # Vaadin UI 层
    ├── LoginView.kt           # 登录页面
    ├── MainLayout.kt          # 主布局
    ├── component/             # 可复用组件
    │   ├── ConfirmDialog.kt   # 确认对话框
    │   └── PaginationComponent.kt  # 分页组件
    ├── dialog/                # 弹窗
    │   ├── UserFormDialog.kt
    │   ├── RoleFormDialog.kt
    │   ├── RoleAssignFormDialog.kt
    │   └── PermissionFormDialog.kt
    └── view/                  # 视图页面
        ├── DashboardView.kt   # 首页
        ├── UserListView.kt    # 用户列表
        ├── RoleListView.kt    # 角色列表
        ├── PermissionTreeView.kt  # 权限树
        └── OperationLogView.kt    # 操作日志
```

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Gradle 8.x

### 2. 数据库初始化

执行 `db-init.sql` 文件初始化数据库：

```bash
mysql -u root -p < db-init.sql
```

### 3. 配置数据库

修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rbac_db
    username: root
    password: root
```

### 4. 运行项目

```bash
./gradlew bootRun
```

或者在 IDE 中直接运行 `RbacApplication.kt`

### 5. 访问系统

浏览器访问: http://localhost:8080

默认登录账号需要先在数据库中手动创建，密码使用 MD5 加密。

## 页面布局

### 主布局
- **左侧**: 导航菜单（可收起/展开）
- **顶部**: Logo、系统名称、用户信息、退出按钮
- **右侧**: 内容区域（动态加载）

### 菜单导航
- 首页 (Dashboard)
- 用户管理
- 角色管理
- 权限管理
- 操作日志

## 核心功能说明

### 分页查询
所有列表页面都支持分页查询，使用 MyBatis-Plus 的 Page 对象实现。

### 操作日志
使用 AOP 切面自动记录用户操作，通过 `@OperationLog` 注解标记需要记录的方法。

### 权限控制
基于 Sa-Token 实现，支持注解方式的权限验证。

### 异常处理
全局异常处理器统一捕获异常，通过 Vaadin Notification 显示错误信息。

## 开发说明

### 添加新功能模块

1. 在 `entity` 包中创建实体类
2. 在 `mapper` 包中创建 Mapper 接口
3. 在 `service` 包中创建 Service 类
4. 在 `ui/view` 包中创建列表视图
5. 在 `ui/dialog` 包中创建表单对话框
6. 在 `MainLayout` 中添加菜单项

### 使用可复用组件

- **确认对话框**: `showConfirmDialog(message, onConfirm)`
- **分页组件**: `PaginationComponent(onPageChange)`
- **异常处理**: 注入 `GlobalExceptionHandler` 使用

## 注意事项

1. 密码使用 MD5 加密存储
2. 删除操作需要确认
3. 所有异常统一通过 Toast 提示
4. 列表页面支持搜索和分页
5. 权限采用树形结构管理

## License

MIT License
