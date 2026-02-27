package com.rbac.ui.user

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.UserDto
import com.rbac.service.SysRoleService
import com.rbac.service.SysUserService
import com.rbac.ui.component.dialogContentStyle
import com.rbac.util.showSuccess
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator

class UserFormDialog(
    private val user: UserDto?,
    private val userService: SysUserService,
    private val roleService: SysRoleService,
    private val onSuccess: () -> Unit
) : Dialog() {

    private val binder = Binder(UserDto::class.java)

    init {
        headerTitle = if (user == null) "新增用户" else "编辑用户"
        width = "500px"

        val roles = roleService.list()
        val dto = user ?: UserDto(status = 1, roleIds = emptyList())

        verticalLayout {
            dialogContentStyle()

            textField("用户名") {
                width = "100%"
                prefixComponent = VaadinIcon.USER.create()
                binder.forField(this)
                    .asRequired("用户名不能为空")
                    .withValidator(StringLengthValidator("用户名长度必须在2-20个字符之间", 2, 20))
                    .bind(UserDto::username.name)
            }

            passwordField("密码") {
                width = "100%"
                placeholder = if (user == null) "请输入密码" else "留空则不修改"
                prefixComponent = VaadinIcon.LOCK.create()
                binder.forField(this)
                    .withValidator({ it.isNullOrBlank() == (user != null) }, "新增用户时密码不能为空")
                    .withValidator({ it.isNullOrBlank() || it.length >= 6 }, "密码长度至少6个字符")
                    .bind(UserDto::password.name)
            }

            select<Int>("状态") {
                width = "100%"
                setItems(1, 0)
                setItemLabelGenerator { if (it == 1) "启用" else "禁用" }
                binder.forField(this).asRequired().bind(UserDto::status.name)
            }

            checkBoxGroup<Long>() {
                label = "分配角色"
                width = "100%"
                setItems(roles.map { it.id!! })
                setItemLabelGenerator { roleId -> roles.find { it.id == roleId }?.roleName ?: "" }
                binder.forField(this)
                    .withConverter({ it?.toList() ?: emptyList() }, { it?.toSet() ?: emptySet() })
                    .bind(UserDto::roleIds.name)
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
        if (!binder.validate().isOk) return

        val dto = UserDto(id = user?.id)
        binder.writeBean(dto)

        if (user == null) userService.saveUser(dto) else userService.updateUser(dto)

        showSuccess("保存成功")
        close()
        onSuccess()
    }
}
