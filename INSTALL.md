# 安装和部署指南

## 环境准备

### 1. 安装 JDK 17+

确保已安装 JDK 17 或更高版本：

```bash
java -version
```

### 2. 安装 MySQL 8.0+

确保 MySQL 服务已启动并可访问。

### 3. 安装 Gradle（可选）

项目包含 Gradle Wrapper，无需单独安装 Gradle。

## 数据库初始化

### 1. 创建数据库和表结构

```bash
mysql -u root -p < db-init.sql
```

### 2. 插入初始数据

```bash
mysql -u root -p < db-init-data.sql
```

初始管理员账号：
- 用户名: `admin`
- 密码: `admin123`

## 配置修改

### 1. 数据库配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rbac_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root  # 修改为你的数据库密码
```

### 2. 端口配置

如需修改端口，在 `application.yml` 中修改：

```yaml
server:
  port: 8080  # 修改为你需要的端口
```

## 运行项目

### 方式一：使用 Gradle Wrapper（推荐）

#### Windows:
```bash
gradlew.bat bootRun
```

#### Linux/Mac:
```bash
./gradlew bootRun
```

### 方式二：使用 IDE

1. 使用 IntelliJ IDEA 打开项目
2. 等待 Gradle 依赖下载完成
3. 运行 `RbacApplication.kt` 主类

### 方式三：打包运行

```bash
# 打包
./gradlew build

# 运行
java -jar build/libs/rbac-system-1.0.0.jar
```

## 访问系统

启动成功后，浏览器访问：

```
http://localhost:8080
```

使用默认账号登录：
- 用户名: `admin`
- 密码: `admin123`

## 常见问题

### 1. 端口被占用

修改 `application.yml` 中的 `server.port` 配置。

### 2. 数据库连接失败

检查：
- MySQL 服务是否启动
- 数据库地址、端口、用户名、密码是否正确
- 数据库 `rbac_db` 是否已创建

### 3. Vaadin 前端资源加载慢

首次启动时，Vaadin 会下载和编译前端资源，需要等待几分钟。

### 4. Gradle 依赖下载慢

可以配置国内镜像源，在 `build.gradle.kts` 的 `repositories` 中添加：

```kotlin
repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/spring") }
    mavenCentral()
}
```

## 开发模式

开发时建议使用 IDE 的热重载功能：

1. IntelliJ IDEA: 启用 "Build project automatically"
2. 修改代码后，使用 Ctrl+F9 (Windows) 或 Cmd+F9 (Mac) 重新编译

## 生产部署

### 1. 打包

```bash
./gradlew clean build -x test
```

### 2. 部署

将 `build/libs/rbac-system-1.0.0.jar` 上传到服务器。

### 3. 运行

```bash
nohup java -jar rbac-system-1.0.0.jar > app.log 2>&1 &
```

### 4. 使用 systemd 管理（推荐）

创建 `/etc/systemd/system/rbac-system.service`：

```ini
[Unit]
Description=RBAC System
After=syslog.target network.target

[Service]
User=your-user
ExecStart=/usr/bin/java -jar /path/to/rbac-system-1.0.0.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl start rbac-system
sudo systemctl enable rbac-system
```

## 性能优化

### 1. JVM 参数优化

```bash
java -Xms512m -Xmx1024m -XX:+UseG1GC -jar rbac-system-1.0.0.jar
```

### 2. 数据库连接池配置

在 `application.yml` 中添加：

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

## 安全建议

1. 修改默认管理员密码
2. 配置 HTTPS
3. 启用防火墙，只开放必要端口
4. 定期备份数据库
5. 使用强密码策略
6. 定期更新依赖版本

## 技术支持

如遇到问题，请检查：
1. 日志文件
2. 数据库连接
3. 端口占用情况
4. JDK 版本是否正确
