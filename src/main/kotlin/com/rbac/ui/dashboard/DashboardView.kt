package com.rbac.ui.dashboard

import com.rbac.config.DateFormatConfig
import com.rbac.entity.SysOperationLog
import com.rbac.service.DashboardService
import com.rbac.service.SysOperationLogService
import com.rbac.ui.MainLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.theme.lumo.LumoUtility

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
        isSpacing = true

        val data = dashboardService.getDashboardData()

        // 统计卡片容器
        val statsLayout = HorizontalLayout().apply {
            width = "100%"
            isSpacing = true
        }

        // 用户统计卡片
        statsLayout.add(
            createStatCard(
                title = "用户总数",
                value = data.userCount.toString(),
                icon = VaadinIcon.USER,
                colorTheme = "primary"
            )
        )

        // 角色统计卡片
        statsLayout.add(
            createStatCard(
                title = "角色总数",
                value = data.roleCount.toString(),
                icon = VaadinIcon.GROUP,
                colorTheme = "success"
            )
        )

        // 权限统计卡片
        statsLayout.add(
            createStatCard(
                title = "权限节点数",
                value = data.permCount.toString(),
                icon = VaadinIcon.LOCK,
                colorTheme = "contrast"
            )
        )

        // 日志统计卡片
        statsLayout.add(
            createStatCard(
                title = "日志总数",
                value = data.logCount.toString(),
                icon = VaadinIcon.RECORDS,
                colorTheme = "error"
            )
        )

        add(statsLayout)

        // 最近操作日志标题
        add(H3("最近操作日志").apply {
            addClassNames(LumoUtility.Margin.Top.MEDIUM)
        })

        // 日志表格
        val grid = Grid(SysOperationLog::class.java, false).apply {
            addColumn { it.username }.setHeader("用户").setAutoWidth(true)
            addColumn { it.module }.setHeader("模块").setAutoWidth(true)
            addColumn { it.operation }.setHeader("操作").setAutoWidth(true)
            addColumn { it.responseCode }.setHeader("状态码").setAutoWidth(true)
            addColumn { it.responseMsg }.setHeader("响应消息").setAutoWidth(true)
            addColumn { it.ip }.setHeader("IP").setAutoWidth(true)
            addColumn { it.executeTime }.setHeader("耗时(ms)").setAutoWidth(true)
            addColumn { log ->
                DateFormatConfig.formatDateTime(log.createTime)
            }.setHeader("操作时间").setAutoWidth(true)

            setItems(logService.getRecentLogs(10))
        }
        add(grid)
    }

    /**
     * 创建统计卡片
     */
    private fun createStatCard(
        title: String,
        value: String,
        icon: VaadinIcon,
        colorTheme: String
    ): Div {
        return Div().apply {
            // 使用 Lumo 工具类设置样式
            addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.LARGE
            )
            width = "25%"

            // 卡片内容容器
            val content = VerticalLayout().apply {
                isPadding = false
                isSpacing = true
            }

            // 图标和标题行
            val headerLayout = HorizontalLayout().apply {
                width = "100%"
                justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
                alignItems = FlexComponent.Alignment.CENTER

                // 标题
                add(Span(title).apply {
                    addClassNames(
                        LumoUtility.FontSize.SMALL,
                        LumoUtility.TextColor.SECONDARY
                    )
                })

                // 图标
                add(icon.create().apply {
                    addClassNames(LumoUtility.IconSize.MEDIUM)
                    when (colorTheme) {
                        "primary" -> element.style.set("color", "var(--lumo-primary-color)")
                        "success" -> element.style.set("color", "var(--lumo-success-color)")
                        "error" -> element.style.set("color", "var(--lumo-error-color)")
                        "contrast" -> element.style.set("color", "var(--lumo-contrast-60pct)")
                    }
                })
            }

            content.add(headerLayout)

            // 数值
            content.add(H2(value).apply {
                addClassNames(
                    LumoUtility.Margin.Top.SMALL,
                    LumoUtility.Margin.Bottom.NONE
                )
            })

            add(content)
        }
    }
}
