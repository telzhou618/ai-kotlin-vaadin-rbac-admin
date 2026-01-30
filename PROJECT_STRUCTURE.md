# 项目结构说明

## 目录结构

```
rbac-system/
├── build.gradle.kts              # Gradle 构建配置
├── settings.gradle.kts           # Gradle 设置
├── gradle.properties             # Gradle 属性配置
├── .gitignore                    # Git 忽略文件
├── README.md                     # 项目说明文档
├── INSTALL.md                    # 安装部署指南
├── PROJECT_STRUCTURE.md          # 项目结构说明（本文件）
├── db-init.sql                   # 数据库表结构初始化脚本
├── db-init-data.sql              # 数据库初始数据脚本
├── start.bat                     # Windows 启动脚本
├── start.sh                      # Linux/Mac 启动脚本
│
└── src/
    └── main/
        ├── kotlin/com/rbac/
        │   ├── RbacApplication.kt              # 应用启动类
        │   │
        │   ├── annotation/                     # 自定义注解
        │   │   └── OperationLog.kt            # 操作日志注解
        │   │
        │   ├── aspect/                         # AOP 切面
        │   │   └── OperationLogAspect.kt      # 操作日志切面实现
        │   │
        │   ├── config/                         # 配置类
        │   │   ├── MybatisPlusConfig.kt       # MyBatis-Plus 配置（分页插件）
        │   │   └── SaTokenConfig.kt           # Sa-Token 配置（拦截器）
        │   │
        │   ├── dto/                            # 数据传输对象
        │   │   ├── DashboardDto.kt            # 首页统计数据 DTO
        │   │   ├── LogQueryDto.kt             # 日志查询条件 DTO
        │   │   ├── PermissionDto.kt           # 权限树形结构 DTO
        │   │   ├── RoleDto.kt                 # 角色数据 DTO
        │   │   └── UserDto.kt                 # 用户数据 DTO
        │   │
        │   ├── entity/                         # 实体类（对应数据库表）
        │   │   ├── SysOperationLog.kt         # 操作日志实体
        │   │   ├── SysPermission.kt           # 权限实体
        │   │   ├── SysRole.kt                 # 角色实体
        │   │   ├── SysRolePermission.kt       # 角色权限关联实体
        │   │   ├── SysUser.kt                 # 用户实体
        │   │   └── SysUserRole.kt             # 用户角色关联实体
        │   │
        │   ├── exception/                      # 异常处理
        │   │   └── GlobalExceptionHandler.kt  # 全局异常处理器
        │   │
        │   ├── mapper/                         # MyBatis Mapper 接口
        │   │   ├── SysOperationLogMapper.kt   # 操作日志 Mapper
        │   │   ├── SysPermissionMapper.kt     # 权限 Mapper
        │   │   ├── SysRoleMapper.kt           # 角色 Mapper
        │   │   ├── SysRolePermissionMapper.kt # 角色权限关联 Mapper
        │   │   ├── SysUserMapper.kt           # 用户 Mapper
        │   │   └── SysUserRoleMapper.kt       # 用户角色关联 Mapper
        │   │
        │   ├── service/                        # 业务逻辑层
        │   │   ├── AuthService.kt             # 认证服务（登录/退出）
        │   │   ├── DashboardService.kt        # 首页统计服务
        │   │   ├── SysOperationLogService.kt  # 操作日志服务
        │   │   ├── SysPermissionService.kt    # 权限服务
        │   │   ├── SysRolePermissionService.kt # 角色权限关联服务
        │   │   ├── SysRoleService.kt          # 角色服务
        │   │   ├── SysUserRoleService.kt      # 用户角色关联服务
        │   │   └── SysUserService.kt          # 用户服务
        │   │
        │   └── ui/                             # Vaadin UI 层
        │       ├── LoginView.kt               # 登录页面
        │       ├── MainLayout.kt              # 主布局（左侧菜单+顶部栏）
        │       │
        │       ├── component/                  # 可复用 UI 组件
        │       │   ├── ConfirmDialog.kt       # 确认对话框组件
        │       │   └── PaginationComponent.kt # 分页组件
        │       │
        │       ├── dialog/                     # 弹窗对话框
        │       │   ├── PermissionFormDialog.kt # 权限表单弹窗
        │       │   ├── RoleAssignFormDialog.kt # 角色分配权限弹窗
        │       │   ├── RoleFormDialog.kt      # 角色表单弹窗
        │       │   └── UserFormDialog.kt      # 用户表单弹窗
        │       │
        │       └── view/                       # 视图页面
        │           ├── DashboardView.kt       # 首页（统计卡片+最近日志）
        │           ├── OperationLogView.kt    # 操作日志页面
        │           ├── PermissionTreeView.kt  # 权限树形管理页面
        │           ├── RoleListView.kt        # 角色列表页面
        │           └── UserListView.kt        # 用户列表页面
        │
        └── resources/
            ├── application.yml                 # Spring Boot 配置文件
            └── banner.txt                      # 启动 Banner
```

## 核心模块说明

### 1. Entity 层（实体类）
- 对应数据库表结构
- 使用 MyBatis-Plus 注解
- 包含 6 张表的实体类

### 2. Mapper 层（数据访问）
- 继承 MyBatis-Plus 的 BaseMapper
- 提供基础 CRUD 操作
- 无需编写 XML 配置

### 3. Service 层（业务逻辑）
- 继承 ServiceImpl 获得基础功能
- 实现业务逻辑和事务管理
- 使用 @OperationLog 注解记录操作

### 4. DTO 层（数据传输）
- 用于前后端数据交互
- 包含查询条件和返回结果
- 与实体类解耦

