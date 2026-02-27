package com.rbac.ui.role

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.PermissionDto
import com.rbac.entity.SysRole
import com.rbac.service.SysPermissionService
import com.rbac.service.SysRoleService
import com.rbac.util.showSuccess
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class RoleAssignFormDialog(
    private val role: SysRole,
    private val roleService: SysRoleService,
    private val permissionService: SysPermissionService,
    private val onSuccess: () -> Unit
) : Dialog() {

    private val selectedPermIds = mutableSetOf<Long>()
    private val checkboxMap = mutableMapOf<Long, Checkbox>()
    private val permissionMap = mutableMapOf<Long, PermissionDto>()
    private val allPermissions = mutableListOf<PermissionDto>()

    private lateinit var searchField: TextField
    private lateinit var contentLayout: VerticalLayout
    private var permTree: List<PermissionDto> = emptyList()

    init {
        headerTitle = "分配权限 - ${role.roleName}"
        width = "700px"
        height = "700px"

        val roleDto = roleService.getRoleDto(role)
        selectedPermIds.addAll(roleDto.permIds)

        permTree = permissionService.getPermissionTree()
        buildPermissionMap(permTree)
        collectAllPermissions(permTree)

        verticalLayout {
            setSizeFull()
            isPadding = false
            isSpacing = false

            add(createToolbar())

            contentLayout = verticalLayout {
                setSizeFull()
                isPadding = true
                element.style.set("overflow-y", "auto")
            }

            renderPermissionTree(contentLayout, permTree, 0)
        }

        footer.add(
            button("取消", VaadinIcon.CLOSE.create()) {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                onLeftClick { close() }
            },
            button("保存", VaadinIcon.CHECK.create()) {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick { save() }
            }
        )
    }

    private fun createToolbar() = horizontalLayout {
        width = "100%"
        isPadding = true
        isSpacing = true
        element.style.apply {
            set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
            set("background", "var(--lumo-contrast-5pct)")
        }

        searchField = textField {
            placeholder = "搜索权限名称或编码"
            width = "100%"
            isClearButtonVisible = true
            prefixComponent = VaadinIcon.SEARCH.create()

            addValueChangeListener { filterPermissions(it.value) }
        }
    }

    private fun filterPermissions(keyword: String?) {
        contentLayout.removeAll()
        checkboxMap.clear()

        if (keyword.isNullOrBlank()) {
            renderPermissionTree(contentLayout, permTree, 0)
            return
        }

        val filtered = allPermissions.filter {
            it.permName.contains(keyword, ignoreCase = true) ||
            it.permCode.contains(keyword, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            contentLayout.add(span("未找到匹配的权限") {
                element.style.apply {
                    set("color", "#999")
                    set("padding", "20px")
                }
            })
        } else {
            renderFilteredPermissions(contentLayout, filtered)
        }
    }

    private fun renderFilteredPermissions(container: VerticalLayout, perms: List<PermissionDto>) {
        perms.forEach { perm ->
            val checkbox = Checkbox("${perm.permName} (${perm.permCode})").apply {
                value = selectedPermIds.contains(perm.id)

                addValueChangeListener { event ->
                    if (event.isFromClient) {
                        if (event.value) selectedPermIds.add(perm.id!!)
                        else selectedPermIds.remove(perm.id)
                    }
                }
            }

            checkboxMap[perm.id!!] = checkbox
            container.add(checkbox)
        }
    }

    private fun collectAllPermissions(perms: List<PermissionDto>) {
        perms.forEach { perm ->
            allPermissions.add(perm)
            if (perm.children.isNotEmpty()) collectAllPermissions(perm.children)
        }
    }

    private fun buildPermissionMap(perms: List<PermissionDto>) {
        perms.forEach { perm ->
            permissionMap[perm.id!!] = perm
            if (perm.children.isNotEmpty()) buildPermissionMap(perm.children)
        }
    }

    private fun renderPermissionTree(container: VerticalLayout, perms: List<PermissionDto>, level: Int) {
        perms.forEach { perm ->
            val checkbox = Checkbox(perm.permName).apply {
                value = selectedPermIds.contains(perm.id)
                element.style.set("margin-left", "${level * 20}px")

                addValueChangeListener { event ->
                    if (event.isFromClient) handleCheckboxChange(perm, event.value)
                }
            }

            checkboxMap[perm.id!!] = checkbox
            container.add(checkbox)

            if (perm.children.isNotEmpty()) renderPermissionTree(container, perm.children, level + 1)
        }
    }

    private fun handleCheckboxChange(perm: PermissionDto, checked: Boolean) {
        if (checked) {
            selectedPermIds.add(perm.id!!)
            selectAllChildren(perm)
            selectAllParents(perm)
        } else {
            selectedPermIds.remove(perm.id)
            deselectAllChildren(perm)
            checkParentDeselection(perm)
        }
    }

    private fun selectAllChildren(perm: PermissionDto) {
        perm.children.forEach { child ->
            selectedPermIds.add(child.id!!)
            checkboxMap[child.id]?.value = true
            if (child.children.isNotEmpty()) selectAllChildren(child)
        }
    }

    private fun deselectAllChildren(perm: PermissionDto) {
        perm.children.forEach { child ->
            selectedPermIds.remove(child.id)
            checkboxMap[child.id]?.value = false
            if (child.children.isNotEmpty()) deselectAllChildren(child)
        }
    }

    private fun selectAllParents(perm: PermissionDto) {
        var parentId = perm.parentId
        while (parentId != 0L) {
            selectedPermIds.add(parentId)
            checkboxMap[parentId]?.value = true
            parentId = permissionMap[parentId]?.parentId ?: 0L
        }
    }

    private fun checkParentDeselection(perm: PermissionDto) {
        var parentId = perm.parentId
        while (parentId != 0L) {
            val parent = permissionMap[parentId] ?: break

            val hasSelectedChild = parent.children.any { child -> selectedPermIds.contains(child.id) }

            if (!hasSelectedChild) {
                selectedPermIds.remove(parentId)
                checkboxMap[parentId]?.value = false
                parentId = parent.parentId
            } else {
                break
            }
        }
    }

    private fun save() {
        roleService.assignPermissions(role.id!!, selectedPermIds.toList())
        showSuccess("分配权限成功")
        close()
        onSuccess()
    }
}
