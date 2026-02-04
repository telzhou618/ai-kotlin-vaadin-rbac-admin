package com.rbac.ui

import com.github.mvysny.karibudsl.v10.*
import com.rbac.service.AuthService
import com.rbac.service.ThemeService
import com.rbac.ui.dashboard.DashboardView
import com.rbac.util.NotifyUtil
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
        // 初始化主题
        themeService.initTheme()
        
        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER
        
        verticalLayout {
            width = "400px"
            isPadding = true
            
            // 标题和主题切换按钮
            horizontalLayout {
                width = "100%"
                justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
                alignItems = FlexComponent.Alignment.CENTER
                
                h2("权限管理系统")
                
                button {
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    icon = if (themeService.isDarkTheme()) {
                        VaadinIcon.SUN_O.create()
                    } else {
                        VaadinIcon.MOON_O.create()
                    }
                    element.setAttribute("title", "切换主题")
                    onLeftClick { 
                        themeService.toggleTheme()
                        // 刷新图标
                        icon = if (themeService.isDarkTheme()) {
                            VaadinIcon.SUN_O.create()
                        } else {
                            VaadinIcon.MOON_O.create()
                        }
                    }
                }
            }
            
            usernameField = textField("用户名") {
                width = "100%"
                placeholder = "请输入用户名"
            }
            
            passwordField = passwordField("密码") {
                width = "100%"
                placeholder = "请输入密码"
            }
            
            button("登录") {
                width = "100%"
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick { handleLogin() }
            }
        }
        
        // 配置 Binder 验证规则
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
                NotifyUtil.showSuccess("登录成功")
                UI.getCurrent().navigate(DashboardView::class.java)
            } else {
                NotifyUtil.showError("用户名或密码错误")
            }
        }
    }
}
