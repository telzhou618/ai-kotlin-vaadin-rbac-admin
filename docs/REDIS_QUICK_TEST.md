# Redis 会话持久化快速测试

## 测试目标

验证应用重启后，用户登录状态是否保持。

## 前置条件

- ✅ Redis 已安装并启动
- ✅ 应用已配置 Redis 连接
- ✅ 依赖已更新

## 快速启动 Redis

### 方法一：使用脚本（推荐）

#### Windows
```bash
redis-start.bat
```

#### Linux/Mac
```bash
chmod +x redis-start.sh
./redis-start.sh
```

### 方法二：手动启动 Docker

```bash
docker run -d \
  --name rbac-redis \
  -p 6379:6379 \
  -v rbac-redis-data:/data \
  redis:7-alpine \
  redis-server --appendonly yes
```

### 方法三：本地 Redis

```bash
# Windows（如果已安装 Redis）
redis-server

# Linux
sudo systemctl start redis

# macOS
brew services start redis
```

## 测试步骤

### 步骤 1：验证 Redis 连接

```bash
# 测试 Redis 是否正常
redis-cli ping
# 应该返回：PONG

# 或使用 Docker
docker exec rbac-redis redis-cli ping
```

### 步骤 2：启动应用

```bash
gradle bootRun
```

查看启动日志，应该看到类似信息：
```
Lettuce initialized
Redis connection established
```

### 步骤 3：登录系统

1. 访问：http://localhost:8080
2. 使用账号：`admin/admin123`
3. 登录成功后，进入 Dashboard

### 步骤 4：查看 Redis 数据

打开新的终端窗口：

```bash
# 查看所有 Sa-Token 相关的 key
redis-cli keys "satoken:*"

# 或使用 Docker
docker exec rbac-redis redis-cli keys "satoken:*"
```

**应该看到类似输出：**
```
1) "satoken:login:token:550e8400-e29b-41d4-a716-446655440000"
2) "satoken:login:session:1"
3) "satoken:login:last-activity:1"
```

### 步骤 5：查看 Token 详情

```bash
# 查看 Token 信息（替换为实际的 token 值）
redis-cli get "satoken:login:token:你的token值"

# 查看 Session 信息
redis-cli get "satoken:login:session:1"

# 查看过期时间（秒）
redis-cli ttl "satoken:login:token:你的token值"
# 返回剩余秒数，例如：2591999（约30天）
```

### 步骤 6：重启应用测试

1. **停止应用**
   - 在运行 `gradle bootRun` 的终端按 `Ctrl + C`
   - 等待应用完全停止

2. **验证 Redis 数据仍然存在**
   ```bash
   redis-cli keys "satoken:*"
   # 应该仍然能看到之前的 key
   ```

3. **重新启动应用**
   ```bash
   gradle bootRun
   ```

4. **刷新浏览器**
   - 按 `F5` 刷新页面
   - **应该仍然保持登录状态**，不需要重新登录
   - 可以正常访问 Dashboard 和其他页面

### 步骤 7：验证功能

1. 点击不同的菜单（用户管理、角色管理等）
2. 确认所有功能正常
3. 确认用户名显示正确
4. 确认权限控制正常

## 对比测试

### 未使用 Redis（重启前）

```
1. 登录系统 ✓
2. 停止应用
3. 启动应用
4. 刷新浏览器
5. 结果：❌ 需要重新登录
```

### 使用 Redis（重启后）

```
1. 登录系统 ✓
2. 停止应用
3. 启动应用
4. 刷新浏览器
5. 结果：✅ 仍然保持登录状态
```

## 高级测试

### 测试 1：多次重启

```bash
# 重复以下步骤 3 次
1. 停止应用
2. 启动应用
3. 刷新浏览器
4. 验证登录状态

# 结果：应该始终保持登录状态
```

### 测试 2：长时间等待

```bash
1. 登录系统
2. 停止应用
3. 等待 5 分钟
4. 启动应用
5. 刷新浏览器

# 结果：应该仍然保持登录状态
```

### 测试 3：清空 Redis

