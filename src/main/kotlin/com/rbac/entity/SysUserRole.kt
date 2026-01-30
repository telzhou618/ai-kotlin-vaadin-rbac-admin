package com.rbac.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("sys_user_role")
data class SysUserRole(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var userId: Long = 0,
    var roleId: Long = 0,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
