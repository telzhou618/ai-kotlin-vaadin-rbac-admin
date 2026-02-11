package com.rbac.ui.component

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent

class ConfirmDialog(
    private val message: String,
    private val onConfirm: () -> Unit
) : Dialog() {

    init {
        headerTitle = "确认?"
        minWidth = "300px"

        horizontalLayout {
            alignItems = FlexComponent.Alignment.CENTER
            isSpacing = true
            icon(VaadinIcon.QUESTION_CIRCLE)
            text(message)
        }

        val cancelButton = button("取消") {
            addThemeVariants(ButtonVariant.LUMO_TERTIARY)
            onLeftClick {
                close()
            }
        }

        val confirmButton = button("确定") {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR)
            onLeftClick {
                onConfirm()
                close()
            }
        }

        footer.add(cancelButton, confirmButton)
    }
}

fun showConfirmDialog(message: String, onConfirm: () -> Unit) {
    ConfirmDialog(message, onConfirm).open()
}
