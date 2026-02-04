# 部署脚本使用说明

## 文件说明

| 文件 | 说明 | 平台 |
|------|------|------|
| `start.sh` | 启动脚本 | Linux/Mac |
| `stop.sh` | 停止脚本 | Linux/Mac |
| `restart.sh` | 重启脚本 | Linux/Mac |
| `status.sh` | 状态检查脚本 | Linux/Mac |
| `start.bat` | 启动脚本 | Windows |
| `stop.bat` | 停止脚本 | Windows |

## 使用方法

### Linux/Mac

#### 1. 赋予执行权限

```bash
chmod +x *.sh
```

#### 2. 启动应用

```bash
./start.sh
```

#### 3. 停止应用

```bash
./stop.sh
```

#### 4. 重启应用

```bash
./restart.sh
```

#### 5. 查看状态

```bash
./status.sh
```

### Windows

#### 1. 启动应用

双击 `start.bat` 或在命令行中运行：
```cmd
start.bat
```

#### 2. 停止应用

双击 `stop.bat` 或在命令行中运行：
```cmd
stop.bat
```

## 部署目录结构

```
deploy/
├── rbac-system-1.0.0.jar       # 应用 JAR 包（需要复制到这里）
├── application-prod.yml         # 生产环境配置（可选）
├── logs/                       # 日志目录（自动创建）
│   ├── output.log             # 应用输出日志
│   ├── rbac-system.log        # 应用日志
│   └── heapdump.hprof         # 堆转储文件（OOM 时生成）
├── start.sh                    # Linux 启动脚本
├── stop.sh                     # Linux 停止脚本
├── restart.sh                  # Linux 重启脚本
├── status.sh                   # Linux 状态检查脚本
├── start.bat                   # Windows 启动脚本
├── stop.bat                    # Windows 停止脚本
└── README.md                   # 本文件
```

## 部署步骤

### 1. 构建 JAR 包

在项目根目录执行：

```bash
# Windows
gradle build -x test

# Linux/Mac
./gradlew build -x test
```

### 2. 复制文件

将以下文件复制到部署目录：

```bash
# 复制 JAR 包
cp build/libs/rbac-system-1.0.0.jar deploy/

# 复制配置文件（可选）
cp src/main/resources/application.yml deploy/application-prod.yml
```

### 3. 修改配置

编辑 `application-prod.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-host:3306/rbac_db
    username: your_username
    password: your_password
```

### 4. 启动应用

```bash
# Linux/Mac
./start.sh

# Windows
start.bat
```

### 5. 验证部署

访问：http://your-server-ip:8080

## 常用命令

### 查看日志

```bash
# 实时查看日志
tail -f logs/output.log

# 查看最近 100 行日志
tail -n 100 logs/output.log

# 搜索错误日志
grep ERROR logs/output.log
```

### 查看进程

```bash
# Linux/Mac
ps aux | grep rbac-system

# Windows
tasklist | findstr java
```

### 查看端口

```bash
# Linux/Mac
netstat -an | grep 8080
lsof -i:8080

# Windows
netstat -ano | findstr :8080
```

## 故障排查

### 应用无法启动

1. 检查 JAR 包是否存在
2. 检查 Java 版本（需要 JDK 17+）
3. 检查端口是否被占用
4. 查看日志文件 `logs/output.log`

### 应用启动后无法访问

1. 检查防火墙设置
2. 检查端口是否正确（默认 8080）
3. 检查数据库连接是否正常
4. 查看应用日志

### 内存不足

修改启动脚本中的内存参数：

```bash
# 增加堆内存
-Xms1024m -Xmx2048m
```

## 生产环境建议

1. **使用 systemd 服务**（Linux）
   - 更稳定的进程管理
   - 开机自启动
   - 自动重启

2. **配置反向代理**（Nginx）
   - 负载均衡
   - SSL 终止
   - 静态资源缓存

3. **定期备份**
   - 数据库备份
   - 应用备份
   - 配置文件备份

4. **监控告警**
   - 应用健康检查
   - 日志监控
   - 性能监控

## 技术支持

如有问题，请查看：
- [完整部署指南](../docs/DEPLOYMENT_GUIDE.md)
- [项目 README](../README.md)

---

**版本**：v1.0  
**更新日期**：2026-02-04
