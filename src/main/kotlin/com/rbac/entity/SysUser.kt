package com.rbac.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("sys_user")
data class SysUser(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var username: String = "",
    var password: String = "",
    var status: Int = 1,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
