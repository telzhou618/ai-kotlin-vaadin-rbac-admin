package com.rbac.dto

data class RoleDto(
    var id: Long? = null,
    var roleCode: String = "",
    var roleName: String = "",
    var roleDesc: String = "",
    var permIds: List<Long> = emptyList()
)

data class RoleQueryDto(
    var roleName: String? = null,
    var roleCode: String? = null
)
