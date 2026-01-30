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
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
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
        
        // 创建主布局
        val mainLayout = VerticalLayout().apply {
            setSizeFull()
            isPadding = false
            isSpacing = false
        }
        
        // 创建工具栏
        mainLayout.add(createToolbar())
        
        // 创建内容区域
        contentLayout = VerticalLayout().apply {
            setSizeFull()
            isPadding = true
            element.style.set("overflow-y", "auto")
        }
        
        renderPermissionTree(contentLayout, permTree, 0)
        mainLayout.add(contentLayout)
        
        add(mainLayout)
        
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
    
    /**
     * 创建工具栏
     */
    private fun createToolbar(): HorizontalLayout {
        return horizontalLayout {
            width = "100%"
            isPadding = true
            isSpacing = true
            element.style.set("border-bottom", "1px solid #e0e0e0")
            
            // 搜索框
            searchField = textField {
                placeholder = "搜索权限名称或编码"
                width = "100%"
                isClearButtonVisible = true
                prefixComponent = VaadinIcon.SEARCH.create()
                
                addValueChangeListener {
                    filterPermissions(it.value)
                }
            }
        }
    }
    
    /**
     * 搜索过滤权限
     */
    private fun filterPermissions(keyword: String?) {
        contentLayout.removeAll()
        checkboxMap.clear()
        
        if (keyword.isNullOrBlank()) {
            // 无搜索关键词，显示完整树
            renderPermissionTree(contentLayout, permTree, 0)
        } else {
            // 有搜索关键词，只显示匹配的权限
            val filteredPerms = allPermissions.filter { perm ->
                perm.permName.contains(keyword, ignoreCase = true) ||
                perm.permCode.contains(keyword, ignoreCase = true)
            }
            
            if (filteredPerms.isEmpty()) {
                contentLayout.add(span("未找到匹配的权限") {
                    element.style.set("color", "#999")
                    element.style.set("padding", "20px")
                })
            } else {
                renderFilteredPermissions(contentLayout, filteredPerms)
            }
        }
    }
    
    /**
     * 渲染过滤后的权限列表（扁平化显示）
     */
    private fun renderFilteredPermissions(container: VerticalLayout, perms: List<PermissionDto>) {
        perms.forEach { perm ->
            val checkbox = Checkbox("${perm.permName} (${perm.permCode})").apply {
                value = selectedPermIds.contains(perm.id)
                
                addValueChangeListener { event ->
                    if (event.isFromClient) {
                        if (event.value) {
                            selectedPermIds.add(perm.id!!)
                        } else {
                            selectedPermIds.remove(perm.id)
                        }
                    }
                }
            }
            
            checkboxMap[perm.id!!] = checkbox
            container.add(checkbox)
        }
    }
    
    /**
     * 收集所有权限（扁平化）
     */
    private fun collectAllPermissions(perms: List<PermissionDto>) {
        perms.forEach { perm ->
            allPermissions.add(perm)
            if (perm.children.isNotEmpty()) {
                collectAllPermissions(perm.children)
            }
        }
    }
    
    /**
     * 构建权限映射表，方便查找父子关系
     */
    private fun buildPermissionMap(perms: List<PermissionDto>) {
        perms.forEach { perm ->
            permissionMap[perm.id!!] = perm
            if (perm.children.isNotEmpty()) {
                buildPermissionMap(perm.children)
            }
        }
    }
    
    /**
     * 渲染权限树
     */
    private fun renderPermissionTree(container: VerticalLayout, perms: List<PermissionDto>, level: Int) {
        perms.forEach { perm ->
            val checkbox = Checkbox(perm.permName).apply {
                value = selectedPermIds.contains(perm.id)
                element.style.set("margin-left", "${level * 20}px")
                
                addValueChangeListener { event ->
                    // 防止递归触发
                    if (event.isFromClient) {
                        handleCheckboxChange(perm, event.value)
                    }
                }
            }
            
            checkboxMap[perm.id!!] = checkbox
            container.add(checkbox)
            
            if (perm.children.isNotEmpty()) {
                renderPermissionTree(container, perm.children, level + 1)
            }
        }
    }
    
    /**
     * 处理复选框变化，实现父子联动
     */
    private fun handleCheckboxChange(perm: PermissionDto, checked: Boolean) {
        if (checked) {
            // 选中：添加当前权限
            selectedPermIds.add(perm.id!!)
            
            // 选中所有子权限
            selectAllChildren(perm)
            
            // 选中所有父权限
            selectAllParents(perm)
        } else {
            // 取消选中：移除当前权限
            selectedPermIds.remove(perm.id)
            
            // 取消选中所有子权限
            deselectAllChildren(perm)
            
            // 检查父权限是否需要取消选中
            checkParentDeselection(perm)
        }
    }
    
    /**
     * 选中所有子权限
     */
    private fun selectAllChildren(perm: PermissionDto) {
        perm.children.forEach { child ->
            selectedPermIds.add(child.id!!)
            checkboxMap[child.id]?.value = true
            
            if (child.children.isNotEmpty()) {
                selectAllChildren(child)
            }
        }
    }
    
    /**
     * 取消选中所有子权限
     */
    private fun deselectAllChildren(perm: PermissionDto) {
        perm.children.forEach { child ->
            selectedPermIds.remove(child.id)
            checkboxMap[child.id]?.value = false
            
            if (child.children.isNotEmpty()) {
                deselectAllChildren(child)
            }
        }
    }
    
    /**
     * 选中所有父权限
     */
    private fun selectAllParents(perm: PermissionDto) {
        var parentId = perm.parentId
        while (parentId != 0L) {
            selectedPermIds.add(parentId)
            checkboxMap[parentId]?.value = true
            
            val parent = permissionMap[parentId]
            parentId = parent?.parentId ?: 0L
        }
    }
    
    /**
     * 检查父权限是否需要取消选中
     * 只有当父权限的所有子权限都未选中时，才取消选中父权限
     */
    private fun checkParentDeselection(perm: PermissionDto) {
        var parentId = perm.parentId
        while (parentId != 0L) {
            val parent = permissionMap[parentId] ?: break
            
            // 检查父权限的所有子权限是否都未选中
            val hasSelectedChild = parent.children.any { child ->
                selectedPermIds.contains(child.id)
            }
            
            if (!hasSelectedChild) {
                // 所有子权限都未选中，取消选中父权限
                selectedPermIds.remove(parentId)
                checkboxMap[parentId]?.value = false
                parentId = parent.parentId
            } else {
                // 还有子权限被选中，停止向上检查
                break
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
