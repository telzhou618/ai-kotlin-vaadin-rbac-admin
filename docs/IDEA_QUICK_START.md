# IDEA 快速构建指南（3 步完成）

## 步骤 1：创建 Run Configuration

### 1.1 打开配置窗口

点击 IDEA 右上角的下拉菜单（运行按钮旁边）→ **Edit Configurations...**

```
┌─────────────────────────────────────────┐
│                    [▼ 配置名称] ▶ □ ⚙  │ ← 点击这里
└─────────────────────────────────────────┘
```

### 1.2 添加新配置

1. 点击左上角的 **+** 号
2. 选择 **Gradle**

### 1.3 填写配置

```
Name: Build Production
Gradle project: rbac-system
Tasks: clean build
Arguments: -Pvaadin.productionMode -x test
```

**重要：** `Arguments` 必须填写 `-Pvaadin.productionMode -x test`

### 1.4 保存

点击 **OK** 保存配置

## 步骤 2：执行构建

### 2.1 选择配置

在右上角的下拉菜单中选择 **Build Production**

### 2.2 点击运行

点击绿色的 **运行按钮** ▶ 或按 `Shift + F10`

### 2.3 等待完成

- 底部会显示构建进度
- 首次构建需要 **5-10 分钟**
- 会下载 Node.js、pnpm 和前端依赖
- 请耐心等待，不要中断

## 步骤 3：查看结果

### 3.1 找到 JAR 包

在左侧 **Project** 面板中：
```
rbac-system
└── build
    └── libs
        └── rbac-system-1.0.0.jar  ← 这个文件
```

### 3.2 验证大小

右键点击 JAR 包 → **Properties** 或 **Show in Explorer**

**正确大小：** 100-120 MB  
**错误大小：** 80-90 MB（说明构建错误）

### 3.3 测试运行

在 IDEA 底部打开 **Terminal**，执行：

```bash
java -jar build/libs/rbac-system-1.0.0.jar
```

访问：http://localhost:8080

## 完成！🎉

现在你可以：
1. 复制 JAR 包到 `deploy` 目录
2. 使用 `deploy/start.bat` 或 `deploy/start.sh` 启动
3. 部署到服务器

## 常见问题

### Q: 找不到 Gradle 面板？

A: 点击菜单 `View` → `Tool Windows` → `Gradle`

### Q: 构建失败？

A: 检查：
1. 是否添加了 `-Pvaadin.productionMode` 参数
2. 网络连接是否正常
3. JDK 版本是否为 17+

### Q: 构建很慢？

A: 首次构建需要下载依赖，正常需要 5-10 分钟。后续构建会快很多（1-2 分钟）。

## 快捷方式

### 方法 A：使用 Gradle 面板

1. 打开右侧的 **Gradle** 面板
2. 展开 `Tasks` → `build`
3. 右键点击 `build` 任务
4. 选择 **Modify Run Configuration...**
5. 在 **Arguments** 中添加：`-Pvaadin.productionMode -x test`
6. 双击 `build` 任务执行

### 方法 B：使用终端

在 IDEA 底部打开 **Terminal**，执行：

```bash
# Windows
gradle clean build -Pvaadin.productionMode -x test

# Linux/Mac
./gradlew clean build -Pvaadin.productionMode -x test
```

## 记住这个参数！

```
-Pvaadin.productionMode
```

**没有这个参数，JAR 包运行会报错！**

---

**详细文档：** [IDEA 完整构建指南](IDEA_BUILD_GUIDE.md)
