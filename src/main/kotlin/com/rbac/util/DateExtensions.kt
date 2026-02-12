package com.rbac.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 日期时间扩展函数
 */

private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
private const val DATE_PATTERN = "yyyy-MM-dd"

private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)

/**
 * 格式化 LocalDateTime 为字符串 (yyyy-MM-dd HH:mm:ss)
 */
fun LocalDateTime?.formatDateTime(): String {
    return this?.format(DATE_TIME_FORMATTER) ?: ""
}

/**
 * 格式化 LocalDate 为字符串 (yyyy-MM-dd)
 */
fun LocalDate?.formatDate(): String {
    return this?.format(DATE_FORMATTER) ?: ""
}

/**
 * 字符串解析为 LocalDateTime
 */
fun String.parseToDateTime(): LocalDateTime? {
    return try {
        LocalDateTime.parse(this, DATE_TIME_FORMATTER)
    } catch (e: Exception) {
        null
    }
}

/**
 * 字符串解析为 LocalDate
 */
fun String.parseToDate(): LocalDate? {
    return try {
        LocalDate.parse(this, DATE_FORMATTER)
    } catch (e: Exception) {
        null
    }
}
