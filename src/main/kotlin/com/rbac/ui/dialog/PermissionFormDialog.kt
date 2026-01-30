package com.rbac.ui.dialog

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.PermissionDto
import com.rbac.exception.GlobalExceptionHandler
import com.rbac.service.SysPermissionService
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.textfield.TextField

class PermissionFormDialog(
    private val perm: PermissionDto?,
    private val parentId: Long,
    private val permissionService: SysPermissionService,
    private val exceptionHandler: GlobalExceptionHandler,
    private val onSuccess: () -> Unit
) : Dialog() {
    
    private lateinit var permCodeField: TextField
    private lateinit var permNameField: TextField
    
    init {
        headerTitle = if (perm == null) "新增权限" else "编辑权限"
        width = "500px"
        
        verticalLayout {
            permCodeField = textField("权限编码") {
                width = "100%"
                value = perm?.permCode ?: ""
            }
            
            permNameField = textField("权限名称") {
                width = "100%"
                value = perm?.permName ?: ""
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
            val dto = PermissionDto(
                id = perm?.id,
                permCode = permCodeField.value.trim(),
                permName = permNameField.value.trim(),
                parentId = parentId
            )
            
            if (dto.permCode.isBlank() || dto.permName.isBlank()) {
                exceptionHandler.showError("权限编码和名称不能为空")
                return
            }
            
            if (perm == null) {
                permissionService.savePerm(dto)
            } else {
                permissionService.updatePerm(dto)
            }
            
            exceptionHandler.showSuccess("保存成功")
            close()
            onSuccess()
        } catch (e: Exception) {
            exceptionHandler.handle(e)
        }
    }
}
