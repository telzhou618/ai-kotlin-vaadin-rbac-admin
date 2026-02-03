package com.rbac.ui.component

import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField

class PaginationComponent(
    private val onPageChange: (page: Long, size: Int) -> Unit
) : HorizontalLayout() {

    var currentPage = 1L
        private set
    private var totalPages = 1L
    private var totalRecords = 0L  // 总记录数
    private var pageSize = 20  // 默认每页20条

    private lateinit var pageInfo: TextField
    private lateinit var recordInfo: TextField
    private lateinit var pageSizeSelect: Select<Int>

    init {
        alignItems = FlexComponent.Alignment.CENTER
        isSpacing = true

        // 总记录数显示
        recordInfo = textField {
            width = "150px"
            isReadOnly = true
        }

        button("首页") {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
            onLeftClick { goToPage(1) }
        }

        button("上一页") {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
            onLeftClick { goToPage(currentPage - 1) }
        }

        pageInfo = textField {
            width = "150px"
            isReadOnly = true
        }

        button("下一页") {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
            onLeftClick { goToPage(currentPage + 1) }
        }

        button("末页") {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
            onLeftClick { goToPage(totalPages) }
        }

        pageSizeSelect = select {
            width = "100px"
            setItems(10, 20, 50, 100)
            value = 20  // 默认选中20
            addValueChangeListener {
                pageSize = it.value
                goToPage(1)
            }
        }
    }

    fun updatePagination(current: Long, total: Long, records: Long = 0L) {
        currentPage = current
        totalPages = total
        totalRecords = records
        pageInfo.value = "第 $currentPage / $totalPages 页"
        recordInfo.value = "共 $totalRecords 条记录"
    }

    private fun goToPage(page: Long) {
        if (page < 1 || page > totalPages) return
        currentPage = page
        onPageChange(currentPage, pageSize)
    }
}
