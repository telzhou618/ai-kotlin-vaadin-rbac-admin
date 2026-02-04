# Lumo 主题明暗切换功能

## 功能说明

本系统已集成 Vaadin 官方的 Lumo 主题，支持明暗模式切换。用户可以根据个人喜好或环境光线选择合适的主题。

## 功能特性

### 1. 主题切换按钮
- **位置**：顶部导航栏右侧（用户名和退出按钮之间）
- **图标**：
  - 亮色模式：显示月亮图标 🌙
  - 暗色模式：显示太阳图标 ☀️
- **操作**：点击按钮即可切换主题

### 2. 主题持久化
- 主题选择会保存在用户会话中
- 刷新页面后主题保持不变
- 重新登录后需要重新选择（可扩展为保存到数据库）

### 3. 全局生效
主题切换在以下页面均生效：
- 登录页面
- 主应用页面（MainLayout）
- 访问拒绝页面
- 所有子页面（用户管理、角色管理等）

## 技术实现

### 核心类：ThemeService

```kotlin
@Service
class ThemeService {
    // 获取当前主题
    fun getCurrentTheme(): String
    
    // 设置主题
    fun setTheme(theme: String)
    
    // 切换主题
    fun toggleTheme()
    
    // 初始化主题
    fun initTheme()
    
    // 判断是否为暗色主题
    fun isDarkTheme(): Boolean
}
```

### 使用方式

#### 1. 在布局中注入服务
```kotlin
class MainLayout(
    private val themeService: ThemeService
) : AppLayout() {
    init {
        // 初始化主题
        themeService.initTheme()
    }
}
```

#### 2. 添加切换按钮
```kotlin
button {
    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
    icon = if (themeService.isDarkTheme()) {
        VaadinIcon.SUN_O.create()
    } else {
        VaadinIcon.MOON_O.create()
    }
    onLeftClick { 
        themeService.toggleTheme()
        // 刷新图标
        icon = if (themeService.isDarkTheme()) {
            VaadinIcon.SUN_O.create()
        } else {
            VaadinIcon.MOON_O.create()
        }
    }
}
```

## Lumo 主题变量

Vaadin Lumo 主题提供了丰富的 CSS 变量，会根据明暗模式自动调整：

### 颜色变量
- `--lumo-primary-color` - 主色调
- `--lumo-error-color` - 错误色
- `--lumo-success-color` - 成功色
- `--lumo-contrast` - 对比色（文字颜色）
- `--lumo-base-color` - 基础背景色

### 使用示例
```kotlin
element.style.set("color", "var(--lumo-error-color)")
element.style.set("background", "var(--lumo-base-color)")
```

## 扩展建议

### 1. 持久化到数据库
在 `sys_user` 表中添加 `theme_preference` 字段：
```sql
ALTER TABLE sys_user ADD COLUMN theme_preference VARCHAR(10) DEFAULT 'light';
```

修改 ThemeService：
```kotlin
fun loadUserTheme(userId: Long) {
    val user = userService.getById(userId)
    setTheme(user.themePreference ?: THEME_LIGHT)
}

fun saveUserTheme(userId: Long, theme: String) {
    userService.updateThemePreference(userId, theme)
    setTheme(theme)
}
```

### 2. 自动切换
根据系统时间自动切换主题：
```kotlin
fun autoSwitchByTime() {
    val hour = LocalTime.now().hour
    val theme = if (hour in 6..18) THEME_LIGHT else THEME_DARK
    setTheme(theme)
}
```

### 3. 跟随系统
使用浏览器 API 检测系统主题：
```javascript
// 在前端添加
const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');
if (darkModeQuery.matches) {
    // 设置为暗色主题
}
```

## 测试步骤

1. 启动应用并访问登录页面
2. 点击右上角的主题切换按钮
3. 观察页面颜色变化：
   - 亮色模式：白色背景，深色文字
   - 暗色模式：深色背景，浅色文字
4. 登录后在主应用中再次测试切换
5. 刷新页面，确认主题保持不变

## 注意事项

1. **会话级别**：当前主题保存在 VaadinSession 中，关闭浏览器后会重置
2. **兼容性**：Lumo 主题在所有现代浏览器中均良好支持
3. **自定义样式**：如果使用了自定义 CSS，需要确保同时支持明暗模式
4. **图标选择**：建议使用 Vaadin 内置图标，它们会自动适配主题颜色

## 相关文件

- `src/main/kotlin/com/rbac/service/ThemeService.kt` - 主题服务
- `src/main/kotlin/com/rbac/ui/MainLayout.kt` - 主布局（包含切换按钮）
- `src/main/kotlin/com/rbac/ui/LoginView.kt` - 登录页面（包含切换按钮）
- `src/main/kotlin/com/rbac/ui/component/AccessDeniedView.kt` - 访问拒绝页面

## 参考资料

- [Vaadin Lumo Theme Documentation](https://vaadin.com/docs/latest/styling/lumo)
- [Lumo Design Tokens](https://vaadin.com/docs/latest/styling/lumo/design-tokens)
- [Dark Mode Best Practices](https://vaadin.com/docs/latest/styling/lumo/variants)
