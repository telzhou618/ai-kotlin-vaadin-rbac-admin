package com.rbac.exception

import com.rbac.util.showError
import com.vaadin.flow.server.ErrorEvent
import com.vaadin.flow.server.ErrorHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GlobalExceptionHandler : ErrorHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    override fun error(event: ErrorEvent) {
        when (val throwable = event.throwable) {
            is BusinessException -> {
                // 业务异常，记录警告级别日志
                logger.warn("业务异常: {} (code: {})", throwable.message, throwable.code)
                showError(throwable.message ?: "操作失败")
            }

            else -> {
                // 系统异常，记录错误级别日志
                logger.error("系统异常", throwable)
                val message = throwable.message ?: "系统异常，请联系管理员"
                showError(message)
            }
        }
    }

    fun handle(e: Exception) {
        when (e) {
            is BusinessException -> {
                logger.warn("业务异常: {} (code: {})", e.message, e.code)
                showError(e.message ?: "操作失败")
            }

            else -> {
                logger.error("系统异常", e)
                showError(e.message ?: "系统异常，请联系管理员")
            }
        }
    }
}
