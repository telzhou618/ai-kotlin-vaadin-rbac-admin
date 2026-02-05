package com.rbac.config

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    fun masterDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun shardingDataSource(): DataSource {
        // TODO
        return masterDataSource()
    }

    @Primary
    @Bean
    fun dataSource(): DataSource {
        val providers = listOf(
            DynamicDataSourceProvider {
                mapOf(
                    "master" to masterDataSource(),
                    "sharding" to shardingDataSource()
                )
            }
        )
        val dynamicDataSource = DynamicRoutingDataSource(providers)
        dynamicDataSource.setPrimary("master")
        return dynamicDataSource
    }
}