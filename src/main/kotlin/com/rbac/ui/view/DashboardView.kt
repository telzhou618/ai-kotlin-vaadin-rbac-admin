package com.rbac.ui.view

import com.github.mvysny.karibudsl.v10.*
import com.rbac.exception.GlobalExceptionHandler
import com.rbac.service.DashboardService
import com.rbac.service.SysOperationLogService
import com.rbac.ui.MainLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route("dashboard", layout = MainLayout::class)
@PageTitle("首页")
class DashboardView(
    private val dashboardService: DashboardService,
    private val logService: SysOperationLogService,
    private val exceptionHandler: GlobalExceptionHandler
) : VerticalLayout() {
    
    init {
        setSizeFull()
        isPadding = true
        
        try {
            val data = dashboardService.getDashboardData()
            
            horizontalLayout {
                width = "100%"
                
                div {
                    width = "25%"
                    element.style.set("padding", "20px")
                    element.style.set("background", "#e3f2fd")
                    element.style.set("border-radius", "8px")
                    h3("用户总数")
                    h2(data.userCount.toString())
                }
                
                div {
                    width = "25%"
                    element.style.set("padding", "20px")
                    element.style.set("background", "#f3e5f5")
                    element.style.set("border-radius", "8px")
                    h3("角色总数")
                    h2(data.roleCount.toString())
                }
                
                div {
                    width = "25%"
                    element.style.set("padding", "20px")
                    element.style.set("background", "#e8f5e9")
                    element.style.set("border-radius", "8px")
                    h3("权限节点数")
                    h2(data.permCount.toString())
                }
                
                div {
                    width = "25%"
                    element.style.set("padding", "20px")
                    element.style.set("background", "#fff3e0")
                    element.style.set("border-radius", "8px")
                    h3("日志总数")
                    h2(data.logCount.toString())
                }
            }
            
            h3("最近操作日志")
            
            val grid = Grid(com.rbac.entity.SysOperationLog::class.java, false).apply {
                addColumn { it.username }.setHeader("用户")
                addColumn { it.module }.setHeader("模块")
                addColumn { it.operation }.setHeader("操作")
                addColumn { it.responseCode }.setHeader("状态")
                addColumn { it.createTime }.setHeader("时间")
                setItems(logService.getRecentLogs(10))
            }
            add(grid)
            
        } catch (e: Exception) {
            exceptionHandler.handle(e)
        }
    }
}
