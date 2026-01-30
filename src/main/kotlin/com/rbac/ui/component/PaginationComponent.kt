package com.rbac.ui.component

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField

class PaginationComponent(
    private val onPageChange: (page: Long, size: Int) -> Unit
) : HorizontalLayout() {
    
    private var currentPage = 1L
    private var totalPages = 1L
    private var pageSize = 10
    
    private lateinit var pageInfo: TextField
    private lateinit var pageSizeSelect: Select<Int>
    
    init {
        setAlignItems(FlexComponent.Alignment.CENTER)
        isSpacing = true
        
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
            value = pageSize
            addValueChangeListener {
                pageSize = it.value
                goToPage(1)
            }
        }
    }
    
    fun updatePagination(current: Long, total: Long) {
        currentPage = current
        totalPages = total
        pageInfo.value = "第 $currentPage / $totalPages 页"
    }
    
    private fun goToPage(page: Long) {
        if (page < 1 || page > totalPages) return
        currentPage = page
        onPageChange(currentPage, pageSize)
    }
}
