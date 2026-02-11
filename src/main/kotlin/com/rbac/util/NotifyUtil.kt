package com.rbac.util

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant

/**
 * 显示错误通知
 */
fun showError(message: String) {
    val notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER)
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
}

/**
 * 显示成功通知
 */
fun showSuccess(message: String) {
    val notification = Notification.show(message, 2000, Notification.Position.TOP_CENTER)
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
}

/**
 * 显示警告通知
 */
fun showWarning(message: String) {
    val notification = Notification.show(message, 2500, Notification.Position.TOP_CENTER)
    notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST)
}

/**
 * 显示信息通知
 */
fun showInfo(message: String) {
    val notification = Notification.show(message, 2000, Notification.Position.TOP_CENTER)
    notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY)
}
