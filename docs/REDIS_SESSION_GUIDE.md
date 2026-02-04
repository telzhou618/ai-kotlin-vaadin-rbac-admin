# Redis 会话持久化配置指南

## 功能说明

通过集成 Redis，实现 Sa-Token 的会话持久化，使得应用重启后用户登录状态不丢失。

## 配置步骤

### 1. 添加依赖

在 `build.gradle.kts` 中已添加：

```kotlin
// Sa-Token Redis 集成（使用 Jackson 序列化）
implementation("cn.dev33:sa-token-redis-jackson:1.37.0")
// Redis 连接池
implementation("org.apache.commons:commons-pool2:2.12.0")
```

**说明：**
- `sa-token-redis-jackson`：Sa-Token 的 Redis 集成，使用 Jackson 序列化
- `commons-pool2`：Redis 连接池，提高性能

### 2. 配置 Redis

在 `application.yml` 中已添加：

```yaml
spring:
  data:
    redis:
      host: localhost          # Redis 服务器地址
      port: 6379              # Redis 端口
      password:               # Redis 密码（如果有）
      database: 0             # 使用的数据库索引
      timeout: 10s            # 连接超时时间
      lettuce:
        pool:
          max-active: 8       # 最大活跃连接数
          max-idle: 8         # 最大空闲连接数
          min-idle: 0         # 最小空闲连接数
          max-wait: -1ms      # 最大等待时间
```

### 3. 安装 Redis

#### Windows

**方法一：使用 Memurai（推荐）**

1. 下载 Memurai（Redis for Windows）
   - 访问：https://www.memurai.com/
   - 下载并安装

2. 启动服务
   ```cmd
   # 自动启动（安装时已配置）
   # 或手动启动
   net start Memurai
   ```

**方法二：使用 WSL2 + Docker**

```bash
# 在 WSL2 中运行
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

**方法三：使用 Redis for Windows（非官方）**

1. 下载：https://github.com/tporadowski/redis/releases
2. 解压后运行 `redis-server.exe`

#### Linux

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis

# CentOS/RHEL
sudo yum install redis
sudo systemctl start redis
sudo systemctl enable redis
```

#### macOS

```bash
# 使用 Homebrew
brew install redis
brew services start redis
```

#### Docker（推荐，跨平台）

```bash
# 启动 Redis
docker run -d \
  --name rbac-redis \
  -p 6379:6379 \
  -v redis-data:/data \
  redis:7-alpine \
  redis-server --appendonly yes

# 查看日志
docker logs -f rbac-redis

# 停止
docker stop rbac-redis

# 启动
docker start rbac-redis
```

### 4. 验证 Redis 连接

```bash
# 使用 redis-cli 测试
redis-cli ping
# 应该返回：PONG

# 查看 Redis 信息
redis-cli info

# 查看所有 key
redis-cli keys "*"
```

## 工作原理

### 存储机制

Sa-Token 会将以下数据存储到 Redis：

1. **Token 信息**
   - Key: `satoken:login:token:{tokenValue}`
   - Value: 用户 ID、登录时间等信息

2. **用户 Session**
   - Key: `satoken:login:session:{userId}`
   - Value: 用户会话数据

3. **权限信息**（如果缓存）
   - Key: `satoken:login:permission:{userId}`
   - Value: 用户权限列表

### 数据结构示例

```
# Token 信息
satoken:login:token:550e8400-e29b-41d4-a716-446655440000
{
  "loginId": "1",
  "loginType": "login",
  "tokenValue": "550e8400-e29b-41d4-a716-446655440000",
  "createTime": 1707048000000
}

# 用户 Session
satoken:login:session:1
{
  "id": "1",
  "username": "admin",
  "loginTime": 1707048000000
}
```

## 测试验证

### 1. 启动应用

```bash
# 确保 Redis 已启动
redis-cli ping

# 启动应用
gradle bootRun
```

### 2. 登录系统

访问：http://localhost:8080

使用账号：`admin/admin123`

### 3. 查看 Redis 数据

```bash
# 查看所有 Sa-Token 相关的 key
redis-cli keys "satoken:*"

# 应该看到类似输出：
# 1) "satoken:login:token:550e8400-e29b-41d4-a716-446655440000"
# 2) "satoken:login:session:1"
```

### 4. 重启应用测试

```bash
# 1. 停止应用（Ctrl + C）
# 2. 重新启动
gradle bootRun

# 3. 刷新浏览器
# 应该仍然保持登录状态，不需要重新登录
```

### 5. 查看 Token 详情

```bash
# 查看 Token 信息
redis-cli get "satoken:login:token:{你的token值}"

# 查看 Session 信息
redis-cli get "satoken:login:session:1"

# 查看 Token 过期时间
redis-cli ttl "satoken:login:token:{你的token值}"
# 返回剩余秒数，-1 表示永不过期，-2 表示已过期
```

## 配置优化

### 1. 生产环境配置

创建 `application-prod.yml`：

```yaml
spring:
  data:
    redis:
      host: your-redis-host
      port: 6379
      password: your-redis-password
      database: 0
      timeout: 10s
      lettuce:
        pool:
          max-active: 20      # 生产环境增加连接数
          max-idle: 10
          min-idle: 5
          max-wait: 3s
      ssl:
        enabled: false        # 如果使用 SSL，设置为 true

# Sa-Token 配置
sa-token:
  token-name: satoken
  timeout: 2592000            # 30 天（秒）
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: false
```

