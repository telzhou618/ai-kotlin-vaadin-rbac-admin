# RBAC 系统打包部署指南

## 环境要求

### 开发环境
- JDK 17+
- Gradle 8.x（已安装）
- MySQL 8.0+

### 生产环境
- JRE 17+（或 JDK 17+）
- MySQL 8.0+
- 至少 512MB 可用内存
- 至少 500MB 磁盘空间

## 打包步骤

### 1. 清理旧的构建文件

```bash
# Windows
gradle clean

# 或使用 gradlew
gradlew.bat clean
```

### 2. 构建生产环境 JAR 包

```bash
# Windows
gradle build -x test

# 或使用 gradlew
gradlew.bat build -x test
```

**参数说明：**
- `build`: 构建项目
- `-x test`: 跳过测试（如果需要运行测试，去掉此参数）

### 3. 查看构建结果

构建成功后，JAR 包位于：
```
build/libs/rbac-system-1.0.0.jar
```

**文件大小：** 约 80-100 MB（包含所有依赖）

### 4. 验证 JAR 包

```bash
# 查看 JAR 包信息
jar -tf build/libs/rbac-system-1.0.0.jar | head -20

# 或在 Windows 中
dir build\libs
```

## 生产环境配置

### 1. 创建生产环境配置文件

在 JAR 包同级目录创建 `application-prod.yml`：

```yaml
spring:
  application:
    name: rbac-system
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://your-production-host:3306/rbac_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_db_user
    password: your_db_password
  sql:
    init:
      mode: never
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    serialization:
      write-dates-as-timestamps: false

# MyBatis-Plus
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: auto

# Sa-Token
sa-token:
  token-name: satoken
  timeout: 2592000
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: false

# Vaadin
vaadin:
  launch-browser: false  # 生产环境不自动打开浏览器
  pnpm:
    enable: true

server:
  port: 8080
  # 生产环境建议配置
  compression:
    enabled: true
  tomcat:
    max-threads: 200
    min-spare-threads: 10

# 日志配置
logging:
  level:
    root: INFO
    com.rbac: INFO
    com.rbac.mapper: WARN  # 生产环境不打印 SQL
  file:
    name: logs/rbac-system.log
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30
```

### 2. 创建生产环境 Logback 配置

创建 `logback-spring-prod.xml`（可选）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/rbac-system.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/rbac-system.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 生产环境日志级别 -->
    <logger name="com.rbac" level="INFO"/>
    <logger name="com.rbac.mapper" level="WARN"/>
    <logger name="org.springframework" level="INFO"/>
    <logger name="com.vaadin" level="WARN"/>

    <root level="INFO">
        <appender-ref ref="FILE"/>
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

## 部署方式

### 方式一：直接运行（推荐用于测试）

```bash
# 使用默认配置运行
java -jar rbac-system-1.0.0.jar

# 使用生产环境配置运行
java -jar rbac-system-1.0.0.jar --spring.profiles.active=prod

# 指定外部配置文件
java -jar rbac-system-1.0.0.jar --spring.config.location=application-prod.yml
```

### 方式二：后台运行（Linux）

```bash
# 后台运行
nohup java -jar rbac-system-1.0.0.jar --spring.profiles.active=prod > output.log 2>&1 &

# 查看进程
ps aux | grep rbac-system

# 查看日志
tail -f output.log
```

### 方式三：使用 systemd 服务（Linux 推荐）

创建服务文件 `/etc/systemd/system/rbac-system.service`：

```ini
[Unit]
Description=RBAC System
After=syslog.target network.target

[Service]
Type=simple
User=rbac
WorkingDirectory=/opt/rbac-system
ExecStart=/usr/bin/java -jar /opt/rbac-system/rbac-system-1.0.0.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**操作命令：**
```bash
# 重新加载 systemd 配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start rbac-system

# 停止服务
sudo systemctl stop rbac-system

# 重启服务
sudo systemctl restart rbac-system

# 查看状态
sudo systemctl status rbac-system

# 开机自启
sudo systemctl enable rbac-system

# 查看日志
sudo journalctl -u rbac-system -f
```

### 方式四：Windows 服务

使用 [WinSW](https://github.com/winsw/winsw) 将 JAR 包注册为 Windows 服务。

创建 `rbac-system.xml`：

```xml
<service>
    <id>rbac-system</id>
    <name>RBAC System</name>
    <description>RBAC 权限管理系统</description>
    <executable>java</executable>
    <arguments>-jar rbac-system-1.0.0.jar --spring.profiles.active=prod</arguments>
    <logpath>logs</logpath>
    <log mode="roll-by-size">
        <sizeThreshold>10240</sizeThreshold>
        <keepFiles>8</keepFiles>
    </log>
