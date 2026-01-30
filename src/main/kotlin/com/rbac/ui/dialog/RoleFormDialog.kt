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
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator

class RoleFormDialog(
    private val role: SysRole?,
    private val roleService: SysRoleService,
    private val exceptionHandler: GlobalExceptionHandler,
    private val onSuccess: () -> Unit
) : Dialog() {
    
    private lateinit var roleCodeField: TextField
    private lateinit var roleNameField: TextField
    private lateinit var roleDescField: TextArea
    
    private val binder = Binder(RoleDto::class.java)
    
    init {
        headerTitle = if (role == null) "新增角色" else "编辑角色"
        width = "500px"
        
        val dto = if (role != null) {
            RoleDto(
                id = role.id,
                roleCode = role.roleCode,
                roleName = role.roleName,
                roleDesc = role.roleDesc
            )
        } else {
            RoleDto()
        }
        
        verticalLayout {
            roleCodeField = textField("角色编码") {
                width = "100%"
            }
            
            roleNameField = textField("角色名称") {
                width = "100%"
            }
            
            roleDescField = textArea("角色描述") {
                width = "100%"
            }
        }
        
        // 配置 Binder 验证规则
        binder.forField(roleCodeField)
            .asRequired("角色编码不能为空")
            .withValidator(StringLengthValidator("角色编码长度必须在2-50个字符之间", 2, 50))
            .bind(RoleDto::roleCode.name)
        
        binder.forField(roleNameField)
            .asRequired("角色名称不能为空")
            .withValidator(StringLengthValidator("角色名称长度必须在2-50个字符之间", 2, 50))
            .bind(RoleDto::roleName.name)
        
        binder.forField(roleDescField)
            .bind(RoleDto::roleDesc.name)
        
        binder.readBean(dto)
        
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
            if (binder.validate().isOk) {
                val dto = RoleDto(id = role?.id)
                binder.writeBean(dto)
                
                if (role == null) {
                    roleService.saveRole(dto)
                } else {
                    roleService.updateRole(dto)
                }
                
                exceptionHandler.showSuccess("保存成功")
                close()
                onSuccess()
            }
        } catch (e: Exception) {
            exceptionHandler.handle(e)
        }
    }
}
