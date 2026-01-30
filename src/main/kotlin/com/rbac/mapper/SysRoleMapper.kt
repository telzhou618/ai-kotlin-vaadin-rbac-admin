package com.rbac.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.rbac.entity.SysRole
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SysRoleMapper : BaseMapper<SysRole>
