package com.rbac.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 日期格式化配置
 * 统一将日期格式化为 yyyy-MM-dd HH:mm:ss
 */
@Configuration
class DateFormatConfig {
    
    companion object {
        const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
        const val DATE_PATTERN = "yyyy-MM-dd"
        
        val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        
        /**
         * 格式化 LocalDateTime 为字符串
         */
        fun formatDateTime(dateTime: LocalDateTime?): String {
            return dateTime?.format(DATE_TIME_FORMATTER) ?: ""
        }
        
        /**
         * 格式化 LocalDate 为字符串
         */
        fun formatDate(date: LocalDate?): String {
            return date?.format(DATE_FORMATTER) ?: ""
        }
    }
    
    @Bean
    fun objectMapper(): ObjectMapper {
        val javaTimeModule = JavaTimeModule()
        
        // LocalDateTime 序列化和反序列化
        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DATE_TIME_FORMATTER))
        javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DATE_TIME_FORMATTER))
        
        // LocalDate 序列化和反序列化
        javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer(DATE_FORMATTER))
        javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer(DATE_FORMATTER))
        
        return Jackson2ObjectMapperBuilder.json()
            .modules(javaTimeModule)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    }
}
