package com.rbac.config

import cn.dev33.satoken.stp.StpInterface
import com.rbac.service.SysUserService
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class SaTokenConfig

/**
 * Sa-Token 权限验证接口实现
 * 用于获取用户的权限和角色列表
 */
@Component
class StpInterfaceImpl(
    private val userService: SysUserService
) : StpInterface {
    
    /**
     * 返回指定用户的权限码集合
     */
    override fun getPermissionList(loginId: Any, loginType: String): List<String> {
        val userId = loginId.toString().toLong()
        return userService.getUserPermissions(userId)
    }
    
    /**
     * 返回指定用户的角色标识集合
     */
    override fun getRoleList(loginId: Any, loginType: String): List<String> {
        val userId = loginId.toString().toLong()
        return userService.getUserRoles(userId)
    }
}
