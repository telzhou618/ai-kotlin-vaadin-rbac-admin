# 主题切换功能 - 快速测试指南

## 功能概述

系统已集成 Vaadin Lumo 主题的明暗模式切换功能，用户可以根据个人喜好选择合适的主题。

## 快速测试

### 1. 在登录页面测试

1. 启动应用，访问 http://localhost:8080
2. 在登录页面右上角找到主题切换按钮
   - 默认是亮色模式，显示 🌙 月亮图标
3. 点击月亮图标
   - 页面立即切换到暗色模式
   - 按钮图标变为 ☀️ 太阳图标
   - 背景变为深色，文字变为浅色
4. 再次点击太阳图标
   - 页面切换回亮色模式

### 2. 在主应用中测试

1. 使用测试账号登录：`admin/admin123`
2. 登录后在顶部导航栏右侧找到主题切换按钮
   - 位置：用户名和退出按钮之间
3. 点击切换按钮测试主题切换
4. 导航到不同页面（用户管理、角色管理等）
   - 确认主题在所有页面保持一致

### 3. 测试主题持久化

1. 切换到暗色模式
2. 刷新浏览器页面（F5）
3. 确认页面仍然是暗色模式
4. 关闭浏览器，重新打开
5. 主题会重置为默认的亮色模式（会话级别）

## 视觉效果对比

### 亮色模式（Light Mode）
- 背景：白色/浅灰色
- 文字：深色/黑色
- 按钮：蓝色主题
- 适合：白天使用、明亮环境

### 暗色模式（Dark Mode）
- 背景：深灰色/黑色
- 文字：浅色/白色
- 按钮：蓝色主题（自动调整亮度）
- 适合：夜间使用、暗光环境、减少眼睛疲劳

## 技术细节

### 实现位置

1. **ThemeService** (`src/main/kotlin/com/rbac/service/ThemeService.kt`)
   - 核心服务类，管理主题状态和切换逻辑

2. **MainLayout** (`src/main/kotlin/com/rbac/ui/MainLayout.kt`)
   - 主应用布局，包含主题切换按钮

3. **LoginView** (`src/main/kotlin/com/rbac/ui/LoginView.kt`)
   - 登录页面，包含主题切换按钮

### 主题存储

- 存储位置：`VaadinSession`
- 存储键：`app.theme`
- 可选值：`light` 或 `dark`
- 生命周期：会话级别（关闭浏览器后重置）

## 常见问题

### Q: 为什么关闭浏览器后主题会重置？
A: 当前主题保存在会话中，关闭浏览器会清除会话。如需持久化，可以扩展功能将主题保存到数据库或浏览器 LocalStorage。

### Q: 如何设置默认主题为暗色模式？
A: 修改 `ThemeService.kt` 中的默认值：
```kotlin
fun getCurrentTheme(): String {
    return VaadinSession.getCurrent()?.getAttribute(THEME_KEY) as? String ?: THEME_DARK
}
```

### Q: 能否根据系统时间自动切换主题？
A: 可以，参考 `docs/THEME_FEATURE.md` 中的扩展建议。

### Q: 自定义样式如何适配明暗模式？
A: 使用 Lumo CSS 变量，例如：
```kotlin
element.style.set("color", "var(--lumo-contrast)")
element.style.set("background", "var(--lumo-base-color)")
```

## 下一步

- 查看完整文档：[docs/THEME_FEATURE.md](THEME_FEATURE.md)
- 扩展功能：将主题保存到数据库
- 自定义主题：修改 Lumo 主题变量
- 添加更多主题：创建自定义主题变体

## 反馈

如有问题或建议，请提交 Issue 或 Pull Request。
