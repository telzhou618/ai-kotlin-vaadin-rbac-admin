# 快速开始指南

## 5 分钟快速启动

### 步骤 1: 初始化数据库

```bash
# 创建数据库和表结构
mysql -u root -p < db-init.sql

# 插入初始数据（包含默认管理员账号）
mysql -u root -p < db-init-data.sql
```

### 步骤 2: 配置数据库连接

编辑 `src/main/resources/application.yml`，修改数据库密码：

```yaml
spring:
  datasource:
    password: root  # 改为你的 MySQL 密码
```

### 步骤 3: 启动项目

#### Windows:
```bash
start.bat
```

#### Linux/Mac:
```bash
chmod +x start.sh
./start.sh
```

或者使用 Gradle 命令：
```bash
./gradlew bootRun
```

### 步骤 4: 访问系统

浏览器打开: http://localhost:8080

**默认账号:**
- 用户名: `admin`
- 密码: `admin123`

## 功能演示

### 1. 登录系统
- 输入用户名 `admin` 和密码 `admin123`
- 点击登录按钮

### 2. 查看首页
- 查看统计卡片（用户数、角色数、权限数、日志数）
- 查看最近操作日志

### 3. 用户管理
- 点击左侧菜单 "用户管理"
- 点击 "新增" 按钮创建新用户
- 为用户分配角色
- 可以编辑、删除用户

### 4. 角色管理
- 点击左侧菜单 "角色管理"
- 点击 "新增" 按钮创建新角色
- 点击 "分配权限" 为角色分配权限（树形选择）
- 可以编辑、删除角色

### 5. 权限管理
- 点击左侧菜单 "权限管理"
- 查看权限树形结构
- 可以新增根权限或子权限
- 可以编辑、删除权限

### 6. 操作日志
- 点击左侧菜单 "操作日志"
- 查看所有操作记录
- 可以按用户名、模块、时间范围筛选
- 点击 "导出" 按钮导出日志到 Excel

## 常用操作

### 新增用户
1. 进入 "用户管理"
2. 点击 "新增" 按钮
3. 填写用户名、密码
4. 选择状态（启用/禁用）
5. 勾选要分配的角色
6. 点击 "保存"

### 为角色分配权限
1. 进入 "角色管理"
2. 找到目标角色，点击 "分配权限"
3. 在权限树中勾选需要的权限
4. 点击 "保存"

### 创建权限树
1. 进入 "权限管理"
2. 点击 "新增根权限" 创建一级权限
3. 选择某个权限，点击 "新增子权限" 创建下级权限
4. 可以创建多级权限结构

### 查询操作日志
1. 进入 "操作日志"
2. 输入查询条件（用户名、模块、时间范围）
3. 点击 "查询" 按钮
4. 查看日志列表
5. 点击 "导出" 导出到 Excel

## 技术特性演示

### 1. 分页功能
- 所有列表页面都支持分页
- 可以选择每页显示数量（10/20/50/100）
- 支持首页、上一页、下一页、末页导航

### 2. 搜索功能
- 用户管理：按用户名搜索
- 角色管理：按角色名称搜索
- 操作日志：按用户名、模块、时间范围搜索

### 3. 删除确认
- 所有删除操作都会弹出确认对话框
- 防止误删除

### 4. 操作日志记录
- 所有增删改操作都会自动记录
- 记录操作用户、模块、操作类型、时间、耗时等

### 5. 异常处理
- 所有异常都会通过 Toast 提示
- 友好的错误信息展示

### 6. 权限树形结构
- 支持无限层级的权限树
- 可以展开/收起节点
- 树形选择器分配权限

## 开发调试

### 查看日志
项目使用 Spring Boot 默认日志配置，控制台会输出：
- SQL 执行日志
- 操作日志记录
- 异常堆栈信息

### 修改日志级别
在 `application.yml` 中添加：

```yaml
logging:
  level:
    com.rbac: DEBUG
    com.baomidou.mybatisplus: DEBUG
```

### 热重载
使用 IDE 的热重载功能：
1. IntelliJ IDEA: Ctrl+F9 (Windows) / Cmd+F9 (Mac)
2. 修改代码后重新编译即可生效

### 数据库查看
使用 MySQL 客户端或 IDE 的数据库工具查看数据：

```sql
-- 查看所有用户
SELECT * FROM sys_user;

-- 查看用户角色关联
SELECT u.username, r.role_name 
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id;

-- 查看角色权限关联
SELECT r.role_name, p.perm_name 
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.perm_id = p.id;

-- 查看操作日志
SELECT * FROM sys_operation_log ORDER BY create_time DESC LIMIT 10;
```

## 测试数据

### 创建测试用户
```sql
-- 密码为 123456 的 MD5 值
INSERT INTO sys_user (username, password, status) 
VALUES ('test', 'e10adc3949ba59abbe56e057f20f883e', 1);
```

### 创建测试角色
```sql
INSERT INTO sys_role (role_code, role_name, role_desc) 
VALUES ('TEST', '测试角色', '用于测试的角色');
```

### 创建测试权限
```sql
INSERT INTO sys_permission (perm_code, perm_name, parent_id) 
VALUES ('test', '测试权限', 0);
```

## 常见问题

### Q1: 启动后访问 localhost:8080 显示空白页？
A: 首次启动时 Vaadin 需要下载和编译前端资源，请等待 3-5 分钟。

### Q2: 登录后提示 "用户名或密码错误"？
A: 确认是否执行了 `db-init-data.sql` 脚本，默认密码是 `admin123`。

### Q3: 如何修改管理员密码？
A: 登录后在用户管理中编辑 admin 用户，输入新密码保存即可。

### Q4: 删除权限时提示 "存在子权限，无法删除"？
A: 需要先删除所有子权限，才能删除父权限。

### Q5: 操作日志导出的文件在哪里？
A: 导出的 Excel 文件在项目根目录下，文件名格式为 `操作日志_时间戳.xlsx`。

## 下一步

- 阅读 [README.md](README.md) 了解项目详情
- 阅读 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) 了解项目结构
- 阅读 [INSTALL.md](INSTALL.md) 了解部署方案
- 开始开发自己的功能模块

## 技术支持

如有问题，请检查：
1. 数据库是否正确初始化
2. 配置文件是否正确
3. 端口是否被占用
4. 日志中的错误信息

祝你使用愉快！🎉
