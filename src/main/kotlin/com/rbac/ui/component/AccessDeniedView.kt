package com.rbac.ui.component

import com.github.mvysny.karibudsl.v10.*
import com.rbac.service.ThemeService
import com.rbac.ui.LoginView
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

@Route("access-denied")
@PageTitle("访问被拒绝")
@AnonymousAllowed
class AccessDeniedView(
    private val themeService: ThemeService
) : VerticalLayout() {
    
    init {
        // 初始化主题
        themeService.initTheme()
        
        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER
        
        verticalLayout {
            alignItems = FlexComponent.Alignment.CENTER
            
            icon(VaadinIcon.BAN) {
                element.style.set("width", "100px")
                element.style.set("height", "100px")
                element.style.set("color", "var(--lumo-error-color)")
            }
            
            add(H1("403 - 访问被拒绝"))
            
            add(Paragraph("抱歉，您没有权限访问此页面"))
            
            button("返回首页") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                icon = VaadinIcon.HOME.create()
                onLeftClick {
                    UI.getCurrent().navigate("")
                }
            }
            
            button("返回登录") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                icon = VaadinIcon.SIGN_IN.create()
                onLeftClick {
                    UI.getCurrent().navigate(LoginView::class.java)
                }
            }
        }
    }
}
