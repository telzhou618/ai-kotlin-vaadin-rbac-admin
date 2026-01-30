package com.rbac.dto

import java.time.LocalDateTime

data class LogQueryDto(
    var username: String? = null,
    var module: String? = null,
    var operation: String? = null,
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null
)
