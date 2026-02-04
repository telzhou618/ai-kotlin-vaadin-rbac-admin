# IntelliJ IDEA 中构建生产环境包

## 方法一：使用 Gradle 面板（推荐）

### 1. 打开 Gradle 面板

在 IDEA 右侧找到 **Gradle** 面板，如果没有显示：
- 点击菜单：`View` → `Tool Windows` → `Gradle`
- 或使用快捷键：`Ctrl + Shift + A`（Windows/Linux）或 `Cmd + Shift + A`（Mac），搜索 "Gradle"

### 2. 配置 Gradle 参数

#### 方式 A：通过 Run Configuration（推荐）

1. 点击 IDEA 右上角的 **Run/Debug Configurations** 下拉菜单
2. 选择 **Edit Configurations...**
3. 点击左上角的 **+** 号
4. 选择 **Gradle**
5. 配置如下：

```
Name: Build Production
Gradle project: rbac-system
Tasks: clean build
Arguments: -Pvaadin.productionMode -x test
```

6. 点击 **OK** 保存
7. 点击右上角的 **绿色运行按钮** 或按 `Shift + F10`

#### 方式 B：直接在 Gradle 面板执行

1. 在 Gradle 面板中，展开项目树：
   ```
   rbac-system
   └── Tasks
       ├── build
       │   ├── build
       │   └── clean
       └── ...
   ```

2. **右键点击** `build` → `build` 任务
3. 选择 **Modify Run Configuration...**
4. 在 **Arguments** 中添加：
   ```
   -Pvaadin.productionMode -x test
   ```
5. 点击 **OK**
6. **双击** `build` 任务执行

### 3. 查看构建进度

构建过程会在 IDEA 底部的 **Build** 面板显示：
- 可以看到实时日志
- 首次构建需要 5-10 分钟
- 会显示下载 Node.js、pnpm 和前端依赖的进度

### 4. 查看构建结果

构建成功后：
1. 在 IDEA 左侧的 **Project** 面板中
2. 展开 `build` → `libs`
3. 找到 `rbac-system-1.0.0.jar`
4. 右键点击 → **Copy Path/Reference...** → **Absolute Path**

## 方法二：使用 IDEA 终端

### 1. 打开终端

- 点击 IDEA 底部的 **Terminal** 标签
- 或使用快捷键：`Alt + F12`（Windows/Linux）或 `Option + F12`（Mac）

### 2. 执行构建命令

```bash
# Windows
gradle clean build -Pvaadin.productionMode -x test

# Linux/Mac
./gradlew clean build -Pvaadin.productionMode -x test
```

### 3. 等待构建完成

终端会显示构建进度，完成后会显示：
```
BUILD SUCCESSFUL in 5m 30s
```

## 方法三：创建自定义 Gradle 任务

### 1. 编辑 build.gradle.kts

在文件末尾添加自定义任务：

```kotlin
// 自定义生产构建任务
tasks.register("buildProduction") {
    group = "build"
    description = "Build production JAR with Vaadin production mode"
    
    dependsOn("clean")
    dependsOn("build")
    
    doFirst {
        // 设置生产模式
        project.ext.set("vaadin.productionMode", true)
    }
}
```

### 2. 刷新 Gradle 项目

- 点击 Gradle 面板右上角的 **刷新** 按钮
- 或右键点击项目 → **Gradle** → **Refresh Gradle Project**

### 3. 执行自定义任务

在 Gradle 面板中：
1. 展开 `Tasks` → `build`
2. 找到 `buildProduction` 任务
3. **双击** 执行

## 方法四：使用 Maven Helper（如果安装了）

如果你安装了 Maven Helper 插件，可以：

1. 右键点击 `build.gradle.kts`
2. 选择 **Run Gradle Task**
3. 输入：`clean build -Pvaadin.productionMode -x test`
4. 按 Enter 执行

## 配置 IDEA 设置

### 1. 增加 Gradle 内存

如果构建时内存不足：

1. 打开 `Settings/Preferences`（`Ctrl + Alt + S` 或 `Cmd + ,`）
2. 导航到：`Build, Execution, Deployment` → `Build Tools` → `Gradle`
3. 在 **Gradle VM options** 中添加：
   ```
   -Xmx4096m -XX:MaxMetaspaceSize=1024m
   ```
4. 点击 **OK**

### 2. 配置 Gradle JVM

确保使用 JDK 17+：

1. 在 Gradle 设置页面
2. **Gradle JVM** 选择 `Project SDK (17)` 或更高版本
3. 如果没有，点击 **Download JDK...** 下载

### 3. 启用离线模式（可选）

如果网络不稳定，首次在线构建成功后：

