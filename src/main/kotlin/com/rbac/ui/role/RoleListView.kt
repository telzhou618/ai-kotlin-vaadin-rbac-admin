package com.rbac.ui.role

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.mvysny.karibudsl.v10.*
import com.rbac.annotation.RequiresPermissions
import com.rbac.dto.RoleQueryDto
import com.rbac.entity.SysRole
import com.rbac.service.SysRoleService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.PaginationComponent
import com.rbac.ui.component.showConfirmDialog
import com.rbac.ui.component.toolbarStyle
import com.rbac.util.NotifyUtil
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route("roles", layout = MainLayout::class)
@PageTitle("角色管理")
@RequiresPermissions("system:role:view")
class RoleListView(
    private val roleService: SysRoleService
) : VerticalLayout() {

    private lateinit var searchField: TextField
    private lateinit var grid: Grid<SysRole>
    private lateinit var pagination: PaginationComponent

    init {
        setSizeFull()
        isPadding = true
        
        h4("角色管理")
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
                    placeholder = "输入角色名称搜索"
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
            setSelectionMode(Grid.SelectionMode.MULTI)
            addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
            
            columnFor(SysRole::id) {
                setHeader("ID")
                width = "80px"
                isSortable = true
            }
            columnFor(SysRole::roleCode) { setHeader("角色编码") }
            columnFor(SysRole::roleName) { setHeader("角色名称") }
            columnFor(SysRole::roleDesc) { setHeader("角色描述") }
            
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
        }
    }

    private fun createPagination() {
        pagination = PaginationComponent { page, size -> loadData(page, size) }
    }

    private fun loadData(page: Long, size: Int) {
        val query = RoleQueryDto(roleName = searchField.value?.trim()?.takeIf { it.isNotBlank() })
        val pageData = roleService.pageQuery(Page(page, size.toLong()), query)

        grid.setItems(pageData.records)
        pagination.updatePagination(pageData.current, pageData.pages, pageData.total)
    }

    private fun showFormDialog(role: SysRole?) {
        RoleFormDialog(role, roleService) {
            loadData(1, 20)
        }.open()
    }

    private fun showAssignDialog(role: SysRole) {
        RoleAssignFormDialog(role, roleService, roleService.permissionService) {
            loadData(1, 20)
        }.open()
    }

    private fun handleDelete(id: Long) {
        showConfirmDialog("确定要删除该角色吗？") {
            roleService.deleteRole(id)
            NotifyUtil.showSuccess("删除成功")
            loadData(1, 20)
        }
    }
}
