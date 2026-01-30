package com.rbac.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.rbac.dto.LogQueryDto
import com.rbac.entity.SysOperationLog
import com.rbac.mapper.SysOperationLogMapper
import org.springframework.stereotype.Service

@Service
class SysOperationLogService : ServiceImpl<SysOperationLogMapper, SysOperationLog>() {
    
    fun pageQuery(page: Page<SysOperationLog>, query: LogQueryDto): Page<SysOperationLog> {
        val wrapper = QueryWrapper<SysOperationLog>()
        query.username?.let { wrapper.like("username", it) }
        query.module?.let { wrapper.eq("module", it) }
        query.operation?.let { wrapper.eq("operation", it) }
        query.startTime?.let { wrapper.ge("create_time", it) }
        query.endTime?.let { wrapper.le("create_time", it) }
        wrapper.orderByDesc("create_time")
        return page(page, wrapper)
    }
    
    fun getRecentLogs(limit: Int): List<SysOperationLog> {
        return list(QueryWrapper<SysOperationLog>()
            .orderByDesc("create_time")
            .last("limit $limit"))
    }
}
