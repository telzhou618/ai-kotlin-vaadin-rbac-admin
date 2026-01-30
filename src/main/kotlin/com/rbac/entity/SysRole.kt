package com.rbac.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("sys_role")
data class SysRole(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var roleCode: String = "",
    var roleName: String = "",
    var roleDesc: String = "",
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
