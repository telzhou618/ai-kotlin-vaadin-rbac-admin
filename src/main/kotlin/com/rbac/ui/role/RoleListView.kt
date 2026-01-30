package com.rbac.ui.role

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.RoleQueryDto
import com.rbac.entity.SysRole
import com.rbac.service.SysRoleService
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

@Route("roles", layout = MainLayout::class)
@PageTitle("角色管理")
class RoleListView(
    private val roleService: SysRoleService
) : VerticalLayout() {
    
    private lateinit var searchField: TextField
    private lateinit var grid: Grid<SysRole>
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
                placeholder = "输入角色名称搜索"
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
        grid = Grid(SysRole::class.java, false).apply {
            addColumn { it.id }.setHeader("ID").width = "80px"
            addColumn { it.roleCode }.setHeader("角色编码")
            addColumn { it.roleName }.setHeader("角色名称")
            addColumn { it.roleDesc }.setHeader("角色描述")
            addComponentColumn { role ->
                horizontalLayout {
                    button("编辑") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL)
                        onLeftClick { showFormDialog(role) }
                    }
                    button("分配权限") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS)
                        onLeftClick { showAssignDialog(role) }
                    }
                    button("删除") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR)
                        onLeftClick { handleDelete(role.id!!) }
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
        val query = RoleQueryDto(roleName = searchField.value?.trim()?.takeIf { it.isNotBlank() })
        val pageData = roleService.pageQuery(Page(page, size.toLong()), query)
        
        grid.setItems(pageData.records)
        pagination.updatePagination(pageData.current, pageData.pages)
    }
    
    private fun showFormDialog(role: SysRole?) {
        RoleFormDialog(role, roleService) {
            loadData(1, 10)
        }.open()
    }
    
    private fun showAssignDialog(role: SysRole) {
        RoleAssignFormDialog(role, roleService, roleService.permissionService) {
            loadData(1, 10)
        }.open()
    }
    
    private fun handleDelete(id: Long) {
        showConfirmDialog("确定要删除该角色吗？") {
            roleService.deleteRole(id)
            NotificationUtil.showSuccess("删除成功")
            loadData(1, 10)
        }
    }
}
