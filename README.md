# RBAC 权限管理系统

基于 Kotlin + Spring Boot 3 + Vaadin + MySQL + MyBatis-Plus + Sa-Token 的 RBAC 权限管理系统

## 技术栈

- **后端**: Spring Boot 3.2.1 + Kotlin 1.9.21 + MyBatis-Plus 3.5.5 + Sa-Token 1.37.0
- **前端**: Vaadin 24.3.0 + Karibu DSL 2.1.2
- **数据库**: MySQL 8.0+
- **构建**: Gradle 8.x

## 功能特性

- ✅ 用户管理（增删改查、启用/禁用、分配角色）
- ✅ 角色管理（增删改查、分配权限）
- ✅ 权限管理（树形结构、多级层级）
- ✅ 操作日志（AOP 自动记录、查询筛选、导出 Excel）
- ✅ 登录认证（Sa-Token、会话管理）
- ✅ 权限控制（动态菜单、路由拦截、注解验证）
- ✅ 首页仪表盘（数据统计、最近日志）

## 项目结构

```
src/main/kotlin/com/rbac/
├── annotation/          # 自定义注解（权限、角色、日志）
├── aspect/              # AOP 切面
├── config/              # 配置类
├── dto/                 # 数据传输对象
├── entity/              # 实体类
├── exception/           # 异常处理
├── mapper/              # MyBatis Mapper
├── service/             # 业务逻辑层
├── ui/                  # Vaadin UI 层
│   ├── component/       # 可复用组件
│   ├── dashboard/       # 首页
│   ├── user/            # 用户管理
│   ├── role/            # 角色管理
│   ├── permission/      # 权限管理
│   └── log/             # 操作日志
└── util/                # 工具类
```

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Gradle 8.x

### 2. 初始化数据库

```bash
mysql -u root -p < sql/db-init.sql
mysql -u root -p < sql/db-init-data.sql
```

### 3. 配置数据库

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rbac_db
    username: root
    password: root  # 修改为你的密码
```

### 4. 运行项目

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### 5. 访问系统

浏览器访问: http://localhost:8080

**测试账号**:
- 管理员: `admin/admin123` (所有权限)
- 普通用户: `test/test123` (部分权限)

## 权限控制

### 权限编码规范

```
system:module:action

示例:
- system:user:view      # 用户查看
- system:user:add       # 用户新增
- system:role:view      # 角色查看
- system:log:view       # 日志查看
```

### 页面权限注解

```kotlin
@Route("users", layout = MainLayout::class)
@RequiresPermissions("system:user:view")
class UserListView : VerticalLayout()
```

### 动态菜单

```kotlin
// MainLayout.kt
if (StpUtil.hasPermission("system:user:view")) {
    nav.addItem(SideNavItem("用户管理", UserListView::class.java))
}
```

### 按钮权限

```kotlin
if (StpUtil.hasPermission("system:user:delete")) {
    button("删除") { /* ... */ }
}
```

## 开发指南

### 添加新模块

1. 创建实体类 (`entity/`)
2. 创建 Mapper (`mapper/`)
3. 创建 Service (`service/`)
4. 创建视图 (`ui/`)
5. 添加菜单 (`MainLayout.kt`)
6. 添加权限数据（数据库）

### 常用组件

```kotlin
// 确认对话框
showConfirmDialog("确定删除？") { /* 确认操作 */ }

// 通知提示
NotificationUtil.showSuccess("操作成功")
NotificationUtil.showError("操作失败")

// 分页组件
val pagination = PaginationComponent { page, size -> loadData(page, size) }
```

### 表单验证

```kotlin
val binder = Binder(UserDto::class.java)
binder.forField(usernameField)
    .asRequired("用户名不能为空")
    .withValidator(StringLengthValidator("长度2-20字符", 2, 20))
    .bind(UserDto::username.name)
```

## 注意事项

- 密码使用 MD5 加密存储
- 权限在登录时缓存，修改后需重新登录
- 默认端口 8080，如有冲突请修改配置

## License

MIT License
