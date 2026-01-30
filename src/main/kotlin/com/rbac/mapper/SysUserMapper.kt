package com.rbac.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.rbac.entity.SysUser
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SysUserMapper : BaseMapper<SysUser>
