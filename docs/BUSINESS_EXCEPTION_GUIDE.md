# BusinessException 使用指南

## 概述

`BusinessException` 是项目中统一的业务异常类，用于处理业务逻辑中的异常情况。相比 `RuntimeException`，它提供了更好的异常分类和错误码支持。

## 特性

- ✅ 统一的业务异常处理
- ✅ 支持错误码（可选）
- ✅ 自动日志分级（业务异常为 WARN，系统异常为 ERROR）
- ✅ 友好的错误提示
- ✅ 预定义常用错误码

## 使用方式

### 1. 基本用法

```kotlin
// 只传递错误消息
throw BusinessException("用户不存在")

// 传递错误消息和错误码
throw BusinessException("用户不存在", BusinessException.USER_NOT_FOUND)

// 传递错误消息和原始异常
throw BusinessException("数据库操作失败", cause)

// 传递完整信息
throw BusinessException("数据库操作失败", BusinessException.OPERATION_FAILED, cause)
```

### 2. 预定义错误码

```kotlin
companion object {
    const val USER_NOT_FOUND = "USER_NOT_FOUND"           // 用户不存在
    const val USER_DISABLED = "USER_DISABLED"             // 用户已禁用
    const val ROLE_NOT_FOUND = "ROLE_NOT_FOUND"           // 角色不存在
    const val PERMISSION_NOT_FOUND = "PERMISSION_NOT_FOUND" // 权限不存在
    const val INVALID_PASSWORD = "INVALID_PASSWORD"       // 密码错误
    const val PASSWORD_SAME = "PASSWORD_SAME"             // 新旧密码相同
    const val DATA_NOT_FOUND = "DATA_NOT_FOUND"           // 数据不存在
    const val OPERATION_FAILED = "OPERATION_FAILED"       // 操作失败
}
```

### 3. 实际应用示例

#### Service 层

```kotlin
@Service
class SysUserService {
    
    fun updateUser(dto: UserDto) {
        // 使用 BusinessException 替代 RuntimeException
        val user = getById(dto.id) 
            ?: throw BusinessException("用户不存在", BusinessException.USER_NOT_FOUND)
        
        // 业务逻辑...
        updateById(user)
    }
    
    fun changePassword(userId: Long, oldPassword: String, newPassword: String) {
        val user = getById(userId) 
            ?: throw BusinessException("用户不存在", BusinessException.USER_NOT_FOUND)
        
        if (user.password != md5(oldPassword)) {
            throw BusinessException("原密码错误", BusinessException.INVALID_PASSWORD)
        }
        
        if (user.password == md5(newPassword)) {
            throw BusinessException("新密码不能与旧密码相同", BusinessException.PASSWORD_SAME)
        }
        
        // 更新密码...
    }
}
```

#### Controller/View 层

```kotlin
try {
    userService.updateUser(dto)
    showSuccess("更新成功")
} catch (e: BusinessException) {
    // BusinessException 会被 GlobalExceptionHandler 自动处理
    // 无需手动处理，异常会自动显示给用户
}
```

## 异常处理流程

```
业务代码抛出异常
    ↓
GlobalExceptionHandler 捕获
    ↓
判断异常类型
    ├─ BusinessException → 记录 WARN 日志 → 显示友好错误消息
    └─ 其他异常 → 记录 ERROR 日志 → 显示系统错误消息
```

## 日志级别

- **BusinessException**: WARN 级别
  - 表示预期内的业务异常
  - 不需要立即处理
  - 示例：用户不存在、密码错误

- **其他异常**: ERROR 级别
  - 表示系统级异常
  - 需要开发人员关注
  - 示例：数据库连接失败、空指针异常

## 最佳实践

### ✅ 推荐做法

```kotlin
// 1. 使用预定义错误码
throw BusinessException("用户不存在", BusinessException.USER_NOT_FOUND)

// 2. 提供清晰的错误消息
throw BusinessException("用户名 '$username' 已存在")

// 3. 在 Service 层抛出异常
fun deleteUser(id: Long) {
    val user = getById(id) ?: throw BusinessException("用户不存在")
    removeById(id)
}
```

### ❌ 不推荐做法

```kotlin
// 1. 不要使用 RuntimeException
throw RuntimeException("用户不存在")  // ❌

// 2. 不要使用模糊的错误消息
throw BusinessException("错误")  // ❌

// 3. 不要在 View 层抛出业务异常
button.onClick {
    throw BusinessException("...")  // ❌ 应该在 Service 层抛出
}
```

## 扩展错误码

如果需要添加新的错误码，在 `BusinessException` 的 companion object 中添加：

```kotlin
companion object {
    // 现有错误码...
    
    // 新增错误码
    const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"
    const val PHONE_INVALID = "PHONE_INVALID"
}
```

## 总结

使用 `BusinessException` 的好处：

1. **统一异常处理**：所有业务异常使用同一个类
2. **错误码支持**：便于前端或 API 调用方识别具体错误
3. **日志分级**：业务异常和系统异常分开记录
4. **代码可读性**：一眼就能看出是业务异常还是系统异常
5. **便于维护**：统一的异常处理逻辑，易于扩展和修改
