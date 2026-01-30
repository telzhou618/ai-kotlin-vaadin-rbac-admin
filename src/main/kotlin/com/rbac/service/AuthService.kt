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
        
        // 登录并缓存用户信息
        StpUtil.login(user.id)
        
        // 获取并缓存用户的角色和权限
        val roles = userService.getUserRoles(user.id!!)
        val permissions = userService.getUserPermissions(user.id!!)
        
        // 存储到 Session 中（Sa-Token 会自动管理）
        StpUtil.getSession().set("roles", roles)
        StpUtil.getSession().set("permissions", permissions)
        
        return true
    }
    
    @OperationLog(module = "系统", operation = "退出")
    fun logout() {
        StpUtil.logout()
    }
}
