package com.rbac.ui

import cn.dev33.satoken.stp.StpUtil
import com.github.mvysny.karibudsl.v10.*
import com.rbac.service.AuthService
import com.rbac.service.SysUserService
import com.rbac.service.ThemeService
import com.rbac.util.NotifyUtil
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
import com.vaadin.flow.theme.lumo.LumoUtility

class MainLayout(
    private val authService: AuthService,
    private val userService: SysUserService,
    private val themeService: ThemeService
) : AppLayout() {
    
    init {
        // 初始化主题
        themeService.initTheme()
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
            alignItems = FlexComponent.Alignment.CENTER
            
            add(DrawerToggle())
            
            add(Span("权限管理系统").apply {
                addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD)
            })
            
            add(Span().apply {
                element.style.set("flex-grow", "1")
            })
            
            add(Span("欢迎你, $username"))
            
            // 主题切换按钮
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
            
            button("退出") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                icon = VaadinIcon.SIGN_OUT.create()
                onLeftClick { handleLogout() }
            }
        }
        
        addToNavbar(true, header)
    }
    
    private fun createDrawer() {
        val nav = SideNav()
        
        // 首页 - 所有登录用户都可以访问
        nav.addItem(SideNavItem("首页", DashboardView::class.java, VaadinIcon.DASHBOARD.create()))
        
        // 用户管理 - 需要 system:user:view 权限
        if (StpUtil.hasPermission("system:user:view")) {
            nav.addItem(SideNavItem("用户管理", UserListView::class.java, VaadinIcon.USER.create()))
        }
        
        // 角色管理 - 需要 system:role:view 权限
        if (StpUtil.hasPermission("system:role:view")) {
            nav.addItem(SideNavItem("角色管理", RoleListView::class.java, VaadinIcon.GROUP.create()))
        }
        
        // 权限管理 - 需要 system:permission:view 权限
        if (StpUtil.hasPermission("system:permission:view")) {
            nav.addItem(SideNavItem("权限管理", PermissionTreeView::class.java, VaadinIcon.LOCK.create()))
        }
        
        // 操作日志 - 需要 system:log:view 权限
        if (StpUtil.hasPermission("system:log:view")) {
            nav.addItem(SideNavItem("操作日志", OperationLogView::class.java, VaadinIcon.RECORDS.create()))
        }
        
        addToDrawer(nav)
    }
    
    private fun handleLogout() {
        authService.logout()
        NotifyUtil.showSuccess("退出成功")
        UI.getCurrent().navigate(LoginView::class.java)
    }
}
