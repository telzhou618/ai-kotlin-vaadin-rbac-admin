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

// 保留 NotifyUtil 对象以保持向后兼容
@Deprecated("使用顶层函数 showError() 代替", ReplaceWith("showError(message)"))
object NotifyUtil {
    fun showError(message: String) = com.rbac.util.showError(message)
    fun showSuccess(message: String) = com.rbac.util.showSuccess(message)
    fun showWarning(message: String) = com.rbac.util.showWarning(message)
    fun showInfo(message: String) = com.rbac.util.showInfo(message)
}
