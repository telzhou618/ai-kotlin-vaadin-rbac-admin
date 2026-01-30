package com.rbac.service

import com.rbac.dto.DashboardDto
import org.springframework.stereotype.Service

@Service
class DashboardService(
    private val userService: SysUserService,
    private val roleService: SysRoleService,
    private val permissionService: SysPermissionService,
    private val logService: SysOperationLogService
) {
    
    fun getDashboardData(): DashboardDto {
        return DashboardDto(
            userCount = userService.count(),
            roleCount = roleService.count(),
            permCount = permissionService.count(),
            logCount = logService.count()
        )
    }
}
