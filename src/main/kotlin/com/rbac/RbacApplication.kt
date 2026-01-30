package com.rbac

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("com.rbac.mapper")
class RbacApplication

fun main(args: Array<String>) {
    runApplication<RbacApplication>(*args)
}
