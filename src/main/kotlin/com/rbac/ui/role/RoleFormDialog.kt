package com.rbac.ui.role

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.RoleDto
import com.rbac.entity.SysRole
import com.rbac.service.SysRoleService
import com.rbac.util.showError
import com.rbac.util.showSuccess
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.validator.StringLengthValidator

class RoleFormDialog(
    private val role: SysRole?,
    private val roleService: SysRoleService,
    private val onSuccess: () -> Unit
) : Dialog() {

    private val binder = BeanValidationBinder(RoleDto::class.java)

    init {
        headerTitle = if (role == null) "新增角色" else "编辑角色"
        width = "500px"

        val dto = role?.let {
            RoleDto(id = it.id, roleCode = it.roleCode, roleName = it.roleName, roleDesc = it.roleDesc)
        } ?: RoleDto()

        verticalLayout {
            isPadding = false
            isSpacing = true

            textField("角色编码") {
                width = "100%"
                prefixComponent = VaadinIcon.CODE.create()
                binder.forField(this)
                    .asRequired("角色编码不能为空")
                    .withValidator(StringLengthValidator("角色编码长度必须在2-50个字符之间", 2, 50))
                    .bind(RoleDto::roleCode.name)
            }

            textField("角色名称") {
                width = "100%"
                prefixComponent = VaadinIcon.TAG.create()
                binder.forField(this)
                    .asRequired("角色名称不能为空")
                    .withValidator(StringLengthValidator("角色名称长度必须在2-50个字符之间", 2, 50))
                    .bind(RoleDto::roleName.name)
            }

            textArea("角色描述") {
                width = "100%"
                binder.forField(this)
                    .bind(RoleDto::roleDesc.name)
            }
        }

        binder.readBean(dto)

        footer.add(
            button("取消", VaadinIcon.CLOSE.create()) {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                onLeftClick { close() }
            },
            button("保存", VaadinIcon.CHECK.create()) {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick { save() }
            }
        )
    }

    private fun save() {
        val dto = RoleDto(id = role?.id)
        // 验证前端、后端、表单数据写入bean
        if (binder.writeBeanIfValid(dto)) {
            if (role == null) {
                roleService.saveRole(dto)
            } else {
                roleService.updateRole(dto)
            }
            showSuccess("保存成功")
            close()
            onSuccess()
        } else {
            showError("表单验证失败, 请检查输入")
        }
    }
}
