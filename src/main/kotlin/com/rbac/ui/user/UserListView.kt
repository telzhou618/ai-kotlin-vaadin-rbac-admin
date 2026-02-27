package com.rbac.ui.user

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.mvysny.karibudsl.v10.*
import com.rbac.annotation.RequiresPermissions
import com.rbac.dto.UserDto
import com.rbac.dto.UserQueryDto
import com.rbac.service.SysUserService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.*
import com.rbac.util.showSuccess
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route("users", layout = MainLayout::class)
@PageTitle("用户管理")
@RequiresPermissions("system:user:view")
class UserListView(
    private val userService: SysUserService
) : VerticalLayout() {

    private val pageSize = 20
    private lateinit var searchField: TextField
    private lateinit var grid: Grid<UserDto>
    private lateinit var pagination: PaginationComponent

    init {
        pageContainerStyle()
        createToolbar()
        createGrid()
        createPagination()
        refresh()
    }

    private fun createToolbar() {
        horizontalLayout {
            width = "100%"
            isPadding = true
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
            toolbarStyle()

            h4("用户管理") { pageTitleStyle() }

            horizontalLayout {
                searchAreaStyle()
                searchField = textField {
                    placeholder = "输入用户名搜索"
                    width = "250px"
                    isClearButtonVisible = true
                }
                button("查询", VaadinIcon.SEARCH.create()) {
                    onLeftClick { refresh() }
                }
                button("新增", VaadinIcon.PLUS.create()) {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    onLeftClick { showFormDialog(null) }
                }
            }
        }
    }

    private fun createGrid() {
        grid = grid {
            applyStandardStyle()
            addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)

            columnFor(UserDto::id) { setHeader("ID"); width = "80px"; isSortable = true }
            columnFor(UserDto::username) { setHeader("用户名") }
            columnFor(UserDto::roleNames) { setHeader("角色") }

            addComponentColumn { user ->
                span(if (user.status == 1) "启用" else "禁用") {
                    badgeStyle(user.status == 1)
                }
            }.apply {
                setHeader("状态")
                width = "100px"
            }

            addComponentColumn { user ->
                horizontalLayout {
                    isSpacing = true
                    element.style.set("gap", "var(--lumo-space-xs)")

                    button("编辑") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY)
                        icon = VaadinIcon.EDIT.create()
                        onLeftClick { showFormDialog(user) }
                    }

                    val enable = user.status == 1
                    button(if (enable) "禁用" else "启用") {
                        addThemeVariants(
                            ButtonVariant.LUMO_SMALL,
                            if (enable) ButtonVariant.LUMO_CONTRAST else ButtonVariant.LUMO_SUCCESS
                        )
                        icon = if (enable) VaadinIcon.BAN.create() else VaadinIcon.CHECK_CIRCLE.create()
                        onLeftClick {
                            toggleStatus(user.id!!, if (enable) 0 else 1)
                        }
                    }

                    button("删除") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR)
                        icon = VaadinIcon.TRASH.create()
                        onLeftClick {
                            delete(user.id!!)
                        }
                    }
                }
            }.apply { setHeader("操作"); width = "280px" }
        }
    }

    private fun createPagination() {
        pagination = PaginationComponent { refresh(it) }
        add(pagination)
    }

    private fun refresh(page: Long = 1) {
        val query = UserQueryDto(username = searchField.value?.trim()?.takeIf { it.isNotBlank() })
        val pageData = userService.pageQuery(Page(page, pageSize.toLong()), query)

        grid.setItems(pageData.records.map { userService.getUserDto(it) })
        pagination.update(pageData.current, pageData.pages, pageData.total)
    }

    private fun showFormDialog(user: UserDto?) {
        UserFormDialog(user, userService, userService.roleService) { refresh() }.open()
    }

    private fun delete(id: Long) {
        showConfirmDialog("确定要删除该用户吗？") {
            userService.deleteUser(id)
            showSuccess("删除成功")
            refresh()
        }
    }

    private fun toggleStatus(id: Long, newStatus: Int) {
        val action = if (newStatus == 1) "启用" else "禁用"
        showConfirmDialog("确定要${action}该用户吗？") {
            userService.toggleUserStatus(id, newStatus)
            showSuccess("${action}成功")
            refresh(pagination.page)
        }
    }
}
