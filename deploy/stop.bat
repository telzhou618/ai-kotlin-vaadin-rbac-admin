@echo off
chcp 65001 >nul
title RBAC 系统停止

set APP_NAME=rbac-system-1.0.0.jar

echo ==========================================
echo 正在停止 RBAC 系统...
echo ==========================================
echo.

REM 查找并停止 Java 进程
for /f "tokens=2" %%i in ('tasklist ^| findstr /i "java.exe"') do (
    wmic process where "ProcessId=%%i" get CommandLine 2>nul | findstr /i "%APP_NAME%" >nul
    if not errorlevel 1 (
        echo 找到进程 PID: %%i
        taskkill /PID %%i /F
        echo 应用已停止
        goto :end
    )
)

echo 未找到运行中的应用

:end
echo.
echo 按任意键关闭此窗口...
pause >nul
