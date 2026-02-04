package com.rbac.util

import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant

object NotifyUtil {

    fun showError(message: String) {
        val notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER)
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
    }

    fun showSuccess(message: String) {
        val notification = Notification.show(message, 2000, Notification.Position.TOP_CENTER)
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS)
    }

    fun showWarning(message: String) {
        val notification = Notification.show(message, 2500, Notification.Position.TOP_CENTER)
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST)
    }

    fun showInfo(message: String) {
        val notification = Notification.show(message, 2000, Notification.Position.TOP_CENTER)
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY)
    }
}
