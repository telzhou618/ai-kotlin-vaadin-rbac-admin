import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.vaadin") version "24.3.0"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
}

group = "com.rbac"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Vaadin
    implementation("com.vaadin:vaadin-spring-boot-starter:24.3.0")

    // Karibu DSL
    implementation("com.github.mvysny.karibudsl:karibu-dsl:2.1.2")

    // MyBatis-Plus
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.5")
    // MySQL
    implementation("com.mysql:mysql-connector-j:8.2.0")
    // 动态数据源
    implementation("com.baomidou:dynamic-datasource-spring-boot-starter:4.2.0")
    // ShardingSphere
    implementation("org.apache.shardingsphere:shardingsphere-jdbc-core:5.4.0")
    // 连接池
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Sa-Token
    implementation("cn.dev33:sa-token-spring-boot3-starter:1.37.0")
    // Sa-Token Redis 集成（使用 Jackson 序列化）
    implementation("cn.dev33:sa-token-redis-jackson:1.37.0")
    // Redis 连接池
    implementation("org.apache.commons:commons-pool2:2.12.0")

    // Hutool
    implementation("cn.hutool:hutool-all:5.8.24")

    // EasyExcel
    implementation("com.alibaba:easyexcel:3.3.4")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:24.3.0")
    }
}

// Vaadin 生产构建配置
vaadin {
    // 生产模式优化
    productionMode = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