```bash
1. 登录系统
2. 清空 Redis：redis-cli flushdb
3. 刷新浏览器

# 结果：应该需要重新登录（因为 Token 被清空）
```

### 测试 4：Token 过期

```bash
1. 登录系统
2. 修改 Token 过期时间为 10 秒：
   # application.yml
   sa-token:
     timeout: 10
3. 重启应用
4. 等待 15 秒
5. 刷新浏览器

# 结果：应该需要重新登录（Token 已过期）
```

## 监控 Redis

### 实时监控

```bash
# 监控所有命令
redis-cli monitor

# 查看统计信息
redis-cli info stats

# 查看内存使用
redis-cli info memory
```

### 查看具体数据

```bash
# 查看所有 key
redis-cli keys "*"

# 查看 key 的类型
redis-cli type "satoken:login:token:xxx"

# 查看 key 的值
redis-cli get "satoken:login:token:xxx"

# 查看 key 的过期时间
redis-cli ttl "satoken:login:token:xxx"
```

## 故障排查

### 问题 1：Redis 连接失败

**症状：**
```
Unable to connect to Redis; nested exception is io.lettuce.core.RedisConnectionException
```

**检查：**
```bash
# 1. Redis 是否启动
redis-cli ping

# 2. 端口是否正确
netstat -an | grep 6379

# 3. 防火墙是否阻止
# Windows: 检查防火墙设置
# Linux: sudo ufw status
```

**解决：**
1. 启动 Redis
2. 检查 `application.yml` 中的配置
3. 关闭防火墙或添加例外

### 问题 2：重启后仍需登录

**可能原因：**
1. Redis 未启动
2. Redis 数据被清空
3. Token 已过期
4. 配置未生效

**检查：**
```bash
# 1. 检查 Redis 状态
redis-cli ping

# 2. 检查 Redis 中的数据
redis-cli keys "satoken:*"

# 3. 检查应用日志
# 查看是否有 Redis 连接错误
```

**解决：**
1. 确保 Redis 启动
2. 检查 Token 过期时间配置
3. 查看应用日志中的错误信息

### 问题 3：依赖冲突

**症状：**
```
ClassNotFoundException: RedisTemplate
```

**解决：**
1. 刷新 Gradle 依赖：
   ```bash
   gradle clean build --refresh-dependencies
   ```
2. 在 IDEA 中：右键项目 → Gradle → Refresh Gradle Project

### 问题 4：序列化错误

**症状：**
```
SerializationException: Could not read JSON
```

**解决：**
1. 清空 Redis 数据：`redis-cli flushdb`
2. 重启应用
3. 重新登录

## 性能测试

### 测试登录性能

```bash
# 使用 Apache Bench 测试
ab -n 1000 -c 10 http://localhost:8080/login

# 或使用 JMeter 进行压力测试
```

### 监控 Redis 性能

```bash
# 查看 Redis 性能统计
redis-cli info stats

# 查看慢查询
redis-cli slowlog get 10

# 查看连接数
redis-cli info clients
```

## 清理测试数据

### 清空所有 Sa-Token 数据

```bash
# 删除所有 satoken 相关的 key
redis-cli keys "satoken:*" | xargs redis-cli del

# 或清空整个数据库（谨慎使用）
redis-cli flushdb
```

### 停止 Redis

```bash
# Docker
docker stop rbac-redis

# 本地 Redis
# Windows
redis-cli shutdown

# Linux
sudo systemctl stop redis

# macOS
brew services stop redis
```

## 成功标准

测试通过的标准：

- ✅ Redis 可以正常连接
- ✅ 登录后 Redis 中有 Token 数据
- ✅ 应用重启后登录状态保持
- ✅ 所有功能正常使用
- ✅ 权限控制正常
- ✅ Token 过期后需要重新登录

## 下一步

测试通过后，可以：

1. 配置生产环境的 Redis
2. 设置 Redis 密码
3. 配置 Redis 持久化
4. 配置 Redis 监控
5. 配置 Redis 高可用

详细配置请参考：[Redis 会话持久化配置指南](REDIS_SESSION_GUIDE.md)

---

**测试日期**：__________  
**测试结果**：□ 通过 □ 失败  
**备注**：__________
