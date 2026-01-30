package com.rbac.ui.dashboard

import com.github.mvysny.karibudsl.v10.*
import com.rbac.config.DateFormatConfig
import com.rbac.entity.SysOperationLog
import com.rbac.service.DashboardService
import com.rbac.service.SysOperationLogService
import com.rbac.ui.MainLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias

@Route("", layout = MainLayout::class)
@RouteAlias("dashboard", layout = MainLayout::class)
@PageTitle("首页")
class DashboardView(
    private val dashboardService: DashboardService,
    private val logService: SysOperationLogService
) : VerticalLayout() {
    
    init {
        setSizeFull()
        isPadding = true
        
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
            
            val grid = Grid(SysOperationLog::class.java, false).apply {
                addColumn { it.username }.setHeader("用户")
                addColumn { it.module }.setHeader("模块")
                addColumn { it.operation }.setHeader("操作")
                addColumn { it.responseCode }.setHeader("状态码")
                addColumn { it.responseMsg }.setHeader("响应消息")
                addColumn { it.ip }.setHeader("IP")
                addColumn { it.executeTime }.setHeader("耗时(ms)")

                // 格式化日期时间列
                addColumn { log ->
                    DateFormatConfig.formatDateTime(log.createTime)
                }.setHeader("操作时间").width = "180px"
                
                setItems(logService.getRecentLogs(10))
            }
            add(grid)
    }
}
