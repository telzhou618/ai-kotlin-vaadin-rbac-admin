package com.rbac.service

import cn.hutool.crypto.digest.DigestUtil
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.rbac.annotation.OperationLog
import com.rbac.dto.UserDto
import com.rbac.dto.UserQueryDto
import com.rbac.entity.SysUser
import com.rbac.exception.BusinessException
import com.rbac.mapper.SysUserMapper
import org.checkerframework.checker.units.qual.t
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SysUserService(
    private val userRoleService: SysUserRoleService,
    val roleService: SysRoleService,
    private val sysPermissionService: SysPermissionService
) : ServiceImpl<SysUserMapper, SysUser>() {

    fun pageQuery(page: Page<SysUser>, query: UserQueryDto): Page<SysUser> {
        val wrapper = QueryWrapper<SysUser>()
        query.username?.let { wrapper.like("username", it) }
        query.status?.let { wrapper.eq("status", it) }
        return page(page, wrapper)
    }

    fun getUserDto(user: SysUser): UserDto {
        val roleIds = userRoleService.getRoleIdsByUserId(user.id!!)
        val roleNames = roleService.listByIds(roleIds).joinToString(", ") { it.roleName }
        return UserDto(
            id = user.id,
            username = user.username,
            status = user.status,
            roleIds = roleIds,
            roleNames = roleNames
        )
    }

    @OperationLog(module = "用户管理", operation = "新增")
    @Transactional
    fun saveUser(dto: UserDto) {
        val user = SysUser(
            username = dto.username,
            password = DigestUtil.md5Hex(dto.password ?: "123456"),
            status = dto.status
        )
        save(user)
        userRoleService.saveUserRoles(user.id!!, dto.roleIds)
    }

    @OperationLog(module = "用户管理", operation = "修改")
    @Transactional
    fun updateUser(dto: UserDto) {
        val user = getById(dto.id) ?: throw BusinessException("用户不存在", BusinessException.USER_NOT_FOUND)
        user.username = dto.username
        user.status = dto.status
        if (!dto.password.isNullOrBlank()) {
            user.password = DigestUtil.md5Hex(dto.password)
        }
        updateById(user)
        userRoleService.saveUserRoles(user.id!!, dto.roleIds)
    }

    @OperationLog(module = "用户管理", operation = "删除")
    @Transactional
    fun deleteUser(id: Long) {
        removeById(id)
        userRoleService.deleteByUserId(id)
    }

    fun getUserByUsername(username: String): SysUser? {
        return getOne(QueryWrapper<SysUser>().eq("username", username))
    }

    /**
     * 获取用户的所有角色编码
     */
    fun getUserRoles(userId: Long): List<String> {
        val roleIds = userRoleService.getRoleIdsByUserId(userId)
        if (roleIds.isEmpty()) return emptyList()

        return roleService.listByIds(roleIds).map { it.roleCode }
    }

    /**
     * 获取用户的所有权限编码
     */
    fun getUserPermissions(userId: Long): List<String> {
        val roleIds = userRoleService.getRoleIdsByUserId(userId)
        if (roleIds.isEmpty()) return emptyList()

        // 获取所有角色的权限
        val permissions = mutableSetOf<String>()
        roleIds.forEach { roleId ->
            val permIds = roleService.getPermissionIdsByRoleId(roleId)
            if (permIds.isNotEmpty()) {
                val perms = roleService.permissionService.listByIds(permIds)
                permissions.addAll(perms.map { it.permCode })
            }
        }

        return permissions.toList()
    }

    /**
     * 切换用户状态（启用/禁用）
     */
    @OperationLog(module = "用户管理", operation = "切换状态")
    @Transactional
    fun toggleUserStatus(id: Long, newStatus: Int) {
        val user = getById(id) ?: throw BusinessException("用户不存在", BusinessException.USER_NOT_FOUND)
        user.status = newStatus
        updateById(user)
    }

    /**
     * 修改密码
     */
    @OperationLog(module = "用户管理", operation = "修改密码")
    @Transactional
    fun changePassword(userId: Long, oldPassword: String, newPassword: String) {
        val user = getById(userId) ?: throw BusinessException("用户不存在", BusinessException.USER_NOT_FOUND)

        // 验证原密码
        val oldPasswordMd5 = DigestUtil.md5Hex(oldPassword)
        if (user.password != oldPasswordMd5) {
            throw BusinessException("原密码错误", BusinessException.INVALID_PASSWORD)
        }

        // 验证新密码不能与旧密码相同
        val newPasswordMd5 = DigestUtil.md5Hex(newPassword)
        if (user.password == newPasswordMd5) {
            throw BusinessException("新密码不能与旧密码相同", BusinessException.PASSWORD_SAME)
        }

        // 更新密码
        user.password = newPasswordMd5
        updateById(user)
    }
}
