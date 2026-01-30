package com.rbac.ui.permission

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.PermissionDto
import com.rbac.service.SysPermissionService
import com.rbac.util.NotificationUtil
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator

class PermissionFormDialog(
    private val perm: PermissionDto?,
    private val parentId: Long,
    private val permissionService: SysPermissionService,
    private val onSuccess: () -> Unit
) : Dialog() {
    
    private lateinit var permCodeField: TextField
    private lateinit var permNameField: TextField
    
    private val binder = Binder(PermissionDto::class.java)
    
    init {
        headerTitle = if (perm == null) "新增权限" else "编辑权限"
        width = "500px"
        
        val dto = perm ?: PermissionDto(parentId = parentId)
        
        verticalLayout {
            permCodeField = textField("权限编码") {
                width = "100%"
            }
            
            permNameField = textField("权限名称") {
                width = "100%"
            }
        }
        
        // 配置 Binder 验证规则
        binder.forField(permCodeField)
            .asRequired("权限编码不能为空")
            .withValidator(StringLengthValidator("权限编码长度必须在2-50个字符之间", 2, 50))
            .bind(PermissionDto::permCode.name)
        
        binder.forField(permNameField)
            .asRequired("权限名称不能为空")
            .withValidator(StringLengthValidator("权限名称长度必须在2-50个字符之间", 2, 50))
            .bind(PermissionDto::permName.name)
        
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
        if (binder.validate().isOk) {
            val dto = PermissionDto(id = perm?.id, parentId = parentId)
            binder.writeBean(dto)
            
            if (perm == null) {
                permissionService.savePerm(dto)
            } else {
                permissionService.updatePerm(dto)
            }
            
            NotificationUtil.showSuccess("保存成功")
            close()
            onSuccess()
        }
    }
}
