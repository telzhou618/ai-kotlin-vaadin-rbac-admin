package com.rbac.ui.permission

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.PermissionDto
import com.rbac.service.SysPermissionService
import com.rbac.ui.component.dialogContentStyle
import com.rbac.util.showSuccess
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator

class PermissionFormDialog(
    private val perm: PermissionDto?,
    private val parentId: Long,
    private val permissionService: SysPermissionService,
    private val onSuccess: () -> Unit
) : Dialog() {

    private lateinit var parentPermField: TextField
    private lateinit var permCodeField: TextField
    private lateinit var permNameField: TextField

    private val binder = Binder(PermissionDto::class.java)

    init {
        headerTitle = if (perm == null) "新增权限" else "编辑权限"
        width = "500px"
        
        val dto = perm ?: PermissionDto(parentId = parentId)
        val parentPerm = permissionService.getById(parentId)
        val parentPermName = if (parentId == 0L) "根权限" else parentPerm?.permName ?: "未知"
        val parentPermCode = if (parentId == 0L) "" else parentPerm?.permCode ?: ""
        
        verticalLayout {
            dialogContentStyle()
            
            parentPermField = textField("父级权限") {
                width = "100%"
                value = "$parentPermName($parentPermCode)"
                isReadOnly = true
                prefixComponent = VaadinIcon.FOLDER_OPEN.create()
            }

            permCodeField = textField("权限编码") {
                width = "100%"
                prefixComponent = VaadinIcon.CODE.create()
            }

            permNameField = textField("权限名称") {
                width = "100%"
                prefixComponent = VaadinIcon.TAG.create()
            }
        }

        configureBinder()
        binder.readBean(dto)

        footer.add(
            button("取消") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                icon = VaadinIcon.CLOSE.create()
                onLeftClick { close() }
            },
            button("保存") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                icon = VaadinIcon.CHECK.create()
                onLeftClick { handleSave() }
            }
        )
    }

    private fun configureBinder() {
        binder.forField(permCodeField)
            .asRequired("权限编码不能为空")
            .withValidator(StringLengthValidator("权限编码长度必须在2-50个字符之间", 2, 50))
            .bind(PermissionDto::permCode.name)

        binder.forField(permNameField)
            .asRequired("权限名称不能为空")
            .withValidator(StringLengthValidator("权限名称长度必须在2-50个字符之间", 2, 50))
            .bind(PermissionDto::permName.name)
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

            showSuccess("保存成功")
            close()
            onSuccess()
        }
    }
}
