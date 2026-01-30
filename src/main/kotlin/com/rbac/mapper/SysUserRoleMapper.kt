package com.rbac.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.rbac.entity.SysUserRole
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SysUserRoleMapper : BaseMapper<SysUserRole>
