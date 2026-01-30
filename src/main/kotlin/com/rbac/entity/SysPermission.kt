package com.rbac.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("sys_permission")
data class SysPermission(
    @TableId(type = IdType.AUTO)
    var id: Long? = null,
    var permCode: String = "",
    var permName: String = "",
    var parentId: Long = 0,
    var createTime: LocalDateTime? = null,
    var updateTime: LocalDateTime? = null
)
