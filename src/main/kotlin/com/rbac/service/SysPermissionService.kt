package com.rbac.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.rbac.annotation.OperationLog
import com.rbac.dto.PermissionDto
import com.rbac.entity.SysPermission
import com.rbac.mapper.SysPermissionMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SysPermissionService : ServiceImpl<SysPermissionMapper, SysPermission>() {
    
    fun getPermissionTree(): List<PermissionDto> {
        val allPerms = list()
        return buildTree(allPerms, 0)
    }
    
    private fun buildTree(allPerms: List<SysPermission>, parentId: Long): List<PermissionDto> {
        return allPerms.filter { it.parentId == parentId }
            .map { perm ->
                PermissionDto(
                    id = perm.id,
                    permCode = perm.permCode,
                    permName = perm.permName,
                    parentId = perm.parentId,
                    children = buildTree(allPerms, perm.id!!).toMutableList()
                )
            }
    }
    
    @OperationLog(module = "权限管理", operation = "新增")
    @Transactional
    fun savePerm(dto: PermissionDto) {
        val perm = SysPermission(
            permCode = dto.permCode,
            permName = dto.permName,
            parentId = dto.parentId
        )
        save(perm)
    }
    
    @OperationLog(module = "权限管理", operation = "修改")
    @Transactional
    fun updatePerm(dto: PermissionDto) {
        val perm = getById(dto.id) ?: throw RuntimeException("权限不存在")
        perm.permCode = dto.permCode
        perm.permName = dto.permName
        perm.parentId = dto.parentId
        updateById(perm)
    }
    
    @OperationLog(module = "权限管理", operation = "删除")
    @Transactional
    fun deletePerm(id: Long) {
        val count = count(QueryWrapper<SysPermission>().eq("parent_id", id))
        if (count > 0) {
            throw RuntimeException("存在子权限，无法删除")
        }
        removeById(id)
    }
}
