package com.rbac.util

import com.alibaba.excel.EasyExcel
import com.vaadin.flow.component.UI
import com.vaadin.flow.server.StreamResource
import java.io.ByteArrayOutputStream

/**
 * Excel 导出工具类（使用 EasyExcel）
 */
object ExcelUtil {
    
    /**
     * 导出 Excel 文件
     * @param filename 文件名（不含扩展名）
     * @param clazz 数据类的 Class
     * @param data 数据列表
     */
    fun <T> exportExcel(filename: String, clazz: Class<T>, data: List<T>) {
        // 使用 ByteArrayOutputStream 生成 Excel
        val byteArrayOutputStream = ByteArrayOutputStream()
        
        EasyExcel.write(byteArrayOutputStream, clazz)
            .sheet("数据")
            .doWrite(data)
        
        // 创建下载资源
        val bytes = byteArrayOutputStream.toByteArray()
        val resource = StreamResource("$filename.xlsx") { stream, _ ->
            stream.write(bytes)
        }
        
        // 注册资源并触发下载
        val registration = UI.getCurrent().session.resourceRegistry.registerResource(resource)
        UI.getCurrent().page.setLocation(registration.resourceUri)
    }
}
