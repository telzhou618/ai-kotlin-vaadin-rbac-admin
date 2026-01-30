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
import com.rbac.util.NotificationUtil
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
        
        createToolbar()
        createGrid()
        createPagination()
        
        loadData(1, 10)
    }
    
    private fun createToolbar() {
        horizontalLayout {
            width = "100%"
            setAlignItems(FlexComponent.Alignment.END)
            
            searchField = textField("搜索") {
                placeholder = "输入用户名搜索"
                width = "300px"
            }
            
            button("查询") {
                icon = VaadinIcon.SEARCH.create()
                onLeftClick { loadData(1, 10) }
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
            addColumn { user -> if (user.status == 1) "启用" else "禁用" }.setHeader("状态")
            addComponentColumn { user ->
                horizontalLayout {
                    button("编辑") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL)
                        onLeftClick { showFormDialog(user) }
                    }
                    button("删除") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR)
                        onLeftClick { handleDelete(user.id!!) }
                    }
                }
            }.setHeader("操作")
            
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
        pagination.updatePagination(pageData.current, pageData.pages)
    }
    
    private fun showFormDialog(user: UserDto?) {
        UserFormDialog(user, userService, userService.roleService) {
            loadData(1, 10)
        }.open()
    }
    
    private fun handleDelete(id: Long) {
        showConfirmDialog("确定要删除该用户吗？") {
            userService.deleteUser(id)
            NotificationUtil.showSuccess("删除成功")
            loadData(1, 10)
        }
    }
}
