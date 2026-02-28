package com.rbac.dto


import jakarta.validation.constraints.Pattern

data class RoleDto(
    var id: Long? = null,
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "角色编码只能包含英文、数字和下划线"
    )
    var roleCode: String = "",
    var roleName: String = "",
    var roleDesc: String = "",
    var permIds: List<Long> = emptyList()
)

data class RoleQueryDto(
    var roleName: String? = null,
    var roleCode: String? = null
)
