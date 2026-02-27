package com.rbac.ui.component

import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

fun VerticalLayout.pageContainerStyle() {
    setSizeFull()
    isPadding = true
    isSpacing = false
    element.style.apply {
        set("background", "var(--lumo-base-color)")
        set("padding", "var(--lumo-space-m)")
    }
}

fun HasStyle.toolbarStyle() {
    element.style.apply {
        set("background", "var(--lumo-contrast-5pct)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("padding", "var(--lumo-space-s)")
    }
}

fun HasStyle.pageTitleStyle() {
    element.style.set("margin", "0")
}

fun HorizontalLayout.searchAreaStyle() {
    alignItems = FlexComponent.Alignment.END
    isSpacing = true
}

fun <T> Grid<T>.applyStandardStyle() {
    setSizeFull()
}

fun VerticalLayout.dialogContentStyle() {
    isPadding = false
    isSpacing = true
}

fun HasStyle.cardStyle() {
    element.style.apply {
        set("background", "var(--lumo-contrast-5pct)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("padding", "var(--lumo-space-l)")
    }
}

fun HasStyle.badgeStyle(success: Boolean) {
    element.style.apply {
        set("padding", "var(--lumo-space-xs) var(--lumo-space-s)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("font-size", "var(--lumo-font-size-s)")
        if (success) {
            set("background-color", "var(--lumo-success-color-10pct)")
            set("color", "var(--lumo-success-text-color)")
        } else {
            set("background-color", "var(--lumo-error-color-10pct)")
            set("color", "var(--lumo-error-text-color)")
        }
    }
}

fun HasStyle.loginContainerStyle() {
    element.style.apply {
        set("background", "var(--lumo-base-color)")
        set("border-radius", "var(--lumo-border-radius-l)")
        set("padding", "var(--lumo-space-xl)")
        set("box-shadow", "var(--lumo-box-shadow-m)")
    }
}
