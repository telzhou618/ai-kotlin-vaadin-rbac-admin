# Vaadin 打包问题修复

## 问题描述

打包后启动 JAR 包时出现错误：

```
Caused by: java.lang.RuntimeException: Atmosphere init failed
Caused by: jakarta.servlet.ServletException: java.lang.IllegalStateException: Shutdown in progress
com.vaadin.flow.server.ServletException: Failed to initialize Atmosphere for SpringServlet. Push will not work.
```

## 问题原因

Vaadin 应用需要在打包时进行**生产模式构建**，将前端资源编译并打包到 JAR 中。如果不进行生产构建，运行时会找不到前端资源，导致 Atmosphere（Vaadin 的推送机制）初始化失败。

## 解决方案

### 方案一：使用 Gradle 生产构建（推荐）

#### 1. 配置 build.gradle.kts

在 `build.gradle.kts` 中添加 Vaadin 生产模式配置：

```kotlin
// Vaadin 生产构建配置
vaadin {
    productionMode = true
}
```

#### 2. 执行生产构建

```bash
# Windows
gradle clean build -Pvaadin.productionMode -x test

# Linux/Mac
./gradlew clean build -Pvaadin.productionMode -x test
```

**重要参数说明：**
- `clean`: 清理旧的构建文件
- `build`: 构建项目
- `-Pvaadin.productionMode`: 启用 Vaadin 生产模式
- `-x test`: 跳过测试（可选）

#### 3. 等待构建完成

生产构建会执行以下步骤：
1. 安装 Node.js 和 pnpm（如果没有）
2. 安装前端依赖
3. 编译前端资源
4. 优化和压缩前端资源
5. 打包到 JAR 中

**构建时间：** 首次构建约 5-10 分钟（需要下载依赖）

### 方案二：使用 bootJar 任务

```bash
# Windows
gradle clean bootJar -Pvaadin.productionMode

# Linux/Mac
./gradlew clean bootJar -Pvaadin.productionMode
```

### 方案三：分步构建

```bash
# 1. 清理
gradle clean

# 2. 构建前端
gradle vaadinBuildFrontend

# 3. 打包
gradle build -x test
```

## 验证构建

### 1. 检查 JAR 包大小

生产构建的 JAR 包会比开发模式大：

```bash
# 开发模式（错误）：约 80-90 MB
# 生产模式（正确）：约 100-120 MB

ls -lh build/libs/rbac-system-1.0.0.jar
```

### 2. 检查 JAR 包内容

```bash
# 查看 JAR 包中是否包含前端资源
jar -tf build/libs/rbac-system-1.0.0.jar | grep "META-INF/VAADIN"

# 应该看到类似输出：
# META-INF/VAADIN/
# META-INF/VAADIN/build/
# META-INF/VAADIN/config/
# META-INF/VAADIN/webapp/
```

### 3. 测试运行

```bash
java -jar build/libs/rbac-system-1.0.0.jar
```

应该能正常启动，无 Atmosphere 错误。

## 常见问题

### Q1: 构建时间太长

**原因：** 首次构建需要下载 Node.js、pnpm 和前端依赖。

**解决方案：**
- 首次构建耐心等待
- 后续构建会使用缓存，速度会快很多
- 可以使用 `--offline` 参数离线构建（需要先在线构建一次）

### Q2: 构建失败 - 网络问题

**错误信息：**
```
Could not download pnpm
Could not download Node.js
```

**解决方案：**

1. 配置国内镜像（推荐）

在项目根目录创建 `.npmrc` 文件：

```
registry=https://registry.npmmirror.com
```

2. 使用代理

```bash
gradle build -Pvaadin.productionMode -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7890
```

3. 手动安装 Node.js 和 pnpm

```bash
# 安装 Node.js 18+
# 从 https://nodejs.org/ 下载安装

# 安装 pnpm
npm install -g pnpm

# 然后再构建
gradle build -Pvaadin.productionMode
```

### Q3: 构建失败 - 内存不足

**错误信息：**
```
JavaScript heap out of memory
```

**解决方案：**

在 `gradle.properties` 中增加内存：

```properties
org.gradle.jvmargs=-Xmx4096m
```

### Q4: 开发模式可以运行，生产模式不行

