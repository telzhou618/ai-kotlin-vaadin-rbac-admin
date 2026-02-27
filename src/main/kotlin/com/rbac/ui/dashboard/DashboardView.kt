package com.rbac.ui.dashboard

import com.github.mvysny.karibudsl.v10.columnFor
import com.github.mvysny.karibudsl.v10.grid
import com.github.mvysny.karibudsl.v10.h4
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.rbac.entity.SysOperationLog
import com.rbac.service.DashboardService
import com.rbac.service.SysOperationLogService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.applyStandardStyle
import com.rbac.ui.component.cardStyle
import com.rbac.ui.component.pageContainerStyle
import com.rbac.util.formatDateTime
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
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
        pageContainerStyle()

        val data = dashboardService.getDashboardData()

        horizontalLayout {
            width = "100%"
            isSpacing = true

            add(createStatCard("用户总数", data.userCount.toString(), VaadinIcon.USER, "primary"))
            add(createStatCard("角色总数", data.roleCount.toString(), VaadinIcon.GROUP, "success"))
            add(createStatCard("权限节点数", data.permCount.toString(), VaadinIcon.LOCK, "contrast"))
            add(createStatCard("日志总数", data.logCount.toString(), VaadinIcon.RECORDS, "error"))
        }

        add(h4("最近操作日志").apply {
            addClassNames(LumoUtility.Margin.Top.MEDIUM)
        })

        grid<SysOperationLog> {
            applyStandardStyle()
            addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)

            columnFor(SysOperationLog::username) { setHeader("用户").isAutoWidth = true }
            columnFor(SysOperationLog::module) { setHeader("模块").isAutoWidth = true }
            columnFor(SysOperationLog::operation) { setHeader("操作").isAutoWidth = true }
            columnFor(SysOperationLog::responseCode) { setHeader("状态码").isAutoWidth = true }
            columnFor(SysOperationLog::responseMsg) { setHeader("响应消息").isAutoWidth = true }
            columnFor(SysOperationLog::ip) { setHeader("IP").isAutoWidth = true }
            columnFor(SysOperationLog::executeTime) { setHeader("耗时(ms)").isAutoWidth = true }

            addColumn { log ->
                log.createTime.formatDateTime()
            }.apply {
                setHeader("操作时间")
                isAutoWidth = true
            }

            setItems(logService.getRecentLogs(10))
        }
    }

    private fun createStatCard(
        title: String,
        value: String,
        icon: VaadinIcon,
        colorTheme: String
    ): Div {
        return Div().apply {
            cardStyle()
            width = "25%"
            element.style.set("cursor", "default")

            val content = VerticalLayout().apply {
                isPadding = false
                isSpacing = true
            }

            val headerLayout = HorizontalLayout().apply {
                width = "100%"
                justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
                alignItems = FlexComponent.Alignment.CENTER

                add(Span(title).apply {
                    addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY)
                })

                add(icon.create().apply {
                    addClassNames(LumoUtility.IconSize.LARGE)
                    element.style.apply {
                        set(
                            "color", when (colorTheme) {
                                "primary" -> "var(--lumo-primary-color)"
                                "success" -> "var(--lumo-success-color)"
                                "error" -> "var(--lumo-error-color)"
                                else -> "var(--lumo-contrast-60pct)"
                            }
                        )
                        set("opacity", "0.8")
                    }
                })
            }

            content.add(headerLayout)
            content.add(H2(value).apply {
                addClassNames(LumoUtility.Margin.Top.SMALL, LumoUtility.Margin.Bottom.NONE)
                element.style.set("color", "var(--lumo-header-text-color)")
            })

            add(content)
        }
    }
}
