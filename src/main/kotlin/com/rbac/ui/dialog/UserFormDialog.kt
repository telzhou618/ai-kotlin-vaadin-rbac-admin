package com.rbac.ui.dialog

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.UserDto
import com.rbac.exception.GlobalExceptionHandler
import com.rbac.service.SysRoleService
import com.rbac.service.SysUserService
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField

class UserFormDialog(
    private val user: UserDto?,
    private val userService: SysUserService,
    private val roleService: SysRoleService,
    private val exceptionHandler: GlobalExceptionHandler,
    private val onSuccess: () -> Unit
) : Dialog() {
    
    private lateinit var usernameField: TextField
    private lateinit var passwordField: PasswordField
    private lateinit var statusSelect: Select<Int>
    private lateinit var roleCheckbox: CheckboxGroup<Long>
    
    init {
        headerTitle = if (user == null) "新增用户" else "编辑用户"
        width = "500px"
        
        val roles = roleService.list()
        
        verticalLayout {
            usernameField = textField("用户名") {
                width = "100%"
                value = user?.username ?: ""
            }
            
            passwordField = passwordField("密码") {
                width = "100%"
                placeholder = if (user == null) "请输入密码" else "留空则不修改"
            }
            
            statusSelect = select<Int>("状态") {
                width = "100%"
                setItems(1, 0)
                setItemLabelGenerator { if (it == 1) "启用" else "禁用" }
                value = user?.status ?: 1
            }
            
            val roleCheckboxGroup = CheckboxGroup<Long>()
            roleCheckboxGroup.label = "分配角色"
            roleCheckboxGroup.width = "100%"
            roleCheckboxGroup.setItems(roles.map { it.id!! })
            roleCheckboxGroup.setItemLabelGenerator { roleId ->
                roles.find { it.id == roleId }?.roleName ?: ""
            }
            roleCheckboxGroup.value = user?.roleIds?.toSet() ?: emptySet()
            roleCheckbox = roleCheckboxGroup
            add(roleCheckboxGroup)
        }
        
        footer.add(
            button("取消") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                onLeftClick { close() }
            },
            button("保存") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick { handleSave() }
            }
        )
    }
    
    private fun handleSave() {
        try {
            val dto = UserDto(
                id = user?.id,
                username = usernameField.value.trim(),
                password = passwordField.value.trim().takeIf { it.isNotBlank() },
                status = statusSelect.value,
                roleIds = roleCheckbox.value.toList()
            )
            
            if (dto.username.isBlank()) {
                exceptionHandler.showError("用户名不能为空")
                return
            }
            
            if (user == null && dto.password.isNullOrBlank()) {
                exceptionHandler.showError("密码不能为空")
                return
            }
            
            if (user == null) {
                userService.saveUser(dto)
            } else {
                userService.updateUser(dto)
            }
            
            exceptionHandler.showSuccess("保存成功")
            close()
            onSuccess()
        } catch (e: Exception) {
            exceptionHandler.handle(e)
        }
    }
}
