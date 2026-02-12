package com.rbac.exception

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 *
 * @param message 异常消息
 * @param code 错误码（可选）
 * @param cause 原始异常（可选）
 */
class BusinessException : RuntimeException {
    
    val code: String?
    
    constructor(message: String) : super(message) {
        this.code = null
    }
    
    constructor(message: String, code: String) : super(message) {
        this.code = code
    }
    
    constructor(message: String, cause: Throwable) : super(message, cause) {
        this.code = null
    }
    
    constructor(message: String, code: String, cause: Throwable) : super(message, cause) {
        this.code = code
    }
    
    companion object {
        // 常用错误码定义
        const val USER_NOT_FOUND = "USER_NOT_FOUND"
        const val USER_DISABLED = "USER_DISABLED"
        const val ROLE_NOT_FOUND = "ROLE_NOT_FOUND"
        const val PERMISSION_NOT_FOUND = "PERMISSION_NOT_FOUND"
        const val INVALID_PASSWORD = "INVALID_PASSWORD"
        const val PASSWORD_SAME = "PASSWORD_SAME"
        const val DATA_NOT_FOUND = "DATA_NOT_FOUND"
        const val OPERATION_FAILED = "OPERATION_FAILED"
    }
}
