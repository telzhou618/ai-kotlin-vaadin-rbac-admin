package com.rbac.ui.user

import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.rbac.service.AuthService
import com.rbac.service.SysUserService
import com.rbac.ui.LoginView
import com.rbac.ui.component.dialogContentStyle
import com.rbac.util.showError
import com.rbac.util.showSuccess
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator

class ChangePasswordDialog(
    private val userId: Long,
    private val userService: SysUserService,
    private val authService: AuthService,
    private val onSuccess: () -> Unit = {}
) : Dialog() {

    data class Form(
        var oldPassword: String = "",
        var newPassword: String = "",
        var confirmPassword: String = ""
    )

    private val binder = Binder(Form::class.java)
    private lateinit var newPwdField: PasswordField

    init {
        headerTitle = "修改密码"
        width = "450px"

        verticalLayout {
            dialogContentStyle()

            passwordField("原密码") {
                width = "100%"
                prefixComponent = VaadinIcon.LOCK.create()
                binder.forField(this).asRequired("原密码不能为空").bind(Form::oldPassword.name)
            }

            newPwdField = passwordField("新密码") {
                width = "100%"
                prefixComponent = VaadinIcon.KEY.create()
                binder.forField(this)
                    .asRequired("新密码不能为空")
                    .withValidator(StringLengthValidator("密码长度至少6个字符", 6, Int.MAX_VALUE))
                    .bind(Form::newPassword.name)
            }

            passwordField("确认密码") {
                width = "100%"
                prefixComponent = VaadinIcon.CHECK.create()
                binder.forField(this)
                    .asRequired("确认密码不能为空")
                    .withValidator({ it == newPwdField.value }, "两次输入的密码不一致")
                    .bind(Form::confirmPassword.name)
            }
        }

        binder.readBean(Form())

        footer.add(
            button("取消", VaadinIcon.CLOSE.create()) {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                onLeftClick { close() }
            },
            button("确定", VaadinIcon.CHECK.create()) {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick { save() }
            }
        )
    }

    private fun save() {
        if (!binder.validate().isOk) return

        val form = Form()
        binder.writeBean(form)

        runCatching {
            userService.changePassword(userId, form.oldPassword, form.newPassword)
            showSuccess("密码修改成功，即将退出系统")
            close()

            UI.getCurrent().page.executeJs("setTimeout(function() { window.location.reload(); }, 1000);")
            authService.logout()
            UI.getCurrent().navigate(LoginView::class.java)
            onSuccess()
        }.onFailure {
            showError(it.message ?: "密码修改失败")
        }
    }
}
