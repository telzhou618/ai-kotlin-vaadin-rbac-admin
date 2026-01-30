package com.rbac.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.rbac.entity.SysRolePermission
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SysRolePermissionMapper : BaseMapper<SysRolePermission>