</service>
```

**操作命令：**
```bash
# 安装服务
winsw install rbac-system.xml

# 启动服务
winsw start rbac-system.xml

# 停止服务
winsw stop rbac-system.xml

# 卸载服务
winsw uninstall rbac-system.xml
```

### 方式五：Docker 部署

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/rbac-system-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: rbac-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: rbac_db
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - rbac-network

  app:
    build: .
    container_name: rbac-app
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/rbac_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    networks:
      - rbac-network

volumes:
  mysql-data:

networks:
  rbac-network:
    driver: bridge
```

**操作命令：**
```bash
# 构建镜像
docker build -t rbac-system:1.0.0 .

# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 停止服务
docker-compose down
```

## JVM 参数优化

### 基础配置（512MB 内存）

```bash
java -Xms256m -Xmx512m -jar rbac-system-1.0.0.jar
```

### 推荐配置（1GB 内存）

```bash
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=logs/heapdump.hprof \
     -jar rbac-system-1.0.0.jar
```

### 生产环境配置（2GB 内存）

```bash
java -Xms1024m -Xmx2048m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=logs/heapdump.hprof \
     -XX:+PrintGCDetails \
     -XX:+PrintGCDateStamps \
     -Xloggc:logs/gc.log \
     -Dfile.encoding=UTF-8 \
     -Duser.timezone=Asia/Shanghai \
     -jar rbac-system-1.0.0.jar --spring.profiles.active=prod
```

**参数说明：**
- `-Xms`: 初始堆内存
- `-Xmx`: 最大堆内存
- `-XX:+UseG1GC`: 使用 G1 垃圾回收器
- `-XX:MaxGCPauseMillis`: GC 最大暂停时间
- `-XX:+HeapDumpOnOutOfMemoryError`: OOM 时生成堆转储
- `-Dfile.encoding=UTF-8`: 文件编码
- `-Duser.timezone=Asia/Shanghai`: 时区设置

## 部署目录结构

```
/opt/rbac-system/                    # 应用根目录
├── rbac-system-1.0.0.jar           # 应用 JAR 包
├── application-prod.yml             # 生产环境配置
├── logback-spring-prod.xml         # 日志配置（可选）
├── logs/                           # 日志目录
│   ├── rbac-system.log            # 应用日志
│   └── gc.log                     # GC 日志
├── start.sh                        # 启动脚本
├── stop.sh                         # 停止脚本
└── restart.sh                      # 重启脚本
```

## 启动脚本

### start.sh（Linux）

```bash
#!/bin/bash

APP_NAME=rbac-system-1.0.0.jar
APP_HOME=/opt/rbac-system
LOG_FILE=$APP_HOME/logs/output.log

cd $APP_HOME

# 检查是否已运行
PID=$(ps aux | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "应用已在运行，PID: $PID"
    exit 1
fi

# 启动应用
nohup java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=$APP_HOME/logs/heapdump.hprof \
     -Dfile.encoding=UTF-8 \
     -Duser.timezone=Asia/Shanghai \
     -jar $APP_NAME \
     --spring.profiles.active=prod \
     > $LOG_FILE 2>&1 &

echo "应用启动中..."
sleep 3

# 检查是否启动成功
PID=$(ps aux | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "应用启动成功，PID: $PID"
else
    echo "应用启动失败，请查看日志: $LOG_FILE"
    exit 1
fi
```

### stop.sh（Linux）

```bash
#!/bin/bash

APP_NAME=rbac-system-1.0.0.jar

# 查找进程
PID=$(ps aux | grep $APP_NAME | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "应用未运行"
    exit 1
fi

echo "正在停止应用，PID: $PID"
kill $PID

# 等待进程结束
for i in {1..30}; do
    if ! ps -p $PID > /dev/null; then
        echo "应用已停止"
        exit 0
    fi
    sleep 1
done

# 强制停止
echo "应用未响应，强制停止"
kill -9 $PID
echo "应用已强制停止"
```

### restart.sh（Linux）

```bash
#!/bin/bash

./stop.sh
sleep 2
./start.sh
```

### start.bat（Windows）

