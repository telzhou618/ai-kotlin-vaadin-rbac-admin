package com.rbac.ui.view

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.PermissionDto
import com.rbac.exception.GlobalExceptionHandler
import com.rbac.service.SysPermissionService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.showConfirmDialog
import com.rbac.ui.dialog.PermissionFormDialog
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route("permissions", layout = MainLayout::class)
@PageTitle("权限管理")
class PermissionTreeView(
    private val permissionService: SysPermissionService,
    private val exceptionHandler: GlobalExceptionHandler
) : VerticalLayout() {
    
    private lateinit var treeGrid: TreeGrid<PermissionDto>
    
    init {
        setSizeFull()
        isPadding = true
        
        createToolbar()
        createTreeGrid()
        
        loadData()
    }
    
    private fun createToolbar() {
        horizontalLayout {
            width = "100%"
            setAlignItems(FlexComponent.Alignment.END)
            
            button("新增根权限") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                icon = VaadinIcon.PLUS.create()
                onLeftClick { showFormDialog(null, 0) }
            }
            
            button("刷新") {
                icon = VaadinIcon.REFRESH.create()
                onLeftClick { loadData() }
            }
        }
    }
    
    private fun createTreeGrid() {
        treeGrid = TreeGrid<PermissionDto>().apply {
            addHierarchyColumn { it.permName }.setHeader("权限名称")
            addColumn { it.permCode }.setHeader("权限编码")
            addComponentColumn { perm ->
                horizontalLayout {
                    button("新增子权限") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS)
                        onLeftClick { showFormDialog(null, perm.id!!) }
                    }
                    button("编辑") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL)
                        onLeftClick { showFormDialog(perm, perm.parentId) }
                    }
                    button("删除") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR)
                        onLeftClick { handleDelete(perm.id!!) }
                    }
                }
            }.setHeader("操作")
            
            setSizeFull()
        }
        add(treeGrid)
    }
    
    private fun loadData() {
        try {
            val tree = permissionService.getPermissionTree()
            treeGrid.setItems(tree) { it.children }
            treeGrid.expandRecursively(tree, 2)
        } catch (e: Exception) {
            exceptionHandler.handle(e)
        }
    }
    
    private fun showFormDialog(perm: PermissionDto?, parentId: Long) {
        PermissionFormDialog(perm, parentId, permissionService, exceptionHandler) {
            loadData()
        }.open()
    }
    
    private fun handleDelete(id: Long) {
        showConfirmDialog("确定要删除该权限吗？") {
            try {
                permissionService.deletePerm(id)
                exceptionHandler.showSuccess("删除成功")
                loadData()
            } catch (e: Exception) {
                exceptionHandler.handle(e)
            }
        }
    }
}
