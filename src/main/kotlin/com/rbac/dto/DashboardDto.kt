package com.rbac.dto

data class DashboardDto(
    var userCount: Long = 0,
    var roleCount: Long = 0,
    var permCount: Long = 0,
    var logCount: Long = 0
)
