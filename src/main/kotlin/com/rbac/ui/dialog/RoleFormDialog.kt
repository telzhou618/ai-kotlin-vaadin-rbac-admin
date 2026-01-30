package com.rbac.ui.dialog

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.RoleDto
import com.rbac.entity.SysRole
import com.rbac.exception.GlobalExceptionHandler
import com.rbac.service.SysRoleService
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField

class RoleFormDialog(
    private val role: SysRole?,
    private val roleService: SysRoleService,
    private val exceptionHandler: GlobalExceptionHandler,
    private val onSuccess: () -> Unit
) : Dialog() {
    
    private lateinit var roleCodeField: TextField
    private lateinit var roleNameField: TextField
    private lateinit var roleDescField: TextArea
    
    init {
        headerTitle = if (role == null) "新增角色" else "编辑角色"
        width = "500px"
        
        verticalLayout {
            roleCodeField = textField("角色编码") {
                width = "100%"
                value = role?.roleCode ?: ""
            }
            
            roleNameField = textField("角色名称") {
                width = "100%"
                value = role?.roleName ?: ""
            }
            
            roleDescField = textArea("角色描述") {
                width = "100%"
                value = role?.roleDesc ?: ""
            }
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
            val dto = RoleDto(
                id = role?.id,
                roleCode = roleCodeField.value.trim(),
                roleName = roleNameField.value.trim(),
                roleDesc = roleDescField.value.trim()
            )
            
            if (dto.roleCode.isBlank() || dto.roleName.isBlank()) {
                exceptionHandler.showError("角色编码和名称不能为空")
                return
            }
            
            if (role == null) {
                roleService.saveRole(dto)
            } else {
                roleService.updateRole(dto)
            }
            
            exceptionHandler.showSuccess("保存成功")
            close()
            onSuccess()
        } catch (e: Exception) {
            exceptionHandler.handle(e)
        }
    }
}