### 5. UI 层（Vaadin 界面）

#### 5.1 LoginView（登录页面）
- 简洁的登录表单
- 用户名和密码验证
- 登录成功后跳转主页

#### 5.2 MainLayout（主布局）
- 左侧导航菜单
- 顶部栏（Logo、用户信息、退出）
- 右侧内容区域

#### 5.3 View（视图页面）
- **DashboardView**: 首页统计和最近日志
- **UserListView**: 用户列表、搜索、分页
- **RoleListView**: 角色列表、搜索、分页
- **PermissionTreeView**: 权限树形展示
- **OperationLogView**: 操作日志查询和导出

#### 5.4 Dialog（弹窗）
- **UserFormDialog**: 用户新增/编辑表单
- **RoleFormDialog**: 角色新增/编辑表单
- **RoleAssignFormDialog**: 角色分配权限（树形选择）
- **PermissionFormDialog**: 权限新增/编辑表单

#### 5.5 Component（可复用组件）
- **ConfirmDialog**: 删除确认对话框
- **PaginationComponent**: 分页控件

### 6. Config 层（配置）
- **MybatisPlusConfig**: 配置分页插件
- **SaTokenConfig**: 配置登录拦截器

### 7. Aspect 层（切面）
- **OperationLogAspect**: AOP 拦截操作日志

### 8. Exception 层（异常处理）
- **GlobalExceptionHandler**: 统一异常处理和 Toast 提示

## 技术架构

### 后端技术栈
- **Spring Boot 3.2.1**: 应用框架
- **Kotlin 1.9.21**: 开发语言
- **MyBatis-Plus 3.5.5**: ORM 框架
- **Sa-Token 1.37.0**: 权限认证
- **Hutool 5.8.24**: 工具库

### 前端技术栈
- **Vaadin 24.3.1**: Web UI 框架
- **Karibu DSL 2.1.2**: Kotlin DSL for Vaadin

### 数据库
- **MySQL 8.0**: 关系型数据库

## 数据流转

```
用户操作 
  ↓
UI View (Vaadin)
  ↓
Service (业务逻辑 + @OperationLog)
  ↓
Mapper (MyBatis-Plus)
  ↓
Database (MySQL)
  ↓
返回结果
  ↓
UI 展示 / Toast 提示
```

## 权限控制流程

```
用户访问
  ↓
SaTokenConfig 拦截器
  ↓
检查登录状态
  ↓
已登录 → 允许访问
未登录 → 跳转登录页
```

## 操作日志记录流程

```
用户操作（带 @OperationLog 注解的方法）
  ↓
OperationLogAspect 切面拦截
  ↓
记录操作信息（用户、模块、操作、耗时等）
  ↓
保存到 sys_operation_log 表
```

## 分页查询流程

```
用户点击分页按钮
  ↓
PaginationComponent 触发回调
  ↓
View 调用 Service.pageQuery()
  ↓
MyBatis-Plus 分页插件处理
  ↓
返回 Page 对象
  ↓
Grid 展示数据 + 更新分页信息
```

## 开发规范

### 1. 命名规范
- 实体类: SysXxx
- DTO: XxxDto
- Service: XxxService
- Mapper: XxxMapper
- View: XxxView / XxxListView
- Dialog: XxxFormDialog

### 2. 包结构规范
- entity: 实体类
- dto: 数据传输对象
- mapper: 数据访问层
- service: 业务逻辑层
- ui.view: 页面视图
- ui.dialog: 弹窗对话框
- ui.component: 可复用组件

### 3. 代码规范
- 使用 Kotlin 语言特性
- Service 方法添加 @OperationLog 注解
- 异常统一通过 GlobalExceptionHandler 处理
- UI 组件使用 Karibu DSL 构建

## 扩展指南

### 添加新功能模块

1. **创建实体类** (entity/)
2. **创建 Mapper** (mapper/)
3. **创建 Service** (service/)
4. **创建 DTO** (dto/)
5. **创建 ListView** (ui/view/)
6. **创建 FormDialog** (ui/dialog/)
7. **在 MainLayout 添加菜单项**

### 添加可复用组件

1. 在 `ui/component/` 创建组件类
2. 继承 Vaadin 组件或自定义布局
3. 在需要的地方引入使用

### 添加操作日志

在 Service 方法上添加注解：

```kotlin
@OperationLog(module = "模块名", operation = "操作名")
fun yourMethod() {
    // 业务逻辑
}
```

## 性能优化建议

1. **数据库索引**: 已在表结构中添加必要索引
2. **分页查询**: 所有列表都使用分页
3. **连接池**: 使用 HikariCP（Spring Boot 默认）
4. **缓存**: 可考虑添加 Redis 缓存热点数据
5. **异步处理**: 日志记录可改为异步

## 安全建议

1. **密码加密**: 使用 MD5（建议升级为 BCrypt）
2. **SQL 注入**: MyBatis-Plus 自动防护
3. **XSS 防护**: Vaadin 自动转义
4. **CSRF 防护**: Vaadin 内置 CSRF Token
5. **权限控制**: Sa-Token 统一管理

## 测试建议

1. **单元测试**: Service 层业务逻辑
2. **集成测试**: Mapper 层数据访问
3. **UI 测试**: Vaadin TestBench
4. **性能测试**: JMeter 压力测试

## 部署建议

1. **开发环境**: IDE 直接运行
2. **测试环境**: jar 包部署
3. **生产环境**: Docker 容器化部署
4. **监控**: 集成 Spring Boot Actuator
5. **日志**: 使用 ELK 收集分析
