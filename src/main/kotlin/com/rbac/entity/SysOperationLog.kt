package com.rbac.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("sys_operation_log")
data class SysOperationLog(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long = 0,
    var username: String = "",
    var module: String = "",
    var operation: String = "",
    var method: String = "",
    var requestUri: String = "",
    var requestParams: String? = null,
    var responseCode: String = "",
    var responseMsg: String = "",
    var ip: String = "",
    var userAgent: String = "",
    var executeTime: Long = 0,
    var createTime: LocalDateTime? = null
)
