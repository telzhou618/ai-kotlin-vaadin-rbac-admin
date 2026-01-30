package com.rbac.dto

import com.alibaba.excel.annotation.ExcelProperty

data class OperationLogExportDto(
    @ExcelProperty("ID")
    val id: Long?,
    
    @ExcelProperty("用户")
    val username: String?,
    
    @ExcelProperty("模块")
    val module: String?,
    
    @ExcelProperty("操作")
    val operation: String?,
    
    @ExcelProperty("状态码")
    val responseCode: String?,
    
    @ExcelProperty("响应消息")
    val responseMsg: String?,
    
    @ExcelProperty("IP")
    val ip: String?,
    
    @ExcelProperty("耗时(ms)")
    val executeTime: Long?,
    
    @ExcelProperty("操作时间")
    val createTime: String?
)
