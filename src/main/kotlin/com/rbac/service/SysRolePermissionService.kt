package com.rbac.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.rbac.entity.SysRolePermission
import com.rbac.mapper.SysRolePermissionMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SysRolePermissionService : ServiceImpl<SysRolePermissionMapper, SysRolePermission>() {
    
    fun getPermIdsByRoleId(roleId: Long): List<Long> {
        return list(QueryWrapper<SysRolePermission>().eq("role_id", roleId))
            .map { it.permId }
    }
    
    @Transactional
    fun saveRolePermissions(roleId: Long, permIds: List<Long>) {
        remove(QueryWrapper<SysRolePermission>().eq("role_id", roleId))
        if (permIds.isNotEmpty()) {
            val rolePerms = permIds.map { SysRolePermission(roleId = roleId, permId = it) }
            saveBatch(rolePerms)
        }
    }
    
    @Transactional
    fun deleteByRoleId(roleId: Long) {
        remove(QueryWrapper<SysRolePermission>().eq("role_id", roleId))
    }
}
