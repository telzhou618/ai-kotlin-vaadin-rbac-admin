package com.rbac.exception

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    fun handle(e: Exception) {
        logger.error("系统异常", e)
        showError(e.message ?: "系统异常，请联系管理员")
    }
    
    fun showError(message: String) {
        val notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER)
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
    }
    
    fun showSuccess(message: String) {
        val notification = Notification.show(message, 2000, Notification.Position.TOP_CENTER)
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
    }
}
