package com.rbac.aspect

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.json.JSONUtil
import com.rbac.annotation.OperationLog
import com.rbac.entity.SysOperationLog
import com.rbac.service.SysOperationLogService
import com.rbac.service.SysUserService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Aspect
@Component
class OperationLogAspect(
    private val operationLogService: SysOperationLogService,
    private val userService: SysUserService
) {
    
    private val logger = LoggerFactory.getLogger(OperationLogAspect::class.java)
    
    @Around("@annotation(com.rbac.annotation.OperationLog)")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()
        val signature = joinPoint.signature as MethodSignature
        val targetMethod = signature.method
        val annotation = targetMethod.getAnnotation(OperationLog::class.java)
        
        val log = SysOperationLog().apply {
            module = annotation.module
            operation = annotation.operation
            method = "UI"
            requestUri = signature.name
            requestParams = JSONUtil.toJsonStr(joinPoint.args)
            ip = "127.0.0.1"
            userAgent = "Vaadin"
            createTime = LocalDateTime.now()
        }
        
        // 获取当前登录用户信息
        try {
            if (StpUtil.isLogin()) {
                val userId = StpUtil.getLoginIdAsLong()
                log.userId = userId
                
                // 从数据库获取真实用户名
                val user = userService.getById(userId)
                log.username = user?.username ?: "未知用户"
            }
        } catch (e: Exception) {
            logger.warn("获取登录信息失败", e)
        }
        
        var result: Any? = null
        try {
            result = joinPoint.proceed()
            log.responseCode = "200"
            log.responseMsg = "成功"
        } catch (e: Exception) {
            log.responseCode = "500"
            log.responseMsg = e.message ?: "失败"
            throw e
        } finally {
            log.executeTime = System.currentTimeMillis() - startTime
            try {
                operationLogService.save(log)
            } catch (e: Exception) {
                logger.error("保存操作日志失败", e)
            }
        }
        
        return result
    }
}
