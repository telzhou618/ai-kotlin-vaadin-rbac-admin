package com.rbac.ui

import cn.dev33.satoken.stp.StpUtil
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.route
import com.github.mvysny.karibudsl.v23.sideNav
import com.rbac.service.AuthService
import com.rbac.service.SysUserService
import com.rbac.service.ThemeService
import com.rbac.ui.component.showConfirmDialog
import com.rbac.ui.dashboard.DashboardView
import com.rbac.ui.log.OperationLogView
import com.rbac.ui.permission.PermissionTreeView
import com.rbac.ui.role.RoleListView
import com.rbac.ui.user.ChangePasswordDialog
import com.rbac.ui.user.UserListView
import com.rbac.util.showSuccess
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.menubar.MenuBar
import com.vaadin.flow.component.menubar.MenuBarVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.theme.lumo.LumoUtility
import java.util.*

class MainLayout(
    private val authService: AuthService,
    private val userService: SysUserService,
    private val themeService: ThemeService
) : AppLayout() {

    init {
        themeService.initTheme()
        primarySection = Section.NAVBAR
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
            element.style.apply {
                set("background", "var(--lumo-base-color)")
                set("box-shadow", "var(--lumo-box-shadow-xs)")
                set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
            }
            drawerToggle()
            span("权限管理系统") {
                addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD)
                element.style.set("color", "var(--lumo-primary-text-color)")
            }
            span {
                element.style.set("flex-grow", "1")
            }
            // 主题切换按钮
            button {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON)
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
            // 用户菜单
            add(createUserMenu(username))
        }

        addToNavbar(true, header)
    }

    private fun createUserMenu(username: String): Component {
        val menuBar = MenuBar().apply {
            addThemeVariants(MenuBarVariant.LUMO_TERTIARY)
        }

        // 创建用户菜单项，包含头像和用户名
        val userMenuItem = menuBar.addItem(
            HorizontalLayout().apply {
                alignItems = FlexComponent.Alignment.CENTER
                isSpacing = true
                element.style.set("gap", "var(--lumo-space-s)")
                avatar(username.uppercase(Locale.getDefault())) {
                    element.style.set("width", "32px")
                    element.style.set("height", "32px")
                }
                span(username) {
                    addClassNames(LumoUtility.FontSize.MEDIUM)
                }
                icon(VaadinIcon.CHEVRON_DOWN) {
                    element.style.set("width", "var(--lumo-icon-size-s)")
                    element.style.set("height", "var(--lumo-icon-size-s)")
                }
            }
        )

        val subMenu = userMenuItem.subMenu

        // 修改密码
        subMenu.addItem(
            createMenuItem(VaadinIcon.PASSWORD, "修改密码")
        ) { showChangePasswordDialog() }

        // 分隔线
        subMenu.add(Hr().apply {
            element.style.apply {
                set("margin", "var(--lumo-space-xs) 0")
                set("border", "none")
                set("border-top", "1px solid var(--lumo-contrast-10pct)")
            }
        })

        // 退出登录
        subMenu.addItem(
            createMenuItem(VaadinIcon.SIGN_OUT, "退出登录")
        ) { handleLogout() }

        return menuBar
    }

    private fun createMenuItem(icon: VaadinIcon, text: String): Component {
        return HorizontalLayout().apply {
            alignItems = FlexComponent.Alignment.CENTER
            isSpacing = true
            element.style.set("gap", "var(--lumo-space-s)")

            add(Icon(icon).apply {
                element.style.apply {
                    set("width", "var(--lumo-icon-size-s)")
                    set("height", "var(--lumo-icon-size-s)")
                }
            })

            add(Span(text))
        }
    }

    private fun createDrawer() {
        drawer {
            sideNav {
                route(DashboardView::class, VaadinIcon.DASHBOARD, "首页")
                if (StpUtil.hasPermission("system:user:view")) {
                    route(UserListView::class, VaadinIcon.USER, "用户管理")
                }
                if (StpUtil.hasPermission("system:role:view")) {
                    route(RoleListView::class, VaadinIcon.GROUP, "角色管理")
                }
                if (StpUtil.hasPermission("system:permission:view")) {
                    route(PermissionTreeView::class, VaadinIcon.LOCK, "权限管理")
                }
                if (StpUtil.hasPermission("system:log:view")) {
                    route(OperationLogView::class, VaadinIcon.RECORDS, "操作日志")
                }
            }
        }
    }

    private fun handleLogout() {
        showConfirmDialog("确定要退出登录吗？") {
            authService.logout()
            showSuccess("退出成功")
            UI.getCurrent().navigate(LoginView::class.java)
        }
    }

    private fun showChangePasswordDialog() {
        val userId = StpUtil.getLoginIdAsLong()
        ChangePasswordDialog(userId, userService, authService).open()
    }
}
