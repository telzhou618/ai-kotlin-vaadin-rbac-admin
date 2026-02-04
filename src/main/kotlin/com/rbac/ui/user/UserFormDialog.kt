package com.rbac.ui.user

import com.github.mvysny.karibudsl.v10.*
import com.rbac.dto.UserDto
import com.rbac.service.SysRoleService
import com.rbac.service.SysUserService
import com.rbac.util.NotifyUtil
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.StringLengthValidator

class UserFormDialog(
    private val user: UserDto?,
    private val userService: SysUserService,
    private val roleService: SysRoleService,
    private val onSuccess: () -> Unit
) : Dialog() {
    
    private lateinit var usernameField: TextField
    private lateinit var passwordField: PasswordField
    private lateinit var statusSelect: Select<Int>
    private lateinit var roleCheckbox: CheckboxGroup<Long>
    
    private val binder = Binder(UserDto::class.java)
    
    init {
        headerTitle = if (user == null) "新增用户" else "编辑用户"
        width = "500px"
        
        val roles = roleService.list()
        val dto = user ?: UserDto(status = 1, roleIds = emptyList())
        
        verticalLayout {
            usernameField = textField("用户名") {
                width = "100%"
            }
            
            passwordField = passwordField("密码") {
                width = "100%"
                placeholder = if (user == null) "请输入密码" else "留空则不修改"
            }
            
            statusSelect = select<Int>("状态") {
                width = "100%"
                setItems(1, 0)
                setItemLabelGenerator { if (it == 1) "启用" else "禁用" }
            }
            
            val roleCheckboxGroup = CheckboxGroup<Long>()
            roleCheckboxGroup.label = "分配角色"
            roleCheckboxGroup.width = "100%"
            roleCheckboxGroup.setItems(roles.map { it.id!! })
            roleCheckboxGroup.setItemLabelGenerator { roleId ->
                roles.find { it.id == roleId }?.roleName ?: ""
            }
            roleCheckbox = roleCheckboxGroup
            add(roleCheckboxGroup)
        }
        
        // 配置 Binder 验证规则
        binder.forField(usernameField)
            .asRequired("用户名不能为空")
            .withValidator(StringLengthValidator("用户名长度必须在2-20个字符之间", 2, 20))
            .bind(UserDto::username.name)
        
        binder.forField(passwordField)
            .withValidator({ value ->
                if (user == null) {
                    !value.isNullOrBlank()
                } else {
                    true
                }
            }, "新增用户时密码不能为空")
            .withValidator({ value ->
                if (!value.isNullOrBlank()) {
                    value.length >= 6
                } else {
                    true
                }
            }, "密码长度至少6个字符")
            .bind(UserDto::password.name)
        
        binder.forField(statusSelect)
            .asRequired("请选择状态")
            .bind(UserDto::status.name)
        
        binder.forField(roleCheckbox)
            .withConverter(
                { it?.toList() ?: emptyList() },
                { it?.toSet() ?: emptySet() }
            )
            .bind(UserDto::roleIds.name)
        
        binder.readBean(dto)
        
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
    
    private fun handleSave() {
        if (binder.validate().isOk) {
            val dto = UserDto(id = user?.id)
            binder.writeBean(dto)
            
            if (user == null) {
                userService.saveUser(dto)
            } else {
                userService.updateUser(dto)
            }
            
            NotifyUtil.showSuccess("保存成功")
            close()
            onSuccess()
        }
    }
}
