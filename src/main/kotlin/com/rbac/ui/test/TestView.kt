package com.rbac.ui.test

import com.github.mvysny.karibudsl.v10.*
import com.rbac.ui.MainLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route("test", layout = MainLayout::class)
@PageTitle("测试日志")
class TestView(

) : VerticalLayout() {

    init {
        setSizeFull()
        isPadding = true
        h2("测试页面")
    }
}
