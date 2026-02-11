package com.rbac.ui.component

import com.vaadin.flow.component.HasStyle

/**
 * UI 组件扩展函数，提供统一的样式工具
 */

/**
 * 设置工具栏样式
 */
fun HasStyle.toolbarStyle() {
    element.style.apply {
        set("background", "var(--lumo-contrast-5pct)")
        set("border-radius", "var(--lumo-border-radius-m)")
    }
}

/**
 * 设置卡片样式
 */
fun HasStyle.cardStyle() {
    element.style.apply {
        set("background", "var(--lumo-contrast-5pct)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("padding", "var(--lumo-space-l)")
    }
}

/**
 * 设置状态徽章样式
 */
fun HasStyle.badgeStyle(success: Boolean) {
    element.style.apply {
        set("padding", "4px 8px")
        set("border-radius", "4px")
        set("font-size", "12px")
        set("font-weight", "500")
        if (success) {
            set("background-color", "#e7f5e9")
            set("color", "#2e7d32")
        } else {
            set("background-color", "#fdecea")
            set("color", "#d32f2f")
        }
    }
}
