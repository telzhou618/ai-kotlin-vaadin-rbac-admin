package com.rbac.annotation

/**
 * 页面权限注解
 * 用于标注访问页面所需的权限
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermissions(
    /**
     * 所需权限列表
     * 例如：["user:view", "user:edit"]
     */
    vararg val value: String,
    
    /**
     * 验证模式
     * AND: 需要拥有所有权限
     * OR: 只需拥有其中一个权限
     */
    val logical: Logical = Logical.AND
)

enum class Logical {
    AND,  // 必须拥有所有权限
    OR    // 只需拥有其中一个权限
}
