package com.rbac.dto

data class PermissionDto(
    var id: Long? = null,
    var permCode: String = "",
    var permName: String = "",
    var parentId: Long = 0,
    var children: MutableList<PermissionDto> = mutableListOf()
)
