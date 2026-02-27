package com.rbac.ui.log

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.placeholder
import com.rbac.annotation.RequiresPermissions
import com.rbac.dto.LogQueryDto
import com.rbac.dto.OperationLogExportDto
import com.rbac.entity.SysOperationLog
import com.rbac.service.SysOperationLogService
import com.rbac.ui.MainLayout
import com.rbac.ui.component.*
import com.rbac.util.ExcelUtil
import com.rbac.util.formatDateTime
import com.rbac.util.showError
import com.rbac.util.showSuccess
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
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
@RequiresPermissions("system:log:view")
class OperationLogView(
    private val logService: SysOperationLogService
) : VerticalLayout() {

    private lateinit var usernameField: TextField
    private lateinit var moduleField: TextField
    private lateinit var startDatePicker: DatePicker
    private lateinit var endDatePicker: DatePicker
    private lateinit var grid: Grid<SysOperationLog>
    private var pageSize = 20
    private lateinit var pagination: PaginationComponent

    init {
        pageContainerStyle()
        
        createToolbar()
        createGrid()
        createPagination()
        loadData(1, 20)
    }

    private fun createToolbar() {
        horizontalLayout {
            width = "100%"
            isPadding = true
            justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
            alignItems = FlexComponent.Alignment.CENTER
            toolbarStyle()
            
            h4("操作日志") {
                pageTitleStyle()
            }
            
            horizontalLayout {
                searchAreaStyle()
                
                usernameField = textField {
                    placeholder = "用户名"
                    width = "120px"
                    isClearButtonVisible = true
                }

                moduleField = textField {
                    placeholder = "模块"
                    width = "120px"
                    isClearButtonVisible = true
                }

                startDatePicker = datePicker {
                    placeholder = "开始日期"
                    width = "140px"
                }

                endDatePicker = datePicker {
                    placeholder = "结束日期"
                    width = "140px"
                }

                button("查询") {
                    icon = VaadinIcon.SEARCH.create()
                    onLeftClick { loadData(1, 20) }
                }
                
                button("导出") {
                    addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                    icon = VaadinIcon.DOWNLOAD.create()
                    onLeftClick { exportToExcel() }
                }
            }
        }
    }

    private fun createGrid() {
        grid = grid {
            applyStandardStyle()
            addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
            
            columnFor(SysOperationLog::id) {
                setHeader("ID")
                width = "80px"
                isSortable = true
            }
            columnFor(SysOperationLog::username) { setHeader("用户") }
            columnFor(SysOperationLog::module) { setHeader("模块") }
            columnFor(SysOperationLog::operation) { setHeader("操作") }
            columnFor(SysOperationLog::responseCode) { setHeader("状态码") }
            columnFor(SysOperationLog::responseMsg) { setHeader("响应消息") }
            columnFor(SysOperationLog::ip) { setHeader("IP") }
            columnFor(SysOperationLog::executeTime) { setHeader("耗时(ms)") }

            addColumn { log ->
                log.createTime.formatDateTime()
            }.apply {
                setHeader("操作时间")
                width = "180px"
            }
        }
    }

    private fun createPagination() {
        pagination = PaginationComponent { page -> loadData(page, pageSize) }
        add(pagination)
    }

    private fun loadData(page: Long, size: Int) {
        val query = buildQuery()
        val pageData = logService.pageQuery(Page(page, size.toLong()), query)
        grid.setItems(pageData.records)
        pagination.update(pageData.current, pageData.pages, pageData.total)
    }

    /**
     * 导出 Excel
     */
    private fun exportToExcel() {
        runCatching {
            val query = buildQuery()
            val pageData = logService.pageQuery(Page(0, Long.MAX_VALUE), query)
            val records = pageData.records
            
            if (records.isEmpty()) {
                showError("没有数据可导出")
                return
            }
            
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
                    createTime = log.createTime.formatDateTime()
                )
            }
            
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val fileName = "操作日志_${timestamp}.xlsx"

            ExcelUtil.exportExcel(fileName, OperationLogExportDto::class.java, exportData)
            showSuccess("导出成功，共 ${records.size} 条记录")

        }.onFailure { e ->
            showError("导出失败：${e.message}")
        }
    }
    
    private fun buildQuery() = LogQueryDto(
        username = usernameField.value?.trim()?.takeIf { it.isNotBlank() },
        module = moduleField.value?.trim()?.takeIf { it.isNotBlank() },
        startTime = startDatePicker.value?.atStartOfDay(),
        endTime = endDatePicker.value?.atTime(23, 59, 59)
    )
}