**原因：** 开发模式使用 Vite 开发服务器，生产模式使用预编译的资源。

**解决方案：**
- 确保使用 `-Pvaadin.productionMode` 参数
- 清理缓存：`gradle clean`
- 删除 `node_modules` 和 `frontend/generated` 目录
- 重新构建

### Q5: 构建后 JAR 包很大

**原因：** 生产构建包含了所有前端资源。

**正常大小：**
- 开发模式：80-90 MB
- 生产模式：100-120 MB

**如果超过 150 MB：**
- 检查是否包含了不必要的依赖
- 检查是否有重复的资源

## 完整构建流程

### 开发环境

```bash
# 开发模式运行（不需要生产构建）
gradle bootRun
```

### 生产环境

```bash
# 1. 清理旧构建
gradle clean

# 2. 生产构建
gradle build -Pvaadin.productionMode -x test

# 3. 验证 JAR 包
ls -lh build/libs/rbac-system-1.0.0.jar

# 4. 测试运行
java -jar build/libs/rbac-system-1.0.0.jar

# 5. 访问测试
curl http://localhost:8080
```

## 自动化脚本

### build-prod.sh（Linux/Mac）

```bash
#!/bin/bash

echo "=========================================="
echo "开始生产构建..."
echo "=========================================="
echo ""

# 清理
echo "1. 清理旧构建..."
./gradlew clean

# 构建
echo "2. 执行生产构建（这可能需要几分钟）..."
./gradlew build -Pvaadin.productionMode -x test

# 检查结果
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "构建成功！"
    echo "=========================================="
    echo ""
    echo "JAR 包位置: build/libs/rbac-system-1.0.0.jar"
    echo "JAR 包大小:"
    ls -lh build/libs/rbac-system-1.0.0.jar
    echo ""
    echo "测试运行: java -jar build/libs/rbac-system-1.0.0.jar"
else
    echo ""
    echo "=========================================="
    echo "构建失败！"
    echo "=========================================="
    echo ""
    echo "请查看上面的错误信息"
    exit 1
fi
```

### build-prod.bat（Windows）

```batch
@echo off
chcp 65001 >nul
title 生产构建

echo ==========================================
echo 开始生产构建...
echo ==========================================
echo.

REM 清理
echo 1. 清理旧构建...
gradle clean

REM 构建
echo 2. 执行生产构建（这可能需要几分钟）...
gradle build -Pvaadin.productionMode -x test

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================
    echo 构建成功！
    echo ==========================================
    echo.
    echo JAR 包位置: build\libs\rbac-system-1.0.0.jar
    echo JAR 包大小:
    dir build\libs\rbac-system-1.0.0.jar
    echo.
    echo 测试运行: java -jar build\libs\rbac-system-1.0.0.jar
) else (
    echo.
    echo ==========================================
    echo 构建失败！
    echo ==========================================
    echo.
    echo 请查看上面的错误信息
    pause
    exit /b 1
)

pause
```

## 性能优化

### 1. 启用构建缓存

在 `gradle.properties` 中：

```properties
org.gradle.caching=true
org.gradle.parallel=true
```

### 2. 使用 Gradle 守护进程

```properties
org.gradle.daemon=true
```

### 3. 增加内存

```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
```

## 持续集成

### GitHub Actions

```yaml
name: Build Production

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    
    - name: Cache Gradle packages
      uses: actions/cache@v2
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
    
    - name: Build with Gradle
      run: ./gradlew clean build -Pvaadin.productionMode -x test
    
    - name: Upload artifact
      uses: actions/upload-artifact@v2
      with:
        name: rbac-system
        path: build/libs/*.jar
```

## 总结

**关键点：**
1. ✅ 必须使用 `-Pvaadin.productionMode` 参数
2. ✅ 首次构建需要 5-10 分钟
3. ✅ 生产 JAR 包约 100-120 MB
4. ✅ 构建后必须包含 `META-INF/VAADIN` 目录

**正确命令：**
```bash
gradle clean build -Pvaadin.productionMode -x test
```

**错误命令：**
```bash
gradle build -x test  # ❌ 缺少 -Pvaadin.productionMode
```

---

**更新日期**：2026-02-04  
**适用版本**：Vaadin 24.3.0
