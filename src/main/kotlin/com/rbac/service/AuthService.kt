package com.rbac.service

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.crypto.digest.DigestUtil
import com.rbac.annotation.OperationLog
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: SysUserService
) {
    
    @OperationLog(module = "系统", operation = "登录")
    fun login(username: String, password: String): Boolean {
        val user = userService.getUserByUsername(username) ?: return false
        if (user.status == 0) {
            throw RuntimeException("用户已被禁用")
        }
        if (user.password != DigestUtil.md5Hex(password)) {
            return false
        }
        StpUtil.login(user.id)
        return true
    }
    
    @OperationLog(module = "系统", operation = "退出")
    fun logout() {
        StpUtil.logout()
    }
}
