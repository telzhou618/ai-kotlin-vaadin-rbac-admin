#!/bin/bash

# RBAC 系统状态检查脚本

APP_NAME=rbac-system-1.0.0.jar
APP_HOME=$(cd "$(dirname "$0")"; pwd)

echo "=========================================="
echo "RBAC 系统状态检查"
echo "=========================================="
echo ""

# 检查进程
PID=$(ps aux | grep $APP_NAME | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "状态: 未运行"
    exit 1
else
    echo "状态: 运行中"
    echo "PID: $PID"
    echo ""
    
    # 显示进程信息
    echo "进程信息:"
    ps aux | grep $PID | grep -v grep
    echo ""
    
    # 检查端口
    echo "端口监听:"
    netstat -an | grep 8080 | grep LISTEN
    echo ""
    
    # 检查内存使用
    echo "内存使用:"
    ps -p $PID -o pid,vsz,rss,pmem,comm
    echo ""
    
    # 检查应用健康
    echo "应用健康检查:"
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080)
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "302" ]; then
        echo "✓ 应用正常响应 (HTTP $HTTP_CODE)"
    else
        echo "✗ 应用无响应或异常 (HTTP $HTTP_CODE)"
    fi
fi

echo ""
echo "=========================================="
