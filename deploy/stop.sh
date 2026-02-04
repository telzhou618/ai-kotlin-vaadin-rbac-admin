#!/bin/bash

# RBAC 系统停止脚本

APP_NAME=rbac-system-1.0.0.jar

# 查找进程
PID=$(ps aux | grep $APP_NAME | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "应用未运行"
    exit 1
fi

echo "正在停止应用，PID: $PID"
kill $PID

# 等待进程结束
echo -n "等待应用停止"
for i in {1..30}; do
    if ! ps -p $PID > /dev/null 2>&1; then
        echo ""
        echo "应用已停止"
        exit 0
    fi
    echo -n "."
    sleep 1
done

# 强制停止
echo ""
echo "应用未响应，强制停止..."
kill -9 $PID
echo "应用已强制停止"
