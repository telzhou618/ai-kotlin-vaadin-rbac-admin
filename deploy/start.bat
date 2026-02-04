@echo off
chcp 65001 >nul
title RBAC 系统启动

set APP_NAME=rbac-system-1.0.0.jar
set APP_HOME=%~dp0

cd /d %APP_HOME%

echo ==========================================
echo 正在启动 RBAC 系统...
echo ==========================================
echo.

REM 检查 JAR 包是否存在
if not exist %APP_NAME% (
    echo 错误: 找不到 %APP_NAME%
    echo 请确保 JAR 包在当前目录下
    pause
    exit /b 1
)

REM 启动应用
start "RBAC System" java -Xms512m -Xmx1024m ^
     -XX:+UseG1GC ^
     -XX:MaxGCPauseMillis=200 ^
     -Dfile.encoding=UTF-8 ^
     -Duser.timezone=Asia/Shanghai ^
     -jar %APP_NAME% ^
     --spring.profiles.active=prod

echo.
echo 应用启动中，请稍候...
timeout /t 5 /nobreak >nul

echo.
echo ==========================================
echo 应用已启动！
echo 访问地址: http://localhost:8080
echo ==========================================
echo.
echo 按任意键关闭此窗口...
pause >nul