### 2. Redis 密码配置

如果 Redis 设置了密码：

```yaml
spring:
  data:
    redis:
      password: your_redis_password
```

### 3. 使用 Redis Sentinel（高可用）

```yaml
spring:
  data:
    redis:
      sentinel:
        master: mymaster
        nodes:
          - 192.168.1.100:26379
          - 192.168.1.101:26379
          - 192.168.1.102:26379
      password: your_password
```

### 4. 使用 Redis Cluster（集群）

```yaml
spring:
  data:
    redis:
      cluster:
        nodes:
          - 192.168.1.100:6379
          - 192.168.1.101:6379
          - 192.168.1.102:6379
        max-redirects: 3
      password: your_password
```

## 性能优化

### 1. 连接池配置

```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 20      # 根据并发量调整
          max-idle: 10
          min-idle: 5
          max-wait: 3s
        shutdown-timeout: 100ms
```

### 2. 序列化配置

Sa-Token 默认使用 Jackson 序列化，性能较好。如果需要更高性能，可以考虑：

- **Kryo**：更快的序列化
- **Protobuf**：更小的数据体积

### 3. 缓存策略

```yaml
sa-token:
  timeout: 2592000           # Token 有效期（30 天）
  activity-timeout: 1800     # 活跃超时（30 分钟无操作自动过期）
  is-concurrent: true        # 允许同一账号并发登录
  is-share: false            # 不共享 Token
```

## 监控和维护

### 1. 查看 Redis 状态

```bash
# 查看 Redis 信息
redis-cli info

# 查看内存使用
redis-cli info memory

# 查看连接数
redis-cli info clients

# 查看 key 数量
redis-cli dbsize
```

### 2. 清理过期数据

Redis 会自动清理过期的 key，但也可以手动清理：

```bash
# 清理所有 Sa-Token 数据
redis-cli keys "satoken:*" | xargs redis-cli del

# 清理特定用户的 Session
redis-cli del "satoken:login:session:1"
```

### 3. 备份 Redis 数据

```bash
# 手动触发 RDB 快照
redis-cli save

# 或使用后台保存
redis-cli bgsave

# 备份文件位置
# Linux: /var/lib/redis/dump.rdb
# Windows: Redis 安装目录/dump.rdb
```

## 常见问题

### Q1: 连接 Redis 失败

**错误信息：**
```
Unable to connect to Redis
```

**解决方案：**
1. 检查 Redis 是否启动：`redis-cli ping`
2. 检查端口是否正确：默认 6379
3. 检查防火墙设置
4. 检查 Redis 配置文件中的 `bind` 设置

### Q2: 认证失败

**错误信息：**
```
NOAUTH Authentication required
```

**解决方案：**
在 `application.yml` 中配置密码：
```yaml
spring:
  data:
    redis:
      password: your_password
```

### Q3: 重启后仍然需要登录

**可能原因：**
1. Redis 未启动
2. Redis 数据被清空
3. Token 已过期
4. 配置未生效

**解决方案：**
1. 检查 Redis 状态：`redis-cli ping`
2. 查看 Redis 中的 key：`redis-cli keys "satoken:*"`
3. 检查 Token 过期时间配置
4. 查看应用日志

### Q4: 内存占用过高

**解决方案：**
1. 设置合理的 Token 过期时间
2. 启用 Redis 的内存淘汰策略：
   ```
   # redis.conf
   maxmemory 256mb
   maxmemory-policy allkeys-lru
   ```
3. 定期清理无用数据

### Q5: 性能问题

**解决方案：**
1. 增加连接池大小
2. 使用 Redis 集群
3. 启用 Redis 持久化（AOF 或 RDB）
4. 监控 Redis 性能指标

## Docker Compose 部署

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  redis:
    image: redis:7-alpine
    container_name: rbac-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes --requirepass your_password
    restart: unless-stopped
    networks:
      - rbac-network

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
    restart: unless-stopped
    networks:
      - rbac-network

volumes:
  redis-data:
  mysql-data:

networks:
  rbac-network:
    driver: bridge
```

启动：
```bash
docker-compose up -d
```

## 安全建议

1. **设置 Redis 密码**
   ```bash
   # redis.conf
   requirepass your_strong_password
   ```

2. **限制访问 IP**
   ```bash
   # redis.conf
   bind 127.0.0.1 192.168.1.100
   ```

3. **禁用危险命令**
   ```bash
   # redis.conf
   rename-command FLUSHDB ""
   rename-command FLUSHALL ""
   rename-command CONFIG ""
   ```

4. **启用 SSL/TLS**（生产环境）
   ```yaml
   spring:
     data:
       redis:
         ssl:
           enabled: true
   ```

## 总结

通过集成 Redis，实现了：

1. ✅ **会话持久化** - 应用重启后登录状态不丢失
2. ✅ **高性能** - Redis 内存存储，访问速度快
3. ✅ **可扩展** - 支持集群部署，水平扩展
4. ✅ **高可用** - 支持 Sentinel 和 Cluster 模式
5. ✅ **易维护** - 可视化工具丰富，监控方便

**下一步：**
- 配置 Redis 监控（如 RedisInsight）
- 设置 Redis 备份策略
- 配置 Redis 高可用方案

---

**更新日期**：2026-02-04  
**Redis 版本**：7.x  
**Sa-Token 版本**：1.37.0
