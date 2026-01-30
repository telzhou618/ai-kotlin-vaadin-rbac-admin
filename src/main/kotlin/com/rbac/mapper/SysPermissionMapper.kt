package com.rbac.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.rbac.entity.SysPermission
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SysPermissionMapper : BaseMapper<SysPermission>
