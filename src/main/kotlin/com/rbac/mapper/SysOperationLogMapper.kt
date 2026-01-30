package com.rbac.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.rbac.entity.SysOperationLog
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SysOperationLogMapper : BaseMapper<SysOperationLog>
