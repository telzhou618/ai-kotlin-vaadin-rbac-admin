package com.rbac.ui.test

import cn.hutool.json.JSONUtil
import com.github.mvysny.karibudsl.v10.*
import com.rbac.ui.MainLayout
import com.rbac.util.NotifyUtil
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.EmailValidator
import com.vaadin.flow.data.validator.StringLengthValidator
import com.vaadin.flow.router.Route

@Route("dsl-form", layout = MainLayout::class)
class BasicFormView : VerticalLayout() {

    data class Person(
        var name: String = "",
        var email: String = "",
    )

    private val binder = Binder(Person::class.java)

    init {
        setSizeFull()
        isPadding = true

        h2("人员信息表单")

        formLayout {
            setResponsiveSteps(
                FormLayout.ResponsiveStep("0", 1),   // 屏幕宽度 0px 以上：1列
                FormLayout.ResponsiveStep("500px", 2)  // 屏幕宽度 500px 以上：2列
            )
            // 姓名字段 - 必填，长度 2-20
            textField("姓名") {
                setWidthFull()
                placeholder = "请输入姓名"
                isRequired = true
                binder.forField(this)
                    .asRequired("姓名不能为空")
                    .withValidator(StringLengthValidator("姓名长度必须在 2-20 个字符之间", 2, 20))
                    .bind(Person::name.name)
            }
            // 邮箱字段 - 必填，邮箱格式验证
            emailField("邮箱") {
                setWidthFull()
                placeholder = "请输入邮箱"
                isRequired = true
                binder.forField(this)
                    .asRequired("邮箱不能为空")
                    .withValidator(EmailValidator("请输入有效的邮箱地址"))
                    .bind(Person::email.name)
            }
        }

        // 按钮组
        horizontalLayout {
            button("保存") {
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                icon = VaadinIcon.CHECK.create()
                onLeftClick {
                    if (binder.validate().isOk) {
                        val person = Person()
                        binder.writeBean(person)
                        NotifyUtil.showInfo(JSONUtil.toJsonStr(person))
                    } else {
                        NotifyUtil.showError("请检查表单输入")
                    }
                }
            }
            button("重置") {
                addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                icon = VaadinIcon.REFRESH.create()
                onLeftClick {
                    binder.readBean(Person())
                }
            }
        }
    }
}
