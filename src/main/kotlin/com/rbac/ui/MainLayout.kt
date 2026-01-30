package com.rbac.ui

import cn.dev33.satoken.stp.StpUtil
import com.github.mvysny.karibudsl.v10.*
import com.rbac.service.AuthService
import com.rbac.service.SysUserService
import com.rbac.util.NotificationUtil
import com.rbac.ui.dashboard.DashboardView
import com.rbac.ui.log.OperationLogView
import com.rbac.ui.permission.PermissionTreeView
import com.rbac.ui.role.RoleListView
import com.rbac.ui.user.UserListView
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility

@Route("")
class MainLayout(
    private val authService: AuthService,
    private val userService: SysUserService
) : AppLayout() {
    
    init {
        createHeader()
        createDrawer()
    }
    
    private fun createHeader() {
        val userId = StpUtil.getLoginIdAsLong()
        val user = userService.getById(userId)
        val username = user?.username ?: "未知用户"
        
        val header = HorizontalLayout().apply {
            width = "100%"
            isPadding = true
            setAlignItems(FlexComponent.Alignment.CENTER)
            
            add(DrawerToggle())
            
            add(Span("权限管理系统").apply {
                addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD)
            })
            
            add(Span().apply {
                element.style.set("flex-grow", "1")
            })
            
            add(Span("欢迎你, $username"))
            
            button("退出") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                icon = VaadinIcon.SIGN_OUT.create()
                onLeftClick { handleLogout() }
            }
        }
        
        addToNavbar(true, header)
    }
    
    private fun createDrawer() {
        val nav = SideNav().apply {
            addItem(SideNavItem("首页", DashboardView::class.java, VaadinIcon.DASHBOARD.create()))
            addItem(SideNavItem("用户管理", UserListView::class.java, VaadinIcon.USER.create()))
            addItem(SideNavItem("角色管理", RoleListView::class.java, VaadinIcon.GROUP.create()))
            addItem(SideNavItem("权限管理", PermissionTreeView::class.java, VaadinIcon.LOCK.create()))
            addItem(SideNavItem("操作日志", OperationLogView::class.java, VaadinIcon.RECORDS.create()))
        }
        
        addToDrawer(nav)
    }
    
    private fun handleLogout() {
        authService.logout()
        NotificationUtil.showSuccess("退出成功")
        UI.getCurrent().navigate(LoginView::class.java)
    }
}