```batch
@echo off
set APP_NAME=rbac-system-1.0.0.jar
set APP_HOME=%~dp0

cd /d %APP_HOME%

echo 启动应用...
start "RBAC System" java -Xms512m -Xmx1024m -jar %APP_NAME% --spring.profiles.active=prod

echo 应用启动中，请稍候...
timeout /t 5

echo 应用已启动，访问地址: http://localhost:8080
```

## 数据库初始化

### 1. 创建数据库

```bash
mysql -u root -p < sql/db-init.sql
```

### 2. 导入初始数据

```bash
mysql -u root -p < sql/db-init-data.sql
```

### 3. 验证数据

```sql
USE rbac_db;
SELECT COUNT(*) FROM sys_user;
SELECT COUNT(*) FROM sys_role;
SELECT COUNT(*) FROM sys_permission;
```

## 健康检查

### 1. 检查应用是否启动

```bash
# 检查端口
netstat -an | grep 8080

# 或使用 lsof（Linux）
lsof -i:8080

# 或使用 curl
curl http://localhost:8080
```

### 2. 检查日志

```bash
# 查看最新日志
tail -f logs/rbac-system.log

# 查看错误日志
grep ERROR logs/rbac-system.log

# 查看启动日志
grep "Started RbacApplication" logs/rbac-system.log
```

### 3. 访问应用

```
http://your-server-ip:8080
```

## 常见问题

### 问题 1：端口被占用

**错误信息：**
```
Port 8080 was already in use
```

**解决方案：**
```bash
# 查找占用端口的进程
netstat -ano | findstr :8080  # Windows
lsof -i:8080                  # Linux

# 修改端口
java -jar rbac-system-1.0.0.jar --server.port=8081
```

### 问题 2：数据库连接失败

**错误信息：**
```
Could not connect to database
```

**解决方案：**
1. 检查数据库是否启动
2. 检查数据库地址、用户名、密码
3. 检查防火墙设置
4. 检查 MySQL 是否允许远程连接

### 问题 3：内存不足

**错误信息：**
```
java.lang.OutOfMemoryError: Java heap space
```

**解决方案：**
```bash
# 增加堆内存
java -Xms1024m -Xmx2048m -jar rbac-system-1.0.0.jar
```

### 问题 4：找不到主类

**错误信息：**
```
no main manifest attribute
```

**解决方案：**
检查 `build.gradle.kts` 中是否有 Spring Boot 插件：
```kotlin
plugins {
    id("org.springframework.boot") version "3.2.1"
}
```

## 性能监控

### 1. JVM 监控

```bash
# 查看 JVM 进程
jps -l

# 查看 JVM 参数
jinfo <pid>

# 查看堆内存使用
jmap -heap <pid>

# 查看线程信息
jstack <pid>
```

### 2. 应用监控

可以集成 Spring Boot Actuator：

```kotlin
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-actuator")
```

```yaml
# application-prod.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

访问监控端点：
- 健康检查：`http://localhost:8080/actuator/health`
- 应用信息：`http://localhost:8080/actuator/info`
- 性能指标：`http://localhost:8080/actuator/metrics`

## 备份和恢复

### 数据库备份

```bash
# 备份数据库
mysqldump -u root -p rbac_db > rbac_db_backup_$(date +%Y%m%d).sql

# 恢复数据库
mysql -u root -p rbac_db < rbac_db_backup_20260204.sql
```

### 应用备份

```bash
# 备份应用目录
tar -czf rbac-system-backup-$(date +%Y%m%d).tar.gz /opt/rbac-system
```

## 升级部署

### 1. 备份当前版本

```bash
cp rbac-system-1.0.0.jar rbac-system-1.0.0.jar.bak
```

### 2. 停止应用

```bash
./stop.sh
```

### 3. 替换 JAR 包

```bash
cp /path/to/new/rbac-system-1.1.0.jar .
```

### 4. 启动新版本

```bash
./start.sh
```

### 5. 验证升级

```bash
# 查看日志
tail -f logs/rbac-system.log

# 访问应用
curl http://localhost:8080
```

## 安全建议

1. **修改默认密码**
   - 修改数据库 root 密码
   - 修改应用 admin 密码

2. **配置防火墙**
   ```bash
   # 只允许特定 IP 访问
   sudo ufw allow from 192.168.1.0/24 to any port 8080
   ```

3. **使用 HTTPS**
   - 配置 SSL 证书
   - 强制 HTTPS 访问

4. **定期备份**
   - 每天备份数据库
   - 每周备份应用

5. **监控日志**
   - 定期检查错误日志
   - 设置日志告警

---

**文档版本**：v1.0  
**更新日期**：2026-02-04
