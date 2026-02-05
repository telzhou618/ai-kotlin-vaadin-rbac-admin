package com.rbac.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class TransactionConfig {

    @Primary
    @Bean("normalTransactionManager")
    fun normalTransactionManager(
        @Qualifier("masterDataSource") dataSource: DataSource
    ): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean("shardingTransactionManager")
    fun shardingTransactionManager(
        @Qualifier("shardingDataSource") dataSource: DataSource
    ): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }
}