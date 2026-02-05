package com.rbac.ui.user

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.mvysny.karibudsl.v10.*
import com.rbac.annotation.RequiresPermissions
import com.rbac.dto.UserDto
import com.rbac.dto.UserQueryDto
import com.rbac.service.SysUserService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.PaginationComponent
import com.rbac.ui.component.showConfirmDialog
import com.rbac.util.NotifyUtil
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route("users", layout = MainLayout::class)
@PageTitle("用户管理")
@RequiresPermissions("system:user:view")  // 需要用户查看权限
class UserListView(
    private val userService: SysUserService
) : VerticalLayout() {

    private lateinit var searchField: TextField
    private lateinit var grid: Grid<UserDto>
    private lateinit var pagination: PaginationComponent

    init {
        setSizeFull()
        isPadding = true
        h4("用户管理")
        createToolbar()
        createGrid()
        createPagination()

        loadData(1, 20)
    }

    private fun createToolbar() {
        horizontalLayout {
            width = "100%"
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.END

            horizontalLayout {
                alignItems = FlexComponent.Alignment.END
                searchField = textField("搜索") {
                    placeholder = "输入用户名搜索"
                    width = "300px"
                }
                button("查询") {
                    icon = VaadinIcon.SEARCH.create()
                    onLeftClick { loadData(1, 20) }
                }
            }
            button("新增") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                icon = VaadinIcon.PLUS.create()
                onLeftClick { showFormDialog(null) }
            }
        }
    }

    private fun createGrid() {
        grid = Grid(UserDto::class.java, false).apply {
            addColumn { it.id }.setHeader("ID").width = "80px"
            addColumn { it.username }.setHeader("用户名")
            addColumn { it.roleNames }.setHeader("角色")

            // 状态列 - 带颜色高亮
            addComponentColumn { user ->
                val statusText = if (user.status == 1) "启用" else "禁用"
                val badge = span(statusText) {
                    element.themeList.add(if (user.status == 1) "badge success" else "badge error")
                    element.style.set("padding", "4px 8px")
                    element.style.set("border-radius", "4px")
                    element.style.set("font-size", "12px")
                    element.style.set("font-weight", "500")
                    if (user.status == 1) {
                        element.style.set("background-color", "#e7f5e9")
                        element.style.set("color", "#2e7d32")
                    } else {
                        element.style.set("background-color", "#fdecea")
                        element.style.set("color", "#d32f2f")
                    }
                }
                badge
            }.setHeader("状态").width = "100px"

            addComponentColumn { user ->
                horizontalLayout {
                    button("编辑") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL)
                        onLeftClick { showFormDialog(user) }
                    }

                    // 启用/禁用按钮
                    if (user.status == 1) {
                        button("禁用") {
                            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST)
                            onLeftClick { handleToggleStatus(user.id!!, 0) }
                        }
                    } else {
                        button("启用") {
                            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS)
                            onLeftClick { handleToggleStatus(user.id!!, 1) }
                        }
                    }

                    button("删除") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR)
                        onLeftClick { handleDelete(user.id!!) }
                    }
                }
            }.setHeader("操作").width = "280px"

            setSizeFull()
        }
        add(grid)
    }

    private fun createPagination() {
        pagination = PaginationComponent { page, size -> loadData(page, size) }
        add(pagination)
    }

    private fun loadData(page: Long, size: Int) {
        val query = UserQueryDto(username = searchField.value?.trim()?.takeIf { it.isNotBlank() })
        val pageData = userService.pageQuery(Page(page, size.toLong()), query)

        val userDtos = pageData.records.map { userService.getUserDto(it) }
        grid.setItems(userDtos)
        pagination.updatePagination(pageData.current, pageData.pages, pageData.total)
    }

    private fun showFormDialog(user: UserDto?) {
        UserFormDialog(user, userService, userService.roleService) {
            loadData(1, 20)
        }.open()
    }

    private fun handleDelete(id: Long) {
        showConfirmDialog("确定要删除该用户吗？") {
            userService.deleteUser(id)
            NotifyUtil.showSuccess("删除成功")
            loadData(1, 20)
        }
    }

    private fun handleToggleStatus(id: Long, newStatus: Int) {
        val action = if (newStatus == 1) "启用" else "禁用"
        showConfirmDialog("确定要${action}该用户吗？") {
            userService.toggleUserStatus(id, newStatus)
            NotifyUtil.showSuccess("${action}成功")
            loadData(pagination.currentPage, 10)
        }
    }
}
