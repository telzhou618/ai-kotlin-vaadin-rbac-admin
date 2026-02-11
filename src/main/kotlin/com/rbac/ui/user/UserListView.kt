package com.rbac.ui.user

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.mvysny.karibudsl.v10.*
import com.rbac.annotation.RequiresPermissions
import com.rbac.dto.UserDto
import com.rbac.dto.UserQueryDto
import com.rbac.service.SysUserService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.PaginationComponent
import com.rbac.ui.component.badgeStyle
import com.rbac.ui.component.showConfirmDialog
import com.rbac.ui.component.toolbarStyle
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
            isPadding = true
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.END
            toolbarStyle()
            
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
        grid = grid {
            setSizeFull()
            addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)

            columnFor(UserDto::id) {
                setHeader("ID")
                width = "80px"
                isSortable = true
            }
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
                    button("编辑") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL)
                        onLeftClick { showFormDialog(user) }
                    }

                    button(if (user.status == 1) "禁用" else "启用") {
                        addThemeVariants(
                            ButtonVariant.LUMO_SMALL,
                            if (user.status == 1) ButtonVariant.LUMO_CONTRAST else ButtonVariant.LUMO_SUCCESS
                        )
                        onLeftClick { handleToggleStatus(user.id!!, if (user.status == 1) 0 else 1) }
                    }

                    button("删除") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR)
                        onLeftClick { handleDelete(user.id!!) }
                    }
                }
            }.apply {
                setHeader("操作")
                width = "280px"
            }
        }
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
            showSuccess("删除成功")
            loadData(1, 20)
        }
    }

    private fun handleToggleStatus(id: Long, newStatus: Int) {
        val action = if (newStatus == 1) "启用" else "禁用"
        showConfirmDialog("确定要${action}该用户吗？") {
            userService.toggleUserStatus(id, newStatus)
            showSuccess("${action}成功")
            loadData(pagination.currentPage, 10)
        }
    }
}