1. 在 Gradle 设置页面
2. 勾选 **Offline work**
3. 后续构建会使用本地缓存

## 常见问题

### Q1: Gradle 面板找不到任务

**解决方案：**
1. 点击 Gradle 面板右上角的 **刷新** 按钮
2. 或右键点击项目 → **Gradle** → **Refresh Gradle Project**
3. 等待 IDEA 重新加载 Gradle 配置

### Q2: 构建失败 - 找不到 Gradle

**解决方案：**
1. 确保项目根目录有 `gradlew` 或 `gradlew.bat`
2. 在 Gradle 设置中，选择 **Use Gradle from: 'gradle-wrapper.properties' file**

### Q3: 构建很慢

**解决方案：**
1. 增加 Gradle 内存（见上面的配置）
2. 启用 Gradle 守护进程（默认已启用）
3. 启用并行构建：在 `gradle.properties` 中添加：
   ```properties
   org.gradle.parallel=true
   org.gradle.caching=true
   ```

### Q4: 网络问题导致下载失败

**解决方案：**

1. 配置 IDEA 代理：
   - `Settings` → `Appearance & Behavior` → `System Settings` → `HTTP Proxy`
   - 配置代理服务器

2. 配置 npm 镜像：
   - 在项目根目录创建 `.npmrc` 文件
   - 添加：`registry=https://registry.npmmirror.com`

### Q5: 构建后找不到 JAR 包

**解决方案：**
1. 在 Project 面板中右键点击项目根目录
2. 选择 **Reload from Disk**
3. 展开 `build` → `libs` 查看

## 快捷操作

### 创建快捷键

1. 打开 `Settings` → `Keymap`
2. 搜索你创建的 Run Configuration（如 "Build Production"）
3. 右键点击 → **Add Keyboard Shortcut**
4. 设置快捷键（如 `Ctrl + Shift + B`）
5. 点击 **OK**

以后只需按快捷键即可一键构建！

### 添加到工具栏

1. 右键点击 IDEA 工具栏空白处
2. 选择 **Customize Toolbar...**
3. 点击 **+** 号
4. 选择 **Add Action...**
5. 搜索你的 Run Configuration
6. 添加到工具栏

## 构建完成后

### 1. 验证 JAR 包

在 Project 面板中：
1. 找到 `build/libs/rbac-system-1.0.0.jar`
2. 右键点击 → **Open In** → **Explorer/Finder**
3. 查看文件大小（应该是 100-120 MB）

### 2. 测试运行

在 IDEA 终端中：
```bash
java -jar build/libs/rbac-system-1.0.0.jar
```

或创建 Run Configuration：
1. **Edit Configurations...**
2. 点击 **+** → **JAR Application**
3. 配置：
   ```
   Name: Run Production JAR
   Path to JAR: build/libs/rbac-system-1.0.0.jar
   ```
4. 点击 **OK**
5. 运行测试

### 3. 复制到部署目录

在 IDEA 中：
1. 右键点击 JAR 包
2. 选择 **Copy**
3. 在 Project 面板中找到 `deploy` 目录
4. 右键点击 → **Paste**

## 推荐工作流

### 日常开发

```
1. 修改代码
2. 使用 bootRun 任务运行（开发模式）
3. 测试功能
4. 提交代码
```

### 发布版本

```
1. 更新版本号（build.gradle.kts）
2. 使用 "Build Production" 配置构建
3. 等待 5-10 分钟（首次）
4. 验证 JAR 包
5. 测试运行
6. 部署到服务器
```

## 视觉指南

### Gradle 面板位置

```
┌─────────────────────────────────────────┐
│ File  Edit  View  Navigate  Code  ...  │
├─────────────────────────────────────────┤
│                                    │ G  │
│                                    │ r  │
│        代码编辑区域                │ a  │
│                                    │ d  │
│                                    │ l  │
│                                    │ e  │
├─────────────────────────────────────────┤
│ Terminal  Build  Run  Debug  ...       │
└─────────────────────────────────────────┘
```

### Run Configuration 位置

```
┌─────────────────────────────────────────┐
│ File  Edit  View  ...  [▼ Build Prod] ▶│ ← 这里
├─────────────────────────────────────────┤
│                                         │
│        代码编辑区域                     │
│                                         │
└─────────────────────────────────────────┘
```

## 总结

**最简单的方法：**

1. 创建 Run Configuration（只需配置一次）
2. 点击运行按钮
3. 等待构建完成
4. 在 `build/libs` 中找到 JAR 包

**关键参数：** `-Pvaadin.productionMode -x test`

**不要忘记！** 否则会出现 Atmosphere 错误。

---

**提示：** 首次构建需要下载依赖，请确保网络连接良好，耐心等待 5-10 分钟。
