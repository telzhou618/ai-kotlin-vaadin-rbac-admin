package com.rbac.config

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.NotPermissionException
import cn.dev33.satoken.stp.StpUtil
import com.rbac.exception.GlobalExceptionHandler
import com.rbac.ui.AccessDeniedView
import com.rbac.ui.LoginView
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.stereotype.Component

@Component
class VaadinSecurityConfig(
    private val exceptionHandler: GlobalExceptionHandler
) : VaadinServiceInitListener {
    
    override fun serviceInit(event: ServiceInitEvent) {
        event.source.addUIInitListener { uiEvent ->
            // 注册全局异常处理器
            uiEvent.ui.session.errorHandler = exceptionHandler
            
            uiEvent.ui.addBeforeEnterListener { beforeEnterEvent ->
                checkAccess(beforeEnterEvent)
            }
        }
    }
    
    private fun checkAccess(event: BeforeEnterEvent) {
        val targetView = event.navigationTarget
        
        // 检查目标视图是否允许匿名访问
        val isAnonymousAllowed = targetView.isAnnotationPresent(AnonymousAllowed::class.java)
        
        // 如果允许匿名访问，直接放行
        if (isAnonymousAllowed) {
            return
        }
        
        // 检查是否登录
        if (!StpUtil.isLogin()) {
            // 未登录，重定向到登录页
            event.rerouteTo(LoginView::class.java)
            return
        }
        
        // 这里可以添加权限检查逻辑
        // 例如：检查用户是否有访问该页面的权限
        try {
            // 如果需要权限验证，可以在这里添加
            // StpUtil.checkPermission("page:${targetView.simpleName}")
        } catch (e: NotPermissionException) {
            // 没有权限，重定向到403页面
            event.rerouteTo(AccessDeniedView::class.java)
        } catch (e: NotLoginException) {
            // 登录过期，重定向到登录页
            event.rerouteTo(LoginView::class.java)
        }
    }
}
