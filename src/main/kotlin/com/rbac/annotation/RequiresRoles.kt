package com.rbac.annotation

/**
 * 页面角色注解
 * 用于标注访问页面所需的角色
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoles(
    /**
     * 所需角色列表
     * 例如：["admin", "manager"]
     */
    vararg val value: String,
    
    /**
     * 验证模式
     * AND: 需要拥有所有角色
     * OR: 只需拥有其中一个角色
     */
    val logical: Logical = Logical.AND
)
