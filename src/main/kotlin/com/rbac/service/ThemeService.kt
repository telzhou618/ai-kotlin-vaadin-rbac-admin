package com.rbac.service

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinSession
import com.vaadin.flow.theme.lumo.Lumo
import org.springframework.stereotype.Service

/**
 * 主题服务
 * 管理 Lumo 主题的明暗切换
 */
@Service
class ThemeService {
    
    companion object {
        private const val THEME_KEY = "app.theme"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
    }
    
    /**
     * 获取当前主题
     */
    fun getCurrentTheme(): String {
        return VaadinSession.getCurrent()?.getAttribute(THEME_KEY) as? String ?: THEME_LIGHT
    }
    
    /**
     * 设置主题
     */
    fun setTheme(theme: String) {
        VaadinSession.getCurrent()?.setAttribute(THEME_KEY, theme)
        applyTheme(theme)
    }
    
    /**
     * 切换主题
     */
    fun toggleTheme() {
        val currentTheme = getCurrentTheme()
        val newTheme = if (currentTheme == THEME_LIGHT) THEME_DARK else THEME_LIGHT
        setTheme(newTheme)
    }
    
    /**
     * 应用主题到当前 UI
     */
    private fun applyTheme(theme: String) {
        val ui = UI.getCurrent() ?: return
        val themeList = ui.element.themeList
        
        when (theme) {
            THEME_DARK -> {
                themeList.remove(Lumo.LIGHT)
                themeList.add(Lumo.DARK)
            }
            else -> {
                themeList.remove(Lumo.DARK)
                themeList.add(Lumo.LIGHT)
            }
        }
    }
    
    /**
     * 初始化主题（在页面加载时调用）
     */
    fun initTheme() {
        val theme = getCurrentTheme()
        applyTheme(theme)
    }
    
    /**
     * 判断当前是否为暗色主题
     */
    fun isDarkTheme(): Boolean {
        return getCurrentTheme() == THEME_DARK
    }
}
