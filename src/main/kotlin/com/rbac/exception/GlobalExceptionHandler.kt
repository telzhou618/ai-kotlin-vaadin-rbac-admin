package com.rbac.exception

import com.rbac.util.NotificationUtil
import com.vaadin.flow.server.ErrorEvent
import com.vaadin.flow.server.ErrorHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GlobalExceptionHandler : ErrorHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    override fun error(event: ErrorEvent) {
        val throwable = event.throwable
        logger.error("系统异常", throwable)
        
        val message = throwable.message ?: "系统异常，请联系管理员"
        NotificationUtil.showError(message)
    }
    
    fun handle(e: Exception) {
        logger.error("系统异常", e)
        NotificationUtil.showError(e.message ?: "系统异常，请联系管理员")
    }
}

