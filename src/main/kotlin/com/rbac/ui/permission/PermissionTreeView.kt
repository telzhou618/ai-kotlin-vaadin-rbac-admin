package com.rbac.ui.permission

import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.h4
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.rbac.annotation.RequiresPermissions
import com.rbac.dto.PermissionDto
import com.rbac.service.SysPermissionService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.*
import com.rbac.util.showSuccess
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route("permissions", layout = MainLayout::class)
@PageTitle("权限管理")
@RequiresPermissions("system:permission:view")
class PermissionTreeView(
    private val permissionService: SysPermissionService
) : VerticalLayout() {

    private lateinit var treeGrid: TreeGrid<PermissionDto>

    init {
        pageContainerStyle()
        createToolbar()
        createTreeGrid()
        refresh()
    }

    private fun createToolbar() {
        horizontalLayout {
            width = "100%"
            isPadding = true
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
            toolbarStyle()

            h4("权限管理") { pageTitleStyle() }

            horizontalLayout {
                searchAreaStyle()
                button("刷新", VaadinIcon.REFRESH.create()) {
                    onLeftClick { refresh() }
                }
                button("新增根权限", VaadinIcon.PLUS.create()) {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    onLeftClick { showFormDialog(null, 0) }
                }
            }
        }
    }

    private fun createTreeGrid() {
        treeGrid = TreeGrid<PermissionDto>().apply {
            applyStandardStyle()
            addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)

            addHierarchyColumn { it.permName }.setHeader("权限名称")
            addColumn { it.permCode }.setHeader("权限编码")

            addComponentColumn { perm ->
                horizontalLayout {
                    isSpacing = true
                    element.style.set("gap", "var(--lumo-space-xs)")

                    button("新增子权限") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS)
                        icon = VaadinIcon.PLUS_CIRCLE.create()
                        onLeftClick { showFormDialog(null, perm.id!!) }
                    }
                    button("编辑") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY)
                        icon = VaadinIcon.EDIT.create()
                        onLeftClick { showFormDialog(perm, perm.parentId) }
                    }
                    button("删除") {
                        addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR)
                        icon = VaadinIcon.TRASH.create()
                        onLeftClick { delete(perm.id!!) }
                    }
                }
            }.setHeader("操作")
        }
        add(treeGrid)
    }

    private fun refresh() {
        val tree = permissionService.getPermissionTree()
        treeGrid.setItems(tree) { it.children }
        treeGrid.expandRecursively(tree, 2)
    }

    private fun showFormDialog(perm: PermissionDto?, parentId: Long) {
        PermissionFormDialog(perm, parentId, permissionService) {
            refresh()
        }.open()
    }

    private fun delete(id: Long) {
        showConfirmDialog("确定要删除该权限吗？") {
            permissionService.deletePerm(id)
            showSuccess("删除成功")
            refresh()
        }
    }
}
