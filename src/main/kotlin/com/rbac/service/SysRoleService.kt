package com.rbac.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.rbac.annotation.OperationLog
import com.rbac.dto.RoleDto
import com.rbac.dto.RoleQueryDto
import com.rbac.entity.SysRole
import com.rbac.mapper.SysRoleMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SysRoleService(
    private val rolePermissionService: SysRolePermissionService,
    val permissionService: SysPermissionService
) : ServiceImpl<SysRoleMapper, SysRole>() {
    
    fun pageQuery(page: Page<SysRole>, query: RoleQueryDto): Page<SysRole> {
        val wrapper = QueryWrapper<SysRole>()
        query.roleName?.let { wrapper.like("role_name", it) }
        query.roleCode?.let { wrapper.like("role_code", it) }
        return page(page, wrapper)
    }
    
    fun getRoleDto(role: SysRole): RoleDto {
        val permIds = rolePermissionService.getPermIdsByRoleId(role.id!!)
        return RoleDto(
            id = role.id,
            roleCode = role.roleCode,
            roleName = role.roleName,
            roleDesc = role.roleDesc,
            permIds = permIds
        )
    }
    
    @OperationLog(module = "角色管理", operation = "新增")
    @Transactional
    fun saveRole(dto: RoleDto) {
        val role = SysRole(
            roleCode = dto.roleCode,
            roleName = dto.roleName,
            roleDesc = dto.roleDesc
        )
        save(role)
    }
    
    @OperationLog(module = "角色管理", operation = "修改")
    @Transactional
    fun updateRole(dto: RoleDto) {
        val role = getById(dto.id) ?: throw RuntimeException("角色不存在")
        role.roleCode = dto.roleCode
        role.roleName = dto.roleName
        role.roleDesc = dto.roleDesc
        updateById(role)
    }
    
    @OperationLog(module = "角色管理", operation = "删除")
    @Transactional
    fun deleteRole(id: Long) {
        removeById(id)
        rolePermissionService.deleteByRoleId(id)
    }
    
    @OperationLog(module = "角色管理", operation = "分配权限")
    @Transactional
    fun assignPermissions(roleId: Long, permIds: List<Long>) {
        rolePermissionService.saveRolePermissions(roleId, permIds)
    }
    
    /**
     * 获取角色的所有权限ID列表
     */
    fun getPermissionIdsByRoleId(roleId: Long): List<Long> {
        return rolePermissionService.getPermIdsByRoleId(roleId)
    }
}
