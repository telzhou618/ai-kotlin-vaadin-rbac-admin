package com.rbac.ui

import com.github.mvysny.karibudsl.v10.*
import com.rbac.service.AuthService
import com.rbac.service.ThemeService
import com.rbac.ui.component.dialogContentStyle
import com.rbac.ui.component.loginContainerStyle
import com.rbac.ui.dashboard.DashboardView
import com.rbac.util.showError
import com.rbac.util.showSuccess
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

data class LoginForm(
    var username: String = "",
    var password: String = ""
)

@Route("login")
@AnonymousAllowed
class LoginView(
    private val authService: AuthService,
    private val themeService: ThemeService
) : VerticalLayout() {

    private lateinit var usernameField: TextField
    private lateinit var passwordField: PasswordField

    private val binder = Binder(LoginForm::class.java)

    init {
        themeService.initTheme()

        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER
        element.style.set("background", "var(--lumo-contrast-5pct)")

        verticalLayout {
            width = "480px"
            isPadding = false
            loginContainerStyle()

            horizontalLayout {
                width = "100%"
                content { align(center, middle) }
                h2("权限管理系统") {
                    element.style.apply {
                        set("color", "var(--lumo-header-text-color)")
                        set("font-size", "var(--lumo-font-size-xxl)")
                        set("font-weight", "600")
                    }
                }

                button {
                    addThemeVariants(
                        ButtonVariant.LUMO_TERTIARY,
                        ButtonVariant.LUMO_ICON
                    )
                    icon = if (themeService.isDarkTheme()) {
                        VaadinIcon.SUN_O.create()
                    } else {
                        VaadinIcon.MOON_O.create()
                    }
                    element.setAttribute("title", "切换主题")
                    onLeftClick {
                        themeService.toggleTheme()
                        icon = if (themeService.isDarkTheme()) {
                            VaadinIcon.SUN_O.create()
                        } else {
                            VaadinIcon.MOON_O.create()
                        }
                    }
                }
            }

            verticalLayout {
                dialogContentStyle()

                usernameField = textField() {
                    width = "100%"
                    placeholder = "请输入用户名"
                    prefixComponent = VaadinIcon.USER.create()
                }

                passwordField = passwordField() {
                    width = "100%"
                    placeholder = "请输入密码"
                    prefixComponent = VaadinIcon.LOCK.create()
                }

                button("登录") {
                    width = "100%"
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE)
                    icon = VaadinIcon.SIGN_IN.create()
                    onLeftClick { handleLogin() }
                }
            }
        }

        configureBinder()
        binder.readBean(LoginForm())
    }

    private fun configureBinder() {
        binder.forField(usernameField)
            .asRequired("用户名不能为空")
            .bind(LoginForm::username.name)

        binder.forField(passwordField)
            .asRequired("密码不能为空")
            .bind(LoginForm::password.name)

        binder.readBean(LoginForm())
    }

    private fun handleLogin() {
        if (binder.validate().isOk) {
            val form = LoginForm()
            binder.writeBean(form)

            if (authService.login(form.username, form.password)) {
                showSuccess("登录成功")
                UI.getCurrent().navigate(DashboardView::class.java)
            } else {
                showError("用户名或密码错误")
            }
        }
    }
}
