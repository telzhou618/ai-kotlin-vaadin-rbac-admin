package com.rbac.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("sys_role_permission")
data class SysRolePermission(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var roleId: Long = 0,
    var permId: Long = 0,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
