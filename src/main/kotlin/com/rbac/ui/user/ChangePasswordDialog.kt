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

data class ChangePasswordForm(
    var oldPassword: String = "",
    var newPassword: String = "",
    var confirmPassword: String = ""
)

class ChangePasswordDialog(
    private val userId: Long,
    private val userService: SysUserService,
    private val authService: AuthService,
    private val onSuccess: () -> Unit = {}
) : Dialog() {

    private lateinit var oldPasswordField: PasswordField
    private lateinit var newPasswordField: PasswordField
    private lateinit var confirmPasswordField: PasswordField

    private val binder = Binder(ChangePasswordForm::class.java)

    init {
        headerTitle = "修改密码"
        width = "450px"

        verticalLayout {
            dialogContentStyle()

            oldPasswordField = passwordField("原密码") {
                width = "100%"
                placeholder = "请输入原密码"
                prefixComponent = VaadinIcon.LOCK.create()
            }

            newPasswordField = passwordField("新密码") {
                width = "100%"
                placeholder = "请输入新密码"
                prefixComponent = VaadinIcon.KEY.create()
            }

            confirmPasswordField = passwordField("确认密码") {
                width = "100%"
                placeholder = "请再次输入新密码"
                prefixComponent = VaadinIcon.CHECK.create()
            }
        }

        configureBinder()
        binder.readBean(ChangePasswordForm())

        footer.add(
            button("取消") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                icon = VaadinIcon.CLOSE.create()
                onLeftClick { close() }
            },
            button("确定") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                icon = VaadinIcon.CHECK.create()
                onLeftClick { handleChangePassword() }
            }
        )
    }

    private fun configureBinder() {
        binder.forField(oldPasswordField)
            .asRequired("原密码不能为空")
            .bind(ChangePasswordForm::oldPassword.name)

        binder.forField(newPasswordField)
            .asRequired("新密码不能为空")
            .withValidator(StringLengthValidator("密码长度至少6个字符", 6, Int.MAX_VALUE))
            .bind(ChangePasswordForm::newPassword.name)

        binder.forField(confirmPasswordField)
            .asRequired("确认密码不能为空")
            .withValidator({ value ->
                value == newPasswordField.value
            }, "两次输入的密码不一致")
            .bind(ChangePasswordForm::confirmPassword.name)
    }

    private fun handleChangePassword() {
        if (binder.validate().isOk) {
            val form = ChangePasswordForm()
            binder.writeBean(form)

            try {
                userService.changePassword(userId, form.oldPassword, form.newPassword)
                showSuccess("密码修改成功，即将退出系统")
                close()
                
                // 延迟执行退出和跳转，让用户看到成功提示
                UI.getCurrent().page.executeJs(
                    "setTimeout(function() { window.location.reload(); }, 1000);"
                )
                
                // 执行退出
                authService.logout()
                UI.getCurrent().navigate(LoginView::class.java)
                
                onSuccess()
            } catch (e: Exception) {
                showError(e.message ?: "密码修改失败")
            }
        }
    }
}
