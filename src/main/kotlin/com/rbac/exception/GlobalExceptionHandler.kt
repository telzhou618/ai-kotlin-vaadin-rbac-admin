package com.rbac.exception

import com.rbac.util.NotifyUtil
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
        NotifyUtil.showError(message)
    }

    fun handle(e: Exception) {
        logger.error("系统异常", e)
        NotifyUtil.showError(e.message ?: "系统异常，请联系管理员")
    }
}

