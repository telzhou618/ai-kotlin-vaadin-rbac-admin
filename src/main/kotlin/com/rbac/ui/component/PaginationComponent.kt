package com.rbac.ui.component

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.shared.Tooltip
import com.vaadin.flow.component.textfield.TextField

class PaginationComponent(
    private val onPageChange: (page: Long, size: Int) -> Unit
) : HorizontalLayout() {

    var currentPage = 1L
        private set
    private var totalPages = 1L
    private var totalRecords = 0L
    private var pageSize = 20

    private lateinit var firstPageButton: Button
    private lateinit var prevPageButton: Button
    private lateinit var nextPageButton: Button
    private lateinit var lastPageButton: Button
    private lateinit var pageInfo: TextField
    private lateinit var recordInfo: TextField
    private lateinit var pageSizeSelect: Select<Int>

    init {
        alignItems = FlexComponent.Alignment.CENTER
        isSpacing = true
        
        // 总记录数显示
        recordInfo = textField {
            width = "120px"
            isReadOnly = true
            element.style.set("text-align", "center")
        }

        // 首页按钮
        firstPageButton = button {
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY)
            icon = VaadinIcon.ANGLE_DOUBLE_LEFT.create()
            Tooltip.forComponent(this).setText("首页")
            onLeftClick { goToPage(1) }
        }

        // 上一页按钮
        prevPageButton = button {
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY)
            icon = VaadinIcon.ANGLE_LEFT.create()
            Tooltip.forComponent(this).setText("上一页")
            onLeftClick { goToPage(currentPage - 1) }
        }

        // 页码信息
        pageInfo = textField {
            width = "120px"
            isReadOnly = true
            element.style.set("text-align", "center")
        }

        // 下一页按钮
        nextPageButton = button {
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY)
            icon = VaadinIcon.ANGLE_RIGHT.create()
            Tooltip.forComponent(this).setText("下一页")
            onLeftClick { goToPage(currentPage + 1) }
        }

        // 末页按钮
        lastPageButton = button {
            addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY)
            icon = VaadinIcon.ANGLE_DOUBLE_RIGHT.create()
            Tooltip.forComponent(this).setText("末页")
            onLeftClick { goToPage(totalPages) }
        }

        // 每页显示数量选择
        pageSizeSelect = select {
            width = "90px"
            setItems(10, 20, 50, 100)
            value = 20
            setItemLabelGenerator { "$it 条/页" }
            addValueChangeListener { event ->
                if (event.isFromClient) {
                    pageSize = event.value
                    currentPage = 1
                    onPageChange(currentPage, pageSize)
                }
            }
        }
        
        // 初始化按钮状态
        updateButtonStates()
    }

    fun updatePagination(current: Long, total: Long, records: Long = 0L) {
        currentPage = current
        totalPages = if (total > 0) total else 1
        totalRecords = records
        
        pageInfo.value = "第 $currentPage / $totalPages 页"
        recordInfo.value = "共 $totalRecords 条"
        
        updateButtonStates()
    }

    private fun updateButtonStates() {
        // 首页和上一页按钮状态
        val isFirstPage = currentPage <= 1
        firstPageButton.isEnabled = !isFirstPage
        prevPageButton.isEnabled = !isFirstPage
        
        // 下一页和末页按钮状态
        val isLastPage = currentPage >= totalPages
        nextPageButton.isEnabled = !isLastPage
        lastPageButton.isEnabled = !isLastPage
    }

    private fun goToPage(page: Long) {
        if (page < 1 || page > totalPages || page == currentPage) return
        currentPage = page
        onPageChange(currentPage, pageSize)
    }
}
