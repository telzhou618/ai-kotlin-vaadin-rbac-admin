package com.rbac.config

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.NotPermissionException
import cn.dev33.satoken.exception.NotRoleException
import cn.dev33.satoken.stp.StpUtil
import com.rbac.annotation.Logical
import com.rbac.annotation.RequiresPermissions
import com.rbac.annotation.RequiresRoles
import com.rbac.exception.GlobalExceptionHandler
import com.rbac.ui.component.AccessDeniedView
import com.rbac.ui.LoginView
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class VaadinSecurityConfig(
    private val exceptionHandler: GlobalExceptionHandler
) : VaadinServiceInitListener {
    
    private val logger = LoggerFactory.getLogger(VaadinSecurityConfig::class.java)
    
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
        val viewName = targetView.simpleName
        
        // 检查目标视图是否允许匿名访问
        val isAnonymousAllowed = targetView.isAnnotationPresent(AnonymousAllowed::class.java)
        
        // 如果允许匿名访问，直接放行
        if (isAnonymousAllowed) {
            logger.debug("页面 {} 允许匿名访问", viewName)
            return
        }
        
        // 检查是否登录
        if (!StpUtil.isLogin()) {
            logger.warn("用户未登录，尝试访问 {}", viewName)
            event.rerouteTo(LoginView::class.java)
            return
        }
        
        try {
            // 检查角色权限
            checkRolePermission(targetView)
            
            // 检查功能权限
            checkFunctionPermission(targetView)
            
            logger.debug("用户 {} 成功访问页面 {}", StpUtil.getLoginIdAsLong(), viewName)
            
        } catch (e: NotRoleException) {
            logger.warn("用户 {} 无角色权限访问 {}: {}", StpUtil.getLoginIdAsLong(), viewName, e.message)
            event.rerouteTo(AccessDeniedView::class.java)
        } catch (e: NotPermissionException) {
            logger.warn("用户 {} 无功能权限访问 {}: {}", StpUtil.getLoginIdAsLong(), viewName, e.message)
            event.rerouteTo(AccessDeniedView::class.java)
        } catch (e: NotLoginException) {
            logger.warn("用户登录已过期，尝试访问 {}", viewName)
            event.rerouteTo(LoginView::class.java)
        }
    }
    
    /**
     * 检查角色权限
     */
    private fun checkRolePermission(targetView: Class<*>) {
        val requiresRoles = targetView.getAnnotation(RequiresRoles::class.java) ?: return
        
        val roles = requiresRoles.value
        if (roles.isEmpty()) return
        
        when (requiresRoles.logical) {
            Logical.AND -> {
                // 必须拥有所有角色
                StpUtil.checkRoleAnd(*roles)
            }
            Logical.OR -> {
                // 只需拥有其中一个角色
                StpUtil.checkRoleOr(*roles)
            }
        }
    }
    
    /**
     * 检查功能权限
     */
    private fun checkFunctionPermission(targetView: Class<*>) {
        val requiresPermissions = targetView.getAnnotation(RequiresPermissions::class.java) ?: return
        
        val permissions = requiresPermissions.value
        if (permissions.isEmpty()) return
        
        when (requiresPermissions.logical) {
            Logical.AND -> {
                // 必须拥有所有权限
                StpUtil.checkPermissionAnd(*permissions)
            }
            Logical.OR -> {
                // 只需拥有其中一个权限
                StpUtil.checkPermissionOr(*permissions)
            }
        }
    }
}
