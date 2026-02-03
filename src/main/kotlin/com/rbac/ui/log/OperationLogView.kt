package com.rbac.ui.log

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.mvysny.karibudsl.v10.*
import com.rbac.annotation.RequiresPermissions
import com.rbac.config.DateFormatConfig
import com.rbac.dto.LogQueryDto
import com.rbac.dto.OperationLogExportDto
import com.rbac.entity.SysOperationLog
import com.rbac.service.SysOperationLogService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.PaginationComponent
import com.rbac.util.ExcelExportUtil
import com.rbac.util.NotificationUtil
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Route("logs", layout = MainLayout::class)
@PageTitle("操作日志")
@RequiresPermissions("system:log:view")  // 需要日志查看权限
class OperationLogView(
    private val logService: SysOperationLogService
) : VerticalLayout() {

    private lateinit var usernameField: TextField
    private lateinit var moduleField: TextField
    private lateinit var startDatePicker: DatePicker
    private lateinit var endDatePicker: DatePicker
    private lateinit var grid: Grid<SysOperationLog>
    private lateinit var pagination: PaginationComponent

    init {
        setSizeFull()
        isPadding = true
        h3("日志管理")
        createToolbar()
        createGrid()
        createPagination()

        loadData(1, 20)
    }

    private fun createToolbar() {

        horizontalLayout {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.END

            horizontalLayout {
                width = "100%"
                alignItems = FlexComponent.Alignment.END

                usernameField = textField("用户名") {
                    placeholder = "输入用户名"
                    width = "150px"
                }

                moduleField = textField("模块") {
                    placeholder = "输入模块"
                    width = "150px"
                }

                startDatePicker = datePicker("开始日期") {
                    width = "150px"
                }

                endDatePicker = datePicker("结束日期") {
                    width = "150px"
                }

                button("查询") {
                    icon = VaadinIcon.SEARCH.create()
                    onLeftClick { loadData(1, 20) }
                }
            }

            button("导出") {
                addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                icon = VaadinIcon.DOWNLOAD.create()
                onLeftClick { exportToExcel() }
            }
        }
    }

    private fun createGrid() {
        grid = Grid(SysOperationLog::class.java, false).apply {
            addColumn { it.id }.setHeader("ID").width = "80px"
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

            setSizeFull()
        }
        add(grid)
    }

    private fun createPagination() {
        pagination = PaginationComponent { page, size -> loadData(page, size) }
        add(pagination)
    }

    private fun loadData(page: Long, size: Int) {
        val query = LogQueryDto(
            username = usernameField.value?.trim()?.takeIf { it.isNotBlank() },
            module = moduleField.value?.trim()?.takeIf { it.isNotBlank() },
            startTime = startDatePicker.value?.atStartOfDay(),
            endTime = endDatePicker.value?.atTime(23, 59, 59)
        )

        val pageData = logService.pageQuery(Page(page, size.toLong()), query)
        grid.setItems(pageData.records)
        pagination.updatePagination(pageData.current, pageData.pages)
    }


    /**
     * 导出 Excel
     */
    private fun exportToExcel() {
        runCatching {
            // 获取数据
            val query = LogQueryDto(
                username = usernameField.value?.trim()?.takeIf { it.isNotBlank() },
                module = moduleField.value?.trim()?.takeIf { it.isNotBlank() },
                startTime = startDatePicker.value?.atStartOfDay(),
                endTime = endDatePicker.value?.atTime(23, 59, 59)
            )
            val pageData = logService.pageQuery(Page(0, Long.MAX_VALUE), query)
            val records = pageData.records
            // 验证数据
            if (records.isEmpty()) {
                NotificationUtil.showError("没有数据可导出")
                return
            }
            // 转换并导出
            val exportData = records.map { log ->
                OperationLogExportDto(
                    id = log.id,
                    username = log.username,
                    module = log.module,
                    operation = log.operation,
                    responseCode = log.responseCode,
                    responseMsg = log.responseMsg,
                    ip = log.ip,
                    executeTime = log.executeTime,
                    createTime = DateFormatConfig.formatDateTime(log.createTime)
                )
            }
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val fileName = "操作日志_${timestamp}.xlsx"

            ExcelExportUtil.exportExcel(fileName, OperationLogExportDto::class.java, exportData)
            NotificationUtil.showSuccess("导出成功，共 ${records.size} 条记录")

        }.onFailure { e ->
            NotificationUtil.showError("导出失败：${e.message}")
        }
    }
}
