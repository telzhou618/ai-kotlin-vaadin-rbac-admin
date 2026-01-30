package com.rbac.dto

data class UserDto(
    var id: Long? = null,
    var username: String = "",
    var password: String? = null,
    var status: Int = 1,
    var roleIds: List<Long> = emptyList(),
    var roleNames: String = ""
)

data class UserQueryDto(
    var username: String? = null,
    var status: Int? = null
)
