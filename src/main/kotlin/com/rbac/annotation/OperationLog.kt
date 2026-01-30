package com.rbac.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OperationLog(
    val module: String,
    val operation: String
)
