package com.rbac.ui.component

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog

class ConfirmDialog(
    private val message: String,
    private val onConfirm: () -> Unit
) : Dialog() {
    
    init {
        headerTitle = "确认"
        
        verticalLayout {
            text(message)
        }
        
        val cancelButton = Button("取消")
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        cancelButton.addClickListener { close() }
        
        val confirmButton = Button("确定")
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR)
        confirmButton.addClickListener {
            onConfirm()
            close()
        }
        
        footer.add(cancelButton, confirmButton)
    }
}

fun showConfirmDialog(message: String, onConfirm: () -> Unit) {
    ConfirmDialog(message, onConfirm).open()
}
