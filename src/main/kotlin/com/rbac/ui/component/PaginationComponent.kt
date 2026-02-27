package com.rbac.ui.component

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class PaginationComponent(
    private val onChange: (page: Long) -> Unit
) : HorizontalLayout() {

    var page = 1L
        private set
    private var totalPages = 1L

    private lateinit var prevBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var pageNumSpan: Span
    private lateinit var pageTotalSpan: Span
    private lateinit var totalRecordsSpan: Span

    init {
        alignItems = FlexComponent.Alignment.CENTER

        span("共 ")
        totalRecordsSpan = span("0")
        span(" 条 ")

        prevBtn = button {
            icon = VaadinIcon.ANGLE_LEFT.create()
            addThemeVariants(ButtonVariant.LUMO_SMALL)
            onLeftClick { goTo(page - 1) }
        }

        span("第 ")
        pageNumSpan = span("1")
        span(" / ")
        pageTotalSpan = span("1")
        span(" 页")

        nextBtn = button {
            icon = VaadinIcon.ANGLE_RIGHT.create()
            addThemeVariants(ButtonVariant.LUMO_SMALL)
            onLeftClick { goTo(page + 1) }
        }

        updateStates()
    }

    fun update(current: Long, total: Long, records: Long = 0) {
        page = current
        totalPages = total.coerceAtLeast(1)
        pageNumSpan.text = "$page"
        pageTotalSpan.text = "$totalPages"
        totalRecordsSpan.text = "$records"
        updateStates()
    }

    private fun updateStates() {
        prevBtn.isEnabled = page > 1
        nextBtn.isEnabled = page < totalPages
    }

    private fun goTo(target: Long) {
        if (target in 1..totalPages && target != page) {
            page = target
            onChange(page)
        }
    }
}
