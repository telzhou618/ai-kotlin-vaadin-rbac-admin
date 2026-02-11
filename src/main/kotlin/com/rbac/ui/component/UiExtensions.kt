package com.rbac.ui.component

import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * UI 组件扩展函数，提供统一的样式工具
 */

// ==================== 容器样式 ====================

/**
 * 设置工具栏样式
 */
fun HasStyle.toolbarStyle() {
    element.style.apply {
        set("background", "var(--lumo-contrast-5pct)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("box-shadow", "var(--lumo-box-shadow-xs)")
        set("margin-bottom", "var(--lumo-space-m)")
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
        set("box-shadow", "var(--lumo-box-shadow-xs)")
        set("transition", "box-shadow 0.2s, transform 0.2s")
    }
}

/**
 * 设置卡片悬停效果
 */
fun HasStyle.cardHoverStyle() {
    cardStyle()
    element.setAttribute("onmouseover", "this.style.boxShadow='var(--lumo-box-shadow-s)'; this.style.transform='translateY(-2px)'")
    element.setAttribute("onmouseout", "this.style.boxShadow='var(--lumo-box-shadow-xs)'; this.style.transform='translateY(0)'")
}

/**
 * 设置表单容器样式
 */
fun HasStyle.formContainerStyle() {
    element.style.apply {
        set("background", "var(--lumo-base-color)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("padding", "var(--lumo-space-m)")
    }
}

/**
 * 设置内容区域样式
 */
fun HasStyle.contentAreaStyle() {
    element.style.apply {
        set("background", "var(--lumo-base-color)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("padding", "var(--lumo-space-m)")
        set("box-shadow", "var(--lumo-box-shadow-xs)")
    }
}

// ==================== 徽章样式 ====================

/**
 * 设置状态徽章样式
 */
fun HasStyle.badgeStyle(success: Boolean) {
    element.style.apply {
        set("padding", "var(--lumo-space-xs) var(--lumo-space-s)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("font-size", "var(--lumo-font-size-s)")
        set("font-weight", "500")
        set("display", "inline-block")
        if (success) {
            set("background-color", "var(--lumo-success-color-10pct)")
            set("color", "var(--lumo-success-text-color)")
        } else {
            set("background-color", "var(--lumo-error-color-10pct)")
            set("color", "var(--lumo-error-text-color)")
        }
    }
}

/**
 * 设置主要徽章样式
 */
fun HasStyle.primaryBadgeStyle() {
    element.style.apply {
        set("padding", "var(--lumo-space-xs) var(--lumo-space-s)")
        set("border-radius", "var(--lumo-border-radius-m)")
        set("font-size", "var(--lumo-font-size-s)")
        set("font-weight", "500")
        set("display", "inline-block")
        set("background-color", "var(--lumo-primary-color-10pct)")
        set("color", "var(--lumo-primary-text-color)")
    }
}

// ==================== 布局样式 ====================

/**
 * 设置页面容器样式
 */
fun VerticalLayout.pageContainerStyle() {
    setSizeFull()
    isPadding = true
    isSpacing = true
    element.style.apply {
        set("background", "var(--lumo-base-color)")
        set("gap", "var(--lumo-space-m)")
    }
}

/**
 * 设置页面标题样式
 */
fun HasStyle.pageTitleStyle() {
    element.style.apply {
        set("margin", "0")
        set("padding", "var(--lumo-space-s) 0")
        set("color", "var(--lumo-header-text-color)")
        set("font-size", "var(--lumo-font-size-xxl)")
        set("font-weight", "600")
    }
}

/**
 * 设置搜索区域样式
 */
fun HorizontalLayout.searchAreaStyle() {
    alignItems = FlexComponent.Alignment.END
    isSpacing = true
    element.style.set("gap", "var(--lumo-space-s)")
}

/**
 * 设置操作按钮区域样式
 */
fun HorizontalLayout.actionAreaStyle() {
    isSpacing = true
    justifyContentMode = FlexComponent.JustifyContentMode.END
    element.style.set("gap", "var(--lumo-space-s)")
}

// ==================== Grid 样式 ====================

/**
 * 应用标准 Grid 样式
 */
fun <T> Grid<T>.applyStandardStyle() {
    setSizeFull()
    element.style.apply {
        set("border-radius", "var(--lumo-border-radius-m)")
        set("overflow", "hidden")
        set("box-shadow", "var(--lumo-box-shadow-xs)")
        set("background", "var(--lumo-base-color)")
    }
}

/**
 * 设置 Grid 容器样式
 */
fun HasStyle.gridContainerStyle() {
    element.style.apply {
        set("flex", "1")
        set("min-height", "0")
        set("display", "flex")
        set("flex-direction", "column")
    }
}

// ==================== 间距工具 ====================

/**
 * 设置顶部间距
 */
fun HasStyle.marginTop(size: String = "m") {
    element.style.set("margin-top", "var(--lumo-space-$size)")
}

/**
 * 设置底部间距
 */
fun HasStyle.marginBottom(size: String = "m") {
    element.style.set("margin-bottom", "var(--lumo-space-$size)")
}

/**
 * 设置左侧间距
 */
fun HasStyle.marginLeft(size: String = "m") {
    element.style.set("margin-left", "var(--lumo-space-$size)")
}

/**
 * 设置右侧间距
 */
fun HasStyle.marginRight(size: String = "m") {
    element.style.set("margin-right", "var(--lumo-space-$size)")
}

// ==================== 文本样式 ====================

/**
 * 设置文本居中
 */
fun HasStyle.textCenter() {
    element.style.set("text-align", "center")
}

/**
 * 设置文本右对齐
 */
fun HasStyle.textRight() {
    element.style.set("text-align", "right")
}

/**
 * 设置文本左对齐
 */
fun HasStyle.textLeft() {
    element.style.set("text-align", "left")
}

// ==================== 对话框样式 ====================

/**
 * 设置对话框内容样式
 */
fun VerticalLayout.dialogContentStyle() {
    isPadding = false
    isSpacing = true
    element.style.apply {
        set("gap", "var(--lumo-space-m)")
        set("padding", "var(--lumo-space-m)")
    }
}

/**
 * 设置登录容器样式
 */
fun HasStyle.loginContainerStyle() {
    element.style.apply {
        set("background", "var(--lumo-base-color)")
        set("border-radius", "var(--lumo-border-radius-l)")
        set("padding", "var(--lumo-space-xl)")
        set("box-shadow", "var(--lumo-box-shadow-m)")
        set("border", "1px solid var(--lumo-contrast-10pct)")
    }
}

/**
 * 设置页面标题样式（H2）
 */
fun HasStyle.loginTitleStyle() {
    element.style.apply {
        set("margin", "0 0 var(--lumo-space-l) 0")
        set("color", "var(--lumo-header-text-color)")
        set("font-size", "var(--lumo-font-size-xxl)")
        set("font-weight", "600")
    }
}
