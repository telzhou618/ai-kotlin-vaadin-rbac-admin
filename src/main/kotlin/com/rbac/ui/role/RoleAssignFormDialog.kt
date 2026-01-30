package com.rbac.ui.role

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.PermissionDto
import com.rbac.entity.SysRole
import com.rbac.service.SysPermissionService
import com.rbac.service.SysRoleService
import com.rbac.util.NotificationUtil
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class RoleAssignFormDialog(
    private val role: SysRole,
    private val roleService: SysRoleService,
    private val permissionService: SysPermissionService,
    private val onSuccess: () -> Unit
) : Dialog() {
    
    private val selectedPermIds = mutableSetOf<Long>()
    
    init {
        headerTitle = "分配权限 - ${role.roleName}"
        width = "600px"
        height = "600px"
        
        val roleDto = roleService.getRoleDto(role)
        selectedPermIds.addAll(roleDto.permIds)
        
        val content = VerticalLayout().apply {
            setSizeFull()
            isPadding = true
        }
        
        val permTree = permissionService.getPermissionTree()
        renderPermissionTree(content, permTree, 0)
        
        add(content)
        
        footer.add(
            button("取消") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                onLeftClick { close() }
            },
            button("保存") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                onLeftClick { handleSave() }
            }
        )
    }
    
    private fun renderPermissionTree(container: VerticalLayout, perms: List<PermissionDto>, level: Int) {
        perms.forEach { perm ->
            val checkbox = Checkbox(perm.permName).apply {
                value = selectedPermIds.contains(perm.id)
                element.style.set("margin-left", "${level * 20}px")
                addValueChangeListener { event ->
                    if (event.value) {
                        selectedPermIds.add(perm.id!!)
                    } else {
                        selectedPermIds.remove(perm.id)
                    }
                }
            }
            container.add(checkbox)
            
            if (perm.children.isNotEmpty()) {
                renderPermissionTree(container, perm.children, level + 1)
            }
        }
    }
    
    private fun handleSave() {
        roleService.assignPermissions(role.id!!, selectedPermIds.toList())
        NotificationUtil.showSuccess("分配权限成功")
        close()
        onSuccess()
    }
}
